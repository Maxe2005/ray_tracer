package ray_tracer.geometry;

import org.junit.jupiter.api.Test;
import ray_tracer.parsing.Camera;

import static org.junit.jupiter.api.Assertions.*;

public class OrthonormalTest {

    @Test
    public void fromCamera_generates_orthonormal_basis() {
        Camera cam = new Camera(0,0,5, 0,0,0, 0,1,0, 60);
        Orthonormal o = Orthonormal.fromCamera(cam);

        // w should be the camera direction normalized
        Vector expectedW = cam.getDirection().normalize();
        assertEquals(expectedW.getX(), o.getW().getX(), 1e-12);
        assertEquals(expectedW.getY(), o.getW().getY(), 1e-12);
        assertEquals(expectedW.getZ(), o.getW().getZ(), 1e-12);

        // u and v should be orthogonal to w
        double dotU_W = o.getU().scalarProduct(o.getW());
        double dotV_W = o.getV().scalarProduct(o.getW());
        assertEquals(0.0, dotU_W, 1e-12);
        assertEquals(0.0, dotV_W, 1e-12);
    }

}
