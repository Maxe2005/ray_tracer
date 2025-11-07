package ray_tracer.parsing;

public class Camera {
    private double lookFromX, lookFromY, lookFromZ;
    private double lookAtX, lookAtY, lookAtZ;
    private double upDirX, upDirY, upDirZ;
    private double fov;

    public Camera(double lookFromX, double lookFromY, double lookFromZ,
            double lookAtX, double lookAtY, double lookAtZ,
            double upDirX, double upDirY, double upDirZ,
            double fov) {
        this.lookFromX = lookFromX;
        this.lookFromY = lookFromY;
        this.lookFromZ = lookFromZ;
        this.lookAtX = lookAtX;
        this.lookAtY = lookAtY;
        this.lookAtZ = lookAtZ;
        this.upDirX = upDirX;
        this.upDirY = upDirY;
        this.upDirZ = upDirZ;
        this.fov = fov;
    }

    public double getLookFromX() {
        return lookFromX;
    }

    public double getLookFromY() {
        return lookFromY;
    }

    public double getLookFromZ() {
        return lookFromZ;
    }

    public double getLookAtX() {
        return lookAtX;
    }

    public double getLookAtY() {
        return lookAtY;
    }

    public double getLookAtZ() {
        return lookAtZ;
    }

    public double getUpDirX() {
        return upDirX;
    }

    public double getUpDirY() {
        return upDirY;
    }

    public double getUpDirZ() {
        return upDirZ;
    }

    public double getFov() {
        return fov;
    }

    public String toString() {
        return "Camera(lookFrom: [" + lookFromX + ", " + lookFromY + ", " + lookFromZ + "], "
                + "lookAt: [" + lookAtX + ", " + lookAtY + ", " + lookAtZ + "], "
                + "upDir: [" + upDirX + ", " + upDirY + ", " + upDirZ + "], "
                + "fov: " + fov + ")";
    }

}
