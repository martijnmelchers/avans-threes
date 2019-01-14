package avans.threes.controller;

import avans.threes.model.Direction;
import avans.threes.model.EnvironmentVariables;
import avans.threes.model.Tile;
import avans.threes.model.services.BoardService;
import javafx.scene.paint.Color;

import static javafx.scene.paint.Color.rgb;

public class BoardController {
    private BoardService _boardService;

    public BoardController() {
        this._boardService = new BoardService();
    }

    /* Get the size of the board */
    public int getBoardSize() {
        return this._boardService.getSize();
    }

    /* Generate a board */
    public void generateBoard(int size, boolean empty, boolean loadInput) {
        this._boardService.generateBoard(size, empty, loadInput);
    }

    /* Get all tiles so they can be rendered */
    public Tile[][] getTiles() {
        return this._boardService.getTiles();
    }

    /* Move all times to specified direction */
    public int moveTiles(Direction direction) {
        return this._boardService.moveTiles(direction, false);
    }

    /* Get whether or not the game is over */
    public boolean isGameOver() {
        return this._boardService.isGameOver();
    }

    /* Get the round */
    public String getRound() {
        return this._boardService.getRound();
    }

    /* Get the score */
    public String getScore() {
        return this._boardService.getScore();
    }

    /* Get a tile according to a tile color */
    public Color getTileColor(int value) {
        switch (value) {
            case -1:
                return EnvironmentVariables.GRID_COLOR;
            case 1:
                return rgb(41, 128, 185);
            case 2:
                return rgb(231, 76, 60);
            default:
                return Color.WHITE;
        }
    }

    /* Get the tile text */
    public Color getTextColor(int value) {
        switch (value) {
            case 1:
            case 2:
                return Color.WHITE;
            default:
                return Color.BLACK;
        }
    }
}
