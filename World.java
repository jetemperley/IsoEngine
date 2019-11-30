import java.util.*;

public class World {

    private float seed;
    final static int WORLD_SIZE = 4;
    // grid of chunks
    Chunk[][] chunks;
    Thing selected = null;
    // Cell[y][x][vert] holds list of visible cells for each [y][x]chunck

    World() {
        this((float) Math.random());
    }

    World(float s) {
        seed = s;
        chunks = new Chunk[WORLD_SIZE][WORLD_SIZE];
        for (int y = 0; y < chunks.length; y++) {
            for (int x = 0; x < chunks[y].length; x++) {
                chunks[y][x] = new Chunk(x, y);
            }
        }

        for (int y = 0; y < chunks.length; y++) {
            for (int x = 0; x < chunks[y].length; x++) {

                chunks[y][x].visiCells = new ArrayList<Cell>();
                chunks[y][x].things = new ArrayList<Thing>();
                System.out.println("started get visi " + x + " " + y);
                chunks[y][x].visiCells = getVisiCells(x, y);
                System.out.println("finished get visi " + x + " " + y);
                chunks[y][x].things = getThings(x, y);

            }
        }

    }

    void draw(GLGraphics g) {

        // draw all the chunks
        for (int y = 0; y < chunks.length; y++) {
            for (int x = 0; x < chunks[y].length; x++) {
                chunks[y][x].update(this);
            }
        }
        for (int y = 0; y < chunks.length; y++) {
            for (int x = 0; x < chunks[y].length; x++) {
                chunks[y][x].draw(g);
            }
        }

        // draw the selection cube
        if (selected != null) {
            g.setColorLoc(0, 1, 0, 0.5f);
            g.setDrawLoc(selected.getX(), selected.getY(), selected.getZ());
            g.drawCube();
        }

    }

    // teleports piece at xyz by xiyizi
    void move(Thing t, int xi, int yi, int zi) {
        if (get(t.x + xi, t.y + yi, t.z + zi) == null) {
            setThing(t.x + xi, t.y + yi, t.z + zi, t);
            setThing(t.x, t.y, t.z, null);
        }
        t.x += xi;
        t.y += yi;
        t.z += zi;
    }

    void updateCells() {
        System.out.println("update cells");
        for (int y = 0; y < chunks.length; y++) {
            for (int x = 0; x < chunks[y].length; x++) {

                chunks[y][x].visiCells = getVisiCells(x, y);

            }
        }
    }

    void updateThingsList() {
        for (int y = 0; y < chunks.length; y++) {
            for (int x = 0; x < chunks[y].length; x++) {

                chunks[y][x].things = getThings(x, y);

            }
        }
    }

    void select(int x, int y, int z) {
        try {
            selected = chunks[y / Chunk.SIZE][x / Chunk.SIZE].data[z][y % Chunk.SIZE][x % Chunk.SIZE];
            // System.out.println(selected.x + " " + selected.y + " " + selected.z);
        } catch (ArrayIndexOutOfBoundsException e) {
            selected = null;
        }
    }

    Thing get(int x, int y, int z) {
        try {
            return chunks[y / Chunk.SIZE][x / Chunk.SIZE].data[z][y % Chunk.SIZE][x % Chunk.SIZE];
        } catch (ArrayIndexOutOfBoundsException e) {
            // System.out.println("world get returned null");
            return null;
        }
    }

    void setThing(int x, int y, int z, Thing t) {
        try {
            chunks[y / Chunk.SIZE][x / Chunk.SIZE].data[z][y % Chunk.SIZE][x % Chunk.SIZE] = t;
            chunks[y / Chunk.SIZE][x / Chunk.SIZE].visiCells = getVisiCells(x / Chunk.SIZE, y / Chunk.SIZE);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("setThing out of bounds");
        }

    }

    static int getAbsSize() {
        return WORLD_SIZE * Chunk.SIZE;
    }

    void updateVisiCell(int x, int y, int z) {
        Thing t = get(x, y, z);
        if (t.getType() == Thing.CELL) {

        }

    }

    

