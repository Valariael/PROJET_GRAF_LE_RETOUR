import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Main class used to create the JavaFX application for the PERT Project.
 */
public class UserInterface extends Application
{
    Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException
    {
        this.primaryStage = primaryStage;

        // init
        final FXMLLoader fxmlLoader = new FXMLLoader();
        FileInputStream fxmlStream = new FileInputStream("resources/main_menu.fxml");
        BorderPane rootLayout = fxmlLoader.load(fxmlStream);
        MainMenuController controller = fxmlLoader.getController();
        controller.setObserver(this);

        Scene scene = new Scene(rootLayout);
        primaryStage.setTitle("PERT Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
