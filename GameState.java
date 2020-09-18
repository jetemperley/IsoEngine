import com.jogamp.opengl.math.Matrix4;

import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;

import java.nio.*;
import java.awt.event.*;

class GameState {

    World world;
    int hoverx, hovery, hoverz;
    static MoveSets moveSets;
    boolean pause = false;

    boolean getDepth = true;
    float lastDepth = 0;

    Menu menu;
    final static float SELECT_EMPTY_SPACE = -0.01f, SELECT_OCCUPIED_SPACE = 0.01f;
    static float DEFAULT_SELECTION = SELECT_EMPTY_SPACE, SELECTION_TYPE = DEFAULT_SELECTION;

    GameState() {
        moveSets = new MoveSets();
        world = new World();
        menu = new Menu();

    }

    void draw(GLGraphics g) {
        if (!pause) {
            // System.out.println("draw");
            g.clear();
            world.draw(g);

            // BUG: the initial depth grab is always from 0, 0, events that use this will
            // occur before the first valid depth grab
            g.readyModelProg();
            if (getDepth) {
                lastDepth = g.getDepthAt(IsoEngine.pressX, IsoEngine.pressY);
            }
            getDepth = true;

            // System.out.println(g.getDepthAt(Game.mouseX, Game.mouseY));

            menu.setHovered(IsoEngine.mouseX, IsoEngine.mouseY);
            if (menu.hovered == null) {
                calcEnvSelect(g);
            }
            g.readyUIProg();
            menu.draw(g);
        }

    }

    void calcEnvSelect(GLGraphics g) {

        // get the viewspcace coords of the users mause location based on depth
        float z = g.getDepthAt(IsoEngine.mouseX, IsoEngine.mouseY);

        float[] loc = { 
                (IsoEngine.mouseX / (float) Camera.GRID_SIZE) - Camera.CLIP_EDGE,
                ((IsoEngine.HEIGHT - IsoEngine.mouseY) / (float) Camera.GRID_SIZE) - Camera.CLIP_EDGE,
                (z * 2 * Camera.CLIP_DEPTH) - Camera.CLIP_DEPTH + SELECTION_TYPE*Camera.CLIP_EDGE, 
                1 };
        
        // System.out.println(loc[0] + " " + loc[1] + " " + loc[2]);

        Matrix4 camInv = new Matrix4();
        camInv.multMatrix(g.cam.view);
        camInv.invert();

        loc = GeoVerts.transformSet(loc, camInv);
        //   + SELECTION_TYPE
        

        // use the view location to calculate which should be selected and where to
        // render
        hoverx = (int) ((loc[0] + 0.5) - g.cam.xoff);
        hovery = (int) ((loc[1] + 0.5) - g.cam.yoff);
        hoverz = (int) (loc[2] + 0.5);

        loc[0] = (float) ((int) Math.floor((loc[0] + 0.5 - g.cam.xoff))) + g.cam.xoff;
        loc[1] = (float) ((int) Math.floor((loc[1] + 0.5 - g.cam.yoff))) + g.cam.yoff;
        loc[2] = (float) ((int) Math.floor((loc[2] + 0.5)));
        
        // System.out.println("x " + loc[0] + " y " + loc[1] + " z " + loc[2]);
        // hoverx = (int)(loc[0] - g.cam.xoff);
        // hovery = (int)(loc[1] - g.cam.zoff);
        // hoverz = (int)(loc[2] - g.cam.zoff);
        
        g.readyModelProg();
        g.setCustDrawLoc(loc[0], loc[1], loc[2]);
        g.setPV(g.cam.getCamera());
        g.setColorLoc(1, 0, 0, 0.5f);
        g.drawCube();
    }

    void doPointRotation(int x, int y) {
        // get the screen location of the mouse
        // float z = Game.graphics.getDepthAt(Game.pressX, Game.pressY);
        // System.out.println(Game.pressX + " " + Game.pressY);
        float[] loc = { (IsoEngine.pressX / (float) Camera.GRID_SIZE) - Camera.CLIP_EDGE,
                ((IsoEngine.HEIGHT - IsoEngine.pressY) / (float) Camera.GRID_SIZE) - Camera.CLIP_EDGE,
                (lastDepth * 2 * Camera.CLIP_DEPTH) - Camera.CLIP_DEPTH, 1 };

        // transform the screen to the view location
        Matrix4 camInv = new Matrix4();
        camInv.loadIdentity();
        camInv.multMatrix(IsoEngine.graphics.cam.view.getMatrix());
        camInv.invert();
        loc = GeoVerts.transformSet(loc, camInv);

        loc[0] = -loc[0];
        loc[1] = -loc[1];
        loc[2] = -loc[2];

        // transform the world mat4, rotate, then transform back
        IsoEngine.graphics.cam.view.translate(-loc[0], -loc[1], -loc[2]);
        IsoEngine.graphics.cam.elevate((float) (y / 200.0 * Math.PI));
        IsoEngine.graphics.cam.rotate((float) (x / 200.0 * Math.PI), 0, 0, 1f);
        IsoEngine.graphics.cam.view.translate(loc[0], loc[1], loc[2]);

    }

