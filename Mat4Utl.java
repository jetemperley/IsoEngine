import com.jogamp.opengl.math.Matrix4;

public class Mat4Utl {

    private static float[] matrixar = null;
    private static float[] angles;

    public static void writeMat4(Matrix4 mat4) {
        if (mat4 != null) {

            matrixar = mat4.getMatrix();
            for (int i = 0; i < matrixar.length; i++) {
                if (i%4 == 0){
                    System.out.println();
                }
                System.out.print(matrixar[i] + " ");
                
            }
            System.out.println("");
        }
    }

    public static float[] getRotations(Matrix4 mat){
        // writeMat4(mat);
        matrixar = mat.getMatrix();
        // System.out.println("l = " + curM4.length);

        angles = new float[3];

        angles[0] = (float)Math.atan2(matrixar[9], matrixar[10]);
        float c2 = (float)Math.sqrt((matrixar[0]*matrixar[0]) + (matrixar[4]*matrixar[4]));
        angles[1] = (float)Math.atan2(-matrixar[8], c2);
        float s1 = (float)Math.sin(angles[0]);
        float c1 = (float)Math.cos(angles[0]);
        angles[2] = (float)Math.atan2((s1*matrixar[2]) - (c1*matrixar[1]), (c1*matrixar[5]) - (s1 * matrixar[6]));

        return angles;

    }

    public static Matrix4 getNegitiveMatrix(Matrix4 mat){
        float[] angles  = Mat4Utl.getRotations(mat);
        angles[0] = -angles[0];
        angles[1] = -angles[1];
        angles[2] = -angles[2];
        // System.out.println("angles "+angles[0]+" "+angles[1]+" "+angles[2]);

        Matrix4 negitiveRotation = new Matrix4();
        negitiveRotation.loadIdentity();
        negitiveRotation.rotate(angles[0], 1, 0, 0);
        negitiveRotation.rotate(angles[1], 0, 1, 0);
        negitiveRotation.rotate(angles[2], 0, 0, 1);
        return negitiveRotation;
    }

    


}