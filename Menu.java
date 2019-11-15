import java.nio.FloatBuffer;
import java.util.*;

class Menu{

    // cellsize is the 0-1 portion of the screen space eg 0.5 = 50% 
    float xcells = 10, ycells, cellSize, cellSizePixels;
    private MButton[][] menu;
    private ArrayList<MButton> butts;
    FloatBuffer buffer;
    MButton hovered, selected;
    

    Menu(){
        buffer = FloatBuffer.allocate(4);
        cellSize = 1/xcells;
        ycells = IsoEngine.HEIGHT/cellSize;
        cellSizePixels = IsoEngine.WIDTH/xcells;
        menu = new MButton[(int)ycells][(int)xcells];
        butts = new ArrayList<MButton>();
        add(new DigToggle(), 0, 0);
        add(new AddBlock(), 1, 0);
        
        System.out.println("cellSize " + cellSize);
        System.out.println("cellSizePix " + cellSizePixels);
        
    }

    void draw(GLGraphics g){
        // System.out.println("drawing buttons");
        
        for (MButton but : butts) {
            but.draw(g, this);
        }
        // System.out.println((int)(Game.mouseX/cellSizePixels) + " " + (int)(Game.mouseY/cellSizePixels) + " " + g.getDepthAt(Game.mouseX, Game.mouseY, buffer));
        // if (g.getDepthAt(Game.mouseX, Game.mouseY, buffer) != 1.0){
        //     hovering = true;
        //     // menu[(int)(Game.mouseY/cellSizePixels)][(int)(Game.mouseX/cellSizePixels)].highlight(g, this);
        // } else {
        //     hovering = false;
        // }
            
        if (hovered != null){
            hovered.highlight(g, this, 1, 0, 0, 0.5f);
        }
        if (selected != null){
            selected.highlight(g, this, 0, 1, 0, 0.5f);
        }


    }

    boolean isHovering(int x, int y){
        if (menu[(int)(y/cellSizePixels)][(int)(x/cellSizePixels)] == null){
            return false;
        }
        return true;
    }

    // void highlight(int x, int y, GLGraphics g){
    //     menu[x][y].highlight(g, this);
    // }

    void setHovered(int x, int y){
        hovered = menu[(int)(y/cellSizePixels)][(int)(x/cellSizePixels)];
    }

    void add(MButton butt, int x, int y){
        butt.xpos = x;
        butt.ypos = y;
        butts.add(butt);
        butt.addButton(menu);
    }

    void add(MButton butt){
        butts.add(butt);
        butt.addButton(menu);
    }


    void onClick(int button){
        selected = hovered;
        hovered.onClick(button);
    }

    public class DigToggle extends MButton {


        void onClick(int press) {
            System.out.println("set to select occupied");
            GameState.SELECTION_TYPE = GameState.SELECT_OCCUPIED_SPACE;
        }

        void draw(GLGraphics g, Menu m) {
            super.draw(g, m, Assets.PICK);
        }

        void use(GameState s){
            s.world.setThing(s.hoverx, s.hovery, s.hoverz, null);
        }

        

    }

    public class WalkToggle extends MButton {

        void onClick(int press) {
            System.out.println("set to select empty ");
            GameState.SELECTION_TYPE = GameState.SELECT_EMPTY_SPACE;
        }

        void draw(GLGraphics g, Menu m) {
            super.draw(g, m, Assets.WALK);
        }

        


    }

    class AddBlock extends MButton {

        void onClick(int press) {
            // System.out.println("set to select empty ");
            GameState.SELECTION_TYPE = GameState.SELECT_EMPTY_SPACE;
        }

        void draw(GLGraphics g, Menu m) {
            super.draw(g, m, Assets.PLUS);
        }

        void use(GameState s){
            s.world.setThing(s.hoverx, s.hovery, s.hoverz, new Cell(s.hoverx, s.hovery, s.hoverz));
        }

    }
    
    

}