    void doAngledDrag(int xdrag, int ydrag) {

        float[] angles = IsoEngine.graphics.cam.getAngles();
        float angle = IsoEngine.graphics.cam.getElevation() - (float) (Math.PI / 2);
        angle = (float) (1 / Math.sin(angle));
        // System.out.println("angle: " + angle);

        float[] loc = { -xdrag / (float) Camera.GRID_SIZE, -ydrag * angle / (float) Camera.GRID_SIZE, 0, 0 };
        float[] loc2 = new float[4];
        Matrix4 cam = new Matrix4();
        cam.loadIdentity();

        cam.rotate(angles[2], 0, 0, 1);
        cam.multVec(loc, loc2);

        IsoEngine.graphics.cam.offset(loc2[0], loc2[1], 0);

    }

    Vec3 getWorldCoordsOfScreenRayIntercept(int x, int y, Vec3 planeNorm) {

        float[] screenCoords = { (x / Camera.GRID_SIZE) - Camera.CLIP_EDGE,
                ((IsoEngine.HEIGHT - y) / Camera.GRID_SIZE) - Camera.CLIP_EDGE, Camera.CLIP_EDGE, 1 };

        float[] screenCoords2 = { (x / Camera.GRID_SIZE) - Camera.CLIP_EDGE,
                ((IsoEngine.HEIGHT - y) / Camera.GRID_SIZE) - Camera.CLIP_EDGE, -Camera.CLIP_EDGE, 1 };

        Plane p = new Plane(planeNorm);
        Vec3 intercept = p.getIntersect(new Line3(new Vec3(screenCoords), new Vec3(screenCoords2)));
        return intercept;

    }

    void mouseClick(int button) {
        // System.out.println("clicked " + hoverx + " " + hovery + " " + hoverz);

        if (menu.hovered != null) {
            menu.onClick(button);
        } else if (menu.selected != null) {
            menu.selected.use(this, button);

        } else {
            defaultWorldClick(button);
        }

    }

    void defaultWorldClick(int button) {
        switch (button) {

        case 1:
            world.select(hoverx, hovery, hoverz);
            break;
        case 3:
            if (world.selected != null) {
                world.selected.rightClick(world, hoverx, hovery, hoverz);
            }
            break;

        }
    }

    void mouseDrag(int button, int xdrag, int ydrag) {

        switch (button) {
        case MouseEvent.BUTTON3 + IsoEngine.MOUSE_KEY_OFFSET:
            doAngledDrag(xdrag, ydrag);
            break;
        case MouseEvent.BUTTON1 + IsoEngine.MOUSE_KEY_OFFSET:
            if (lastDepth != 1.0f) {
                doPointRotation(xdrag, ydrag);
            }

            // Game.graphics.cam.elevate((float)(ydrag / 200.0 * Math.PI));
            // Game.graphics.cam.rotate((float)(xdrag / 200.0 * Math.PI), 0, 0, 1);
            break;

        }

    }

    void keyPress(int key, GLGraphics g) {
        switch (key) {
        case KeyEvent.VK_LEFT:
            // x--
            g.cam.move(1f, 1);
            break;
        case KeyEvent.VK_UP:
            // y++;
            g.cam.move(1.5f, 1);
            break;
        case KeyEvent.VK_RIGHT:
            // x++;
            g.cam.move(0, 1);
            break;
        case KeyEvent.VK_DOWN:
            // y--;
            g.cam.move(0.5f, 1);
            break;
        case KeyEvent.VK_Q:
            float[] angs = IsoEngine.graphics.cam.getAngles();
            System.out.println("angles: x " + angs[0] + ", y " + angs[1] + ", z " + angs[2]);
            // System.out.println("ratio: " + (angs[0]/angs[1]));
            // g.cam.move(0, 0, 0.1f);
            break;
        case KeyEvent.VK_W:
            System.out.println("elevation: " + g.cam.getElevation());
            // g.cam.move(0, 0, -0.1f);
            break;
        case KeyEvent.VK_Z: // z
            g.cam.rotate((float) Math.PI / 8, 1, 0, 0);
            break;
        case KeyEvent.VK_X: // x
            g.cam.rotate((float) Math.PI / 8, 0, 1, 0);
            break;
        case KeyEvent.VK_C:
            g.cam.rotate((float) Math.PI / 8, 0, 0, 1);
            break;
        case KeyEvent.VK_A:
            Mat4Utl.writeMat4(g.cam.camera);
            break;
        case KeyEvent.VK_D:
            g.cam.initView();
            break;
        case KeyEvent.VK_F:
            IsoEngine.printlog = true;
            break;
        case MouseEvent.BUTTON1 + IsoEngine.MOUSE_KEY_OFFSET:
            getDepth = false;
            break;
        case KeyEvent.VK_V:
            IsoEngine.graphics.cam.elevate((float) Math.PI / 8);
            break;
        case KeyEvent.VK_B:
            IsoEngine.graphics.cam.elevate((float) Math.PI / 8);
            break;
        case KeyEvent.VK_SPACE:
            pause = !pause;
            break;

        }
    }

    void mouseWheel(int n) {
        IsoEngine.graphics.cam.zoom(n);
    }

}