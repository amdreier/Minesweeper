package org.cis1200.minesweeper;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import javax.swing.*;

public class Minefield extends JPanel {
    private final static String INSTRUCTIONS =
        """
        Welcome to Minesweeper!
        Each field is randomly generated with bombs hidden throughout!
        The goal of the game is to clear all non-bomb tiles without opening a bomb tile.
        When a tile is clicked, it will show the number of bomb tiles adjacent to it.
        The first tile clicked is always adjacent to 0 bombs, which is displayed as an empty tile.
        Empty tiles will automatically click all tiles adjacent to it.
        You can flag tiles you believe to contain a bomb by right-clicking on it.
        The number of bombs is equal to the number of flags you start out with.
        
        You can save one started game by clicked "Save game".
        Clicking "Load game" will open that saved game.
        Good luck!""";
    private final JLabel status;
    private final JLabel flagsStatus;
    private final Timer timer;
    private final JLabel time;
    private long startTime;
    private long offSet;
    private boolean gameOver = false;
    boolean firstClick = true;
    private MinesweeperModel gameModel;
    private final JPanel grid = new JPanel();
    private final JFrame instruct;
    private Tile[][] tiles;
    private ColoredSquareButton[][] squares;
    private final SimpleDateFormat df = new SimpleDateFormat("mm:ss");
    private int rows;
    private int cols;
    private int flags;
    private class ColoredSquareButton extends JButton {
        private final int row;
        private final int col;
        private boolean flagged = false;

        public void reset() {
            setPreferredSize(new Dimension(50, 50));
            setOpaque(true);
            setBorder(BorderFactory.createRaisedBevelBorder());
            setForeground(Color.RED);
            setBorderPainted(true);
            setFont(new Font("Sans Serif", Font.BOLD, 30));
            setBackground(new Color(0, 84, 0, 255));

            addActionListener(e -> {
                if (!gameOver) {
                    clickSquare();
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    if (!gameOver && SwingUtilities.isRightMouseButton(e)) {
                        flag();
                    }
                }
            });

            setText("");
        }

        public void flag() {
            if (!tiles[row][col].isClicked() && !gameOver) {
                if (flagged) {
                    flagged = false;
                    flags++;
                    setText("");
                } else {
                    flagged = true;
                    flags--;
                    setText("F");
                }
                flagsStatus.setText("Flags: " + flags);
            }
        }

        public void clickSquare() {
            if (firstClick) {
                firstClick = false;
                makeBoard(row, col);
                startTime = System.currentTimeMillis();
                timer.restart();
            }

            if (!(tiles[row][col].isClicked() || gameOver || flagged)) {
                int numAdj = tiles[row][col].getNumAdjacent();
                gameModel.clickTile(row, col);
                setBackground(new Color(88, 211, 88));
                setBorder(BorderFactory.createLoweredBevelBorder());
                if (gameModel.playerLost()) {
                    setBackground(new Color(255, 0, 0));
                    showBombs(Color.RED, true);
                    gameOver = true;
                    status.setText("You lost :(");
                } else if (gameModel.playerWon()) {
                    status.setText("You won!");
                    showBombs(new Color(18, 189, 227), false);
                    gameOver = true;
                    setForeground(Color.BLACK);
                    if (numAdj != 0) {
                        setText("" + numAdj);
                    }
                } else {
                    setForeground(Color.BLACK);
                    if (numAdj != 0) {
                        setText("" + numAdj);
                    }
                    if (tiles[row][col].getNumAdjacent() == 0) {
                        for (int i = Math.max(0, row - 1); i <= Math.min(rows - 1, row + 1); i++) {
                            for (int j = Math.max(0, col - 1); j <= Math.min(cols - 1, col + 1);
                                 j++) {
                                if (!(i == row && j == col)) {
                                    squares[i][j].clickSquare();
                                }
                            }
                        }
                    }
                }
            }
        }

        public ColoredSquareButton(int row, int col) {
            super();
            this.row = row;
            this.col = col;
            reset();
        }
    }

    public void showInstruct() {
        instruct.setVisible(true);
        instruct.requestFocus();
    }

