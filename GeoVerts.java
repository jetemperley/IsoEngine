
import com.jogamp.opengl.math.Matrix4;

// holds the vertecies for cube faces in GL_TRIANGLE_STRIP form
// assumes z + up, and camera facing + y
public class GeoVerts {

    public static float[] getDiamondVerts() {

        Vec3 n = new Vec3(0, -1, 1);
        n.normalise();

        float[] v1 = { 0, 0, 0.5f, 1, n.x, n.y, n.z, 1,

                -0.5f, -0.5f, 0, 1, n.x, n.y, n.z, 1,

                -0.5f, 0.5f, 0, 1, n.x, n.y, n.z, 1 };

        // 4 vals * (3 verts + 3 norms) * 8 faces
        float[] v2 = new float[4 * 6 * 8];

        for (int i = 0; i < v1.length; i++) {
            v2[i] = v1[i];
        }

        // rotation for top half of shape
        Matrix4 m = new Matrix4();
        m.loadIdentity();
        m.rotate((float) Math.PI / 2, 0, 0, 1);

        // outre loop represents each face rotated for the top half
        for (int i = 0; i < 3; i++) {
            // inner loop represents each vertex
            for (int j = 0; j < 6; j++) {

                // 12 values per face (+12 norms = 24), 4 per vertex
                m.multVec(v2, (i * 24) + (j * 4), v2, ((i + 1) * 24) + (j * 4));
            }
        }

        // invert single face
        m.loadIdentity();
        m.rotate((float) Math.PI, 1, 0, 0);

        for (int i = 0; i < 6; i++) {
            m.multVec(v2, i * 4, v2, (24 * 4) + (i * 4));
        }

        m.loadIdentity();
        m.rotate((float) Math.PI / 2, 0, 0, 1);

        // outre loop represents each face rotated for the top half
        for (int i = 0; i < 3; i++) {
            // inner loop represents each vertex
            for (int j = 0; j < 6; j++) {

                // 12 values per face, 4 per vertex
                m.multVec(v2, ((i + 4) * 24) + (j * 4), v2, ((i + 5) * 24) + (j * 4));
            }
        }

        // remove 1s, 3 vals * ( 3 verts + 3 norms) * 8 faces
        float[] out = new float[3 * 6 * 8];

        int j = 0;
        for (int i = 0; i < v2.length; i += 4) {
            out[j] = v2[i];
            out[j + 1] = v2[i + 1];
            out[j + 2] = v2[i + 2];
            j += 3;
        }

        return out;
    }

    static float[] transformSet(float[] set, Matrix4 mat) {
        float[] out = new float[set.length];
        for (int i = 0; i < set.length; i+=4){
            mat.multVec(set, i, out, i);
        }

        return out;
    }

    static float[] removeEvery4th(float[] v){
        float[] out = new float[v.length*3/4];
        int j = 0;
        for (int i = 0; i < v.length; i += 4) {
            out[j] = v[i];
            out[j + 1] = v[i + 1];
            out[j + 2] = v[i + 2];
            j += 3;
        }
        return out;
    }

    static float[] getFaceTexCoords(){

        float[] arr = {
            1, 1,
            0, 1,
            1, 0,
            0, 0

        };
        return arr;

    }

    private static float[] getTopFaceVerts() {

        float[] arr = { 
            -0.5f, 0.5f, 0.5f, 1,
            0, 0, 1, 1,
            0.5f, 0.5f, 0.5f, 1,
            0, 0, 1, 1,
            -0.5f, -0.5f, 0.5f, 1,
            0, 0, 1, 1,
            0.5f, -0.5f, 0.5f, 1,
            0, 0, 1, 1 
        };
        return arr;

    }

    public static float[] getBotVerts() {
        Matrix4 mat = new Matrix4();
        mat.loadIdentity();
        mat.rotate((float)Math.PI, 1, 0, 0);

        float[] arr = transformSet(getTopFaceVerts(), mat);
        arr = removeEvery4th(arr);
        return arr;
    }

    public static float[] getTopVerts() {
        float[] arr = removeEvery4th(getTopFaceVerts());
        return arr;
    }

    public static float[] getFrontVerts() {
        Matrix4 mat = new Matrix4();
        mat.loadIdentity();
        mat.rotate((float)Math.PI/2, 1, 0, 0);

        float[] arr = transformSet(getTopFaceVerts(), mat);
        arr = removeEvery4th(arr);
        return arr;
    }

    public static float[] getBackVerts() {
        Matrix4 mat = new Matrix4();
        mat.loadIdentity();
        mat.rotate((float)-Math.PI/2, 1, 0, 0);

        float[] arr = transformSet(getTopFaceVerts(), mat);
        arr = removeEvery4th(arr);
        return arr;
    }

    public static float[] getLeftVerts() {
        Matrix4 mat = new Matrix4();
        mat.loadIdentity();
        mat.rotate((float)-Math.PI/2, 0, 1, 0);

        float[] arr = transformSet(getTopFaceVerts(), mat);
        arr = removeEvery4th(arr);
        return arr;
    }

    public static float[] getRightVerts() {
        Matrix4 mat = new Matrix4();
        mat.loadIdentity();
        mat.rotate((float)Math.PI/2, 0, 1, 0);

        float[] arr = transformSet(getTopFaceVerts(), mat);
        arr = removeEvery4th(arr);
        return arr;
    }

    static float[] interleave(float[] verts, float[] normals){
        float[] combined = new float[verts.length + normals.length];
        for (int i = 0; i < verts.length; i+=3){
            combined[i*2] = verts[i];
            combined[i*2+1] = verts[i+1];
            combined[i*2+2] = verts[i+2];

            combined[i*2+3] = normals[i];
            combined[i*2+4] = normals[i+1];
            combined[i*2+5] = normals[i+2];

        }

        return combined;
    }
}