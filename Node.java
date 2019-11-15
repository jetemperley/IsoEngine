class Node {
    int[] pos;
    float cost, goalDist;
    int steps;
    Node parent;

    Node(Node last, int step, int[] goal, int[] p) {
        goalDist = Vec3.simpleDist(p[0], p[1], p[2], goal[0], goal[1], goal[2]);
        pos = p;
        cost = (step*step) + goalDist;
        steps = step;
        parent = last;
    }

    Node(Node last, int step, int[] goal, int x, int y, int z){
        int[] i = {x, y, z};
        pos = i;
        steps = step;

        goalDist = Vec3.simpleDist(pos[0], pos[1], pos[2], goal[0], goal[1], goal[2]);
        cost = (step*step) + goalDist;
        parent = last;
    }

}
