import java.util.ArrayList;

import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.awt.*;

// TYPE = 1
class Player extends Thing {

    ArrayList<int[]> path;
    // list of action IDs to be completed in sequence
    ArrayList<int[]> targets;
    

    float dx, dy;

    Player() {
        super(0, 0, 0);
    }

    Player(int x, int y, int z) {
        super(x, y, z);
        targets = new ArrayList<int[]>();

    }

    @Override
    int getType() {
        return Thing.PLAYER;
    }

    @Override
    void draw(GLGraphics g) {
        g.setDrawLoc(getX(), getY(), getZ());
        g.drawMesh(VBOManager.MAN);

        if (path != null) {
            // System.out.println("path != null");
            for (int[] v : path) {
                g.setDrawLoc(v[0], v[1], v[2]);
                g.setColorLoc(0, 0, 1, 0.5f);
                g.drawCube();
            }
        }

    }

    @Override
    void update() {
        /*
        if (currentAction == null) {
            if (actions.size() > 0) {
                // currentAction = Action.getByID(actions.get(0));
                // actions.remove(0);
                // currentAction.execute(this);
            }
        } else if (!currentAction.isDone()) { // maybe do this first?
            currentAction.execute(this);
            if (currentAction.isDone()) {
                currentAction = null;
            }
        }
        */

    }

    @Override
    void rightClick(World w, int x, int y, int z) {
        // if (w.get(x, y, z) != null) {
        if (w.get(x, y, z - 1) != null && w.get(x, y, z - 1).getType() == Thing.CELL && w.get(x, y, z) == null) {

            // System.out.println("right click in player");
            path = Path.findPath(w, getLocX(), getLocY(), getLocZ(), x, y, z);
        }
        // }

    }

    @Override
    ArrayList<int[]> getPossibleSteps(World w, int x, int y, int z) {
        // System.out.println("got steps from " + x + " " + y + " " + z);

        ArrayList<int[]> steps = new ArrayList<int[]>();

        // Horizontal & down steps
        // x+1 step
        if (w.get(x + 1, y, z) == null) {
            if (w.get(x + 1, y, z - 1) != null) {
                int[] step = { x + 1, y, z };
                steps.add(step);
            } else if (w.get(x + 1, y, z - 2) != null) {
                int[] step = { 
                    x + 1, y, z - 1,
                    x + 1, y, z 
                };
                steps.add(step);
            }
        }
        // x-1 step
        if (w.get(x - 1, y, z) == null) {
            if (w.get(x - 1, y, z - 1) != null) {
                int[] step = { x - 1, y, z };
                steps.add(step);
            } else if (w.get(x - 1, y, z - 2) != null) {
                int[] step = { 
                    x - 1, y, z - 1,
                    x - 1, y, z
                };
                steps.add(step);
            }
        }
        // y+1 step
        if (w.get(x, y + 1, z) == null) {
            if (w.get(x, y + 1, z - 1) != null) {
                int[] step = { x, y + 1, z };
                steps.add(step);
            } else if (w.get(x, y + 1, z - 2) != null) {
                int[] step = { 
                    x, y + 1, z - 1,
                    x, y + 1, z 
                };
                steps.add(step);
            }
        }
        // y-1 step
        if (w.get(x, y - 1, z) == null) {
            if (w.get(x, y - 1, z - 1) != null) {
                int[] step = { x, y - 1, z };
                steps.add(step);
            } else if (w.get(x, y - 1, z - 2) != null) {
                int[] step = { 
                    x, y - 1, z - 1,
                    x, y - 1, z 
                };
                steps.add(step);
            }
        }

        // climbing up steps
        if (w.get(x, y, z + 1) == null) {
            if (w.get(x + 1, y, z) != null && w.get(x + 1, y, z + 1) == null) {
                int[] step = { x + 1, y, z + 1, x, y, z + 1 };
                steps.add(step);
            }
            // x-1 step
            if (w.get(x - 1, y, z) != null && w.get(x - 1, y, z + 1) == null) {
                int[] step = { x - 1, y, z + 1, x, y, z + 1 };
                steps.add(step);
            }
            // y+1 step
            if (w.get(x, y + 1, z) != null && w.get(x, y + 1, z + 1) == null) {
                int[] step = { x, y + 1, z + 1, x, y, z + 1 };
                steps.add(step);
            }
            // y-1 step
            if (w.get(x, y - 1, z) != null && w.get(x, y - 1, z + 1) == null) {
                int[] step = { x, y - 1, z + 1, x, y, z + 1 };
                steps.add(step);
            }
        }

        return steps;
    }

    void takeSteps(ArrayList<int[]> steps){
        
    }
}