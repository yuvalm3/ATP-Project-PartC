package com.example.atpprojectpartc.ViewModel;

import com.example.atpprojectpartc.Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;

import java.util.Observable;
import java.util.Observer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;

/**
 * ViewModel observes the model and re-publishes via Observable.
 */
public class MyViewModel extends Observable implements Observer {
    private final IModel model;

    private final IntegerProperty rows = new SimpleIntegerProperty(this, "rows", 20);
    private final IntegerProperty cols = new SimpleIntegerProperty(this, "cols", 20);

    private final ReadOnlyObjectWrapper<Maze>     maze     =
            new ReadOnlyObjectWrapper<>(this, "maze");
    private final ReadOnlyObjectWrapper<Solution> solution =
            new ReadOnlyObjectWrapper<>(this, "solution");
    private final ReadOnlyObjectWrapper<Position> position =
            new ReadOnlyObjectWrapper<>(this, "position");

    public MyViewModel(IModel model) {
        this.model = model;
        model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o != model) return;
        switch ((String) arg) {
            case "maze":
                maze.set(model.getMaze());
                position.set(model.getCurrentPosition()); // <-- חובה!
                setChanged();
                notifyObservers("maze");
                break;
            case "solution":
                solution.set(model.getSolution());
                setChanged();
                notifyObservers("solution");
                break;
            case "position":
                position.set(model.getCurrentPosition());
                setChanged();
                notifyObservers("position");
                break;
        }
    }

    public void generateMaze() {
        model.generateMaze(rows.get(), cols.get());
    }
    public void solveMaze() {
        model.solveMaze();
    }
    public void moveCharacter(Position next) {
        model.setCurrentPosition(next);
    }

    public IntegerProperty rowsProperty() { return rows; }
    public IntegerProperty colsProperty() { return cols; }
    public ReadOnlyObjectProperty<Maze> mazeProperty()         { return maze.getReadOnlyProperty(); }
    public ReadOnlyObjectProperty<Solution> solutionProperty() { return solution.getReadOnlyProperty(); }
    public ReadOnlyObjectProperty<Position> positionProperty() { return position.getReadOnlyProperty(); }
    public Maze getMaze() {
        return maze.get();
    }

    public Position getCurrentPosition() {
        return position.get();
    }

    public void setMazeDimensions(int rows, int cols) {
        this.rows.set(rows);
        this.cols.set(cols);
    }

}
