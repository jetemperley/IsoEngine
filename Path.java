import java.util.ArrayList;

class Path {

    // Vec3 a, b;

    // Path(int ax, int ay, int az, int bx, int by, int bz) {
    // a = new Vec3(ax, ay, az);
    // b = new Vec3(bx, by, bz);
    // }

    // Path(Vec3 ai, Vec3 bi) {
    // a = ai;
    // b = bi;
    // }

    static ArrayList<int[]> findPath(World w, int ax, int ay, int az, int bx, int by, int bz) {

        int[] goal = { bx, by, bz };
        Node[][][] net = new Node[World.Chunk.SIZE][World.getAbsSize()][World.getAbsSize()];
        ArrayList<Node> open = new ArrayList<Node>();
        boolean done = false, possible = true;

        net[az][ay][ax] = new Node(null, 0, goal, ax, ay, az);
        Node shortest = net[az][ay][ax];
        int count = 0;

        while (!done) {

            // get the list of moves from the curernt space based on the piece
            ArrayList<int[]> steps = w.get(ax, ay, az).getPossibleSteps(w, shortest.pos[0], shortest.pos[1],
                    shortest.pos[2]
            );

            // iterate through the steps to see if the step has already been taken and add
            // it to the list of open steps
            for (int[] s : steps) {
                try {
                    if (net[s[2]][s[1]][s[0]] == null) {
                        // System.out.println("step " + s[0] + " " + s[1] + " " + s[2]);
                        net[s[2]][s[1]][s[0]] = new Node(shortest, shortest.steps + s.length/3, goal, s);
                        open.add(net[s[2]][s[1]][s[0]]);
                        
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                }
            }

            open.remove(shortest);

            if (open.size() > 0) {
                shortest = open.get(0);
                for (Node n : open) {
                    if (n.cost < shortest.cost) {
                        shortest = n;
                    }
                }
            } else {
                // this indicates there is NO path
                done = true;
                possible = false;
            }
            // System.out.println(shortest.pos[0] + " " + shortest.pos[1] + " " + shortest.pos[2]  + " == " + goal[0] + " " +  goal[1] + " " +  goal[2]);
            if (shortest.pos[0] == goal[0] && shortest.pos[1] == goal[1] && shortest.pos[2] == goal[2]) {
                done = true;
            }
            // System.out.println("shortest dist " + shortest.cost);

        }

        if (possible) {
            ArrayList<int[]> finalPath = new ArrayList<int[]>();
            Node next = shortest;
            while (next.parent != null) {
                finalPath.add(next.pos);
                next = next.parent;
            }

            // for (Node n : open) {
            //     finalPath.add(n.pos);
            // }

            return finalPath;
        } else {
            System.out.println("path isx not possible");
            return null;
        }

    }

}