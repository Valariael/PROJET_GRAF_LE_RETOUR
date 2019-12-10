import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller class for the Remove Edge interface 'remove_edge_dialog.xml'.
 */
public class RemoveEdgeDialogController implements Initializable {
    private Stage stage;
    private List<Edge> edges;
    private Edge edge = null;
    private MainMenuController mainController;

    @FXML
    private VBox removeEdgeDialogBox;
    @FXML
    private Button removeEdgeDialogCancel, removeEdgeDialogOK;

    @FXML
    URL location;

    @FXML
    ResourceBundle resources;

    public RemoveEdgeDialogController() {}

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        ToggleGroup group = new ToggleGroup();

        edges = PertGraf.getInstance().getAllEdges();
        for(Edge n : edges) {
            group.getToggles().add(new RadioButton(n.toString()));
        }

        for (Toggle radioButton : group.getToggles()) {
            removeEdgeDialogBox.getChildren().add((RadioButton) radioButton);
        }

        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                edge = edges.get(group.getToggles().indexOf(newValue));
            }
        });

        removeEdgeDialogCancel.setOnAction(event -> stage.close());

        removeEdgeDialogOK.setOnAction(event -> {
            if(edge != null) {
                PertGraf.getInstance().adjList.get(edge.getHead()).remove(edge.getTail());
                mainController.displayGraf();
                stage.close();
            }
        });
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
