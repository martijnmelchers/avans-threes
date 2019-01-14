package avans.threes;

import avans.threes.model.EnvironmentVariables;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static Stage stage;

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("view/Board.fxml"));

        stage.setTitle("Avans: Threes!");
        stage.setScene(new Scene(root, EnvironmentVariables.GRID_WIDTH, 600));
        stage.setResizable(false);

        Main.stage = stage;

        stage.show();

    }

    public static void setTitle(String title) {
        Main.stage.setTitle(title);
    }
}
