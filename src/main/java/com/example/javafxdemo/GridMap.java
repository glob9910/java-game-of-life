package com.example.javafxdemo;

import java.io.Serializable;

public class GridMap implements Serializable {

    public boolean[][] map;
    public String name;
    public int columns;

    public GridMap(int columns, int rows) {
        map = new boolean[columns][rows];

        for(int i=0; i<columns; i++)
            for(int j=0; j<rows; j++)
                map[i][j] = false;

        name = columns + "pies";
        this.columns = columns;
    }

    public void setSquare(int column, int row) {
        map[column][row] = true;
    }

    public void setCells(boolean[][] cells) {
        map = cells;
    }

    public boolean[][] getMap() {
        return map;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getHeight() {
        return columns;
    }
}
