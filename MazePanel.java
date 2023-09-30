package org.cis1200.maze;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MazePanel extends JPanel implements KeyListener {
    private static final int SCREEN_WIDTH = 854;
    private static final int SCREEN_HEIGHT = 480;
    private static final double FIELD_OF_VIEW = Math.PI / 2; // 60 degrees
    private static final double RAY_PRECISION = 128.0d; // higher values are less efficient but more
    // accurate
    private static final int THINK_TIME = 42; // milliseconds
    private static final Color CEILING_COLOR = new Color(119, 114, 60);
    private static final Color FLOOR_COLOR = new Color(100, 93, 39);
    private static final Color TIMER_COLOR = Color.WHITE;
    private static final double MOVEMENT_MODIFIER = 0.1d;
    private static final double ROTATION_INCREMENT = Math.PI / 30;
    private boolean forwardDown = false;
    private boolean backwardDown = false;
    private boolean leftDown = false;
    private boolean rightDown = false;

    public MazePanel() {
        super();

        this.setFocusable(true);
        this.addKeyListener(this);

        Timer timer = new Timer(THINK_TIME, e -> think());
        timer.start();
    }

    public void think() {
        Save.setTimer(Save.getTimer() + this.THINK_TIME);

        this.repaint();

        // reached the end
        if ((int) Save.getPlayerPosition().getX() == Save.getMap()[0].length - 2
                && (int) Save.getPlayerPosition().getY() == Save.getMap().length - 2) {
            JOptionPane.showMessageDialog(
                    this,
                    String.format(
                            "Congratulations, you've escaped the maze in %d seconds! " +
                                    "Click OK to continue to another maze.",
                            Save.getTimer() / 1000
                    )
            );
            // reset map and save data
            Save.initialize();

            // reset key down trackers
            this.forwardDown = false;
            this.backwardDown = false;
            this.leftDown = false;
            this.rightDown = false;
        }

        // handle controls
        if (forwardDown) {
            double newXW = Save.getPlayerPosition().getX()
                    + (Save.getPlayerHeading().getX() * MOVEMENT_MODIFIER);
            double newYW = Save.getPlayerPosition().getY()
                    + (Save.getPlayerHeading().getY() * MOVEMENT_MODIFIER);
            // check to make sure we're not walking through a wall
            if (Save.getMap()[(int) newXW][(int) Save.getPlayerPosition().getY()] == null) {
                Save.getPlayerPosition().setX(newXW);
            }
            if (Save.getMap()[(int) Save.getPlayerPosition().getX()][(int) newYW] == null) {
                Save.getPlayerPosition().setY(newYW);
            }
        }
        if (backwardDown) {
            double newXS = Save.getPlayerPosition().getX()
                    - (Save.getPlayerHeading().getX() * MOVEMENT_MODIFIER);
            double newYS = Save.getPlayerPosition().getY()
                    - (Save.getPlayerHeading().getY() * MOVEMENT_MODIFIER);
            // check to make sure we're not walking through a wall
            if (Save.getMap()[(int) newXS][(int) Save.getPlayerPosition().getY()] == null) {
                Save.getPlayerPosition().setX(newXS);
            }
            if (Save.getMap()[(int) Save.getPlayerPosition().getX()][(int) newYS] == null) {
                Save.getPlayerPosition().setY(newYS);
            }
        }
        if (leftDown) {
            Save.setPlayerHeading(Save.getPlayerHeading().rotate(-ROTATION_INCREMENT));
        }
        if (rightDown) {
            Save.setPlayerHeading(Save.getPlayerHeading().rotate(ROTATION_INCREMENT));
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // this ray-march algorithm is highly inefficient and could be replaced with a
        // much more efficient DDA algorithm
        // the problem is that i simply don't care enough to do that
        Vector2 rayHeading = Save.getPlayerHeading().rotate(-(FIELD_OF_VIEW / 2.0d));
        double rayHeadingIncrement = FIELD_OF_VIEW / SCREEN_WIDTH;
        for (int ray = 0; ray < SCREEN_WIDTH; ray++) {
            Vector2 rayPosition = Save.getPlayerPosition().copy();

            // march ray until we have a collision, then stop
            Tile wall = Save.getMap()[(int) rayPosition.getX()][(int) rayPosition.getY()];
            while (wall == null) {
                rayPosition = rayPosition.add(rayHeading.scale(1.0d / RAY_PRECISION));
                wall = Save.getMap()[(int) rayPosition.getX()][(int) rayPosition.getY()];
            }

            // calculate distance (we will use this to find the height for the line we want
            // to draw)
            double rayDistance = Save.getPlayerPosition().distance(rayPosition);
            rayDistance *= Math.cos(
                    Math.atan2(rayHeading.getY(), rayHeading.getX())
                            - Math.atan2(
                            Save.getPlayerHeading().getY(),
                            Save.getPlayerHeading().getX()
                    )
            ); // fix fisheye distortion by getting the cosine of the difference of the ray
            // angle and the player angle

            // the height of our line is conveniently half of our viewport height divided by
            // the ray distance
            double lineHeight = (SCREEN_HEIGHT / 2.0d) / rayDistance;

            // draw ceiling and floor as solid colors, these can be textured too but it gets
            // quite complicated
            g.setColor(CEILING_COLOR);
            g.drawLine(ray, 0, ray, (int) ((SCREEN_HEIGHT / 2.0d) - lineHeight));
            g.setColor(FLOOR_COLOR);
            g.drawLine(ray, (int) ((SCREEN_HEIGHT / 2.0d) + lineHeight), ray, SCREEN_HEIGHT);

            Color[][] texture = wall.getTexture();

            // get the column of the texture that we will be reading from
            int textureX = (int) (texture[0].length * (rayPosition.getX() + rayPosition.getY()));
            textureX %= texture[0].length;

            // march line and swap color for every pixel we cross through in the texture on
            // the way down
            double textureYStep = (lineHeight * 2.0d) / texture.length; // this is the amount of
            // pixels on screen per
            // pixel in the texture
            double textureY = (SCREEN_HEIGHT / 2.0d) - lineHeight; // start where we'd normally
            // start this line (end of
            // ceiling line)
            for (int i = 0; i < texture.length; i++) {
                g.setColor(texture[i][textureX]);
                g.drawLine(ray, (int) textureY, ray, (int) (textureY + textureYStep));

                textureY += textureYStep;
            }

            // rotate the ray by our increment angle on the way to completing our full FOV
            rayHeading = rayHeading.rotate(rayHeadingIncrement);
        }

        // draw timer
        g.setColor(TIMER_COLOR);
        g.setFont(g.getFont().deriveFont(g.getFont().getStyle() | Font.BOLD, 16.0f));
        g.drawString(String.format("Time Elapsed: %d", Save.getTimer() / 1000), 4, 16);


        // debug maze/player/heading display
        /*
        g.setColor(Color.GRAY);
        // g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        for (int r = 0; r < Save.map.length; r++) {
            for (int c = 0; c < Save.map[0].length; c++) {
                if (Save.map[r][c] != null) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.WHITE);
                }

                g.fillRect((r * 8) + 1, (c * 8) + 1, 7, 7);
            }
        }

        g.setColor(Color.ORANGE);
        g.fillOval(
                (int) ((Save.playerPosition.getX() * 8) - 2), (int)
                        ((Save.playerPosition.getY() * 8) -
                                2), 4,
                4
        );
        g.drawLine(
                (int) (Save.playerPosition.getX() * 8), (int) (Save.playerPosition.getY() *
                        8),
                (int) ((Save.playerPosition.getX() * 8) + (Save.playerHeading.getX() * 8)),
                (int) ((Save.playerPosition.getY() * 8) + (Save.playerHeading.getY() * 8))
        );*/

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_W:
                this.forwardDown = true;
                break;
            case KeyEvent.VK_S:
                this.backwardDown = true;
                break;
            case KeyEvent.VK_A:
                this.leftDown = true;
                break;
            case KeyEvent.VK_D:
                this.rightDown = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_W:
                this.forwardDown = false;
                break;
            case KeyEvent.VK_S:
                this.backwardDown = false;
                break;
            case KeyEvent.VK_A:
                this.leftDown = false;
                break;
            case KeyEvent.VK_D:
                this.rightDown = false;
                break;
            default:
                break;
        }
    }
}
