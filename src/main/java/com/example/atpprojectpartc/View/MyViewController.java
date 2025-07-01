package com.example.atpprojectpartc.View;

import algorithms.mazeGenerators.Maze;
import com.example.atpprojectpartc.ViewModel.MyViewModel;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.List;
import algorithms.search.MazeState;
import java.util.Observable;
import java.util.Observer;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;


public class MyViewController implements IView, Observer {

    @FXML private StackPane centerStack;
    @FXML private MazeCanvas mazeCanvas;
    @FXML private Button btnSaveMaze;
    @FXML private Button btnSolveMaze;
    @FXML private Button btnBackToMenu;

    private MyViewModel vm;

    @FXML
    public void initialize() {
        mazeCanvas.widthProperty().bind(centerStack.widthProperty());
        mazeCanvas.heightProperty().bind(centerStack.heightProperty());
        mazeCanvas.widthProperty().addListener((o, ov, nv) -> mazeCanvas.redraw());
        mazeCanvas.heightProperty().addListener((o, ov, nv) -> mazeCanvas.redraw());

        mazeCanvas.setFocusTraversable(true);
        mazeCanvas.setOnKeyPressed(this::handleKeyPress);
        mazeCanvas.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                Platform.runLater(() -> {
                    if (!btnSolveMaze.isFocused() && !btnSaveMaze.isFocused() && !btnBackToMenu.isFocused()) {
                        mazeCanvas.requestFocus();
                    }
                });
            }
        });
        centerStack.setOnMousePressed(e -> Platform.runLater(() -> mazeCanvas.requestFocus()));

        Platform.runLater(() -> mazeCanvas.requestFocus());
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
            if (parts.length != 2) {
                showError("Invalid input", "Please enter two numbers separated by a comma.");
                return;
            }

            try {
                int rows = Integer.parseInt(parts[0].trim());
                int cols = Integer.parseInt(parts[1].trim());

                if (rows <= 0 || cols <= 0) {
                    showError("Invalid input", "Rows and columns must be positive integers.");
                    return;
                }

            vm.setMazeDimensions(rows, cols);
            vm.generateMaze();
            mainContent.setVisible(false);
            btnSolveMaze.setVisible(true);
            btnSolveMaze.setManaged(true);
            btnSaveMaze.setVisible(true);
            btnSaveMaze.setManaged(true);
            btnBackToMenu.setVisible(true);
            btnBackToMenu.setManaged(true);

            } catch (NumberFormatException e) {
                showError("Invalid input", "Please enter valid integers.");
            }
        });
        Platform.runLater(() -> {
            mazeCanvas.requestFocus();
        });
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML private void onSolveMaze() {
        vm.solveMaze();
        mazeCanvas.requestFocus(); // Focus on canvas after button pressed
    }

    // Present the properties file
    @FXML private void onOptions() {
        try {
            Properties props = new Properties();
            File file = new File("resources/config.properties");
            if (!file.exists()) {
                showAlert("Settings", "File not found: " + file.getAbsolutePath());
                return;
            }

            FileInputStream input = new FileInputStream(file);
            props.load(input);

            StringBuilder content = new StringBuilder();
            for (String key : props.stringPropertyNames()) {
                String value = props.getProperty(key);
                content.append(key).append(" = ").append(value).append("\n");
            }

            showAlert("Application Settings", content.toString());

        } catch (IOException e) {
            showAlert("Error", "Failed to load settings:\n" + e.getMessage());
        }
    }

    @FXML
    private void onHelp() {
        String helpText = """
            Welcome to the Maze Game!

            🎯 Goal:
            Reach the green goal circle using the arrow keys or diagonal keys.

            🎮 Controls:
            - Arrow Keys (↑↓←→): Move up, down, left, right.
            - Diagonal Movement: Use keys 1, 3, 7, 9 or Numpad 1,3,7,9:
              ↖ 1 | ↗ 3
              ↙ 7 | ↘ 9

            📍 Board Markings:
            - Wall image cells = walls (not walkable)
            - Green cells = paths
            - Messi image = your current position
            - Goalpost circle = goal position
            - Soccer Ball path = solution path

            Good luck!
            """;

        showAlert("Maze Game Help", helpText);
    }

    @FXML
    private void onAbout() {
        String aboutText = """
            Maze World Cup Final - About

            👨‍💻 Developers:
            - Yuval - 2nd year Student in SISE, BGU University Inc.
            - Almog - 2nd year Student in SISE, BGU University Inc.

            🔧 Maze Generation Algorithm:
            - Using a tweaked version of Prim's algorithm.
                   * The idea is to start with all walls (1), and slowly carve out paths (0)
                   * in a way that ensures an interesting layout with twists and branches.
            - Supports adjustable dimensions (rows x columns)

            🧠 Maze Solving Algorithm:
            - Breadth-First Search - used to explore or traverse a maze level by level,
              starting from the root node and visiting all neighbors before moving to the next level.
              Uses a queue to keep track of nodes to visit next.
              Guarantees the shortest path.
          
            ⚙️ Technologies Used:
            - JavaFX (UI)
            - Java 15 (Project language level)
            - MVVM architecture with Observer design pattern 

            📁 Resources:
            - External config: /resources/config.properties
            - Images for GUI: /resources/images (png&jpg)
            - Modular design and threading for server-client

            Enjoy solving the maze!
            """;

        showAlert("About Maze Game", aboutText);
    }

    @FXML private void onExit() { Platform.exit(); }

    @FXML
    private void onSaveMaze() {
        Maze maze = vm.getMaze();
        Position playerPos = vm.getCurrentPosition();

        if (maze == null || playerPos == null) {
            showAlert("Save Maze", "No maze to save.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Maze As");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(mazeCanvas.getScene().getWindow());

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                int[][] grid = maze.getMazeMatrix();
                writer.println("Maze:");
                for (int[] row : grid) {
                    for (int cell : row) {
                        writer.print(cell + " ");
                    }
                    writer.println();
                }
                writer.println("Player: " + playerPos.getRowIndex() + "," + playerPos.getColumnIndex());
                showAlert("Success", "Maze saved successfully.");
            } catch (IOException e) {
                showAlert("Error", "Failed to save maze: " + e.getMessage());
            }
        }
    }


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
                    mazeCanvas.requestFocus(); // Request focus again
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
            case DIGIT1, NUMPAD7 -> tryMoveTo(row - 1, col - 1); // ↖
            case DIGIT3, NUMPAD9 -> tryMoveTo(row - 1, col + 1); // ↗
            case DIGIT7, NUMPAD1 -> tryMoveTo(row + 1, col - 1); // ↙
            case DIGIT9, NUMPAD3 -> tryMoveTo(row + 1, col + 1); // ↘
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

        // Check if we're in maze end point
        if (newPos.equals(maze.getGoalPosition())) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Maze Completed!");
                alert.setHeaderText(null);
                alert.setContentText("Messi scored a Goal!!! 🏁");
                alert.showAndWait();
            });
        }
        mazeCanvas.requestFocus();
    }

    public MazeCanvas getMazeCanvas() {
        return mazeCanvas;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void onBackToMenu() {
        // Clean Maze GUI
        mazeCanvas.setMaze(null);
        mazeCanvas.setPlayerPosition(null);
        mazeCanvas.setSolutionPath(null);
        mazeCanvas.redraw();

        // Present Main Menu
        mainContent.setVisible(true);

        btnSolveMaze.setVisible(false);
        btnSolveMaze.setManaged(false);
        btnSaveMaze.setVisible(false);
        btnSaveMaze.setManaged(false);
        btnBackToMenu.setVisible(false);
        btnBackToMenu.setManaged(false);

        // Canvas focus request
        mazeCanvas.requestFocus();
    }
}
