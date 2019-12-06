import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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
    public void initialize(URL location, ResourceBundle resources)
    {
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
                PertGraf.getInstance().addEdge(fromNode, toTask);
                mainController.displayGraf();
                stage.close();
            }
        });

        addEdgeDialogCancel.setOnAction(event -> stage.close());
    }

    void setObserver(Stage u) {
        this.stage = u;
    }

    void setOriginController(MainMenuController u) {
        this.mainController = u;
    }
}