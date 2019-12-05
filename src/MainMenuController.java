import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

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
            menuExportPdf,
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

        menuExportPdf.setOnAction(event -> {
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
            //TODO
        });

        menuDFS.setOnAction(event -> {
            //TODO
        });

        menuTransitiveClosure.setOnAction(event -> {
            //TODO
        });

        menuLongestPath.setOnAction(event -> {
            //TODO
        });

        menuEarlyTimes.setOnAction(event -> {
            //TODO
        });

        menuLateTimes.setOnAction(event -> {
            //TODO
        });

        menuCriticalpath.setOnAction(event -> {
            //TODO
        });

        menuListScheduling.setOnAction(event -> {
            //TODO
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
