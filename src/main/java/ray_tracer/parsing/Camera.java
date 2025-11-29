package ray_tracer.parsing;

import ray_tracer.geometry.Point;
import ray_tracer.geometry.Vector;

public class Camera {
    private Point lookFrom;
    private Point lookAt;
    private Vector upDir;
    private double fov;

    public Camera(double lookFromX, double lookFromY, double lookFromZ,
            double lookAtX, double lookAtY, double lookAtZ,
            double upDirX, double upDirY, double upDirZ,
            double fov) {
        this.lookFrom = new Point(lookFromX, lookFromY, lookFromZ);
        this.lookAt = new Point(lookAtX, lookAtY, lookAtZ);
        this.upDir = new Vector(upDirX, upDirY, upDirZ).normalize();
        this.fov = fov;
    }

    public double getRadiansFov() {
        return (fov * Math.PI) / 180.0;
    }

    public Vector getDirection() {
        return lookFrom.subtraction(lookAt).normalize();
    }

    public Point getLookFrom() {
        return lookFrom;
    }
    public Point getLookAt() {
        return lookAt;
    }

    public Vector getUpDir() {
        return upDir;
    }

    public double getFov() {
        return fov;
    }

    public String toString() {
        return "Camera(lookFrom: [" + lookFrom + "], "
                + "lookAt: [" + lookAt + "], "
                + "upDir: [" + upDir + "], "
                + "fov: " + fov + ")";
    }

    /**
     * Return a shallow copy of this camera (points/vectors are immutable wrappers here).
     */
    public Camera copy() {
        return new Camera(lookFrom.getX(), lookFrom.getY(), lookFrom.getZ(),
                lookAt.getX(), lookAt.getY(), lookAt.getZ(),
                upDir.getX(), upDir.getY(), upDir.getZ(),
                fov);
    }

    /**
     * Builder convenience for constructing and transforming cameras.
     */
    public static class Builder {
        private Point lookFrom;
        private Point lookAt;
        private Vector upDir;
        private double fov;

        public Builder lookFrom(Point p) { this.lookFrom = p; return this; }
        public Builder lookAt(Point p) { this.lookAt = p; return this; }
        public Builder upDir(Vector v) { this.upDir = v; return this; }
        public Builder fov(double f) { this.fov = f; return this; }

        public Camera build() {
            return new Camera(lookFrom.getX(), lookFrom.getY(), lookFrom.getZ(),
                    lookAt.getX(), lookAt.getY(), lookAt.getZ(),
                    upDir.getX(), upDir.getY(), upDir.getZ(),
                    fov);
        }
    }

    /**
     * Return a translated camera by adding the vector to lookFrom and lookAt.
     */
    public Camera translate(Vector v) {
        Point nf = new Point(lookFrom.getX() + v.getX(), lookFrom.getY() + v.getY(), lookFrom.getZ() + v.getZ());
        Point na = new Point(lookAt.getX() + v.getX(), lookAt.getY() + v.getY(), lookAt.getZ() + v.getZ());
        return new Camera(nf.getX(), nf.getY(), nf.getZ(), na.getX(), na.getY(), na.getZ(), upDir.getX(), upDir.getY(), upDir.getZ(), fov);
    }

    /**
     * Set lookFrom/LookAt explicitly (returns a new Camera instance).
     */
    public Camera setLookFromLookAt(Point from, Point at) {
        return new Camera(from.getX(), from.getY(), from.getZ(), at.getX(), at.getY(), at.getZ(), upDir.getX(), upDir.getY(), upDir.getZ(), fov);
    }
}
