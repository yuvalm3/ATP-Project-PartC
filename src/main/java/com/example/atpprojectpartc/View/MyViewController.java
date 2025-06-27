package com.example.atpprojectpartc.View;

import com.example.atpprojectpartc.ViewModel.MyViewModel;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import java.util.stream.Collectors;
import java.util.List;
import algorithms.search.MazeState;
import java.util.Observable;
import java.util.Observer;

public class MyViewController implements IView, Observer {

    @FXML private StackPane centerStack;
    @FXML private MazeCanvas mazeCanvas;
    @FXML private Button btnNewGame;
    @FXML private Button btnSaveGame;
    @FXML private Button btnOptions;
    @FXML private Button btnHelp;
    @FXML private Button btnAbout;
    @FXML private Button btnExit;

    private MyViewModel vm;

    @FXML
    public void initialize() {
        btnSaveGame.setDisable(true);
        mazeCanvas.widthProperty().bind(centerStack.widthProperty());
        mazeCanvas.heightProperty().bind(centerStack.heightProperty());
    }

    @Override
    public void setViewModel(MyViewModel vm) {
        if (this.vm != null) {
            this.vm.deleteObserver(this);
        }
        this.vm = vm;
        vm.addObserver(this);
    }

    @FXML private void onNewGame() { vm.generateMaze(); btnSaveGame.setDisable(false); }
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
                Platform.runLater(() -> {
                    mazeCanvas.setMaze(vm.mazeProperty().get());
                    mazeCanvas.setPlayerPosition(vm.positionProperty().get());
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
                Platform.runLater(() -> mazeCanvas.setPlayerPosition(vm.positionProperty().get()));
                break;
        }
    }
}
