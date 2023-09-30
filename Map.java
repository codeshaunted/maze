package org.cis1200.maze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Map {
    private static Random random = new Random();
    private static Tile brickTile;
    private static Tile startTile;
    private static Tile endTile;

    enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    private static int randomInt(int minimum, int maximum) {
        return Map.random.nextInt((maximum - minimum) + 1) + minimum;
    }

    private static void carve(Tile[][] map, int x, int y, int width, int height) {
        // get all directions and randomize them (hence "random DFS")
        ArrayList<Direction> directions = new ArrayList<Direction>();
        directions.add(Direction.UP);
        directions.add(Direction.DOWN);
        directions.add(Direction.LEFT);
        directions.add(Direction.RIGHT);
        Collections.shuffle(directions);

        for (Direction direction : directions) {
            // every time we carve, we are carving in intervals of 2 (the nature of doing
            // this with a grid rather
            // than with thin walls), so we need to keep track of an "other" X and Y to
            // carve out as well as the one
            // that's 2 units in front of our last one.
            int newX = x;
            int newY = y;
            int newXOther = x;
            int newYOther = y;
            switch (direction) {
                case UP:
                    newY = y - 2;
                    newYOther = y - 1;
                    break;
                case DOWN:
                    newY = y + 2;
                    newYOther = y + 1;
                    break;
                case LEFT:
                    newX = x - 2;
                    newXOther = x - 1;
                    break;
                case RIGHT:
                    newX = x + 2;
                    newXOther = x + 1;
                    break;
                default:
                    break;
            }

            if (newX < 1 || newX > width - 2 || newY < 1 || newY > height - 2) {
                continue; // out of bounds, not valid
            }

            // carve out our new 2 unit long hole
            if (map[newY][newX] != null) {
                map[newY][newX] = null;
                map[newYOther][newXOther] = null;

                // recursively call
                Map.carve(map, newX, newY, width, height);
            }
        }
    }

    public static Tile[][] generate(int width, int height) {
        if (Map.brickTile == null || Map.startTile == null || Map.endTile == null) {
            Map.brickTile = TileManager.getTile("brick");
            Map.startTile = TileManager.getTile("start");
            Map.endTile = TileManager.getTile("end");
        }

        Tile[][] map = new Tile[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                map[i][j] = Map.brickTile;
            }
        }

        Map.carve(map, 1, 1, width, height);
        map[1][0] = startTile; // place start in top left
        map[height - 1][width - 2] = endTile; // place end in bottom right

        return map;
    }
}
