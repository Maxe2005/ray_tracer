package ray_tracer.geometry.accel;

import ray_tracer.geometry.AABB;
import ray_tracer.geometry.Point;
import ray_tracer.geometry.shapes.Shape;
import ray_tracer.geometry.Intersection;
import ray_tracer.raytracer.Ray;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

public class BVHNode {
    private final AABB box;
    private final BVHNode left;
    private final BVHNode right;
    private final List<Shape> shapes; // non-null only for leaf

    private static final int DEFAULT_MAX_LEAF = 4;
    private static final int NUM_SAH_BUCKETS = 12;

    private BVHNode(AABB box, BVHNode left, BVHNode right, List<Shape> shapes) {
        this.box = box;
        this.left = left;
        this.right = right;
        this.shapes = shapes;
    }

    public static BVHNode build(List<Shape> inputShapes) {
        return build(inputShapes, DEFAULT_MAX_LEAF);
    }

    public static BVHNode build(List<Shape> inputShapes, int maxLeafSize) {
        if (inputShapes == null || inputShapes.isEmpty()) return null;
        List<Shape> shapes = new ArrayList<>(inputShapes);
        return buildRecursive(shapes, maxLeafSize);
    }

    private static BVHNode buildRecursive(List<Shape> shapes, int maxLeafSize) {
        // compute bounding box for all shapes
        AABB nodeBox = null;
        for (Shape s : shapes) {
            AABB b = s.getBounds();
            nodeBox = (nodeBox == null) ? b : AABB.surroundingBox(nodeBox, b);
        }

        if (shapes.size() <= maxLeafSize) {
            return new BVHNode(nodeBox, null, null, shapes);
        }
        // Use a simple SAH via binning to choose split
        // compute centroid bounds
        Point c0 = shapes.get(0).getBounds().centroid();
        double minX = c0.getX(), minY = c0.getY(), minZ = c0.getZ();
        double maxX = c0.getX(), maxY = c0.getY(), maxZ = c0.getZ();
        for (int i = 1; i < shapes.size(); i++) {
            Point c = shapes.get(i).getBounds().centroid();
            if (c.getX() < minX) minX = c.getX();
            if (c.getY() < minY) minY = c.getY();
            if (c.getZ() < minZ) minZ = c.getZ();
            if (c.getX() > maxX) maxX = c.getX();
            if (c.getY() > maxY) maxY = c.getY();
            if (c.getZ() > maxZ) maxZ = c.getZ();
        }

        double extentX = maxX - minX;
        double extentY = maxY - minY;
        double extentZ = maxZ - minZ;

        int axis = 0;
        if (extentY > extentX && extentY >= extentZ) axis = 1;
        else if (extentZ > extentX && extentZ > extentY) axis = 2;
        final int ax = axis;

        // If extent is negligible, fallback to leaf
        double maxExtent = Math.max(extentX, Math.max(extentY, extentZ));
        if (maxExtent < 1e-9) {
            return new BVHNode(nodeBox, null, null, shapes);
        }

        // Prepare buckets
        int B = NUM_SAH_BUCKETS;
        class BucketInfo { int count = 0; AABB bounds = null; }
        BucketInfo[] buckets = new BucketInfo[B];
        for (int i = 0; i < B; i++) buckets[i] = new BucketInfo();

        // fill buckets
        for (Shape s : shapes) {
            Point cen = s.getBounds().centroid();
            double coord = (axis == 0) ? cen.getX() : (axis == 1) ? cen.getY() : cen.getZ();
            double minCoord = (axis == 0) ? minX : (axis == 1) ? minY : minZ;
            double extent = (axis == 0) ? extentX : (axis == 1) ? extentY : extentZ;
            int b = (int) (B * ((coord - minCoord) / extent));
            if (b < 0) b = 0;
            if (b >= B) b = B - 1;
            BucketInfo bi = buckets[b];
            bi.count++;
            bi.bounds = (bi.bounds == null) ? s.getBounds() : AABB.surroundingBox(bi.bounds, s.getBounds());
        }

        // compute SAH cost for splits between buckets
        double[] leftArea = new double[B - 1];
        int[] leftCount = new int[B - 1];
        AABB acc = null;
        int accCount = 0;
        for (int i = 0; i < B - 1; i++) {
            if (buckets[i].count > 0) {
                acc = (acc == null) ? buckets[i].bounds : AABB.surroundingBox(acc, buckets[i].bounds);
            }
            accCount += buckets[i].count;
            leftArea[i] = (acc == null) ? 0.0 : surfaceArea(acc);
            leftCount[i] = accCount;
        }

        double[] rightArea = new double[B - 1];
        int[] rightCount = new int[B - 1];
        acc = null;
        accCount = 0;
        for (int i = B - 1; i > 0; i--) {
            int idx = i - 1;
            if (buckets[i].count > 0) {
                acc = (acc == null) ? buckets[i].bounds : AABB.surroundingBox(acc, buckets[i].bounds);
            }
            accCount += buckets[i].count;
            rightArea[idx] = (acc == null) ? 0.0 : surfaceArea(acc);
            rightCount[idx] = accCount;
        }

        // total area used for normalization
        double totalArea = surfaceArea(nodeBox);

        double bestCost = Double.POSITIVE_INFINITY;
        int bestSplit = -1;
        for (int i = 0; i < B - 1; i++) {
            if (leftCount[i] == 0 || rightCount[i] == 0) continue;
            // SAH cost: assume traversal cost = 1, intersection cost = 1
            double cost = 1.0 + (leftArea[i] * leftCount[i] + rightArea[i] * rightCount[i]) / totalArea;
            if (cost < bestCost) {
                bestCost = cost;
                bestSplit = i;
            }
        }

        // cost to make leaf (intersect all primitives)
        double leafCost = shapes.size();
        if (bestSplit == -1 || bestCost >= leafCost) {
            // fallback to median split if SAH not beneficial
            Collections.sort(shapes, new Comparator<Shape>() {
                @Override
                public int compare(Shape s1, Shape s2) {
                        Point p1 = s1.getBounds().centroid();
                        Point p2 = s2.getBounds().centroid();
                        double v1 = (ax == 0) ? p1.getX() : (ax == 1) ? p1.getY() : p1.getZ();
                        double v2 = (ax == 0) ? p2.getX() : (ax == 1) ? p2.getY() : p2.getZ();
                        return Double.compare(v1, v2);
                    }
            });
            int mid = shapes.size() / 2;
            List<Shape> leftList = new ArrayList<>(shapes.subList(0, mid));
            List<Shape> rightList = new ArrayList<>(shapes.subList(mid, shapes.size()));
            BVHNode leftNode = buildRecursive(leftList, maxLeafSize);
            BVHNode rightNode = buildRecursive(rightList, maxLeafSize);
            AABB combined = nodeBox;
            if (leftNode != null && rightNode != null) combined = AABB.surroundingBox(leftNode.box, rightNode.box);
            else if (leftNode != null) combined = leftNode.box;
            else if (rightNode != null) combined = rightNode.box;
            return new BVHNode(combined, leftNode, rightNode, null);
        }

        // partition by bucket index <= bestSplit to left
        List<Shape> leftList = new ArrayList<>();
        List<Shape> rightList = new ArrayList<>();
        for (Shape s : shapes) {
            Point cen = s.getBounds().centroid();
            double coord = (axis == 0) ? cen.getX() : (axis == 1) ? cen.getY() : cen.getZ();
            double minCoord = (axis == 0) ? minX : (axis == 1) ? minY : minZ;
            double extent = (axis == 0) ? extentX : (axis == 1) ? extentY : extentZ;
            int b = (int) (B * ((coord - minCoord) / extent));
            if (b < 0) b = 0;
            if (b >= B) b = B - 1;
            if (b <= bestSplit) leftList.add(s); else rightList.add(s);
        }

        // If one side empty, fallback to median as safe guard
        if (leftList.isEmpty() || rightList.isEmpty()) {
            Collections.sort(shapes, new Comparator<Shape>() {
                @Override
                public int compare(Shape s1, Shape s2) {
                        Point p1 = s1.getBounds().centroid();
                        Point p2 = s2.getBounds().centroid();
                        double v1 = (ax == 0) ? p1.getX() : (ax == 1) ? p1.getY() : p1.getZ();
                        double v2 = (ax == 0) ? p2.getX() : (ax == 1) ? p2.getY() : p2.getZ();
                        return Double.compare(v1, v2);
                    }
            });
            int mid = shapes.size() / 2;
            leftList = new ArrayList<>(shapes.subList(0, mid));
            rightList = new ArrayList<>(shapes.subList(mid, shapes.size()));
        }

        BVHNode leftNode = buildRecursive(leftList, maxLeafSize);
        BVHNode rightNode = buildRecursive(rightList, maxLeafSize);

        AABB combined = nodeBox;
        if (leftNode != null && rightNode != null) combined = AABB.surroundingBox(leftNode.box, rightNode.box);
        else if (leftNode != null) combined = leftNode.box;
        else if (rightNode != null) combined = rightNode.box;

        return new BVHNode(combined, leftNode, rightNode, null);
    }

