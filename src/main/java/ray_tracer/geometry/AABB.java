package ray_tracer.geometry;

import ray_tracer.raytracer.Ray;

public class AABB {
    private final Point min;
    private final Point max;

    public AABB(Point min, Point max) {
        this.min = min;
        this.max = max;
    }

    public Point getMin() {
        return min;
    }

    public Point getMax() {
        return max;
    }

    public static AABB surroundingBox(AABB a, AABB b) {
        double minX = Math.min(a.min.getX(), b.min.getX());
        double minY = Math.min(a.min.getY(), b.min.getY());
        double minZ = Math.min(a.min.getZ(), b.min.getZ());
        double maxX = Math.max(a.max.getX(), b.max.getX());
        double maxY = Math.max(a.max.getY(), b.max.getY());
        double maxZ = Math.max(a.max.getZ(), b.max.getZ());
        return new AABB(new Point(minX, minY, minZ), new Point(maxX, maxY, maxZ));
    }

    public Point centroid() {
        return new Point((min.getX() + max.getX()) * 0.5,
                         (min.getY() + max.getY()) * 0.5,
                         (min.getZ() + max.getZ()) * 0.5);
    }

    /**
     * Ray-box intersection (slab method). Returns true if the ray hits the box with t in [0, tMax].
     */
    public boolean hit(Ray r, double tMax) {
        double tMin = 0.0;
        double tMaxLocal = tMax;

        double originX = r.getOrigin().getX();
        double originY = r.getOrigin().getY();
        double originZ = r.getOrigin().getZ();
        double dirX = r.getDirection().getX();
        double dirY = r.getDirection().getY();
        double dirZ = r.getDirection().getZ();

        // X slab
        if (Math.abs(dirX) < 1e-12) {
            if (originX < min.getX() || originX > max.getX()) return false;
        } else {
            double invD = 1.0 / dirX;
            double t0 = (min.getX() - originX) * invD;
            double t1 = (max.getX() - originX) * invD;
            if (invD < 0.0) {
                double tmp = t0; t0 = t1; t1 = tmp;
            }
            tMin = Math.max(tMin, t0);
            tMaxLocal = Math.min(tMaxLocal, t1);
            if (tMaxLocal <= tMin) return false;
        }

        // Y slab
        if (Math.abs(dirY) < 1e-12) {
            if (originY < min.getY() || originY > max.getY()) return false;
        } else {
            double invD = 1.0 / dirY;
            double t0 = (min.getY() - originY) * invD;
            double t1 = (max.getY() - originY) * invD;
            if (invD < 0.0) {
                double tmp = t0; t0 = t1; t1 = tmp;
            }
            tMin = Math.max(tMin, t0);
            tMaxLocal = Math.min(tMaxLocal, t1);
            if (tMaxLocal <= tMin) return false;
        }

        // Z slab
        if (Math.abs(dirZ) < 1e-12) {
            if (originZ < min.getZ() || originZ > max.getZ()) return false;
        } else {
            double invD = 1.0 / dirZ;
            double t0 = (min.getZ() - originZ) * invD;
            double t1 = (max.getZ() - originZ) * invD;
            if (invD < 0.0) {
                double tmp = t0; t0 = t1; t1 = tmp;
            }
            tMin = Math.max(tMin, t0);
            tMaxLocal = Math.min(tMaxLocal, t1);
            if (tMaxLocal <= tMin) return false;
        }

        return true;
    }
}
