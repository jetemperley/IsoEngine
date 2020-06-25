import java.util.ArrayList;

import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.math.Matrix4;

// TYPE = 1
class Player extends Thing {

    ArrayList<Action> moveSet;
    ArrayList<Action> actions;
    float time = 0;
    private int moveSpeed = 5;
    private float animSpeed = moveSpeed / 100.0f, rotation = 0;;
    Matrix4 pvm = new Matrix4();

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

        // System.out.println(time);
        Matrix4[] bones = Assets.meshs.get(2).anim.getPose(time);
        
        g.setBones(bones);

        Matrix4 PVM = new Matrix4();
        PVM.multMatrix(g.cam.getCamera());

        Matrix4 loc = new Matrix4();
        loc.translate(getX() + g.cam.xoff, getY() + g.cam.yoff, getZ() + g.cam.zoff);
        // System.out.println(getX() + " " + getY() + " " + getZ());
        loc.rotate(rotation, 0, 0, 1);

        PVM.multMatrix(loc);
        g.setPV(PVM);

        loc.loadIdentity();
        loc.rotate(rotation, 0, 0, 1);
        g.setModelForm(loc);

        g.setCustDrawLoc(0,0,0);
        g.drawMesh(VBOManager.MAN, Assets.MAN_TEX);

    }

    @Override
    void update(World w) {
        // System.out.println("actions null: " + (actions == null));

        if (dx != 0 || dy != 0 || dz != 0) {
            // System.out.println("moving offset");
            
            rotation = ((float) Math.PI / 2) * (Math.signum(dx) + (Math.signum(dy) - 1) * Math.signum(dy));
            // rotation.rotate(rot, 0, 0, 1);
            // System.out.println(rotation);
            dx -= moveSpeed * Math.signum(dx);
            dy -= moveSpeed * Math.signum(dy);
            dz -= moveSpeed * Math.signum(dz);

            time += animSpeed;
            // TODO: overlap the leftover time with %?
            if (time >= 1.0f) {
                time = 0.0f;
            }

        } else if (actions.size() > 0) {
            // System.out.println("player action");
            if (!actions.get(0).exe(w, this)) {
                System.out.println("failed action");
                nextAction();
            }

            time = 0;
        }
        // System.out.println("updated player");
    }

    void doAction(World w, Action a) {
        a.exe(w, this);
    }

    void nextAction() {
        if (actions.size() > 0) {
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

        for (Action a : moveSet) {
            if (a.check(w, xi, yi, zi)) {
                steps.add(a);
            }
        }

        return steps;
    }

    // void drawPath() {
    //     if (path != null) {
    //         // System.out.println("path != null");
    //         for (int[] v : path) {
    //             g.setDrawLoc(v[0], v[1], v[2]);
    //             g.setColorLoc(0, 0, 1, 0.5f);
    //             g.drawCube();
    //         }
    //     }
    // }
}