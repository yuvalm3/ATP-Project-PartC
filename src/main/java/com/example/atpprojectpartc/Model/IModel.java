// com/example/atpprojectpartc/model/IModel.java
package com.example.atpprojectpartc.Model;

import java.util.Observable;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;

/**
 * Base model: observable and defines maze API.
 */
public abstract class IModel extends Observable {
        public abstract void generateMaze(int rows, int cols);
        public abstract void solveMaze();
        public abstract Maze getMaze();
        public abstract Solution getSolution();
        public abstract Position getCurrentPosition();
        public abstract void setCurrentPosition(Position pos);
}
