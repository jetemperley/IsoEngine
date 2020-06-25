

class Rock extends Thing{

    void draw(GLGraphics g){
        super.draw(g);
        g.setDrawLoc(getX(), getY(), getZ());
        g.setColorLoc(0.5f, 0.5f, 0.5f, 1);
        g.setModelForm(Mat4Utl.getIdentity());
        g.drawMesh(VBOManager.ROCK);
    }

    int getType(){
        return Thing.ROCK;
    }

}