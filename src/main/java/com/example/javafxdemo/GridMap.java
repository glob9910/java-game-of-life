package com.example.javafxdemo;

import java.io.Serializable;

public class GridMap implements Serializable {

    private final boolean[][] map;
    private String name = "drawing";
    private final int columns;

    public GridMap(int columns, int rows) {

        this.columns = columns;
        map = new boolean[rows][columns];

        for(int i=0; i<rows; i++)
            for(int j=0; j<columns; j++)
                map[i][j] = false;
    }


    public boolean[][] getMap() {
        return map;
    }

    public int getHeight() {
        return columns;
    }

    public void setName(String name) {
        this.name = columns + "x" + columns + " " + name;
    }

    public void setSquare(int column, int row) {
        map[column][row] = true;
    }

    @Override
    public String toString() {
        return name;
    }
}
