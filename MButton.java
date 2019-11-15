
abstract class MButton {

    // the position and size in CELLS defined by menues
    int xpos, ypos, width, height;

    MButton(){
        this(0, 0);
    }

    MButton(int x, int y){
        this(x, y, 1, 1);
    }

    MButton(int x, int y, int w, int h){
        xpos = x;
        ypos = y;
        width = w;
        height = h;
        System.out.println(xpos + " " + ypos + " " + width + " " + height);
    }

    abstract void onClick(int press);

    void draw(GLGraphics g, Menu m){
        draw(g, m, Assets.SMILE);
    }


    void draw(GLGraphics g, Menu m, int asset){
        
        g.drawMenuFace(m.cellSize*xpos + (m.cellSize/2), m.cellSize*ypos - (m.cellSize/2), m.cellSize*width, m.cellSize*height, asset);
        
    }

    void highlight(GLGraphics gl, Menu m, float r, float g, float b, float a){
        gl.setColorLoc(r, g, b, a);
        draw(gl, m, Assets.SELECT);
        gl.setColorLoc(0, 0, 0, 0);
    }

    boolean isAvailable(int x, int y) {

        return true;

    }

    void addButton(MButton[][] menu) {
        for (int y = ypos; y < ypos + height; y++) {
            for (int x = xpos; x < xpos + width; x++) {
                menu[y][x] = this;
            }
        }
    }

    void use(GameState s){

    }

    

    

}