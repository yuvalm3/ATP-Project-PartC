// com/example/atpprojectpartc/model/MyModel.java
package com.example.atpprojectpartc.Model;

import IO.MyDecompressorInputStream;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;

import java.io.*;
import java.net.InetAddress;

import Client.Client;
import Client.IClientStrategy;

/**
 * Concrete model: loads/saves via sockets, then notifies observers.
 */
public class MyModel extends IModel {
    private Maze maze;
    private Position currentPosition;
    private Solution solution;

    private final InetAddress serverIP;
    private int GENERATE_PORT = 5400;
    private int SOLVE_PORT = 5401;

    public MyModel(InetAddress serverIP, int generatePort, int solvePort) {
        this.serverIP = serverIP;
        this.GENERATE_PORT = generatePort;
        this.SOLVE_PORT = solvePort;
    }

    @Override
    public void generateMaze(int rows, int cols) {
        // holder for the compressed bytes returned by the server
        final byte[][] compressedHolder = new byte[1][];

        // define the strategy that speaks the maze‐generation protocol
        IClientStrategy strat = (inRaw, outRaw) -> {
            try {
                // open streams
                ObjectOutputStream oos = new ObjectOutputStream(outRaw);
                oos.flush(); // send header
                ObjectInputStream ois = new ObjectInputStream(inRaw);

                // send dimensions
                oos.writeObject(new int[]{rows, cols});
                oos.flush();

                // read back compressed maze bytes
                compressedHolder[0] = (byte[]) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("Error communicating with generator server", e);
            }
        };

        // invoke the client
        Client client = new Client(serverIP, GENERATE_PORT, strat);
        client.communicateWithServer();

        // decompress the returned bytes into the raw maze
        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressedHolder[0]);
             MyDecompressorInputStream mdin = new MyDecompressorInputStream(bais);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = mdin.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            byte[] rawMaze = baos.toByteArray();

            // reconstruct Maze + reset solution + set start position
            this.maze = new Maze(rawMaze);
            this.currentPosition = this.maze.getStartPosition();
            this.solution = null;

            // notify observers that a new maze is available
            setChanged();
            notifyObservers("maze");

        } catch (IOException e) {
            throw new RuntimeException("Error decompressing maze", e);
        }
    }

    @Override
    public void solveMaze() {
        if (maze == null) return;

        // holder for the Solution object returned by the server
        final Solution[] solutionHolder = new Solution[1];

        // define the strategy that speaks the maze‐solving protocol
        IClientStrategy strat = (inRaw, outRaw) -> {
            try {
                // open streams
                ObjectOutputStream oos = new ObjectOutputStream(outRaw);
                oos.flush();
                ObjectInputStream ois = new ObjectInputStream(inRaw);

                // send current maze
                oos.writeObject(this.maze);
                oos.flush();

                // read back Solution
                solutionHolder[0] = (Solution) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("Error communicating with solver server", e);
            }
        };

        // invoke the client
        Client client = new Client(serverIP, SOLVE_PORT, strat);
        client.communicateWithServer();

        // store solution and notify
        this.solution = solutionHolder[0];

        setChanged();
        notifyObservers("solution");
    }



    @Override public Maze getMaze()                { return maze; }
    @Override public Solution getSolution()        { return solution; }
    @Override public Position getCurrentPosition() { return currentPosition; }
    @Override
    public void setCurrentPosition(Position pos) {
        this.currentPosition = pos;
        setChanged();
        notifyObservers("position");
    }
}
