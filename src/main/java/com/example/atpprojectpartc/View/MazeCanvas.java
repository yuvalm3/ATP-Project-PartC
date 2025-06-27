package com.example.atpprojectpartc.View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.List;

public class MazeCanvas extends Canvas {
    private final ObjectProperty<Maze> maze = new SimpleObjectProperty<>(this, "maze");
    private final ObjectProperty<Position> playerPosition = new SimpleObjectProperty<>(this, "position");
    private final ObjectProperty<List<Position>> solutionPath = new SimpleObjectProperty<>(this, "solution");

    public MazeCanvas() {
        widthProperty().addListener(o -> draw());
        heightProperty().addListener(o -> draw());
        maze.addListener((o, oldV, newV) -> draw());
        playerPosition.addListener((o, oldV, newV) -> draw());
        solutionPath.addListener((o, oldV, newV) -> draw());
    }

    public void setMaze(Maze m) {
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

        if (m == null) return;

        int rows = m.getRows(), cols = m.getColumns();
        double cellW = w / cols, cellH = h / rows;

        gc.setFill(Color.BLACK);
        int[][] grid = m.getMazeMatrix();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 1) {
                    gc.fillRect(c * cellW, r * cellH, cellW, cellH);
                }
            }
        }

        List<Position> sol = solutionPath.get();
        if (sol != null) {
            gc.setFill(Color.YELLOW.deriveColor(0, 1, 1, 0.5));
            for (Position p : sol) {
                gc.fillRect(p.getColumnIndex() * cellW,
                        p.getRowIndex() * cellH,
                        cellW, cellH);
            }
        }

        Position pos = playerPosition.get();
        if (pos != null) {
            gc.setFill(Color.BLUE);
            gc.fillOval(pos.getColumnIndex() * cellW,
                    pos.getRowIndex() * cellH,
                    cellW, cellH);
        }

        Position goal = m.getGoalPosition();
        if (goal != null) {
            gc.setFill(Color.GREEN);
            gc.fillOval(goal.getColumnIndex() * cellW,
                    goal.getRowIndex() * cellH,
                    cellW, cellH);
        }
    }
}