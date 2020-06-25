
class Tree extends Thing{

    void draw(GLGraphics g){
        super.draw(g);
        g.setDrawLoc(getX(), getY(), getZ());
        g.setColorLoc(0, 1, 0, 1);
        g.setModelForm(Mat4Utl.getIdentity());
        g.drawMesh(VBOManager.TREE);
    }

    int getType(){
        return Thing.TREE;
    }

}