    ArrayList<Cell> getVisiCells(int chunkx, int chunky) {

        ArrayList<Cell> locs = new ArrayList<Cell>();

        // loop through all the chunk data[][][]
        for (int z = 0; z < Chunk.SIZE; z++) {
            for (int y = chunky * Chunk.SIZE; y < chunky * Chunk.SIZE + Chunk.SIZE; y++) {
                for (int x = chunkx * Chunk.SIZE; x < chunkx * Chunk.SIZE + Chunk.SIZE; x++) {
                    // if the thing is a block
                    if (get(x, y, z) != null && get(x, y, z).getType() == Thing.CELL) {
                        // check if any any of teh edges are open to air (open to a null)

                        // if (get(x + 1, y, z) == null || get(x, y + 1, z) == null || get(x, y, z + 1) == null
                        //         || get(x - 1, y, z) == null || get(x, y - 1, z) == null || get(x, y, z - 1) == null) {
                        //     locs.add((Cell) get(x, y, z));
                        //     // System.out.println("added cell " + x + " " + y + " " + z);

                        // }
                        try{
                            if (get(x + 1, y, z).getType() != Thing.CELL || get(x, y + 1, z).getType() != Thing.CELL || get(x, y, z + 1).getType() != Thing.CELL
                                || get(x - 1, y, z).getType() != Thing.CELL || get(x, y - 1, z).getType() != Thing.CELL || get(x, y, z - 1).getType() != Thing.CELL){
                                
                                locs.add((Cell)get(x, y, z));
                                
                            }
                        } catch (NullPointerException e){
                            locs.add((Cell)get(x, y, z));
                        }

                        

                    }

                }
            }

        }

        for (Cell c : locs) {
            c.updateFaces(this);
        }

        return locs;

    }

    ArrayList<Thing> getThings(int chunkx, int chunky) {
        ArrayList<Thing> locs = new ArrayList<Thing>();

        // loop through all the chunk data[][][]
        for (int z = 0; z < Chunk.SIZE; z++) {
            for (int y = chunky * Chunk.SIZE; y < chunky * Chunk.SIZE + Chunk.SIZE; y++) {
                for (int x = chunkx * Chunk.SIZE; x < chunkx * Chunk.SIZE + Chunk.SIZE; x++) {

                    if ((chunks[y / Chunk.SIZE][x / Chunk.SIZE].data[z][y % Chunk.SIZE][x % Chunk.SIZE] != null)
                            && chunks[y / Chunk.SIZE][x / Chunk.SIZE].data[z][y % Chunk.SIZE][x % Chunk.SIZE]
                                    .getType() != 0) {
                        locs.add(chunks[y / Chunk.SIZE][x / Chunk.SIZE].data[z][y % Chunk.SIZE][x % Chunk.SIZE]);
                    }
                }
            }
        }

        return locs;
    }

    void addRandomly(Thing piece) {
        int x = (int) (Math.random() * Chunk.SIZE);
        int y = (int) (Math.random() * Chunk.SIZE);
        int xc = (int) (Math.random() * World.WORLD_SIZE);
        int yc = (int) (Math.random() * World.WORLD_SIZE);

        for (int i = Chunk.SIZE - 1; i >= 0; i--) {
            if ((chunks[yc][xc].data[i][y][x] == null) && (chunks[yc][xc].data[i - 1][y][x] != null)) {
                // piece.x = xc * World.Chunk.SIZE + x;
                // piece.y = yc * World.Chunk.SIZE + y;
                // piece.z = i;
                piece.setLoc(xc * World.Chunk.SIZE + x, yc * World.Chunk.SIZE + y, i);
                chunks[yc][xc].data[i][y][x] = piece;
            }
        }
        // System.out.println("added piece to " + piece.x + " "+ piece.y + " " + piece.z
        // + " ");
        if (piece.getType() != 0) {
            chunks[piece.getLocY() / Chunk.SIZE][piece.getLocX() / Chunk.SIZE].things.add(piece);
        }

    }

    public class Chunk {

        final static int SIZE = 8;
        int chunkx, chunky;
        Thing[][][] data;
        ArrayList<Cell> visiCells;
        ArrayList<Thing> things;

        public Chunk(int xoff, int yoff) {

            chunkx = xoff;
            chunky = yoff;

            xoff *= SIZE;
            yoff *= SIZE;

            data = new Thing[SIZE][SIZE][SIZE];
            float n;

            // use noise to randomly generate height of terrain
            for (int y = 0; y < SIZE; y++) {
                for (int x = 0; x < SIZE; x++) {
                    n = 10 * SimplexNoise.getNormNoiseAt(x + xoff, y + yoff, 0.01f);
                    for (int z = (int) n; z >= 0; z--) {
                        data[z][y][x] = new Cell(x + chunkx * Chunk.SIZE, y + chunky * Chunk.SIZE, z);
                    }
                }
            }
            // visLocs=getVisibleGridLocs();

        }

        void update(World w) {
            for (Thing t : things) {
                t.update(w);
            }
        }

        void draw(GLGraphics g) {

            g.setColorLoc(0, 0, 0, 1);

            for (Cell c : visiCells) {
                c.draw(g);
            }

            for (Thing c : things) {
                c.draw(g);
            }
        }

        Chunk() {
            this(0, 0);
        }

    }

}