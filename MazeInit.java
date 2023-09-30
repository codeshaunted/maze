package org.cis1200.maze;

import javax.swing.*;

public class MazeInit implements Runnable {
    public static void main(String[] args) {
        MazeInit init = new MazeInit();
        init.run();
    }

    public void run() {
        TileManager.initialize();

        Save.initialize();

        final JFrame frame = new JFrame("Maze");
        final MazePanel mazePanel;
        mazePanel = new MazePanel();

        JMenuBar menuBar = new MazeMenuBar();

        frame.add(mazePanel);
        frame.setJMenuBar(menuBar);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
