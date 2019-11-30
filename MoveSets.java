import java.util.ArrayList;

class MoveSets {

    static ArrayList<Action> basicMoves;

    TakePath getPathAct(World w, int x, int y, int z){
        return new TakePath(w, x, y, z);
    }
    

    MoveSets() {

        basicMoves = new ArrayList<Action>();
        for (int i = -1; i < 2; i += 2) {
            // System.out.println("moves " + i);
            basicMoves.add(new Walk(i, 0));
            basicMoves.add(new Walk(0, i));
            basicMoves.add(new ClimbUp(i, 0));
            basicMoves.add(new ClimbUp(0, i));
            basicMoves.add(new ClimbDown(0, i));
            basicMoves.add(new ClimbDown(i, 0));
        }
    }

    class ClimbUp extends Action {

        ClimbUp(int xi, int yi) {
            x = xi;
            y = yi;
            z = 1;

        }

        public boolean exe(World w, Player t) {
            if (check(w, t.x, t.y, t.z)) {
                t.dx += -x*100;
                t.dy += -y*100;
                t.dz += -z*100;
                w.move(t, x, y, z);
                return true;
            }
            return false;

        }

        public boolean check(World w, int xp, int yp, int zp) {
            if (w.get(xp + x, yp + y, zp) != null) {
                if (w.get(xp + x, yp + y, zp + z) == null) {
                    if (w.get(xp, yp, zp + z) == null) {
                        return true;
                    }
                }
            }
            return false;
        }

    }

    class ClimbDown extends Action {

        ClimbDown(int xi, int yi) {
            x = xi;
            y = yi;
            z = -1;

        }

        public boolean exe(World w, Player t) {
            if (check(w, t.x, t.y, t.z)) {
                t.dx += -x*100;
                t.dy += -y*100;
                t.dz += -z*100;
                w.move(t, x, y, z);
                return true;
            }
            return false;
        }

        public boolean check(World w, int xp, int yp, int zp) {
            if (w.get(xp + x, yp + y, zp) == null) {
                if (w.get(xp + x, yp + y, zp + z) == null) {
                    if (w.get(xp + x, yp + y, zp + z -1) != null) {
                        return true;
                    }
                }
            }
            return false;
        }

    }

    class Walk extends Action {

        Walk(int xi, int yi) {
            x = xi;
            y = yi;
            z = 0;
        }

        public boolean exe(World w, Player t) {
            if (check(w, t.x, t.y, t.z)) {
                t.dx += -x*100;
                t.dy += -y*100;
                w.move(t, x, y, z);
                return true;
            }
            return false;
        }

        public boolean check(World w, int xp, int yp, int zp) {
            if (w.get(xp + x, yp + y, zp) == null) {
                if (w.get(xp + x, yp + y, zp -1) != null) {
                    return true;
                }
            }
            return false;
        }

    }

    class TakePath extends Action {

        World world;
        ArrayList<Action> path;
        int xtarget, ytarget, ztarget;

        TakePath(World w, int x, int y, int z) {
            xtarget = x;
            ytarget = y;
            ztarget = z;
            world = w;
        }

        public boolean exe(World w, Player t) {
            if (path == null) {
                // if this is the first execute, calc the path
                System.out.println("path was null");
                path = Path.findPath(world, t, xtarget, ytarget, ztarget);
                return true;
            } else if (path.size() > 0){
                // do the next move
                System.out.println("executed the next step");
                if (path.get(path.size()-1).exe(w, t)){
                    path.remove(path.size()-1);
                    return true;
                }
                return false;
            } else {
                System.out.println("moved to the next action");
                t.nextAction();
                return false;
            }

        }

        public boolean check(World w, int xp, int yp, int zp) {
            return true;
        }

    }



}