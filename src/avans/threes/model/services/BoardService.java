package avans.threes.model.services;

import avans.threes.model.Direction;
import avans.threes.model.EnvironmentVariables;
import avans.threes.model.Tile;
import javafx.scene.control.Alert;

import java.util.Random;

public class BoardService {
    private Tile[][] tiles;
    private Integer size = EnvironmentVariables.GRID_SIZE;
    private Integer round = 1;
    private Integer score = 0;
    private Integer previousSpawnedNumber = 0;
    private Boolean gameActive = false;

    /* Returns the size */
    public int getSize() {
        return size;
    }

    /* Returns the tiles */
    public Tile[][] getTiles() {
        return this.tiles.clone();
    }

    public int moveTiles(Direction direction, boolean simulate) {
        if (!this.gameActive)
            return 0;

        var placedTiles = 0;

        switch (direction) {
            case DOWN:
            case RIGHT: {
                for (int y = this.size - 1; y >= 0; y--) {
                    for (int x = 0; x < this.size; x++) {
                        /* If the direction is down pass parameters as X and Y if its right switch them so we dont have duplicate code */
                        if (direction == Direction.DOWN)
                            placedTiles = this.shiftTile(direction, x, y, simulate) ? placedTiles + 1 : placedTiles;
                        else
                            placedTiles = this.shiftTile(direction, y, x, simulate) ? placedTiles + 1 : placedTiles;

                    }
                }
            }
            break;
            case UP:
            case LEFT: {
                for (int y = 0; y < this.size; y++) {
                    for (int x = 0; x < this.size; x++) {
                        /* If the direction is up pass parameters as X and Y if its left switch them so we dont have duplicate code */
                        if (direction == Direction.UP)
                            placedTiles = this.shiftTile(direction, x, y, simulate) ? placedTiles + 1 : placedTiles;
                        else
                            placedTiles = this.shiftTile(direction, y, x, simulate) ? placedTiles + 1 : placedTiles;
                    }
                }
            }
            break;
        }

        /* Spawn a new tile when there were actually tiles moved */
        if (!simulate) {
            if (placedTiles > 0) {
                this.spawnNewTile(direction);
                this.round++;
            }
        }


        return placedTiles;
    }

    /* Check if there are moves left */
    public boolean isGameOver() {
        if (!this.gameActive)
            return false;

        var totalMoves = 0;

        for (Direction direction : Direction.values()) {
            totalMoves += this.moveTiles(direction, true);
        }

        if (totalMoves == 0)
            this.gameActive = false;

        return totalMoves == 0;
    }


    /* Spawn a new tile at a specified direction */
    private void spawnNewTile(Direction direction) {
        var tilePlaced = false;


        while (!tilePlaced) {
            var position = new Random().nextInt(this.size);
            var value = this.getRandomTile();

            while (value == previousSpawnedNumber) {
                value = this.getRandomTile();
            }

            Tile tile;
            switch (direction) {
                case UP:
                    tile = this.tiles[position][this.size - 1];
                    break;
                case DOWN:
                    tile = this.tiles[position][0];
                    break;
                case LEFT:
                    tile = this.tiles[this.size - 1][position];
                    break;
                case RIGHT:
                    tile = this.tiles[0][position];
                    break;
                default:
                    tile = new Tile();
                    break;
            }

            if (tile.isFilled()) {
                continue;
            }

            try {
                tile.setValue(value);
                tilePlaced = true;
                this.previousSpawnedNumber = value;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean shiftTile(Direction direction, int x, int y, boolean simulate) {
        var newX = this.calculateNewX(direction, x);
        var newY = this.calculateNewY(direction, y);


        var tile = this.tiles[x][y];

        /*  Check if the new X and Y axis are in bounds, if they are, and the tile is not empty and has not been moved this round */
        if (this.isInBounds(newX, newY) && tile.isFilled() && tile.getLastMovedRound() != this.round) {
            var newTile = this.tiles[newX][newY];

            if (this.isMergeableTile(newX, newY)) {

                /* Check if is a simulation */
                if (simulate) {
                    return newTile.simulateMerge(tile.getValue());
                } else {
                    try {
                        newTile.merge(tile.getValue());
                        tile.setValue(-1);

                        score += newTile.getValue();
                    } catch (Exception e) {
                        return false;
                    }
                }


            } else {
                try {
                    if (simulate) {
                        return true;
                    } else {
                        newTile.setValue(tile.getValue());
                        tile.setValue(-1);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            tile.setLastMovedRound(this.round);
            if (newTile.getValue() != -1)
                newTile.setLastMovedRound(this.round);

            return true;
        }

        return false;
    }

    /* Calculate a new Y value using the direction */
    private int calculateNewY(Direction direction, int y) {
        if (direction == Direction.DOWN)
            return y + 1;
        if (direction == Direction.UP)
            return y - 1;

        return y;
    }

    /* Calculate a new X value using the direction */
    private int calculateNewX(Direction direction, int x) {
        if (direction == Direction.RIGHT)
            return x + 1;
        if (direction == Direction.LEFT)
            return x - 1;

        return x;
    }

    /* Check if tile is mergable */
    private boolean isMergeableTile(int x, int y) {
        try {
            return this.tiles[x][y].isFilled();
        } catch (Exception e) {
            return false;
        }

    }

    /* Check if tile is in bounds */
    private boolean isInBounds(int x, int y) {
        return (x >= 0 && x < this.size) && (y >= 0 && y < this.size);
    }

    /* Returns the round */
    public String getRound() {
        return this.round.toString();
    }

    /* Generate a new board */
    public void generateBoard(int size, boolean empty, boolean loadInput) {
        this.size = size;
        this.round = 1;
        this.score = 0;
        this.tiles = new Tile[size][size];

        if (loadInput && !empty) {
            try {
                this.tiles = InputService.load(size);
                this.gameActive = true;
                return;
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Bord kon niet worden geladen uit de input! Er word een willekeurig bord geladen!").show();
            }
        }

        var filledSpots = 0;
        var spotsToBeFilled = Math.ceil((double) (size * size) / 100 * 40); // Fill 40% of the board so the filled tiles are equivalent to the size

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                this.tiles[x][y] = new Tile();
            }
        }

        /*
         * Try to fill the board until the required tiles has been filled.
         */
        while (spotsToBeFilled != filledSpots && !empty) {
            for (int i = 0; i < spotsToBeFilled; i++) {
                if (spotsToBeFilled == filledSpots) continue;

                var x = new Random().nextInt(size);
                var y = new Random().nextInt(size);

                if (this.tiles[x][y].isFilled())
                    continue;

                try {
                    this.tiles[x][y].setValue(this.getRandomTile());
                    filledSpots++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        this.gameActive = true;
    }


    /* Return the scoree */
    public String getScore() {
        return this.score.toString();
    }

    /* Get a random tile value */
    private int getRandomTile() {
        return (int) (Math.random() * 3 + 1);
    }
}
