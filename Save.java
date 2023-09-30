package org.cis1200.maze;

import java.io.*;

public class Save {
    private static int timer;
    public static Vector2 playerPosition;
    public static Vector2 playerHeading;
    public static Tile[][] map;

    public static void initialize() {
        Save.timer = 0;
        Save.playerPosition = new Vector2(1.5d, 1.5d);
        Save.playerHeading = new Vector2(0.0d, -1.0d);
        Save.map = Map.generate(17, 17);
    }

    public static void writeSave(String filePath) throws IOException {
        DataOutputStream file = new DataOutputStream(new FileOutputStream(filePath));

        file.writeInt(Save.timer);

        file.writeDouble(Save.playerPosition.getX());
        file.writeDouble(Save.playerPosition.getY());
        file.writeDouble(Save.playerHeading.getX());
        file.writeDouble(Save.playerHeading.getY());

        file.writeShort(Save.map.length);
        file.writeShort(Save.map[0].length);

        for (int i = 0; i < Save.map.length; i++) {
            for (int j = 0; j < Save.map[0].length; j++) {
                if (Save.map[i][j] == null) {
                    file.writeByte(0); // 0 is null
                } else {
                    file.writeByte(Save.map[i][j].getID());
                }
            }
        }
    }

    public static void readSave(String filePath) throws IOException {
        DataInputStream file = new DataInputStream(new FileInputStream(filePath));

        Save.timer = file.readInt();

        Save.playerPosition.setX(file.readDouble());
        Save.playerPosition.setY(file.readDouble());
        Save.playerHeading.setX(file.readDouble());
        Save.playerHeading.setY(file.readDouble());

        int width = file.readShort();
        int height = file.readShort();
        Save.map = new Tile[height][width];

        for (int i = 0; i < Save.map.length; i++) {
            for (int j = 0; j < Save.map[0].length; j++) {
                Save.map[i][j] = TileManager.getTile(file.readByte());
            }
        }
    }

    public static int getTimer() {
        return Save.timer;
    }

    public static void setTimer(int timer) {
        Save.timer = timer;
    }

    public static Vector2 getPlayerPosition() {
        return Save.playerPosition;
    }

    public static void setPlayerPosition(Vector2 playerPosition) {
        Save.playerPosition = playerPosition;
    }

    public static Vector2 getPlayerHeading() {
        return Save.playerHeading;
    }

    public static void setPlayerHeading(Vector2 playerHeading) {
        Save.playerHeading = playerHeading;
    }

    public static Tile[][] getMap() {
        return Save.map;
    }

    public static void setMap(Tile[][] map) {
        Save.map = map;
    }
}
