package org.cis1200.maze;

import java.awt.*;
import java.io.*;

public class Tile {
    private Color[][] texture;
    private int id;

    public Tile(int id, String filePath) {
        try {
            this.id = id;
            DataInputStream file = new DataInputStream(new FileInputStream(filePath));

            int colorCount = file.readByte();
            Color[] colorDictionary = new Color[colorCount];
            for (int i = 0; i < colorCount; i++) {
                colorDictionary[i] = new Color(file.readInt());
            }

            int width = file.readByte();
            int height = file.readByte();
            this.texture = new Color[height][width];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    this.texture[i][j] = colorDictionary[file.readByte()];
                }
            }

            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Color[][] getTexture() {
        return this.texture;
    }

    public int getID() {
        return this.id;
    }
}
