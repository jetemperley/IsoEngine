import java.util.ArrayList;

import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.awt.*;

// TYPE = 1
class Player extends Thing {

    ArrayList<Action> moveSet;
    ArrayList<Action> actions;

    Player() {
        this(0, 0, 0);

    }

    Player(int x, int y, int z) {
        super(x, y, z);
        actions = new ArrayList<Action>();
        moveSet = MoveSets.basicMoves;

    }

    @Override
    int getType() {
        return Thing.PLAYER;
    }

    @Override
    void draw(GLGraphics g) {
        g.setDrawLoc(getX(), getY(), getZ());
        g.drawMesh(VBOManager.MAN, Assets.MAN_TEX);

        // if (path != null) {
        //     // System.out.println("path != null");
        //     for (int[] v : path) {
        //         g.setDrawLoc(v[0], v[1], v[2]);
        //         g.setColorLoc(0, 0, 1, 0.5f);
        //         g.drawCube();
        //     }
        // }

    }

    @Override
    void update(World w) {
        // System.out.println("actions null: " + (actions == null));
        if (dx != 0 || dy != 0 || dz != 0){
            // System.out.println("moving offset");
            dx -= 10*Math.signum(dx);
            dy -= 10*Math.signum(dy);
            dz -= 10*Math.signum(dz);

        } else if (actions.size() > 0) {
            // System.out.println("player action");
            if (!actions.get(0).exe(w, this)){
                System.out.println("failed action");
                nextAction();
            }
        }
        // System.out.println("updated player");
    }

    void doAction(World w, Action a){
        a.exe(w, this);
    }

    void nextAction(){
        if (actions.size() > 0){
            actions.remove(0);
        }
    }

    @Override
    void rightClick(World w, int x, int y, int z) {
        // if (w.get(x, y, z) != null) {
        System.out.println("x " + x + " y " + y + " z " + z);
        if (w.get(x, y, z - 1) != null && w.get(x, y, z) == null) {
            actions.add(GameState.moveSets.getPathAct(w, x, y, z));
        } else {
            System.out.println("failed click check");
        }
        // }

    }

    @Override
    ArrayList<Action> getPossibleSteps(World w, int xi, int yi, int zi) {
        // System.out.println("got steps from " + x + " " + y + " " + z);

        ArrayList<Action> steps = new ArrayList<Action>();

        for (Action a : moveSet){
            if (a.check(w, xi, yi, zi)){
                steps.add(a);
            }
        }

        return steps;
    }
}