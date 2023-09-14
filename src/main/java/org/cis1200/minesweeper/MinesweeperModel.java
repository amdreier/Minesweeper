package org.cis1200.minesweeper;

/*
 * Keeps track of all elements of the gameplay model
 *
 *
 */
public class MinesweeperModel {
    /* game status fields */
    private boolean playerLost = false;
    private boolean playerWon = false;

    // keeps track of the game board
    private Tile[][] tiles;

    private final int rows;
    private final int cols;
    private final int numBombs;
    private int tilesLeft;

    private boolean[][] getBombMap(int freeRow, int freeCol) {
        boolean[][] bombMap = new boolean[rows][cols];
        for (int row = Math.max(0, freeRow - 1); row <= Math.min(rows - 1, freeRow + 1); row++) {
            for (int col = Math.max(0, freeCol - 1); col <= Math.min(cols - 1, freeCol + 1);
                 col++) {
                bombMap[row][col] = true;
            }
        }

        for (int i = 0; i < numBombs; i++) {
            int rand = (int) (Math.random() * (rows * cols - (i + 9)));

            int row = 0;
            int col = 0;
            while ((rand > 0) || bombMap[row][col]) {

                if (!bombMap[row][col]) {
                    rand--;
                }

                if (col == cols - 1) {
                    col = 0;
                    row++;
                } else {
                    col++;
                }


            }

            bombMap[row][col] = true;
        }

        for (int row = Math.max(0, freeRow - 1); row <= Math.min(rows - 1, freeRow + 1); row++) {
            for (int col = Math.max(0, freeCol - 1); col <= Math.min(cols - 1, freeCol + 1);
                 col++) {
                bombMap[row][col] = false;
            }
        }

        return bombMap;
    }

    public void generateTiles(int freeRow, int freeCol) {
        playerLost = false;
        playerWon = false;
        tilesLeft = rows * cols;

        boolean[][] bombMap = getBombMap(freeRow, freeCol);

        tiles = new Tile[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                tiles[row][col] = new Tile(bombMap[row][col]);
            }
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (!tiles[row][col].hasBomb()) {
                    int numAdjacent = 0;
                    for (int i = Math.max(0, row - 1); i <= Math.min(rows - 1, row + 1); i++) {
                        for (int j = Math.max(0, col - 1); j <= Math.min(cols - 1, col + 1); j++) {
                            if (!(i == row && j == col) && tiles[i][j].hasBomb()) {
                                numAdjacent++;
                            }
                        }
                    }

                    tiles[row][col].setNumAdjacent(numAdjacent);
                } else {
                    tiles[row][col].setNumAdjacent(10);
                }
            }
        }
    }

    public MinesweeperModel(int rows, int cols, int numBombs) {
        this.rows = rows;
        this.cols = cols;
        tilesLeft = rows * cols;
        if ((numBombs > (rows * cols - 9)) || rows < 3 || cols < 3) {
            throw new IllegalArgumentException();
        }
        this.numBombs = numBombs;
    }

    public void clickTile(int row, int col) {
        if (row >= rows || col >= cols) {
            throw new IllegalArgumentException();
        }

        Tile tile = tiles[row][col];

        if (!tile.isClicked()) {
            tile.click();
            tilesLeft--;
            if (tile.hasBomb()) {
                playerLost = true;
            } else if (tilesLeft == numBombs) {
                playerWon = true;
            }


        }
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public boolean playerLost() {
        return playerLost;
    }

    public boolean playerWon() {
        return playerWon;
    }

    public int getNumBombs() {
        return numBombs;
    }

    public int getTilesLeft() {
        return tilesLeft;
    }

    public void setPlayerLost(boolean playerLost) {
        this.playerLost = playerLost;
    }

    public void setPlayerWon(boolean playerWon) {
        this.playerWon = playerWon;
    }

    public void setTilesLeft(int tilesLeft) {
        this.tilesLeft = tilesLeft;
    }

    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
    }
}
