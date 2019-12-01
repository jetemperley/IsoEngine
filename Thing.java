import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.awt.*;
import java.util.ArrayList;

abstract class Thing {

    static MoveSets moveSets;
    // absoltute world location
    int x, y, z;

    // offset to absolute location (animation purposes)
    float dx = 0, dy = 0, dz = 0;

    static int UNSPECIFIED = -1, CELL = 0, PLAYER = 1;

    Thing(){
        this(0, 0, 0);
    }

    Thing(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        
    }

    ArrayList<Action> getPossibleSteps(World w) {
        return getPossibleSteps(w, x, y, z);
    }

    ArrayList<Action> getPossibleSteps(World w, int x, int y, int z) {
        return null;
    }

    void rightClick(World w, int x, int y, int z) {

    }

    int getType() {
        return UNSPECIFIED;
    }

    void draw(GLGraphics g) {

    }

    void update(World w){
        
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

    float getX(){
        return x+(dx/100);
    }

    float getY(){
        return y+(dy/100);
    }

    float getZ(){
        return z+(dz/100);
    }

    void setLoc(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    void moveBy(int x, int y, int z){
        this.x += x;
        this.y += y;
        this.z += z;
    }

}