package com.example.atpprojectpartc;

import Server.Server;
import com.example.atpprojectpartc.Model.MyModel;
import com.example.atpprojectpartc.View.MyViewController;
import com.example.atpprojectpartc.ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import java.net.InetAddress;

/**
 * Main class that launches the Maze Game JavaFX application.
 * It initializes the servers, loads the main view from FXML,
 * connects the ViewModel to the View and Model, and handles clean shutdown.
 */
public class Main extends Application {

    /**
     * Starts the JavaFX application.
     *
     * @param primaryStage the primary stage for this application.
     * @throws Exception if FXML loading or server initialization fails.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Start servers for maze generation and solving
        Server genServer = new Server(5400, 1000, new
                ServerStrategyGenerateMaze());
        Server solServer = new Server(5401, 1000, new
                ServerStrategySolveSearchProblem());
        genServer.start();
        solServer.start();

        // Load FXML and set up controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/MyView.fxml"));
        Parent root = loader.load();
        MyViewController controller = loader.getController();

        // Set up MVVM architecture
        MyModel model = new MyModel(InetAddress.getLocalHost(), 5400, 5401);
        MyViewModel viewModel = new MyViewModel(model);
        controller.setViewModel(viewModel);

        // Configure scene and stage
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Maze Game");

        // Ensure clean shutdown on window close
        primaryStage.setOnCloseRequest(event -> {
            genServer.stop();
            solServer.stop();
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();
        // Request focus on canvas
        controller.getMazeCanvas().requestFocus();
    }

    /**
     * Entry point of the application.
     *
     * @param args command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
