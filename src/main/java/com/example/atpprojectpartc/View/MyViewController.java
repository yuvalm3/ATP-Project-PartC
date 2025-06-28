package com.example.atpprojectpartc.View;

import algorithms.mazeGenerators.Maze;
import com.example.atpprojectpartc.ViewModel.MyViewModel;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import java.util.stream.Collectors;
import java.util.List;
import algorithms.search.MazeState;
import java.util.Observable;
import java.util.Observer;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;


public class MyViewController implements IView, Observer {

    @FXML private StackPane centerStack;
    @FXML private MazeCanvas mazeCanvas;
    @FXML private Button btnNewGame;
    @FXML private Button btnSaveGame;
    @FXML private Button btnOptions;
    @FXML private Button btnHelp;
    @FXML private Button btnAbout;
    @FXML private Button btnExit;
    @FXML private TextField txtRows;
    @FXML private TextField txtCols;
    @FXML private Button btnSolveMaze;



    private MyViewModel vm;

    @FXML
    public void initialize() {
        btnSaveGame.setDisable(true);

        // קישור גודל הקנבס לגודל הפנימי של ה-StackPane
        mazeCanvas.widthProperty().bind(centerStack.widthProperty());
        mazeCanvas.heightProperty().bind(centerStack.heightProperty());

        // בכל שינוי גודל – לצייר מחדש
        mazeCanvas.widthProperty().addListener((obs, oldVal, newVal) -> mazeCanvas.redraw());
        mazeCanvas.heightProperty().addListener((obs, oldVal, newVal) -> mazeCanvas.redraw());
    }


    @Override
    public void setViewModel(MyViewModel vm) {
        if (this.vm != null) {
            this.vm.deleteObserver(this);
        }
        this.vm = vm;
        vm.addObserver(this);
    }

    @FXML private GridPane mainContent;

    @FXML
    private void onNewGame() {
        TextInputDialog inputDialog = new TextInputDialog("20,20");
        inputDialog.setTitle("New Maze");
        inputDialog.setHeaderText("Enter maze size");
        inputDialog.setContentText("Rows,Columns:");

        inputDialog.showAndWait().ifPresent(input -> {
            String[] parts = input.split(",");
            int rows = Integer.parseInt(parts[0].trim());
            int cols = Integer.parseInt(parts[1].trim());

            vm.setMazeDimensions(rows, cols);
            vm.generateMaze();
            btnSaveGame.setDisable(false);
            mainContent.setVisible(false); // הסתרת התפריט
            btnSolveMaze.setVisible(true);
            btnSolveMaze.setManaged(true);
        });
        mazeCanvas.requestFocus();
    }

    @FXML private void onSolveMaze() {
        vm.solveMaze();
        mazeCanvas.requestFocus(); // 👈 מחזיר את הפוקוס לקנבס אחרי לחיצה על כפתור
    }


    @FXML private void onSaveGame() { /* TODO */ }
    @FXML private void onOptions() { /* TODO */ }
    @FXML private void onHelp() { /* TODO */ }
    @FXML private void onAbout() { /* TODO */ }
    @FXML private void onExit() { Platform.exit(); }

    @Override
    public void update(Observable o, Object arg) {
        if (vm == null) return;
        String evt = (String) arg;

        switch (evt) {
            case "maze":
                Maze maze = vm.getMaze();
                Position pos = vm.getCurrentPosition();
                Platform.runLater(() -> {
                    mazeCanvas.setMaze(maze);
                    mazeCanvas.setPlayerPosition(pos);
                    mazeCanvas.requestFocus(); // 👈 כאן נדאג שיקבל פוקוס אחרי ציור מבוך
                });
                break;

            case "solution":
                Platform.runLater(() -> {
                    Solution sol = vm.solutionProperty().get();
                    List<Position> path = sol == null ? null : sol.getSolutionPath().stream()
                            .map(state -> ((MazeState)state).getPosition())
                            .collect(Collectors.toList());
                    mazeCanvas.setSolutionPath(path);
                });
                break;

            case "position":
                Platform.runLater(() -> {
                    mazeCanvas.setPlayerPosition(vm.getCurrentPosition());
                });
                mazeCanvas.requestFocus();
                break;
        }
    }

    public void handleKeyPress(KeyEvent event) {
        if (vm == null) return;

        Position current = vm.getCurrentPosition();
        int row = current.getRowIndex();
        int col = current.getColumnIndex();

        switch (event.getCode()) {
            case UP -> tryMoveTo(row - 1, col);
            case DOWN -> tryMoveTo(row + 1, col);
            case LEFT -> tryMoveTo(row, col - 1);
            case RIGHT -> tryMoveTo(row, col + 1);
        }
    }

    private void tryMoveTo(int newRow, int newCol) {
        Maze maze = vm.getMaze();
        if (maze == null) return;

        int[][] grid = maze.getMazeMatrix();
        if (newRow < 0 || newRow >= grid.length || newCol < 0 || newCol >= grid[0].length)
            return;

        if (grid[newRow][newCol] == 1) return; // קיר

        Position newPos = new Position(newRow, newCol);
        vm.moveCharacter(newPos);

        // בדיקה אם הגענו ליעד
        if (newPos.equals(maze.getGoalPosition())) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Maze Completed!");
                alert.setHeaderText(null);
                alert.setContentText("Goal!!! 🏁");
                alert.showAndWait();
            });
        }
        mazeCanvas.requestFocus();
    }

    public MazeCanvas getMazeCanvas() {
        return mazeCanvas;
    }



}
