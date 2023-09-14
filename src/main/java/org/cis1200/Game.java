package org.cis1200;

import javax.swing.*;

public class Game {
    public static void main(String[] args) {
        // Set the game you want to run here
        Runnable game = new org.cis1200.minesweeper.RunMinesweeper();

        SwingUtilities.invokeLater(game);
    }
}
