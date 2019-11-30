import java.util.ArrayList;

class Path {

    static ArrayList<Action> findPath(World w, Player p, int bx, int by, int bz) {
        System.out.println("starting find path");

        int[] goal = { bx, by, bz };
        Node[][][] net = new Node[World.Chunk.SIZE][World.getAbsSize()][World.getAbsSize()];
        ArrayList<Node> open = new ArrayList<Node>();
        boolean done = false, possible = true;

        net[p.z][p.y][p.x] = new Node(null, 0, goal, p.x, p.y, p.z, null);
        Node shortest = net[p.z][p.y][p.x];
        int count = 0;

        int x, y, z;
        while (!done) {

            // get the list of moves from the curernt space based on the piece
            ArrayList<Action> steps = p.getPossibleSteps(w, shortest.pos[0], shortest.pos[1], shortest.pos[2]);
            // System.out.println("steps size " + steps.size());

            // iterate through the steps to see if the step has already been taken and add
            // it to the list of open steps
            for (Action s : steps) {
                x = shortest.pos[0] + s.x;
                y = shortest.pos[1] + s.y;
                z = shortest.pos[2] + s.z;
                try {
                    // System.out.println(s.x + " " + s.y + " " + s.z);
                    if (net[z][y][x] == null) {

                        // System.out.println("step " + s.x + " " + s.y + " " + s.z);
                        // NOTE was presiously: (i dont know why)
                        // net[shortest.pos[2] + s.z][shortest.pos[1] + s.y][shortest.pos[0] + s.x]
                        // = new Node(shortest, shortest.steps + s.length/3, goal, s);

                        net[z][y][x] = new Node(shortest, shortest.steps + 1, goal, x, y, z, s);
                        open.add(net[z][y][x]);

                    } else {
                        // System.out.println("net was not null");
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("step out of bounds exception");
                }
            }
            // System.out.println("open size " + open.size());

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
            // System.out.println(shortest.pos[0] + " " + shortest.pos[1] + " " +
            // shortest.pos[2] + " == " + goal[0] + " " + goal[1] + " " + goal[2]);
            if (shortest.pos[0] == goal[0] && shortest.pos[1] == goal[1] && shortest.pos[2] == goal[2]) {
                done = true;
            }
            // System.out.println("shortest dist " + shortest.cost);

        }

        if (possible) {
            ArrayList<Action> finalPath = new ArrayList<Action>();
            Node next = shortest;
            while (next.parent != null) {
                if (next.a != null) {
                    finalPath.add(next.a);
                }
                next = next.parent;
            }

            // for (Node n : open) {
            // finalPath.add(n.pos);
            // }
            System.out.println("path complete");
            return finalPath;
        } else {
            System.out.println("path is not possible");
            return new ArrayList<Action>();
        }

    }

}