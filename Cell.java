import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.awt.*;

// type = 0

public class Cell extends Thing{

    boolean[] faces;
    

    Cell(int x, int y, int z){
        super(x, y, z);
        faces = new boolean[6];
        for (int i = 0; i < faces.length; i++){
            faces[i] = false;
        }
    }

    @Override
    int getType(){
        return Thing.CELL;
    }

    void draw(GLGraphics g){
        g.setDrawLoc(getLocX(), getLocY(), getLocZ());
        g.drawCube(faces);
    }
}