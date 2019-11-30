class Node {
    int[] pos;
    float cost, goalDist;
    int steps;
    Node parent;
    Action a;

    Node(Node last, int step, int[] goal, int[] p, Action act) {
        goalDist = Vec3.simpleDist(p[0], p[1], p[2], goal[0], goal[1], goal[2]);
        pos = p;
        cost = (step*step) + goalDist;
        steps = step;
        parent = last;
        a = act;
    }

    Node(Node last, int step, int[] goal, int x, int y, int z, Action act){
        int[] i = {x, y, z};
        pos = i;
        steps = step;

        goalDist = Vec3.simpleDist(pos[0], pos[1], pos[2], goal[0], goal[1], goal[2]);
        cost = (step*step) + goalDist;
        parent = last;
        a = act;
    }

}
