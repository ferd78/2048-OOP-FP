import java.util.ArrayList;

public class Board {
    private Tile[][] tiles;

    public Board(int row, int col) //constructor that initializes the game grid using a 2-dimensional array
    {
        this.tiles = new Tile[row][col];

        int i, j;

        for (i = 0; i < row; i++) {
            for (j = 0; j < col; j++) {
                this.tiles[i][j] = null;
            }
        }

        this.spawnTile();
        this.spawnTile();
    }

    public Board(int[][] values) {
        this.tiles = new Tile[values.length][values[0].length];

        int row, col;

        for (row = 0; row < values.length; row++) {
            for (col = 0; col < values[row].length; col++) {
                if (values[row][col] < 1 || values[row][col] % 2 != 0) {
                    this.tiles[row][col] = null;
                }

                else {
                    this.tiles[row][col] = new Tile(values[row][col]);
                }
            }
        }
    }

    public int getColCount() {
        return (this.tiles[0].length);
    }
    public int getRowCount() {
        return (this.tiles.length);
    }

    public Tile tileAt(int row, int col) {
        return (this.tiles[row][col]);
    }

    public int[][] getTileValues()
    {
        int rowCnt = this.tiles.length;
        int colCnt = this.tiles[0].length;

        int[][] values = new int[rowCnt][colCnt];

        int row, col;

        for (row = 0; row < rowCnt; row++) {
            for (col = 0; col < colCnt; col++) {
                if (this.tiles[row][col] == null) {
                    values[row][col] = 0;
                } else {
                    values[row][col] = this.tiles[row][col].getVal();
                }
            }
        }

        return (values);
    }

    // returns true if the move "down" is possible
    private boolean hasDownMove()
    {
        int row, col;

        for (row = 0; row < this.tiles.length - 1; row++) {
            for (col = 0; col < this.tiles[row].length; col++) {
                Tile t0 = this.tileAt(row, col);
                Tile t1 = this.tileAt(row + 1, col);

                if (t0 != null) {
                    if (t1 == null) {
                        return (true);
                    } else if (t0.equals(t1)) {
                        return (true);
                    }
                }
            }
        }

        return (false);
    }

    public int moveDown() {
        if (!this.hasDownMove()) {
            return (0);
        }

        int score = 0;

        Tile[][] cols = new Tile[this.tiles[0].length][this.tiles.length];

        int i, j;

        // divide board into columns
        for (i = 0; i < cols.length; i++) {
            for (j = 0; j < cols[0].length; j++) {
                cols[i][j] = this.tiles[j][i];
            }
        }

        // format and combine all columns
        for (i = 0; i < cols.length; i++) {
            score += combineCol(cols[i]);
        }

        // add combined columns back into board
        for (i = 0; i < this.tiles.length; i++) {
            for (j = 0; j < this.tiles[0].length; j++) {
                this.tiles[i][j] = cols[j][i];
            }
        }

        this.spawnTile();

        return (score);
    }

    public int moveUp()
    {
        this.rotate90();
        this.rotate90();

        int score = this.moveDown();

        this.rotate90();
        this.rotate90();

        return (score);
    }

    public int moveLeft() {
        this.rotate90();
        this.rotate90();
        this.rotate90();

        int score = this.moveDown();

        this.rotate90();

        return (score);
    }

    public int moveRight()
    {
        this.rotate90();

        int score = this.moveDown();

        this.rotate90();
        this.rotate90();
        this.rotate90();

        return (score);
    }

    // rotates tile array a quarter turn clockwise
    private void rotate90()
    {
        int rows = this.tiles.length;
        int cols = this.tiles[0].length;

        Tile[][] ret = new Tile[cols][rows];

        int row, col;

        for (row = 0; row < rows; row++) {
            for (col = 0; col < cols; col++) {
                ret[col][(rows - 1) - row] = this.tiles[row][col];
            }
        }

        this.tiles = ret;
    }

    // combines like tiles
    private static int combineCol(Tile[] col)
    {
        int score = 0;

        int i;

        for (i = col.length - 1; i > 0; i--) {
            format(col);

            Tile t0 = col[i - 1];
            Tile t1 = col[i];

            if (t1 != null && t1.equals(t0)) {
                col[i - 1] = null;
                col[i]     = new Tile(t1.getVal() + t1.getVal());

                score += col[i].getVal();
            }
        }

        return (score);
    }

    // moves all tiles down
    private static void format(Tile[] col)
    {
        boolean cont = true;

        int i;

        while (cont) {
            cont = false;

            for (i = 0; i < col.length - 1; i++) {
                Tile t0 = col[i];
                Tile t1 = col[i + 1];

                if (t0 != null && t1 == null) {
                    col[i]     = null;
                    col[i + 1] = t0;

                    cont = true;
                }
            }
        }
    }

    // spawns either a 2 or a 4 Tile in a random available spot
    private void spawnTile()
    {
        BoardPos[] avail = this.emptyLocations();

        if (avail.length > 0) {
            int ind = (int)(Math.random() * avail.length);

            this.tiles[avail[ind].row][avail[ind].col] = new Tile();
        }
    }

    public boolean hasMove() {
        if (this.emptyLocations().length > 0) {
            return (true);
        }

        else {
            // check for similar adjacent tiles

            Tile[] adjs;

            Tile t;

            int row, col, i;

            for (row = 0; row < this.tiles.length; row++) {
                for (col = 0; col < this.tiles[row].length; col++) {
                    t    = this.tiles[row][col];
                    adjs = this.adjacentTiles(row, col);

                    for (i = 0; i < adjs.length; i++) {
                        if (t.getVal() == adjs[i].getVal()) {
                            return (true);
                        }
                    }
                }
            }

            return (false);
        }
    }

    // returns an array of empty locations in board
    private BoardPos[] emptyLocations() {
        ArrayList<BoardPos> avail = new ArrayList<BoardPos>();

        int row, col;

        for (row = 0; row < this.tiles.length; row++) {
            for (col = 0; col < this.tiles[row].length; col++) {
                if (this.tiles[row][col] == null) {
                    avail.add(new BoardPos(row, col));
                }
            }
        }

        int i;

        BoardPos[] ret = new BoardPos[avail.size()];

        for (i = 0; i < ret.length; i++) {
            ret[i] = avail.get(i);
        }

        return (ret);
    }

    // returns array of adjacent tiles (not diagonal)
    private Tile[] adjacentTiles(int row, int col)
    {
        ArrayList<Tile> adjacents = new ArrayList<Tile>();

        if (row - 1 >= 0) {
            adjacents.add(this.tiles[row - 1][col]);
        }

        if (row + 1 < this.tiles.length) {
            adjacents.add(this.tiles[row + 1][col]);
        }

        if (col - 1 >= 0) {
            adjacents.add(this.tiles[row][col - 1]);
        }

        if (col + 1 < this.tiles[0].length) {
            adjacents.add(this.tiles[row][col + 1]);
        }

        Tile[] ret = new Tile[adjacents.size()];

        int i;

        for (i = 0; i < ret.length; i++) {
            ret[i] = adjacents.get(i);
        }

        return (ret);
    }


    private class BoardPos {
        public int row;
        public int col;

        public BoardPos(int row, int col) //constructor for board position
        {
            this.row = row;
            this.col = col;
        }
    }
}