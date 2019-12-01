import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.awt.*;

// type = 0

public class Cell extends Thing {

    // faces x-1 = left, x+1 = right, y+1 = back, y-1 = front
    static int TOP_FACE = 0, BOT_FACE = 1, LEFT_FACE = 2, RIGHT_FACE = 3, FRONT_FACE = 4, BACK_FACE = 5;
    boolean[] faces;

    Cell(int x, int y, int z) {
        super(x, y, z);
        faces = new boolean[6];
        for (int i = 0; i < faces.length; i++) {
            faces[i] = false;
        }
    }

    @Override
    int getType() {
        return Thing.CELL;
    }

    @Override
    void draw(GLGraphics g) {
        g.setDrawLoc(getLocX(), getLocY(), getLocZ());
        g.drawCube(faces);
    }

    void updateFaces(World w) {
        updateFace(w, Cell.TOP_FACE, 0, 0, 1);
        updateFace(w, Cell.BOT_FACE, 0, 0, -1);
        updateFace(w, Cell.LEFT_FACE, -1, 0, 0);
        updateFace(w, Cell.RIGHT_FACE, 1, 0, 0);
        updateFace(w, Cell.FRONT_FACE, 0, -1, 0);
        updateFace(w, Cell.BACK_FACE, 0, 1, 0);
    }

    private void updateFace(World w, int face, int xi, int yi, int zi){
        
        try{
            if (w.get(x + xi, y + yi, z + zi).getType() != Thing.CELL){
                faces[face] = true;
            } else {
                faces[face] = false;
            }
        } catch (NullPointerException e){
            faces[face] = true;
        }
    }
    boolean isVisible(){
        for (boolean b : faces){
            if (b){
                return b;
            }
        }
        return false;
    }
}