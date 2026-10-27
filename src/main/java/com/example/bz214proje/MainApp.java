package com.example.bz214proje;

import com.example.bz214proje.controller.SimulationController;
import com.example.bz214proje.model.Direction;
import com.example.bz214proje.model.Position;
import com.example.bz214proje.model.Robot;
import com.example.bz214proje.model.RoomGrid;
import com.example.bz214proje.view.MainView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        RoomGrid grid = new RoomGrid(26, 26);
        Position startPos = new Position(0, 12);

        Robot robot = new Robot(startPos, Direction.NORTH);
        MainView root = new MainView();
        SimulationController controller = new SimulationController(root, grid, robot);

        Scene scene = new Scene(root, 1250, 850);
        stage.setTitle("Robot Süpürge Simülasyonu");
        try {
            stage.getIcons().add(new javafx.scene.image.Image(java.util.Objects.requireNonNull(getClass().getResourceAsStream("/com/example/bz214proje/images/robot.png"))));
        } catch (Exception e) {}
        stage.setScene(scene);
        stage.setResizable(true);

        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}