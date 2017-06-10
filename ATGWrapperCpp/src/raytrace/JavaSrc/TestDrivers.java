import java.util.Vector;

public class TestDrivers
{

    public static void vector3DNormalize(final float x,
                                         final float y,
                                         final float z)
    {
        new Vector3D(x, y, z).normalize();
    }

    public static void surfaceShade(final float rval,
                                    final float gval,
                                    final float bval,
                                    final float a,
                                    final float d,
                                    final float s,
                                    final float n,
                                    final float r,
                                    final float t,
                                    final float index,
                                    final float pX,
                                    final float pY,
                                    final float pZ,
                                    final float nX,
                                    final float nY,
                                    final float nZ,
                                    final float vX,
                                    final float vY,
                                    final float vZ,
                                    final int lType,
                                    final float lX,
                                    final float lY,
                                    final float lZ,
                                    final float lR,
                                    final float lG,
                                    final float lB)
    {
        final Surface surface = new Surface(rval, gval, bval, a, d, s, n, r, t, index);
        final Vector3D pVec = new Vector3D(pX, pY, pZ);
        final Vector3D nVec = new Vector3D(nX, nY, nZ);
        final Vector3D vVec = new Vector3D(vX, vY, vZ);

        final Light l = new Light(lType, new Vector3D(lX, lY, lZ), lR, lG, lB);
        final Vector<Light> lights = new Vector<Light>();
        lights.add(l);

        surface.Shade(pVec, nVec, vVec, lights, new Vector<Renderable>(), new Color(1, 1, 1));
    }

    public static void rayTrace(final float cX,
                                final float cY,
                                final float cZ,
                                final float r,
                                final float eyeX,
                                final float eyeY,
                                final float eyeZ,
                                final float dirX,
                                final float dirY,
                                final float dirZ)
    {
        // Sphere.intersect() does not use the {@code surface} field.
        final Sphere sphere = new Sphere(null, new Vector3D(cX, cY, cZ), r);
        final Vector<Renderable> objects = new Vector<Renderable>();
        objects.add(sphere);

        final Vector3D eye = new Vector3D(eyeX, eyeY, eyeZ);
        final Vector3D dir = new Vector3D(dirX, dirY, dirZ);
        new Ray(eye, dir).trace(objects);
    }

    public static void sphereIntersect(final float rval,
                                       final float gval,
                                       final float bval,
                                       final float a,
                                       final float d,
                                       final float s,
                                       final float n,
                                       final float r,
                                       final float t,
                                       final float index,
                                       final float x,
                                       final float y,
                                       final float z,
                                       final float rad,
                                       final float eyeX,
                                       final float eyeY,
                                       final float eyeZ,
                                       final float dirX,
                                       final float dirY,
                                       final float dirZ)
    {
        final Vector3D eye = new Vector3D(eyeX, eyeY, eyeZ);
        final Vector3D dir = new Vector3D(dirX, dirY, dirZ);
        final Ray ray = new Ray(eye, dir);

        final Surface surface = new Surface(rval, gval, bval, a, d, s, n, r, t, index);
        final Vector3D center = new Vector3D(x, y, z);
        final Sphere sphere = new Sphere(surface, center, rad);

        sphere.intersect(ray);
    }

    public static void sphereShade(final float rval,
                                   final float gval,
                                   final float bval,
                                   final float a,
                                   final float d,
                                   final float s,
                                   final float n,
                                   final float r,
                                   final float t,
                                   final float index,
                                   final float x,
                                   final float y,
                                   final float z,
                                   final float rad,
                                   final float eyeX,
                                   final float eyeY,
                                   final float eyeZ,
                                   final float dirX,
                                   final float dirY,
                                   final float dirZ,
                                   final int lType,
                                   final float lX,
                                   final float lY,
                                   final float lZ,
                                   final float lR,
                                   final float lG,
                                   final float lB,
                                   final float bgR,
                                   final float bgG,
                                   final float bgB)
    {
        final Vector3D eye = new Vector3D(eyeX, eyeY, eyeZ);
        final Vector3D dir = new Vector3D(dirX, dirY, dirZ);
        final Ray ray = new Ray(eye, dir);

        final Surface surface = new Surface(rval, gval, bval, a, d, s, n, r, t, index);
        final Vector3D center = new Vector3D(x, y, z);
        final Sphere sphere = new Sphere(surface, center, rad);
        final Vector<Renderable> objects = new Vector<Renderable>();
        objects.add(sphere);

        final Light light = new Light(lType, new Vector3D(lX, lY, lZ), lR, lG, lB);
        final Vector<Light> lights = new Vector<Light>();
        lights.add(light);

        final Color bgnd = new Color(bgR, bgG, bgB);
        sphere.Shade(ray, lights, objects, bgnd);
    }
}
