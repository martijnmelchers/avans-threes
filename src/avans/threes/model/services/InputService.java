package avans.threes.model.services;

import avans.threes.model.Tile;

import java.nio.file.Files;
import java.nio.file.Paths;

public class InputService {
    public static Tile[][] load(int size) throws Exception {
        var path = Paths.get("input.txt");
        var lines = Files.readAllLines(path);

        /* If the size is not valid throw an exception */
        if (lines.size() / size != size)
            throw new Exception("Invalid input provided!");

        var tiles = new Tile[lines.size() / 2][lines.size() / 2];

        /* Process each line */
        for (String line : lines) {
            String[] values = line.split(" ");

            var x = Integer.parseInt(values[0]);
            var y = Integer.parseInt(values[1]);
            var value = Integer.parseInt(values[2]);

            tiles[x][y] = new Tile(value);
        }

        /* Check if all tiles are filled */
        for(int x = 0; x < tiles.length; x++) {
            for(int y = 0; y < tiles[x].length; y++) {
                if(tiles[x][y] == null)
                    throw new Exception("Invalid input provided!");
            }
        }

        return tiles;
    }
}
