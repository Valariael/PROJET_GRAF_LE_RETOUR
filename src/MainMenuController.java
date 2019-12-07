import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainMenuController implements Initializable {
    private UserInterface userInterface;
    private static DisplayType selectedDisplayType = DisplayType.PERT_FORMAT;
    private File savedDirectory;

    private enum DisplayType {
        PERT_FORMAT,
        DOT_FORMAT,
        ADJACENCY_LIST,
        ADJACENCY_MATRIX,
        SUCCESSOR_ARRAY
    }

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
            menuNewFromPert,
            menuNewWithRange,
            menuNewEmpty,
            menuExportDot,
            menuExportPng,
            menuExportPert,
            menuRandomDirectedGraph,
            menuRandomDAG,
            menuBFS,
            menuDFS,
            menuTransitiveClosure,
            menuLongestPath,
            menuEarlyTimes,
            menuLateTimes,
            menuCriticalpath,
            menuListScheduling;
    @FXML
    BorderPane root;

    @FXML
    URL location;

    @FXML
    ResourceBundle resources;

    public MainMenuController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeMenuListeners();
        initializeButtonListeners();
    }

    private void computeChangesAndSwitch(DisplayType d) {
        switch (selectedDisplayType) {
            case PERT_FORMAT:
                //PertGraf.setInstance(PertGraf.createPertGrafFromPertString(textAreaDisplayGraph.getText()));
                break;
            case DOT_FORMAT:
                //PertGraf.setInstance(PertGraf.createPertGrafFromDotString(textAreaDisplayGraph.getText()));
                break;
        }

        selectedDisplayType = d;
    }

    void displayGraf() {
        switch (selectedDisplayType) {
            case PERT_FORMAT:
                textAreaDisplayGraph.setText(PertGraf.getInstance().toPertString());
                break;
            case DOT_FORMAT:
                textAreaDisplayGraph.setText(PertGraf.getInstance().toDotString());
                break;
            case ADJACENCY_LIST:
                textAreaDisplayGraph.setText(PertGraf.getInstance().toString());
                break;
            case ADJACENCY_MATRIX:
                StringBuilder sb = new StringBuilder();
                int[][] m = PertGraf.getInstance().getAdjMatrix();

                for (int i = 0; i < m.length; i++) {
                    for (int j = 0; j < m[i].length; j++) {
                        sb.append(m[i][j]);
                        if (j < m[i].length - 1) sb.append(", ");
                    }
                    if (i < m.length - 1) sb.append("\n");
                }

                textAreaDisplayGraph.setText(sb.toString());
                break;
            case SUCCESSOR_ARRAY:
                textAreaDisplayGraph.setText(Arrays.toString(PertGraf.getInstance().getPertSuccessorArray()));
                break;
        }
    }

    private File chooseLocation(FileChooser fc, boolean saveMode) {
        if (savedDirectory != null && savedDirectory.exists()) {
            fc.setInitialDirectory(savedDirectory);
        }
        File chosenFile;
        if (saveMode) {
            chosenFile = fc.showSaveDialog(userInterface.primaryStage);
        }
        else {
            chosenFile = fc.showOpenDialog(userInterface.primaryStage);
        }
        if (chosenFile != null) {
            if (chosenFile.isDirectory()) {
                savedDirectory = chosenFile;
            }
            else {
                savedDirectory = chosenFile.getParentFile();
            }
        }
        return chosenFile;
    }

    private void initializeMenuListeners() {
        menuNewFromDot.setOnAction(event -> {
            if(cancelOverwrite()) return;

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load from DOT file");
            File chosenFile = chooseLocation(fileChooser, false);
            //TODO error handling
            try {
                try
                {
                    PertGraf.setInstance(PertGraf.createFromDotFile(chosenFile.getPath()));
                    displayGraf();
                } catch(IndexOutOfBoundsException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("An error occurred while loading the file");
                    alert.setContentText("The file is not in the correct format.");

                    alert.showAndWait();
                }
            } catch (FileNotFoundException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("An error occurred while loading the file");
                alert.setContentText(e.toString());

                alert.showAndWait();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        });

        menuNewFromPert.setOnAction(event -> {
            if(cancelOverwrite()) return;

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load from PERT file");
            File chosenFile = chooseLocation(fileChooser, false);
            //TODO error handling
            try {
                PertGraf.setInstance(PertGraf.createFromPertFile(chosenFile.getPath()));
                displayGraf();
            } catch (FileNotFoundException | InvalidFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("An error occurred while loading the file");
                alert.setContentText(e.toString());

                alert.showAndWait();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        });

        menuNewWithRange.setOnAction(event -> {
            if(cancelOverwrite()) return;

            TextInputDialog dialog = new TextInputDialog("1");
            dialog.setTitle("Creating a new graph");
            dialog.setHeaderText("Chose the amount of nodes generated");
            dialog.setContentText("Enter a number : ");

            boolean success = false;
            do {
                Optional<String> result = dialog.showAndWait();
                try {
                    if(result.isPresent()) {
                        int numberOfNodes = Integer.parseInt(result.get());

                        success = true;
                        PertGraf.resetInstance();
                        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWYZ";
                        StringBuilder buffer = new StringBuilder();

                        for(int i = 0; i < numberOfNodes; i++) {
                            final String name = buffer.toString() + alphabet.charAt(i % 26);
                            PertGraf.getInstance().addNode(new Task(name, (name).toLowerCase(), i));
                            int timesFirst = i / 26;
                            if(timesFirst != 0) {
                                if(buffer.length() == 0) buffer.append(alphabet.charAt(timesFirst % 26));
                                else buffer.setCharAt(0, alphabet.charAt(timesFirst % 26));
                            }
                            int timesSecond = timesFirst / 26;
                            if(timesSecond != 0) {
                                if(buffer.length() == 0) buffer.append(alphabet.charAt(timesSecond % 26));
                                else buffer.setCharAt(1, alphabet.charAt(timesSecond % 26));
                            }
                        }

                        displayGraf();
                    }
                } catch (NumberFormatException e) {
                    dialog.setContentText("Enter a number : \nThat was not a valid input");
                }
            } while(!success);
        });

        menuNewEmpty.setOnAction(event -> {
            if(cancelOverwrite()) return;

            PertGraf.resetInstance();

            displayGraf();
        });

        menuExportDot.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File file = chooseLocation(fileChooser, true);

            if (file != null) {
                try {
                    PertGraf.getInstance().toDotFile(file.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error !");
                    alert.setHeaderText(null);
                    alert.setContentText("An error has occurred, export aborted.");

                    alert.showAndWait();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        menuExportPng.setOnAction(event -> {
            //TODO
        });

        menuExportPert.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File file = chooseLocation(fileChooser, true);

            if (file != null) {
                try {
                    PertGraf.getInstance().toPertFile(file.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error !");
                    alert.setHeaderText(null);
                    alert.setContentText("An error has occurred, export aborted.");

                    alert.showAndWait();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        menuRandomDirectedGraph.setOnAction(event -> {
            //TODO
        });

        menuRandomDAG.setOnAction(event -> {
            //TODO
        });

        menuBFS.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Computed result");
            alert.setHeaderText("Breadth-First-Search result : ");
            StringBuilder sb = new StringBuilder();
            for (Node n : PertGraf.getInstance().getBFS()) {
                sb.append(n.toString());
                sb.append("\n");
            }
            alert.setContentText(sb.toString());

            alert.showAndWait();
        });

        menuDFS.setOnAction(event -> {//TODO: pick starting node ?
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Computed result");
            alert.setHeaderText("Depth-First-Search result : ");
            StringBuilder sb = new StringBuilder();
            for (Node n : PertGraf.getInstance().getDFS()) {
                sb.append(n.toString());
                sb.append("\n");
            }
            alert.setContentText(sb.toString());

            alert.showAndWait();
        });

        menuTransitiveClosure.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm changes");
            alert.setHeaderText("You are about to compute the transitive closure of the graph.");
            alert.setContentText("Do you want to save it as the current graph or print it as PNG ?\n(IT MIGHT BREAK EVERYTHING TO SAVE IT)");

            ButtonType buttonSave = new ButtonType("Save");
            ButtonType buttonPNG = new ButtonType("PNG");
            ButtonType buttonCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonSave, buttonPNG, buttonCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == buttonSave) {
                PertGraf.setInstance((PertGraf) PertGraf.getInstance().getTransitiveClosure());
            } else if (result.isPresent() && result.get() == buttonPNG) {
                //TODO print as png
            }
        });

        menuLongestPath.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Computed result");
            alert.setHeaderText("Longest path result : ");
            StringBuilder sb = new StringBuilder();

            PertGraf p = PertGraf.getInstance();

            Task startingNode = p.addStartingTask(p.getStartingTasks());
            p.addEndingTask(p.getEndingTasks());
            LongestPathInfo<Deque<Node>, Integer> info = p.computeLongestPathFrom(startingNode);

            sb.append("Distance : ");
            sb.append(info.dist);
            sb.append("\n");
            for (Node n : info.list) {
                sb.append(n.toString());
                sb.append("\n");
            }
            alert.setContentText(sb.toString());

            p.removeNode(startingNode);
            p.removeEndingTask();

            alert.showAndWait();
        });

        menuEarlyTimes.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Computed result");
            alert.setHeaderText("Early times result : ");
            StringBuilder sb = new StringBuilder();
            Map<Node, Integer> times = PertGraf.getInstance().computeEarlyTimes();

            for (Map.Entry<Node, Integer> entry : times.entrySet()) {
                sb.append(entry.getKey().toString());
                sb.append(" : ");
                sb.append(entry.getValue().toString());
                sb.append("\n");
            }
            alert.setContentText(sb.toString());

            alert.showAndWait();
        });

        menuLateTimes.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog("10");
            dialog.setTitle("Preparation");
            dialog.setHeaderText("Ending earliest time");
            dialog.setContentText("Enter an ending time from the early times ");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(s -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Computed result");
                alert.setHeaderText("Late times from end result : ");
                StringBuilder sb = new StringBuilder();
                Map<Node, Integer> times = PertGraf.getInstance().computeLateTimesFromEnd(Integer.parseInt(s));

                for (Map.Entry<Node, Integer> entry : times.entrySet()) {
                    sb.append(entry.getKey().toString());
                    sb.append(" : ");
                    sb.append(entry.getValue().toString());
                    sb.append("\n");
                }
                alert.setContentText(sb.toString());

                alert.showAndWait();
            });
        });

        menuCriticalpath.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Computed result");
            alert.setHeaderText("Critical path result : ");
            StringBuilder sb = new StringBuilder();
            for (List<Node> l : PertGraf.getInstance().computeCriticalPaths()) {
                sb.append("Path : \n");
                for (Node n : l) {
                    sb.append(n.toString());
                    sb.append("\n");
                }
            }
            alert.setContentText(sb.toString());

            alert.showAndWait();
        });

        menuListScheduling.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog("1");
            dialog.setTitle("Preparation");
            dialog.setHeaderText("List scheduling");
            dialog.setContentText("Enter a number of workers : ");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(s -> {
                List<String> choices = new ArrayList<>();
                choices.add("Longest path algorithm");
                choices.add("Critical path method");
                choices.add("Longest processing time");
                choices.add("Highest level first algorithm");
                choices.add("HEFT algorithm (Heterogeneous Earliest Finish Time)");

                ChoiceDialog<String> dialogStrategy = new ChoiceDialog<>("Longest path algorithm", choices);
                dialogStrategy.setTitle("Preparation");
                dialogStrategy.setHeaderText("List scheduling");
                dialogStrategy.setContentText("Pick a strategy : ");

                Optional<String> resultStrategy = dialogStrategy.showAndWait();

                resultStrategy.ifPresent(choiceStrategy -> {
                    Map<String, SchedulingStrategies> strategies = new HashMap<>();
                    strategies.put("Longest path algorithm", SchedulingStrategies.LONGEST_PATH);
                    strategies.put("Critical path method", SchedulingStrategies.CRITICAL_PATH);
                    strategies.put("Longest processing time", SchedulingStrategies.LONGEST_PROCESSING_TIME);
                    strategies.put("Highest level first algorithm", SchedulingStrategies.HLF_ALGORITHM);
                    strategies.put("HEFT algorithm (Heterogeneous Earliest Finish Time)", SchedulingStrategies.HEFT_ALGORITHM);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Computed result");
                    alert.setHeaderText("List scheduling result : ");
                    StringBuilder sb = new StringBuilder();
                    for (Node n : PertGraf.getInstance().computeListScheduling(Integer.parseInt(s), strategies.get(choiceStrategy))) {
                        sb.append(n.toString());
                        sb.append("\n");
                    }
                    alert.setContentText(sb.toString());

                    alert.showAndWait();
                });
            });
        });
    }

    private void initializeButtonListeners() {
        displayPertFormat.setOnAction(event -> {
            computeChangesAndSwitch(DisplayType.PERT_FORMAT);
            displayGraf();
        });

        displayDotFormat.setOnAction(event -> {
            computeChangesAndSwitch(DisplayType.DOT_FORMAT);
            displayGraf();
        });

        displayAdjacendyList.setOnAction(event -> {
            computeChangesAndSwitch(DisplayType.ADJACENCY_LIST);
            displayGraf();
        });

        displayAdjacencyMatrix.setOnAction(event -> {
            computeChangesAndSwitch(DisplayType.ADJACENCY_MATRIX);
            displayGraf();
        });

        displaySuccessorArray.setOnAction(event -> {
            computeChangesAndSwitch(DisplayType.SUCCESSOR_ARRAY);
            displayGraf();
        });

        displayReverseGraph.setOnAction(event -> {
            PertGraf.setInstance(PertGraf.getInstance().getReversePert());
            displayGraf();
        });

        class NewNodeInfos {
            private String name;
            private String label;
            private int duration;

            private NewNodeInfos(String name, String label, int duration) {
                this.name = name;
                this.label = label;
                this.duration = duration;
            }
        }

        featureAddNode.setOnAction(event -> {
            Dialog<NewNodeInfos> dialog = new Dialog<>();
            dialog.setTitle("Add a Node");
            dialog.setHeaderText("Please, fill the nodes informations");

            ButtonType confirmButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField nodeName = new TextField();
            nodeName.setPromptText("Task name");
            TextField nodeLabel = new TextField();
            nodeLabel.setPromptText("Task label");
            Spinner<Integer> nodeDuration = new Spinner<>();
            nodeDuration.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-9999, 9999, 0));

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
            result.ifPresent(content -> PertGraf.getInstance().addNode(new Task(content.name, content.label, content.duration)));
        });

        featureAddEdge.setOnAction(event -> {
            final FXMLLoader fxmlLoader = new FXMLLoader();
            FileInputStream fxmlStream;
            try {
                fxmlStream = new FileInputStream("resources/add_edge_dialog.fxml");
                BorderPane rootLayout = fxmlLoader.load(fxmlStream);
                AddEdgeDialogController controller = fxmlLoader.getController();
                Scene scene = new Scene(rootLayout);
                Stage stage = new Stage();
                stage.setTitle("Adding an edge");
                stage.setScene(scene);
                stage.show();
                controller.setObserver(stage);
                controller.setOriginController(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        featureRemoveEdge.setOnAction(event -> {
            final FXMLLoader fxmlLoader = new FXMLLoader();
            FileInputStream fxmlStream;
            try {
                fxmlStream = new FileInputStream("resources/remove_edge_dialog.fxml");
                BorderPane rootLayout = fxmlLoader.load(fxmlStream);
                RemoveEdgeDialogController controller = fxmlLoader.getController();
                Scene scene = new Scene(rootLayout);
                Stage stage = new Stage();
                stage.setTitle("Removing an edge");
                stage.setScene(scene);
                stage.show();
                controller.setObserver(stage);
                controller.setOriginController(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        featureRemoveNode.setOnAction(event -> {
            final FXMLLoader fxmlLoader = new FXMLLoader();
            FileInputStream fxmlStream;
            try {
                fxmlStream = new FileInputStream("resources/remove_node_dialog.fxml");
                BorderPane rootLayout = fxmlLoader.load(fxmlStream);
                RemoveNodeDialogController controller = fxmlLoader.getController();
                Scene scene = new Scene(rootLayout);
                Stage stage = new Stage();
                stage.setTitle("Removing a node");
                stage.setScene(scene);
                stage.show();
                controller.setObserver(stage);
                controller.setOriginController(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private boolean cancelOverwrite() {
        if(!PertGraf.getInstance().adjList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm overwrite");
            alert.setHeaderText("You're about to overwrite the current graph");
            alert.setContentText("Are you sure ?");

            Optional<ButtonType> result = alert.showAndWait();
            return !result.isPresent() || result.get() != ButtonType.OK;
        }

        return false;
    }

    void setObserver(UserInterface u){
        this.userInterface = u;
    }
}