package org.cis1200.maze;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MazeMenuBar extends JMenuBar {
    private JMenu fileMenu;
    private JMenuItem saveGame;
    private JMenuItem loadGame;
    private JMenu helpMenu;
    private JMenuItem instructions;

    public MazeMenuBar() {
        super();

        this.fileMenu = new JMenu("File");
        this.add(this.fileMenu);

        this.saveGame = new JMenuItem("Save Game");
        this.loadGame = new JMenuItem("Load Game");
        this.fileMenu.add(this.saveGame);
        this.fileMenu.add(this.loadGame);

        this.saveGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(saveGame) == JFileChooser.APPROVE_OPTION) {
                    String filePath = chooser.getSelectedFile().getAbsolutePath();

                    if (!filePath.endsWith(".save")) {
                        filePath += ".save";
                    }

                    try {
                        Save.writeSave(filePath);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(saveGame, "Unable to save game!");
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        this.loadGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(loadGame) == JFileChooser.APPROVE_OPTION) {
                    String filePath = chooser.getSelectedFile().getAbsolutePath();

                    try {
                        Save.readSave(filePath);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(saveGame, "Unable to load saved game!");
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        this.helpMenu = new JMenu("Help");
        this.add(this.helpMenu);

        this.instructions = new JMenuItem("Instructions");
        this.helpMenu.add(this.instructions);

        this.instructions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(
                        instructions,
                        "Welcome to the maze!\nUse W and S to move " +
                                "forward and backwards, and use A and D to look " +
                                "left and right.\nYour goal is to escape the maze " +
                                "in as little time as possible (there is a timer in" +
                                " the top-left corner).\nTo escape you must reach " +
                                "the opposite corner of the maze from which you " +
                                "started, and touch the wall labelled 'END'.\nAt " +
                                "any time you can save your game or load a previous" +
                                " save using the file menu. Have fun!"
                );
            }
        });
    }
}
