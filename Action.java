import java.util.ArrayList;

abstract class Action {

    // xyz is the coords of the object or direction of movement
    int x, y, z;
    // exe alters the states of world objects 
    boolean exe(World w, Player t){
        return true;
    }
    // check returns weather the action is avalable/valid
    boolean check(World w, int xp, int yp, int zp){
        return true;
    }

    static Action getMove(int x, int y, int z){


        return null;
    }

}
