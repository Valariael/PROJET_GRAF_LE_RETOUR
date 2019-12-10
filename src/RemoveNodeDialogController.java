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
 * Controller class for the Remove Node interface 'remove_node_dialog.xml'.
 */
public class RemoveNodeDialogController implements Initializable {
    private Stage stage;
    private List<Node> nodes;
    private Node node = null;
    private MainMenuController mainController;

    @FXML
    private VBox removeNodeDialogBox;
    @FXML
    private Button removeNodeDialogCancel, removeNodeDialogOK;

    @FXML
    URL location;

    @FXML
    ResourceBundle resources;

    public RemoveNodeDialogController() {}

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        ToggleGroup group = new ToggleGroup();

        nodes = PertGraf.getInstance().getAllNodes();
        for(Node n : nodes) {
            group.getToggles().add(new RadioButton(n.toString()));
        }

        for (Toggle radioButton : group.getToggles()) {
            removeNodeDialogBox.getChildren().add((RadioButton) radioButton);
        }

        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                node = nodes.get(group.getToggles().indexOf(newValue));
            }
        });

        removeNodeDialogCancel.setOnAction(event -> stage.close());

        removeNodeDialogOK.setOnAction(event -> {
            if(node != null) {
                PertGraf.getInstance().removeNode(node);
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
