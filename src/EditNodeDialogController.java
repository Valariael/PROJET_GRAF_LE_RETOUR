import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller class for the Edit Node interface 'edit_node_dialog.xml.
 */
public class EditNodeDialogController implements Initializable {
    private Stage stage;
    private List<Node> nodes;
    private MainMenuController mainController;
    private Task selectedTask;

    @FXML
    private VBox editNodeDialogBox;
    @FXML
    private Button editNodeDialogOK, editNodeDialogCancel;

    @FXML
    URL location;

    @FXML
    ResourceBundle resources;

    public EditNodeDialogController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup group = new ToggleGroup();

        nodes = PertGraf.getInstance().getAllNodes();
        for(Node n : nodes) {
            group.getToggles().add(new RadioButton(n.getName()));
        }

        for (Toggle radioButton : group.getToggles()) {
            editNodeDialogBox.getChildren().add((RadioButton) radioButton);
        }

        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedTask = (Task) nodes.get(group.getToggles().indexOf(newValue));
            }
        });

        editNodeDialogOK.setOnAction(event -> {
            if(selectedTask != null) {
                Dialog<NewNodeInfos> dialog = new Dialog<>();
                dialog.setTitle("Edit a Node");
                dialog.setHeaderText("Please, edit the nodes information");

                ButtonType confirmButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                TextField nodeName = new TextField();
                nodeName.setPromptText("Task name");
                nodeName.setText(selectedTask.getName());
                TextField nodeLabel = new TextField();
                nodeLabel.setPromptText("Task label");
                nodeLabel.setText(selectedTask.getLabel());
                Spinner<Integer> nodeDuration = new Spinner<>();
                nodeDuration.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-9999, 9999, selectedTask.getDuration()));

                grid.add(new Label("Task name:"), 0, 0);
                grid.add(nodeName, 1, 0);
                grid.add(new Label("Task label:"), 0, 1);
                grid.add(nodeLabel, 1, 1);
                grid.add(new Label("Task duration:"), 0, 2);
                grid.add(nodeDuration, 1, 2);

                dialog.getDialogPane().setContent(grid);

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == confirmButtonType) {
                        return new NewNodeInfos(nodeName.getText(), nodeLabel.getText(), nodeDuration.getValue());
                    }
                    return null;
                });


                Optional<NewNodeInfos> result = dialog.showAndWait();
                result.ifPresent(content -> {
                    Task taskUpdated = new Task(result.get().name, result.get().label, result.get().duration, result.get().duration);
                    PertGraf.getInstance().updateTasks(selectedTask, taskUpdated);
                    mainController.displayGraf();
                    stage.close();
                });
            }
        });

        editNodeDialogCancel.setOnAction(event -> stage.close());
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
