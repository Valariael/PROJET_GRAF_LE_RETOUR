import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainMenuController implements Initializable {
    private UserInterface userInterface;
    private static DisplayType selectedDisplayType = DisplayType.DOT_FORMAT;

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

    public MainMenuController() {}

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

    private void displayGraf() {
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

                for(int i = 0; i < m.length; i++) {
                    for(int j = 0; j < m[i].length; j++) {
                        sb.append(m[i][j]);
                        if(j < m[i].length - 1) sb.append(", ");
                    }
                    if(i < m.length - 1) sb.append("\n");
                }

                textAreaDisplayGraph.setText(sb.toString());
                break;
            case SUCCESSOR_ARRAY:
                textAreaDisplayGraph.setText(Arrays.toString(PertGraf.getInstance().getPertSuccessorArray()));
                break;
        }
    }

    private void initializeMenuListeners() {
        menuNewFromDot.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load from DOT file");
            File chosenFile = fileChooser.showOpenDialog(userInterface.primaryStage);
            //TODO error handling
            try {
                PertGraf.setInstance(PertGraf.createFromDotFile(chosenFile.getPath()));
                displayGraf();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        menuNewFromPert.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load from PERT file");
            File chosenFile = fileChooser.showOpenDialog(userInterface.primaryStage);
            //TODO error handling
            try {
                PertGraf.setInstance(PertGraf.createFromPertFile(chosenFile.getPath()));
                displayGraf();
            } catch (FileNotFoundException | InvalidFormatException e) {
                e.printStackTrace();
            }
        });

        menuNewWithRange.setOnAction(event -> {
            //TODO
        });

        menuNewEmpty.setOnAction(event -> {
            //TODO
        });

        menuExportDot.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showSaveDialog(userInterface.primaryStage);

            if (file != null) {
                try {
                    PertGraf.getInstance().toDotFile(file.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error !");
                    alert.setHeaderText(null);
                    alert.setContentText("An error has occured, export aborted.");

                    alert.showAndWait();
                }
            }
        });

        menuExportPng.setOnAction(event -> {
            //TODO
        });

        menuExportPert.setOnAction(event -> {
            //TODO
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
            for(Node n : PertGraf.getInstance().getBFS()) {
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
            for(Node n : PertGraf.getInstance().getDFS()) {
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
            if (result.get() == buttonSave){
                PertGraf.setInstance((PertGraf) PertGraf.getInstance().getTransitiveClosure());
            } else if (result.get() == buttonPNG) {
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
            for(Node n : info.list) {
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

            for(Map.Entry<Node, Integer> entry : times.entrySet()) {
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

                for(Map.Entry<Node, Integer> entry : times.entrySet()) {
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
            for(List<Node> l : PertGraf.getInstance().computeCriticalPaths()) {
                sb.append("Path : \n");
                for(Node n : l) {
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
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Computed result");
                alert.setHeaderText("List scheduling result : ");
                StringBuilder sb = new StringBuilder();
                for(Node n : PertGraf.getInstance().computeListScheduling(Integer.parseInt(s))) {
                    sb.append(n.toString());
                    sb.append("\n");
                }
                alert.setContentText(sb.toString());

                alert.showAndWait();
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
    }

    void setObserver(UserInterface u) {
        this.userInterface = u;
    }
}
