package org.cis1200.maze;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class TileManager {
    private static HashMap<String, Tile> tiles = new HashMap<String, Tile>();
    private static int lastID = 1;

    public static void loadTile(String name) {
        if (TileManager.tiles.containsKey(name)) {
            return; // don't load it if it's already loaded
        }
        TileManager.tiles.put(name, new Tile(lastID, "tiles/" + name + ".tile"));
        lastID++; // increment so no duplicate IDs
    }

    public static void loadManifest() {
        try {
            DataInputStream file = new DataInputStream(new FileInputStream("tiles/tiles.manifest"));

            int tileCount = file.readByte();

            for (int i = 0; i < tileCount; i++) {
                int nameLength = file.readByte();
                byte[] nameBytes = new byte[nameLength];
                for (int j = 0; j < nameLength; j++) {
                    nameBytes[j] = file.readByte();
                }

                String name = new String(nameBytes);

                TileManager.loadTile(name);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initialize() {
        TileManager.loadManifest();
    }

    public static Tile getTile(String name) {
        return TileManager.tiles.get(name);
    }

    public static Tile getTile(int id) {
        for (Tile tile : TileManager.tiles.values()) {
            if (tile.getID() == id) {
                return tile;
            }
        }

        return null;
    }
}
