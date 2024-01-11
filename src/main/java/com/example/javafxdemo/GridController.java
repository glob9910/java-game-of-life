package com.example.javafxdemo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class GridController implements Initializable {

    @FXML
    private Canvas canvas;
    @FXML
    private TextField myTextField;
    @FXML
    private ChoiceBox<GridMap> siatkiWybieralnia;

    private int iloscKwadratowNaBok = 20;
    private int wielkoscSiatki;
    private GridMap gridMap;
    private GameOfLifeThread[] threads;
    private boolean threadsRunning = false;
    int iloscWatkow = 5;
    Color color = Color.BLUE;

    @FXML
    Slider sleepTimeSlider;

    public volatile int sleepTime;

    public void rozpocznijSiatke() {
        rysujGotowaSiatke();

        if (gridMap == null) {
            iloscKwadratowNaBok = Integer.parseInt(myTextField.getText());
            ustawWielkoscKwadratow();
            int columns = iloscKwadratowNaBok;
            int rows = iloscKwadratowNaBok;
            gridMap = new GridMap(columns, rows);
        } else {
            iloscKwadratowNaBok = gridMap.getHeight();
            ustawWielkoscKwadratow();
        }

        drawGrid();
        drawMap();
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<GridMap> mapki = FXCollections.observableArrayList();
        mapki.addAll(new FileReaderSaver<ArrayList<GridMap>>("siatki.bin").read());
        siatkiWybieralnia.setItems(mapki);
    }

    private void ustawWielkoscKwadratow() {
        wielkoscSiatki = (int) (canvas.getWidth() / iloscKwadratowNaBok);
    }

    private void drawGrid() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);

        for (int x = 0; x <= canvas.getWidth(); x += wielkoscSiatki) {
            gc.strokeLine(x, 0, x, canvas.getHeight());
        }

        for (int y = 0; y <= canvas.getHeight(); y += wielkoscSiatki) {
            gc.strokeLine(0, y, canvas.getWidth(), y);
        }
    }

    @FXML
    private void handleCanvasClick() {
        canvas.setOnMouseClicked(event -> {
            int col = (int) (event.getX() / wielkoscSiatki);
            int row = (int) (event.getY() / wielkoscSiatki);

            gridMap.setSquare(col, row);
            drawSquare(col, row, Color.BLUE);
        });
    }

    private void drawSquare(int col, int row, Color color) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRect(col * wielkoscSiatki, row * wielkoscSiatki, wielkoscSiatki, wielkoscSiatki);
    }

    @FXML
    public void drawMap() {
       canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawGrid();
        for (int i = 0; i < gridMap.getHeight(); i++) {
            for (int j = 0; j < gridMap.getHeight(); j++) {
                if (gridMap.getMap()[j][i]) {
                    drawSquare(j, i, color);
                }
            }
        }
    }

    public void drawThread(Color color, int start, int end) {
        canvas.getGraphicsContext2D().clearRect(0,start*wielkoscSiatki,canvas.getHeight(), (end-start)*wielkoscSiatki);

        for (int i = start; i < end; i++) {
            for (int j = 0; j < gridMap.getHeight(); j++) {
                if (gridMap.getMap()[j][i]) {
                    drawSquare(j, i, color);
                }
            }
        }
        drawGrid();
    }

    @FXML
    private void zatwierdzRysunek() {
        ArrayList<GridMap> mapki = new ArrayList<>();
        if (new FileReaderSaver<ArrayList<GridMap>>("siatki.bin").read() != null) {
            mapki = new FileReaderSaver<ArrayList<GridMap>>("siatki.bin").read();
        }
        mapki.add(gridMap);
        new FileReaderSaver<>("siatki.bin").save(mapki);
    }

    private void rysujGotowaSiatke() {
        gridMap = siatkiWybieralnia.getValue();
    }

    @FXML
    private void setThreads() {

        int scale = iloscKwadratowNaBok/iloscWatkow;
        int actualHeight = 0;
        int nextHeight = 0;


            threads = new GameOfLifeThread[iloscWatkow];

            for (int i = 0; i < threads.length; i++) {
                if ((nextHeight += scale) > iloscKwadratowNaBok)
                    nextHeight = iloscKwadratowNaBok;


                threads[i] = new GameOfLifeThread(
                        this,
                        gridMap.getMap(),
                        iloscKwadratowNaBok,
                        actualHeight,
                        nextHeight
                );
                actualHeight = nextHeight;
            }

            for(Thread thread: threads) {
                thread.start();

            }

    }

    @FXML
    private void stopThreads() {
        for(GameOfLifeThread thread: threads) {
            thread.stopThread();
        }
    }

    @FXML
    private void setSleepTime() {
        this.sleepTime = (int)sleepTimeSlider.getValue();
    }
 }
