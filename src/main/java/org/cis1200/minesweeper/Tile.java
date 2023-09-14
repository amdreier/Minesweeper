package org.cis1200.minesweeper;

/*
 * This class keeps track of all the information needed for game tiles
 */
public class Tile {
    private final boolean hasBomb;
    private boolean clicked = false;
    private int numAdjacent;

    public Tile(boolean hasBomb) {
        this.hasBomb = hasBomb;
    }

    public boolean hasBomb() {
        return hasBomb;
    }

    public void setNumAdjacent(int numAdjacent) {
        this.numAdjacent = numAdjacent;
    }

    public int getNumAdjacent() {
        return numAdjacent;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void click() {
        clicked = true;
    }
}
