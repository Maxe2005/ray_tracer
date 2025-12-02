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

    public Triangle(Point v0, Point v1, Point v2, Color diffuse, Color specular, int shininess) {
        super(diffuse, specular, shininess);
        this.a = v0;
        this.b = v1;
        this.c = v2;
    }

    @Override
    public Optional<Intersection> intersect(Ray ray) {
        return intersect_v1(ray);
    }
    
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
        if (beta < 0){// || beta > 1) {
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


    public Point getV0() {
        return a;
    }

    public Point getV1() {
        return b;
    }

    public Point getV2() {
        return c;
    }

    @Override
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
