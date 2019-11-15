import static java.lang.Math.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.common.nio.Buffers;
import java.util.*;
import java.io.*;
import java.nio.*;

public class Sphere {
    private int numVertices, numIndices, prec; // prec = precision
    private int[] indices;
    private Vec3[] vertices;
    int verts, buffer;

    public Sphere(int p) {
        prec = p;
        initSphere();
    }

    private void initSphere() {
        numVertices = (prec + 1) * (prec + 1);
        numIndices = prec * prec * 6;
        vertices = new Vec3[numVertices];
        indices = new int[numIndices];
        for (int i = 0; i < numVertices; i++) {
            vertices[i] = new Vec3();
        }
        // calculate triangle vertices
        for (int i = 0; i <= prec; i++) {
            for (int j = 0; j <= prec; j++) {
                float y = (float) cos(toRadians(180 - i * 180 / prec));
                float x = -(float) cos(toRadians(j * 360 / prec)) * (float) abs(cos(asin(y)));
                float z = (float) sin(toRadians(j * 360 / prec)) * (float) abs(cos(asin(y)));
                vertices[i * (prec + 1) + j].y = y;
                vertices[i * (prec + 1) + j].x = x;
                vertices[i * (prec + 1) + j].z = z;


            }
        }
        // calculate triangle indices
        for (int i = 0; i < prec; i++) {
            for (int j = 0; j < prec; j++) {
                indices[6 * (i * prec + j) + 0] = i * (prec + 1) + j;
                indices[6 * (i * prec + j) + 1] = i * (prec + 1) + j + 1;
                indices[6 * (i * prec + j) + 2] = (i + 1) * (prec + 1) + j;
                indices[6 * (i * prec + j) + 3] = i * (prec + 1) + j + 1;
                indices[6 * (i * prec + j) + 4] = (i + 1) * (prec + 1) + j + 1;
                indices[6 * (i * prec + j) + 5] = (i + 1) * (prec + 1) + j;
            }
        }
    }

    public int[] getIndices() {
        return indices;
    }

    public Vec3[] getVertices() {
        // actually the code for extracting vertexes using indicies
        // float[] pvalues = new float[indices.length * 3]; // vertex positions

        // for (int i = 0; i < indices.length; i++) {
        //     pvalues[i * 3] = (float) (vertices[indices[i]]).x;
        //     pvalues[i * 3 + 1] = (float) (vertices[indices[i]]).y;
        //     pvalues[i * 3 + 2] = (float) (vertices[indices[i]]).z;
        // }

        return vertices;
    }

    float[] getVerts(){
        float[] verts = new float[indices.length*3];

        for (int i = 0; i  < indices.length; i++){
            verts[i*3] = vertices[indices[i]].x;
            verts[(i*3)+1] = vertices[indices[i]].y;
            verts[(i*3)+2] = vertices[indices[i]].z;
        }
        return verts;

    }

    static float[] getVerts(int p){
        int nv = (p + 1) * (p + 1);
        int ni = p * p * 6;

        // vertexs, normals, tex.x, tex.y
        Vec3[] vs = new Vec3[nv];
        Vec3[] ns = new Vec3[nv];
        float[] tx = new float[nv];
        float[] ty = new float[nv];
        int[]  is = new int[ni];

        // calculate triangle vertices
        for (int i = 0; i <= p; i++) {
            for (int j = 0; j <= p; j++) {
                float z = (float) cos(toRadians(180 - i * 180 / p));
                float x = -(float) cos(toRadians(j * 360 / p)) * (float) abs(cos(asin(z)));
                float y = (float) sin(toRadians(j * 360 / p)) * (float) abs(cos(asin(z)));
                vs[i * (p + 1) + j] = new Vec3(x, y, z);
                tx[i * (p + 1) + j] = ((float)j)/p;
                ty[i * (p + 1) + j] = ((float)i)/p;
                ns[i * (p + 1) + j] = new Vec3(x, y, z);

            }
        }
        // calculate triangle indices
        for (int i = 0; i < p; i++) {
            for (int j = 0; j < p; j++) {
                is[6 * (i * p + j) + 0] = i * (p + 1) + j;
                is[6 * (i * p + j) + 1] = i * (p + 1) + j + 1;
                is[6 * (i * p + j) + 2] = (i + 1) * (p + 1) + j;
                is[6 * (i * p + j) + 3] = i * (p + 1) + j + 1;
                is[6 * (i * p + j) + 4] = (i + 1) * (p + 1) + j + 1;
                is[6 * (i * p + j) + 5] = (i + 1) * (p + 1) + j;
            }
        }

        // batched vert norm calc
        float[] verts = new float[is.length*6];

        for (int i = 0; i  < is.length; i++){
            verts[i*6] = vs[is[i]].x;
            verts[(i*6)+1] = vs[is[i]].y;
            verts[(i*6)+2] = vs[is[i]].z;
            verts[i*6+3] = vs[is[i]].x;
            verts[(i*6)+4] = vs[is[i]].y;
            verts[(i*6)+5] = vs[is[i]].z;
        }
        return verts;
    }
        

}