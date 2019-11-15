import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.texture.*;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.awt.Color;
import javax.imageio.ImageIO;

import java.util.*;

public class Assets {

    static ArrayList<Texture> texs;
    static ArrayList<float[]> meshs;
    static int[] joglTexLocs;
    // texture asset IDs
    static int SMILE = 0, PICK = 1, SELECT = 2, WALK = 3, PLUS = 4;

    Assets() {
        texs = new ArrayList<Texture>();
        meshs = new ArrayList<float[]>();
        texs.add(loadTexture("smile.png"));
        texs.add(loadTexture("pick.png"));
        texs.add(loadTexture("select.png"));
        texs.add(loadTexture("walk.png"));
        texs.add(loadTexture("plus.png"));

        // System.out.println("texs length " + texs.size());
        joglTexLocs = new int[texs.size()];

        for (int i = 0; i < joglTexLocs.length; i++) {
            joglTexLocs[i] = texs.get(i).getTextureObject();
        }

        float[] f = getData(XmlParser.loadXmlFile("tree.dae"));
        meshs.add(f);
        f = getData(XmlParser.loadXmlFile("rock.dae"));
        meshs.add(f);
        f = getData(XmlParser.loadXmlFile("man.dae"));
        meshs.add(f);

    }

    static Texture loadTexture(String textureFileName) {
        Texture tex = null;
        try {
            tex = TextureIO.newTexture(new File(textureFileName), false);
        } catch (Exception e) {
            System.out.println("error on texture " + textureFileName);
            try {
                tex = TextureIO.newTexture(new File("sad.png"), false);
            } catch (Exception ei) {
                System.out.println("couldnt even load backup texture :(");
            }
            // e.printStackTrace();
        }
        return tex;
    }

    static float[] extractVerticiesFromXML(XmlNode mainNode) {
        String objectID = mainNode.getChild("library_geometries").getChild("geometry").getChild("mesh")
                .getChild("vertices").getChild("input").getAttribute("source").substring(1);

        String[] tempPos = mainNode.getChild("library_geometries").getChild("geometry").getChild("mesh")
                .getChildWithAttribute("source", "id", objectID).getChild("float_array").getData().split(" ");

        float[] pos = new float[tempPos.length];
        for (int i = 0; i < tempPos.length; i++) {
            pos[i] = Float.parseFloat(tempPos[i]);
            // System.out.print(pos[i] + " ");
        }
        // System.out.println();
        return pos;

    }

    static float[] extractTrianglesFromXML(XmlNode mainNode, String semantic) {
        String objectID = mainNode.getChild("library_geometries").getChild("geometry").getChild("mesh")
                .getChild("triangles").getChildWithAttribute("input", "semantic", semantic).getAttribute("source")
                .substring(1);

        String[] stringData = mainNode.getChild("library_geometries").getChild("geometry").getChild("mesh")
                .getChildWithAttribute("source", "id", objectID).getChild("float_array").getData().split(" ");

        float[] data = new float[stringData.length];
        for (int i = 0; i < stringData.length; i++) {
            data[i] = Float.parseFloat(stringData[i]);
            // System.out.print(pos[i] + " ");
        }
        // System.out.println();
        return data;

    }

    static int[] extractIndecies(XmlNode mainNode) {
        String[] idxString = mainNode.getChild("library_geometries").getChild("geometry").getChild("mesh")
                .getChild("triangles").getChild("p").getData().split(" ");
        int[] idx = new int[idxString.length];
        for (int i = 0; i < idx.length; i++) {
            idx[i] = Integer.parseInt(idxString[i]);
        }
        return idx;
    }

    static float[] getData(XmlNode mainNode) {
        float[] verts = extractVerticiesFromXML(mainNode);
        float[] normals = extractTrianglesFromXML(mainNode, "NORMAL");
        int[] idx = extractIndecies(mainNode);

        int tris = Integer.parseInt(mainNode.getChild("library_geometries").getChild("geometry").getChild("mesh")
                .getChild("triangles").getAttribute("count"));

        float[] expVerts = new float[tris * 9];
        float[] expNorms = new float[tris * 9];
        int j = 0;
        for (int i = 0; i < idx.length; i += 3) {
            for (int k = 0; k < 3; k++) {
                expVerts[j] = verts[(idx[i]*3) + k];
                expNorms[j] = normals[(idx[i + 1]*3) + k];
                j++;
            }
        }

        float[] expanded = GeoVerts.interleave(expVerts, expNorms);

        return expanded;
    }

}