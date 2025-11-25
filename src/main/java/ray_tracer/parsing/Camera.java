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
}
