package avans.threes.view;

import avans.threes.Main;
import avans.threes.controller.BoardController;
import avans.threes.model.Direction;
import avans.threes.model.EnvironmentVariables;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.File;

public class Board {
    @FXML
    public GridPane playGrid;

    @FXML
    public Pane gridOverlay;

    @FXML
    private TextField boardSize;

    @FXML
    private Label scoreText;

    @FXML
    private Label roundText;

    @FXML
    private Label directionText;

    @FXML
    private Pane endGameScreen;

    @FXML
    private CheckBox loadInput;

    private BoardController controller;

    private Boolean inGame;
    private MediaPlayer mediaPlayer;
    private Boolean processingMove;

    /**
     * Start the music and render a random board
     */
    @FXML
    public void initialize() {
        this.controller = new BoardController();

        /* Start music */
        var music = new Media(new File("music.wav").toURI().toString());
        this.mediaPlayer = new MediaPlayer(music);
        this.mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        this.mediaPlayer.play();

        this.generateBoard(EnvironmentVariables.GRID_SIZE, true, false);
    }


    /**
     * Generate a new board
     */
    private void generateBoard(int size, boolean empty, boolean loadInput) {
        this.directionText.setText("Ready... Set... GO!");
        this.controller.generateBoard(size, empty, loadInput);
        this.playGrid.getChildren().clear();
        this.visualizeBoard();
    }

    /**
     * Render the board
     */
    private void visualizeBoard() {
        this.processingMove = true;
        this.playGrid.requestFocus();
        var size = this.controller.getBoardSize();

        var padding = (EnvironmentVariables.GRID_WIDTH / size) / 10;
        var blockPadding = padding + (((double) padding / size) * 2);

        /* We build the board visually but don't fill it with any tiles. */
        final double blockWidth = (double) (EnvironmentVariables.GRID_WIDTH / size) - blockPadding;
        final double blockHeight = (double) (EnvironmentVariables.GRID_WIDTH / size) - blockPadding;
        this.playGrid.setPadding(new Insets(padding));
        this.playGrid.setHgap(padding);
        this.playGrid.setVgap(padding);
        this.playGrid.setAlignment(Pos.CENTER);

        var tiles = this.controller.getTiles();

        this.scoreText.setText(this.controller.getScore());
        this.roundText.setText(this.controller.getRound());

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                var pane = new StackPane();
                var rectangle = new Rectangle(blockWidth, blockHeight);


                rectangle.setFill(this.controller.getTileColor(tiles[x][y].getValue()));
                rectangle.setArcWidth(10);
                rectangle.setArcHeight(10);

                var label = new Label(tiles[x][y].getText());
                label.setAlignment(Pos.CENTER);
                label.setTextFill(this.controller.getTextColor(tiles[x][y].getValue()));
                label.setFont(new Font(blockWidth / 3));

                pane.getChildren().addAll(rectangle, label);

                this.playGrid.add(pane, x, y);
            }
        }

        this.processingMove = false;
    }

    /**
     * Start the game, render a board and hide the options screen
     */
    @FXML
    public void startGame() {
        if (!this.boardSize.getText().matches("^[0-9]*$"))
            new Alert(Alert.AlertType.ERROR, "Voer een getal in!").show();
        else {
            var size = Integer.parseInt(this.boardSize.getText());

            if (size > 1 && size <= 20) {
                Main.setTitle("Current score: 0");

                this.generateBoard(size, false, this.loadInput.isSelected());
                this.inGame = true;

                var transition = new FadeTransition(Duration.millis(500), this.gridOverlay);
                transition.setToValue(0);
                transition.play();
                transition.setOnFinished(x -> this.gridOverlay.setVisible(false));
            } else {
                new Alert(Alert.AlertType.ERROR, "Het getal moet tussen de 1 en 20 zijn!").show();

            }

        }
    }

    /*
     * Reset the game when requested, renders a new empty board
     */
    @FXML
    public void resetGame() {
        Main.setTitle("Avans: Threes!");

        this.generateBoard(EnvironmentVariables.GRID_SIZE, true, false);
        this.gridOverlay.setVisible(true);
        this.endGameScreen.setVisible(false);
        this.inGame = false;

        var transition = new FadeTransition(Duration.millis(500), this.gridOverlay);
        transition.setToValue(1);
        transition.play();
    }

    /**
     * Triggered when game over, shows end game screen.
     */
    private void endGame() {
        Main.setTitle("Avans: Threes!");

        this.endGameScreen.setVisible(true);
        this.inGame = false;

        var transition = new FadeTransition(Duration.millis(500), this.endGameScreen);
        transition.setToValue(1);
        transition.play();
    }

    /**
     * When a key is pressed process it
     */
    @FXML
    public void keyPress(KeyEvent event) {

        if (!this.inGame || this.processingMove)
            return;

        switch (event.getCode()) {
            case UP:
                if (this.controller.moveTiles(Direction.UP) > 0)
                    this.directionText.setText("Look UP!");
                break;
            case DOWN:
                if (this.controller.moveTiles(Direction.DOWN) > 0)
                    this.directionText.setText("Let's get DOWN!");

                break;
            case LEFT:
                if (this.controller.moveTiles(Direction.LEFT) > 0)
                    this.directionText.setText("What's LEFT?");

                break;
            case RIGHT:
                if (this.controller.moveTiles(Direction.RIGHT) > 0)
                    this.directionText.setText("You're RIGHT!");
                break;
        }

        this.visualizeBoard();

        Main.setTitle("Current score: " + this.controller.getScore());

        if (this.controller.isGameOver())
            this.endGame();

    }
}
