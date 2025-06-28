package com.example.atpprojectpartc.View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;


import java.util.List;

public class MazeCanvas extends Canvas {
    private final ObjectProperty<Maze> maze = new SimpleObjectProperty<>(this, "maze");
    private final ObjectProperty<Position> playerPosition = new SimpleObjectProperty<>(this, "position");
    private final ObjectProperty<List<Position>> solutionPath = new SimpleObjectProperty<>(this, "solution");
    private final Image wallImage = new Image(getClass().getResourceAsStream("/images/wall.png"));
    private final Image playerImage = new Image(getClass().getResourceAsStream("/images/messi.jpg"));
    private final Image backgroundImage = new Image(getClass().getResourceAsStream("/images/mazeBackground.png"));
    private final Image goalImage = new Image(getClass().getResourceAsStream("/images/goal.jpg"));
    private final Image ballImage = new Image(getClass().getResourceAsStream("/images/ball.jpg"));



    public MazeCanvas() {
        widthProperty().addListener(o -> draw());
        heightProperty().addListener(o -> draw());
        maze.addListener((o, oldV, newV) -> draw());
        playerPosition.addListener((o, oldV, newV) -> draw());
        solutionPath.addListener((o, oldV, newV) -> draw());
        setFocusTraversable(true);
    }

    public void setMaze(Maze m) {
        System.out.println("setMaze called with: " + (m != null ? "maze received" : "null"));
        maze.set(m);
    }

    public void setPlayerPosition(Position p) {
        playerPosition.set(p);
    }

    public void setSolutionPath(List<Position> sol) {
        solutionPath.set(sol);
    }

    private void draw() {
        Maze m = maze.get();
        GraphicsContext gc = getGraphicsContext2D();
        double w = getWidth(), h = getHeight();
        gc.clearRect(0, 0, w, h);
        gc.drawImage(backgroundImage, 0, 0, w, h);

        if (m == null) {
            System.out.println("draw(): maze is null");
            return;
        }

        int rows = m.getRows(), cols = m.getColumns();
        int[][] grid = m.getMazeMatrix();
        System.out.println("draw(): maze size = " + rows + "x" + cols);

        double cellW = w / cols, cellH = h / rows;

        gc.setFill(Color.BLACK);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 1) {
                    gc.drawImage(wallImage, c * cellW, r * cellH, cellW, cellH);
                }
            }
        }

        Position pos = playerPosition.get();
        Position goal = m.getGoalPosition();

        // ציור המסלול
        List<Position> path = solutionPath.get();
        if (path != null) {
            for (Position p : path) {
                if (p.equals(pos) || p.equals(goal)) continue;
                gc.drawImage(ballImage,
                        p.getColumnIndex() * cellW,
                        p.getRowIndex() * cellH,
                        cellW, cellH);
            }
        }

        // צייר את השחקן
        if (pos != null) {
            System.out.println("draw(): drawing player at " + pos);
            gc.drawImage(playerImage, pos.getColumnIndex() * cellW,
                    pos.getRowIndex() * cellH,
                    cellW, cellH);
        }

        // צייר את היעד
        if (goal != null) {
            System.out.println("draw(): drawing goal at " + goal);
            gc.drawImage(goalImage, goal.getColumnIndex() * cellW,
                    goal.getRowIndex() * cellH,
                    cellW, cellH);
        }
    }





    public void redraw() {
        draw();
    }
}