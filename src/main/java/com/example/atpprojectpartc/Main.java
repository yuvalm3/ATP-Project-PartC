package com.example.atpprojectpartc;

import Server.Server;
import com.example.atpprojectpartc.Model.MyModel;
import com.example.atpprojectpartc.View.MyViewController;
import com.example.atpprojectpartc.ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;

import java.net.InetAddress;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Server genServer = new Server(5400, 1000, new
                ServerStrategyGenerateMaze());
        Server solServer = new Server(5401, 1000, new
                ServerStrategySolveSearchProblem());
        genServer.start();
        solServer.start();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/MyView.fxml"));
        Parent root = loader.load();

        MyViewController controller = loader.getController();

        MyModel model = new MyModel(InetAddress.getLocalHost(), 5400, 5401);
        MyViewModel viewModel = new MyViewModel(model);

        controller.setViewModel(viewModel);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        scene.setOnKeyPressed(event -> {
            controller.handleKeyPress(event);
        });

        primaryStage.setTitle("Maze Game");
        primaryStage.show();

        controller.getMazeCanvas().requestFocus(); // 👈 חשוב מאוד
    }

    public static void main(String[] args) {
        launch(args);
    }
}
