package com.example.javafxdemo;

import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.util.Random;

public class GameOfLifeThread extends Thread {

    Random random = new Random();
    private final GridController gridController;
    private final boolean[][] cells;
    private final int width;
    private final int startHeight;
    private final int endHeight;

    //volatile? czy będzie konieczne, bo jest czytane w tym samym wątku, ale nie powinno być, bo slider
    private volatile boolean running = true;
    private volatile int sleepTime;
    private final Color color;


    public GameOfLifeThread(
            GridController gridController,
            boolean[][] map,
            int width,
            int startHeight,
            int endHeight
    ) {

        this.gridController = gridController;
        this.cells = map;
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

    //sleepTime, thread nie powinien mieć dostępu do tego pliku fxml, runLater dziwne
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

    //error i czytelnosc i w checkNeighbours
    private void checkCells() {

        synchronized (cells) {
            for (int i = startHeight; i < endHeight; i++) {
                for (int j = 0; j < width; j++) {
                    int howManyNeighbours = checkNeighbours(i, j);
                    if (!cells[i][j] && howManyNeighbours == 3) {
                        cells[i][j] = true;
                    }

                    if (cells[i][j] && (howManyNeighbours != 2 && howManyNeighbours != 3)) {
                        cells[i][j] = false;        //overpopulation or loneliness
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