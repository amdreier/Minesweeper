package org.cis1200.minesweeper;

import java.awt.*;
import javax.swing.*;

/**
 * This class sets up the top-level frame and widgets for the GUI.
 *
 * This game adheres to a Model-View-Controller design framework. This
 * framework is very effective for turn-based games. We STRONGLY
 * recommend you review these lecture slides, starting at slide 8,
 * for more details on Model-View-Controller:
 * https://www.seas.upenn.edu/~cis120/current/files/slides/lec37.pdf
 *
 * In a Model-View-Controller framework, Game initializes the view,
 * implements a bit of controller functionality through the reset
 * button, and then instantiates a GameBoard. The GameBoard will
 * handle the rest of the game's view and controller functionality, and
 * it will instantiate a TicTacToe object to serve as the game's model.
 */
public class RunMinesweeper implements Runnable {
    public void run() {
        final int NUMBOMBS = 17;
        final int ROWS = 10;
        final int COLS = 10;

        // Top-level frame in which game components live
        final JFrame frame = new JFrame("Minesweeper");
        frame.setPreferredSize(new Dimension(700, 700));
        frame.setMinimumSize(new Dimension(600, 600));
        frame.setLocation(300, 300);

        // Status panel
        final JPanel status_panel = new JPanel();
        frame.add(status_panel, BorderLayout.SOUTH);
        final JLabel status = new JLabel("Setting up...");
        final JLabel flags = new JLabel("Flags:");
        final JLabel time = new JLabel("Time: 00:00");
        status_panel.add(flags);
        status_panel.add(new JPanel());
        status_panel.add(status);
        status_panel.add(new JPanel());
        status_panel.add(time);

        // Game board
        final Minefield field = new Minefield(status, flags, time, ROWS, COLS, NUMBOMBS);
        field.reset(ROWS, COLS, NUMBOMBS);
        frame.add(field, BorderLayout.CENTER);

        // Reset button
        final JPanel control_panel = new JPanel();
        frame.add(control_panel, BorderLayout.NORTH);
        final JButton reset = new JButton("New Game");
        reset.addActionListener(e -> field.reset(ROWS, COLS, NUMBOMBS));
        final JButton instruct = new JButton("Instructions");
        instruct.addActionListener(e -> field.showInstruct());
        final JButton save = new JButton("Save Game");
        save.addActionListener(e -> field.saveState());
        final JButton load = new JButton("Load Game");
        load.addActionListener(e -> field.loadState());
        control_panel.add(save);
        control_panel.add(load);
        control_panel.add(reset);
        control_panel.add(instruct);

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        field.showInstruct();
    }
}
