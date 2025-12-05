package ray_tracer.geometry.shapes;

import java.util.Optional;

import ray_tracer.geometry.Intersection;
import ray_tracer.geometry.Point;
import ray_tracer.imaging.Color;
import ray_tracer.raytracer.Ray;
import ray_tracer.geometry.Vector;
/**
  Dans un ray tracer, les triangles sont très importants car :
  - toutes les formes complexes peuvent être découpées en triangles
  - un triangle est la forme 3D la plus simple (3 sommets, une surface plate)
 */

public class Triangle extends Shape {
    // Les trois sommets du triangle
    private final Point a;
    private final Point b;
    private final Point c;

    /**
     * Constructeur triangle défini par trois sommets.
     * @param v0 sommet 0
     * @param v1 sommet 1
     * @param v2 sommet 2
     * @param diffuse couleur diffuse
     * @param specular couleur spéculaire
     * @param shininess brillance
     */
    public Triangle(Point v0, Point v1, Point v2, Color diffuse, Color specular, int shininess) {
        super(diffuse, specular, shininess);
        this.a = v0;
        this.b = v1;
        this.c = v2;
    }

    @Override
    /**
     * Intersection rayon-triangle (méthode principale, délègue à une variante).
     * @param ray rayon testé
     * @return {@code Optional<Intersection>} résultat
     */
    public Optional<Intersection> intersect(Ray ray) {
        return intersect_v1(ray);
    }
    
    /**
     * Variante de l'algorithme d'intersection (Möller–Trumbore-like).
     * @param ray rayon testé
     * @return {@code Optional<Intersection>} résultat
     */
    public Optional<Intersection> intersect_v1(Ray ray) {
        Vector edge1 = b.subtraction(a);
        Vector edge2 = c.subtraction(a);
        Vector p = ray.getDirection().vectorialProduct(edge2);
        double det = edge1.scalarProduct(p);
        if (Math.abs(det) < EPSILON) {
            return Optional.empty(); // Pas d'intersection
        }
        double invDet = 1.0 / det;
        Vector tv = ray.getOrigin().subtraction(a);
        double beta = invDet * tv.scalarProduct(p);
        if (beta < 0 || beta > 1) {
            return Optional.empty(); // Pas d'intersection
        }
        Vector q = tv.vectorialProduct(edge1);
        double gama = invDet * ray.getDirection().scalarProduct(q);
        if (gama < 0 || beta + gama > 1) {
            return Optional.empty(); // Pas d'intersection
        }
        double t = invDet * edge2.scalarProduct(q);
        if (t < EPSILON) {
            return Optional.empty(); // Pas d'intersection
        }
        return Optional.of(new Intersection(ray, t, this));
    }

    /**
     * Variante alternative d'intersection (plus robuste pour certains cas).
     * @param ray rayon testé
     * @return {@code Optional<Intersection>} résultat
     */
    public Optional<Intersection> intersect_v2(Ray ray) {

        Vector rayDir = ray.getDirection();
        Point  rayOrigin = ray.getOrigin();

        // Vecteurs du triangle
        Vector edge1 = b.subtraction(a);
        Vector edge2 = c.subtraction(a);

        // Calcul de la normale du triangle
        Vector triangleNormal = edge1.vectorialProduct(edge2);
        double normalLength = triangleNormal.norm();

        // Triangle dégénéré
        if (normalLength < EPSILON) {
            return Optional.empty();
        }

        // // Normaliser pour l'intersection
        // Vector n = triangleNormal.normalize();

        // p = d × edge2
        Vector p = rayDir.vectorialProduct(edge2);

        // det = edge1 · p
        double det = edge1.scalarProduct(p);

        // Si det ≈ 0 → rayon parallèle au plan du triangle
        if (Math.abs(det) < EPSILON) {
            return Optional.empty();
        }

        double invDet = 1.0 / det;

        // tVec = origin - a
        Vector tVec = rayOrigin.subtraction(a);

        // β = (tVec · p) / det
        double beta = tVec.scalarProduct(p) * invDet;

        if (beta < 0.0) {
            return Optional.empty();
        }

        // q = tVec × edge1
        Vector q = tVec.vectorialProduct(edge1);

        // γ = (d · q) / det
        double gamma = rayDir.scalarProduct(q) * invDet;

        if (gamma < 0.0 || beta + gamma > 1.0) {
            return Optional.empty();
        }

        // t = (edge2 · q) / det
        double tValue = edge2.scalarProduct(q) * invDet;

        if (tValue < EPSILON) {
            return Optional.empty();
        }

        // // Point d'intersection
        // Point hitPoint = rayOrigin.addition(rayDir.scalarMultiplication(tValue));

        // // Correction IMPORTANTE : inversion correcte de la normale
        // Vector intersectionNormal = n;
        // if (det < 0) {
        //     intersectionNormal = n.scalarMultiplication(-1);
        // }
        
        return Optional.of(new Intersection(ray, tValue, this));
    }


    /** @return sommet V0 */
    public Point getV0() {
        return a;
    }

    /** @return sommet V1 */
    public Point getV1() {
        return b;
    }

    /** @return sommet V2 */
    public Point getV2() {
        return c;
    }

    @Override
    /**
     * Calcule la normale du triangle (edge1 × edge2) et la normalise.
     * @param point point sur le triangle (ignoré ici)
     * @return normale unitaire
     */
    public Vector getNormalAt(Point point) {
        Vector edge1 = b.subtraction(a);
        Vector edge2 = c.subtraction(a);
        return edge1.vectorialProduct(edge2).normalize();
    }

    @Override
    public String toString() {
        return "Triangle(a: " + a + ", b: " + b + ", c: " + c
                + ", diffuse: " + getDiffuse() + ", specular: " + getSpecular() + ")";
    }


}
