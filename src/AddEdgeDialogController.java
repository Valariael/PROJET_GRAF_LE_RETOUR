import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller class for the Add Edge interface 'add_edge_dialog.xml.
 */
public class AddEdgeDialogController implements Initializable {
    private Stage stage;
    private List<Node> nodes;
    private Task fromNode = null, toNode = null;
    private MainMenuController mainController;

    @FXML
    private VBox addEdgeDialogFromBox, addEdgeDialogToBox;
    @FXML
    private Button addEdgeDialogOK, addEdgeDialogCancel;

    @FXML
    URL location;

    @FXML
    ResourceBundle resources;

    public AddEdgeDialogController() {}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addEdgeDialogFromBox.setPadding(new Insets(5));
        addEdgeDialogFromBox.setAlignment(Pos.CENTER);
        addEdgeDialogToBox.setPadding(new Insets(5));
        addEdgeDialogToBox.setAlignment(Pos.CENTER);

        ToggleGroup groupFrom = new ToggleGroup();
        ToggleGroup groupTo = new ToggleGroup();

        nodes = PertGraf.getInstance().getAllNodes();
        for(Node n : nodes) {
            groupFrom.getToggles().add(new RadioButton(n.getName()));
            groupTo.getToggles().add(new RadioButton(n.getName()));
        }

        for (Toggle radioButton : groupFrom.getToggles()) {
            addEdgeDialogFromBox.getChildren().add((RadioButton) radioButton);
        }
        for (Toggle radioButton : groupTo.getToggles()) {
            addEdgeDialogToBox.getChildren().add((RadioButton) radioButton);
        }

        groupFrom.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fromNode = (Task) nodes.get(groupFrom.getToggles().indexOf(newValue));
            }
        });
        groupTo.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                toNode = (Task) nodes.get(groupTo.getToggles().indexOf(newValue));
            }
        });

        addEdgeDialogOK.setOnAction(event -> {
            if(fromNode != null && toNode != null) {
                Task toTask = new Task(toNode.getName(), toNode.getLabel(), ((Task) toNode).getDuration(), ((Task) fromNode).getDuration());
                toTask.setToWeightActivated(true);
                if (!PertGraf.getInstance().pathExists(toTask, fromNode)) {
                    PertGraf.getInstance().addEdge(fromNode, toTask);
                    mainController.displayGraf();
                    stage.close();
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Impossible add!");
                    alert.setHeaderText(null);
                    alert.setContentText("Loops aren't possible in a Pert Graf");
                    alert.showAndWait();
                }
            }
        });

        addEdgeDialogCancel.setOnAction(event -> stage.close());
    }

    /**
     * Sets the reference to the Stage instance of the main window.
     *
     * @param u the Stage instance
     */
    void setObserver(Stage u) {
        this.stage = u;
    }

    /**
     * Sets the reference to the MainController instance.
     *
     * @param u the MainController instance
     */
    void setOriginController(MainMenuController u) {
        this.mainController = u;
    }
}
