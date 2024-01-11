package com.example.javafxdemo;

import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.util.Random;

public class GameOfLifeThread extends Thread {

    Random random = new Random();
    private GridController gridController;
    private boolean[][] cells;
    private int width;
    private int startHeight;
    private int endHeight;
    private volatile boolean running = true;
    private volatile int sleepTime;
    private Color color;

    public GameOfLifeThread(GridController gridController, boolean[][] cells, int width, int startHeight, int endHeight) {
        this.gridController = gridController;
        this.cells = cells;
        this.width = width;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        color = Color.rgb(
                random.nextInt(200),
                random.nextInt(200),
                random.nextInt(200)
        );
    }

    public void stopThread() {
        running = false;
    }

    public void run() {
        sleepTime = gridController.sleepTime;
        while (running) {
            checkCells();
            Platform.runLater(() -> {
                gridController.drawThread(color, startHeight, endHeight);
            });

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void checkCells() {
        synchronized (cells) {
            for (int i = startHeight; i < endHeight; i++) {
                for (int j = 0; j < width; j++) {
                    int howManyNeighbours = checkNeighbours(i, j);
                    if (!cells[i][j] && howManyNeighbours == 3) {
                        cells[i][j] = true;
                    }

                    if (cells[i][j] && (howManyNeighbours != 2 && howManyNeighbours != 3)) {
                        cells[i][j] = false;
                    }
                }
            }
        }
    }

    private int checkNeighbours(int i, int j) {
        int howManyAreMarked = 0;
        for (int x = i - 1; x <= i + 1; x++) {
            for (int y = j - 1; y <= j + 1; y++) {
                if (x == i && y == j)
                    continue;
                if (x < 0 || y < 0)
                    continue;
                if (x >= cells.length || y >= cells[0].length)
                    continue;

                try {
                    if (cells[x][y]) {
                        howManyAreMarked++;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("error, but we ignore");
                }
            }
        }
        return howManyAreMarked;
    }
}