    private static double surfaceArea(AABB a) {
        double dx = a.getMax().getX() - a.getMin().getX();
        double dy = a.getMax().getY() - a.getMin().getY();
        double dz = a.getMax().getZ() - a.getMin().getZ();
        return 2.0 * (dx * dy + dy * dz + dz * dx);
    }

    /**
     * Intersect ray with BVH, returning closest intersection if any.
     */
    public Optional<Intersection> intersect(Ray ray) {
        return intersect(ray, Double.POSITIVE_INFINITY);
    }

    private Optional<Intersection> intersect(Ray ray, double tMax) {
        if (box == null) return Optional.empty();
        if (!box.hit(ray, tMax)) return Optional.empty();

        Optional<Intersection> closest = Optional.empty();
        double closestT = tMax;

        if (shapes != null) {
            for (Shape s : shapes) {
                Optional<Intersection> oi = s.intersect(ray);
                if (oi.isPresent()) {
                    Intersection ins = oi.get();
                    if (ins.getDistance() < closestT) {
                        closestT = ins.getDistance();
                        closest = Optional.of(ins);
                    }
                }
            }
            return closest;
        }

        // internal node
        if (left != null) {
            Optional<Intersection> ol = left.intersect(ray, closestT);
            if (ol.isPresent()) {
                closest = ol;
                closestT = ol.get().getDistance();
            }
        }
        if (right != null) {
            Optional<Intersection> or = right.intersect(ray, closestT);
            if (or.isPresent()) {
                if (!closest.isPresent() || or.get().getDistance() < closest.get().getDistance()) {
                    closest = or;
                }
            }
        }

        return closest;
    }
}
