import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {
    private UserInterface userInterface;

    @FXML
    Button displayPertFormat,
            displayDotFormat,
            displayAdjacendyList,
            displayAdjacencyMatrix,
            displaySuccessorArray,
            displayReverseGraph,
            featureAddNode,
            featureAddEdge,
            featureRemoveNode,
            featureRemoveEdge;
    @FXML
    TextArea textAreaDisplayGraph;
    @FXML
    MenuItem menuNewFromDot,
            menuNewWithRange,
            menuNewEmpty,
            menuExportDot,
            menuExportPdf;
    @FXML
    BorderPane root;

    @FXML
    URL location;

    @FXML
    ResourceBundle resources;

    public MainMenuController() {}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuNewFromDot.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load from DOT file");
            File chosenFile = fileChooser.showOpenDialog(userInterface.primaryStage);
            //TODO error handling
            try
            {
                PertGraf.setInstance(PertGraf.create(chosenFile.getPath()));
            } catch (FileNotFoundException | InvalidFormatException e)
            {
                e.printStackTrace();
            }
        });
    }

    void setObserver(UserInterface u) {
        this.userInterface = u;
    }
}