    public Minefield(JLabel statusInit, JLabel flagsStatus, JLabel time, int rows, int cols,
                     int numBombs) {
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setFocusable(true);
        status = statusInit;
        this.flagsStatus = flagsStatus;
        this.time = time;
        grid.setOpaque(false);
        instruct = new JFrame("Instructions");
        instruct.setLayout(new GridLayout(0, 1));
        instruct.setPreferredSize(new Dimension(560, 230));
        JTextArea instructText = new JTextArea(INSTRUCTIONS);
        instructText.setEditable(false);
        instruct.add(instructText);
        instruct.pack();
        instruct.setVisible(true);


        timer = new Timer(1000, e -> {
            if (!gameOver) {
                time.setText("Time: " + df.format(System.currentTimeMillis() - startTime + offSet));
            }
        });
        timer.setRepeats(true);
        timer.start();

        // initialize minesweeper game-model
        gameModel = new MinesweeperModel(rows, cols, numBombs);
        this.rows = gameModel.getRows();
        this.cols = gameModel.getCols();
        this.flags = numBombs;
        this.add(grid);
    }

    private void makeBoard(int freeRow, int freeCol) {
        gameModel.generateTiles(freeRow, freeCol);
        tiles = gameModel.getTiles();
    }

    public void reset(int rows, int cols, int numBombs) {
        timer.stop();
        offSet = 0;
        flags = numBombs;
        gameOver = false;
        gameModel = new MinesweeperModel(rows, cols, numBombs);
        rows = gameModel.getRows();
        cols = gameModel.getCols();
        firstClick = true;
        flagsStatus.setText("Flags: " + flags);
        status.setText("Playing");
        time.setText("Time: 00:00");
        grid.removeAll();
        grid.setLayout(new GridLayout(rows, cols));
        squares = new ColoredSquareButton[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {

                ColoredSquareButton square = new ColoredSquareButton(row, col);
                squares[row][col] = square;
                grid.add(square);
            }
        }
        grid.validate();
        repaint();
    }

    public void saveState() {
        if (!firstClick) {
            File output = new File("savedGame");
            FileWriter writer = null;
            try {
                writer = new FileWriter(output, false);
            } catch (IOException e) {
                System.out.println("Error creating file");
                return;
            }

            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(writer);
            } catch (NullPointerException e) {
                System.out.println("Error opening file");
                return;
            }

            try {
                bw.write(status.getText() + "\n");                                  // status
                bw.write(gameOver + "\n");                                          // gameOver
                bw.write(rows + "\n");                                              // rows
                bw.write(cols + "\n");                                              // cols
                bw.write(System.currentTimeMillis() - startTime + offSet + "\n");   // offset
                bw.write(flags + "\n");                                             // flags

                // tiles[][] + flagged
                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < cols; col++) {
                        Tile tile = tiles[row][col];
                        bw.write(tile.hasBomb() + "\n");            // hasBomb
                        bw.write(tile.isClicked() + "\n");          // isClicked
                        bw.write(tile.getNumAdjacent() + "\n");     // numAdjacent
                        bw.write(squares[row][col].flagged + "\n"); // flagged
                    }
                }

