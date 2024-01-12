package com.example.javafxdemo;

import java.io.Serializable;

public class GridMap implements Serializable {

    public boolean[][] map;

    //parametr: v
    public String name = "pies";

    public int columns;

    public GridMap(int columns, int rows) {

        this.columns = columns;
        map = new boolean[rows][columns];
        this.name = columns + " " + name;

        for(int i=0; i<rows; i++)
            for(int j=0; j<columns; j++)
                map[i][j] = false;
    }

    public void setSquare(int column, int row) {
        map[column][row] = true;
    }

    public boolean[][] getMap() {
        return map;
    }

    public int getHeight() {
        return columns;
    }

    @Override
    public String toString() {
        return name;
    }
}
