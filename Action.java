import java.util.ArrayList;

interface Action {

    // IDs for getting actions
    static int WALK = 1, HOP = 2, TAKE_PATH = 3;

    boolean isDone();
    public void init();
    public void exe(Player t);

    // static Action getBasicMove(int xi, int yi, int zi) {
    //     Action act;
    //     if (zi == 0) {
    //         act = Action.getWalk(xi, yi);
    //     } else {
    //         act = Action.getHop(xi, yi, zi);
    //     }

    //     return act;
    // }
    // // TODO complete the liks between IDs and aquiring action objects
    // static Action getByID(int ID, int[] target) {
    //     switch(ID){
    //         case Action.WALK:
    //             return new Walk(target[0], target[1]);
    //         break;

    //         case Action.HOP:
    //             return new Hop(target[0], target[1], target[2]);
    //         break;

    //         case Action.TAKE_PATH:
    //             return new TakePath(target);
    //         break;
    //     }
    //     return null;
    // }

    // static Action getWalk() {

    //     return new Walk();
    // }

    // static Action getHop() {
    //     return null;
    // }

    // class Hop implements Action{

    //     int dx, dy, dz, az;
    //     boolean done = false;

    //     Hop(int xi, int yi, int zi){
    //         if (zi < 0){
    //             az = 0;
    //         } else if (zi > 0){
    //             az = 0.5;
    //         }
    //         dx = -xi * 10;
    //         dy = -yi * 10;
    //         dz = -zi * 10;

    //     }

    //     public void init(){

    //     }
    //     public void execute(Player t){

    //     }

    //     public boolean isDone(){
    //         return done;
    //     }
    // }

    class Walk implements Action {

        int dx, dy;
        boolean done = false;

        Walk(int xi, int yi) {
            dx = -xi * 10;
            dy = -yi * 10;
        }

        public void init(){

        }

        public void exe(Player t) {

            if (dx != 0) {
                t.dx = dx / 10f;
                dx = dx - (int) Math.signum(dx);
            }

            if (dy != 0) {
                t.dy = dy / 10f;
                dy = dy - (int) Math.signum(dy);
            }

            if (dx == 0 && dy == 0) {
                done = true;
            }

        }

        public boolean isDone() {
            return done;
        }

    }

    class TakePath implements Action {

        ArrayList<int[]> path;
        Action currentStep = null;
        boolean done = false;
        int xtarget, ytarget, ztarget;

        TakePath(Player t, int x, int y, int z) {
            
            

        }

        public void init(){

        }

        public void exe(Player t) {

            if (currentStep.isDone()) {
                if (path.size() > 0) {

                    int[] s = path.get(path.size() - 1);
                    path.remove(path.size() - 1);
                    // currentStep = Action.getBasicMove(s[0] - t.getLocX(), s[1] - t.getLocY(), s[2] - t.getLocZ());

                } else {
                    done = true;
                }
            } else {
                currentStep.exe(t);
            }

        }

        public boolean isDone() {
            return done;
        }
    }

}
