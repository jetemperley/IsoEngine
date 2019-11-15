import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.awt.*;
import java.util.ArrayList;

abstract class Thing {

    VBO v;
    // absoltute world location
    private int x, y, z;

    // offset to absolute location (animation purposes)
    float dx = 0, dy = 0, dz = 0;

    static int CELL = 0, PLAYER = 1;

    Thing(){
        this(0, 0, 0);
    }

    Thing(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        
    }

    ArrayList<int[]> getPossibleSteps(World w) {
        return getPossibleSteps(w, x, y, z);
    }

    ArrayList<int[]> getPossibleSteps(World w, int x, int y, int z) {
        return null;
    }

    void rightClick(World w, int x, int y, int z) {

    }

    int getType() {
        return 0;
    }

    void draw(GLGraphics g) {

    }

    void update(){

    }

    float getX(){
        return x+dx;
    }

    int getLocX(){
        return x;
    }

    int getLocY(){
        return y;
    }

    int getLocZ(){
        return z;
    }

    float getY(){
        return y+dy;
    }

    float getZ(){
        return z+dz;
    }

    void setLoc(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

}