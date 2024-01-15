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
    private final int sleepTime;
    private final Color color;
    private volatile boolean running = true;


    public GameOfLifeThread(
            GridController gridController,
            boolean[][] map,
            int width,
            int startHeight,
            int endHeight,
            double sleepTime
    ) {

        this.gridController = gridController;
        this.cells = map;
        this.width = width;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.sleepTime = (int)sleepTime;
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
            for (int row = startHeight; row < endHeight; row++)
                for (int col = 0; col < width; col++) {

                    int howManyNeighbours = checkNeighbours(row, col);

                    if (!cells[row][col] && howManyNeighbours == 3)
                        cells[row][col] = true;

                    if (cells[row][col] && (howManyNeighbours != 2 && howManyNeighbours != 3))
                        cells[row][col] = false;        //overpopulation or loneliness
                }
        }
    }

    private int checkNeighbours(int row, int col) {

        int howManyAreMarked = 0;

        for (int y = row - 1; y <= row+1; y++) {
            for (int x = col - 1; x <= col+1; x++) {

                if (y == row && x == col)
                    continue;
                if (y < 0 || x < 0)
                    continue;
                if (y >= cells.length || x >= cells[0].length)
                    continue;

                try {
                    if (cells[y][x])
                        howManyAreMarked++;
                    if(row == 0)
                        if(cells[cells.length-1][x])
                            howManyAreMarked++;
                    if(col == 0)
                        if(cells[y][cells.length-1])
                            howManyAreMarked++;
                    if(row == cells.length-1)
                        if(cells[0][x])
                            howManyAreMarked++;
                    if(col == cells.length-1)
                        if (cells[y][0])
                            howManyAreMarked++;
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Out of bound in map error");
                }
            }
        }
        return howManyAreMarked;
    }
}