                // model
                bw.write(gameModel.playerLost() + "\n");        // playerLost
                bw.write(gameModel.playerWon() + "\n");         // playerWon
                bw.write(gameModel.getNumBombs() + "\n");       // numBombs
                bw.write(gameModel.getTilesLeft() + "\n");      // tilesLeft
                bw.flush();
                bw.close();
            } catch (IOException e) {
                System.out.println("Error writing file");
            }
        }
    }

    public void loadState() {
        File input = new File("savedGame");
        FileReader reader = null;
        try {
            reader = new FileReader(input);
        } catch (IOException e) {
            System.out.println("Error finding file");
            return;
        }

        BufferedReader br = null;
        try {
            br = new BufferedReader(reader);
        } catch (NullPointerException e) {
            System.out.println("Error opening file");
            return;
        }

        try {
            String nextString;
            nextString = br.readLine();                     // status
            status.setText(nextString);
            nextString = br.readLine();                     // gameOver
            gameOver = Boolean.parseBoolean(nextString);
            nextString = br.readLine();                     // rows
            rows = Integer.parseInt(nextString);
            nextString = br.readLine();                     // cols
            cols = Integer.parseInt(nextString);
            nextString = br.readLine();                     // offSet
            offSet = Long.parseLong(nextString);
            nextString = br.readLine();                     // flags
            flags = Integer.parseInt(nextString);
            flagsStatus.setText("Flags: " + flags);

            Tile[][] newTiles = new Tile[rows][cols];
            boolean[][] flaggedMap = new boolean[rows][cols];
            // tiles[][] + flagged
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {

                    nextString = br.readLine();             // hasBomb
                    boolean hasBomb = Boolean.parseBoolean(nextString);
                    nextString = br.readLine();             // isClicked
                    boolean isClicked = Boolean.parseBoolean(nextString);
                    nextString = br.readLine();             // numAdjacent
                    int numAdjacent = Integer.parseInt(nextString);
                    nextString = br.readLine();             // flagged
                    flaggedMap[row][col] = Boolean.parseBoolean(nextString);
                    Tile tile = new Tile(hasBomb);
                    if (isClicked) {
                        tile.click();
                    }
                    tile.setNumAdjacent(numAdjacent);

                    newTiles[row][col] = tile;
                }
            }

            // model
            nextString = br.readLine();                     // playerLost
            boolean playerLost = Boolean.parseBoolean(nextString);
            nextString = br.readLine();                     // playerWon
            boolean playerWon = Boolean.parseBoolean(nextString);
            nextString = br.readLine();                     // numBombs
            int numBombs = Integer.parseInt(nextString);
            nextString = br.readLine();                     // tilesLeft
            int tilesLeft = Integer.parseInt(nextString);

            gameModel = new MinesweeperModel(rows, cols, numBombs);
            gameModel.setPlayerLost(playerLost);
            gameModel.setPlayerWon(playerWon);
            gameModel.setTilesLeft(tilesLeft);
            gameModel.setTiles(newTiles);
            tiles = gameModel.getTiles();
            br.close();
            firstClick = false;
            grid.removeAll();
            grid.setLayout(new GridLayout(rows, cols));
            squares = new ColoredSquareButton[rows][cols];
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    Tile tile = tiles[row][col];
                    ColoredSquareButton square = new ColoredSquareButton(row, col);
                    square.flagged = flaggedMap[row][col];
                    int numAdj = tile.getNumAdjacent();
                    if (gameModel.playerLost() && tile.hasBomb()) {
                        square.setBackground(new Color(255, 0, 0));
                        square.setBorder(BorderFactory.createLoweredBevelBorder());
                    } else if (gameModel.playerWon() && tile.hasBomb()) {
                        square.setBorder(BorderFactory.createRaisedBevelBorder());
                        square.setBackground(new Color(18, 189, 227));
                    } else if (tile.isClicked()) {
                        square.setBackground(new Color(88, 211, 88));
                        square.setBorder(BorderFactory.createLoweredBevelBorder());
                        square.setForeground(Color.BLACK);
                        if (numAdj != 0) {
                            square.setText("" + numAdj);
                        }
                    } else {
                        square.setBorder(BorderFactory.createRaisedBevelBorder());
                        square.setBackground(new Color(0, 84, 0, 255));
                        if (square.flagged) {
                            square.setBorder(BorderFactory.createRaisedBevelBorder());
                            square.setForeground(Color.RED);
                            square.setText("F");
                        }
                    }

                    squares[row][col] = square;
                    grid.add(square);
                }
            }

            grid.validate();
            repaint();

            requestFocusInWindow();
            time.setText("Time: " + df.format(offSet));
            startTime = System.currentTimeMillis();
            timer.restart();
        } catch (IOException e) {
            System.out.println("Error reading file");
        } catch (NullPointerException n) {
            System.out.println("No saved game");
        }
    }

    private class TimerActionListener implements ActionListener {
        private Timer prevTimer;
        private ColoredSquareButton square;
        private Color color;
        private boolean clickSquare;
        public TimerActionListener(Timer prevTimer, ColoredSquareButton square, Color color,
                                   boolean clickSquare) {
            super();
            this.prevTimer = prevTimer;
            this.square = square;
            this.color = color;
            this.clickSquare = clickSquare;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            square.flagged = false;
            square.setText("");

            if (clickSquare) {
                square.setBorder(BorderFactory.createLoweredBevelBorder());
            }

            square.setBackground(color);

            if (prevTimer != null) {
                prevTimer.start();
            }
        }
    }

    private void showBombs(Color color, boolean clickSquare) {
        Timer lastTimer = null;
        for (int i = 0; i < grid.getComponentCount(); i++) {
            ColoredSquareButton square = (ColoredSquareButton) grid.getComponent(i);
            Tile tile = tiles[square.row][square.col];
            if (tile.hasBomb() && !tile.isClicked()) {
                Timer timer = new Timer(200, new TimerActionListener(lastTimer, square, color,
                        clickSquare));
                lastTimer = timer;
                timer.setRepeats(false);
            }
        }
        if (lastTimer != null) {
            lastTimer.start();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(300, 300); // must change later
    }
}
