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
    private TextField sizeTextField;
    @FXML
    private TextField numberOfThreadsTextField;
    @FXML
    private ChoiceBox<GridMap> savedGrids;
    @FXML
    Slider sleepTimeSlider;

    ///////// ~controller
    private int squaresPerSide;
    private int squareSize;

    private GridMap gridMap;
    private GameOfLifeThread[] threads;
    private final Color color = Color.BLUE;     //default drawing color

    //?
    public volatile int sleepTime;


    //"siatki.bin"
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ObservableList<GridMap> drawings = FXCollections.observableArrayList();
        if(new FileReaderSaver<ArrayList<GridMap>>("siatki.bin").read() != null)
            drawings.addAll(new FileReaderSaver<ArrayList<GridMap>>("siatki.bin").read());
        savedGrids.setItems(drawings);
    }

    //walidacja danych size
    @FXML
    private void drawMapStart() {
        gridMap = savedGrids.getValue();

        if (gridMap == null) {
            squaresPerSide = Integer.parseInt(sizeTextField.getText());
            setSquareSize();
            gridMap = new GridMap(squaresPerSide, squaresPerSide);
        } else {
            squaresPerSide = gridMap.getHeight();
            setSquareSize();
        }

        drawGrid();
        drawMap();
    }

    //może to powoduje problem z większymi siatkami?
    private void setSquareSize() {
        squareSize = (int) (canvas.getWidth() / squaresPerSide);
    }

    //problem z tym co wyzej
    private void drawGrid() {

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);

        for (int x = 0; x <= canvas.getWidth(); x += squareSize)
            gc.strokeLine(x, 0, x, canvas.getHeight());

        for (int y = 0; y <= canvas.getHeight(); y += squareSize)
            gc.strokeLine(0, y, canvas.getWidth(), y);
    }

    //dziwna lambda
    @FXML
    private void handleCanvasClick() {
        canvas.setOnMouseClicked(event -> {
            int col = (int) (event.getX() / squareSize);
            int row = (int) (event.getY() / squareSize);

            gridMap.setSquare(col, row);
            drawSquare(col, row, color);
        });
    }

    private void drawSquare(int col, int row, Color color) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRect(
                col * squareSize,
                row * squareSize,
                squareSize,
                squareSize
        );
    }

    @FXML
    public void drawMap() {

        canvas.getGraphicsContext2D().clearRect(
               0,
               0,
               canvas.getWidth(),
               canvas.getHeight()
        );

        for (int i = 0; i < gridMap.getHeight(); i++)
            for (int j = 0; j < gridMap.getHeight(); j++)
                if (gridMap.getMap()[j][i])
                    drawSquare(j, i, color);

        drawGrid();
    }

    //prawie to samo co powyzej
    public void drawThread(Color color, int start, int end) {

        canvas.getGraphicsContext2D().clearRect(
                0,
                start* squareSize,
                canvas.getHeight(),
                (end-start)* squareSize
        );

        for (int i = start; i < end; i++)
            for (int j = 0; j < gridMap.getHeight(); j++)
                if (gridMap.getMap()[j][i])
                    drawSquare(j, i, color);

        drawGrid();
    }

    //"siatki.bin", informacje o pomyslnosci zapisu, przekazywanie name
    @FXML
    private void saveDrawing() {

        ArrayList<GridMap> drawings = new ArrayList<>();

        //?
        if (new FileReaderSaver<ArrayList<GridMap>>("siatki.bin").read() != null)
            drawings = new FileReaderSaver<ArrayList<GridMap>>("siatki.bin").read();

        drawings.add(gridMap);
        new FileReaderSaver<>("siatki.bin").save(drawings);
    }

    //walidacja danych threads, ustawia i włącza, problem z dzieleniem nieparzystych wątków, wyłączanie przycisku, bo włączanie się wątków na wątkach
    @FXML
    private void setThreads() {

        int numberOfThreads = Integer.parseInt(numberOfThreadsTextField.getText());

        int scale = squaresPerSide / numberOfThreads;
        int actualHeight = 0;
        int nextHeight = 0;

        threads = new GameOfLifeThread[numberOfThreads];

        for (int i = 0; i < threads.length; i++) {

            if ((nextHeight += scale) > squaresPerSide)
                nextHeight = squaresPerSide;

            threads[i] = new GameOfLifeThread(
                    this,
                    gridMap.getMap(),
                    squaresPerSide,
                    actualHeight,
                    nextHeight
            );
            actualHeight = nextHeight;
        }

        for(Thread thread: threads)
            thread.start();
    }


    //włączanie przycisku setThreads
    @FXML
    private void stopThreads() {
        for(GameOfLifeThread thread: threads)
            thread.stopThread();
    }

    //to chyba niepotrzebne?: Można to zrobić przez EventHandler ze slidera
    @FXML
    private void setSleepTime() {
        this.sleepTime = (int)sleepTimeSlider.getValue();
    }
 }