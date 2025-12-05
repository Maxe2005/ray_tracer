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

        // compute centroid bounds to choose split axis
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
        Collections.sort(shapes, new Comparator<Shape>() {
            @Override
            public int compare(Shape s1, Shape s2) {
                double v1;
                double v2;
                Point p1 = s1.getBounds().centroid();
                Point p2 = s2.getBounds().centroid();
                if (ax == 0) { v1 = p1.getX(); v2 = p2.getX(); }
                else if (ax == 1) { v1 = p1.getY(); v2 = p2.getY(); }
                else { v1 = p1.getZ(); v2 = p2.getZ(); }
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
