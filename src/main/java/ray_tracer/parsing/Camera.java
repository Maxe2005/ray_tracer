package ray_tracer.parsing;

import ray_tracer.geometry.Point;
import ray_tracer.geometry.Vector;

/**
 Représente la caméra de la scène.
 Elle contient :
  - la position de l'oeil (lookFrom)
  - le point visé (lookAt)
  - le vecteur "haut" de la caméra (upDir)
  - l'angle de vue (fov)
 */
public class Camera {
    private Point lookFrom;
    private Point lookAt;
    private Vector upDir;
    private double fov;

    /**
     * Constructeur de la caméra.
     * @param lookFromX position X de l'oeil
     * @param lookFromY position Y de l'oeil
     * @param lookFromZ position Z de l'oeil
     * @param lookAtX position X du point visé
     * @param lookAtY position Y du point visé
     * @param lookAtZ position Z du point visé
     * @param upDirX vecteur up X
     * @param upDirY vecteur up Y
     * @param upDirZ vecteur up Z
     * @param fov champ de vue en degrés
     */
    public Camera(double lookFromX, double lookFromY, double lookFromZ,
            double lookAtX, double lookAtY, double lookAtZ,
            double upDirX, double upDirY, double upDirZ,
            double fov) {
        this.lookFrom = new Point(lookFromX, lookFromY, lookFromZ);
        this.lookAt = new Point(lookAtX, lookAtY, lookAtZ);
        this.upDir = new Vector(upDirX, upDirY, upDirZ).normalize();
        this.fov = fov;
    }

    /** @return champ de vue en radians */
    public double getRadiansFov() {
        return (fov * Math.PI) / 180.0;
    }

    /**
     * Direction principale de la caméra (du point regardé vers l'oeil).
     * @return vecteur direction normalisé
     */
    public Vector getDirection() {
        return lookFrom.subtraction(lookAt).normalize();
    }

    /** @return position de la caméra (lookFrom) */
    public Point getLookFrom() {
        return lookFrom;
    }
    /** @return point visé (lookAt) */
    public Point getLookAt() {
        return lookAt;
    }

    /** @return vecteur "up" de la caméra */
    public Vector getUpDir() {
        return upDir;
    }

    /** @return champ de vue en degrés */
    public double getFov() {
        return fov;
    }

    /** @return représentation textuelle de la caméra */
    public String toString() {
        return "Camera(lookFrom: [" + lookFrom + "], "
                + "lookAt: [" + lookAt + "], "
                + "upDir: [" + upDir + "], "
                + "fov: " + fov + ")";
    }
}
