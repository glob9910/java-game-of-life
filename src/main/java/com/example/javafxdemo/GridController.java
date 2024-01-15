package com.example.javafxdemo;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private TextField pictureName;
    @FXML
    private ChoiceBox<GridMap> savedGrids;
    @FXML
    private Slider sleepTimeSlider;
    @FXML
    private Label errorLabel;
    @FXML
    private Button beginSimulationButton;

    private int squaresPerSide;
    private double squareSize;
    private GridMap gridMap;
    private GameOfLifeThread[] threads;
    private final Color color = Color.BLUE;     //default drawing color

    private final FileReaderSaver<ArrayList<GridMap>> readerSaver;
    private final String dataFileName;

    public GridController(String[] args) {
        readerSaver = new FileReaderSaver<>(args[0]);
        this.dataFileName = args[1];
    }
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setData(dataFileName);

        ArrayList<GridMap> drawings = new ArrayList<>();
        if(readerSaver.read() != null)
            drawings.addAll(readerSaver.read());
        savedGrids.getItems().addAll(drawings);
    }

    private void setData(String dataFile) {

        try {
            String[] data = (Files.readString(Path.of(dataFile))).split("\n");

            sizeTextField.setText(data[0]);
            squaresPerSide = Integer.parseInt(data[0]);


            gridMap = new GridMap(squaresPerSide, squaresPerSide);
            int numberOfLivingSquares = Integer.parseInt(data[3]);

            for(int line = 4; line<4+numberOfLivingSquares; line++) {
                String squareCoordinates[] = data[line].split(" ");
                gridMap.setSquare(
                        Integer.parseInt(squareCoordinates[0]),
                        Integer.parseInt(squareCoordinates[1])
                );
            }

            setSquareSize();
            drawGrid();
            drawMap();
        }
        catch(FileNotFoundException ignored) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void newMap() {

        errorLabel.setText("");

        try {
            if(sizeTextField.getText().isEmpty()) {
                errorLabel.setText("Input a number");
                return;
            }

            squaresPerSide = Integer.parseInt(sizeTextField.getText());
            if(squaresPerSide<=0) {
                errorLabel.setText("Input only positive numbers");
                return;
            }
        } catch(NumberFormatException e) {
            errorLabel.setText("Input only numbers");
        }

        setSquareSize();
        gridMap = new GridMap(squaresPerSide, squaresPerSide);

        drawGrid();
        drawMap();
    }

    @FXML
    private void drawMapStart() {

        gridMap = savedGrids.getValue();
        errorLabel.setText("");

        squaresPerSide = gridMap.getHeight();
        setSquareSize();

        drawGrid();
        drawMap();
    }

    private void setSquareSize() {
        squareSize =  canvas.getWidth()/squaresPerSide;
    }

    private void drawGrid() {

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);

        for (int x = 0; x < squaresPerSide; x++)
            gc.strokeLine(x*squareSize, 0, x*squareSize, squareSize*squaresPerSide);

        for (int y = 0; y < squaresPerSide; y++)
            gc.strokeLine(0, y*squareSize, squareSize*squaresPerSide, y*squareSize);
    }

    @FXML
    private void handleCanvasClick() {

        EventHandler<MouseEvent> handler = event -> {
            int col = (int) (event.getX() / squareSize);
            int row = (int) (event.getY() / squareSize);

            gridMap.setSquare(col, row);
            drawSquare(col, row, color);
        };

        canvas.setOnMouseDragged(handler);
        canvas.setOnMouseClicked(handler);
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

        for (int row = 0; row < gridMap.getHeight(); row++)
            for (int col = 0; col < gridMap.getHeight(); col++)
                if (gridMap.getMap()[col][row])
                    drawSquare(col, row, color);

        drawGrid();
    }

    public void drawThread(Color color, int start, int end) {

        canvas.getGraphicsContext2D().clearRect(
                0,
                start* squareSize,
                canvas.getHeight(),
                (end-start)* squareSize
        );

        for (int row = start; row < end; row++)
            for (int col = 0; col < gridMap.getHeight(); col++)
                if (gridMap.getMap()[col][row])
                    drawSquare(col, row, color);

        drawGrid();
    }

    @FXML
    private void saveDrawing() {

        gridMap.setName(pictureName.getText());
        ArrayList<GridMap> drawings = new ArrayList<>();

        if (readerSaver.read() != null)
            drawings = readerSaver.read();

        savedGrids.getItems().add(gridMap);
        drawings.add(gridMap);
        readerSaver.save(drawings);
        errorLabel.setText("Saved successfully");
    }

    @FXML
    private void setThreads() {

        errorLabel.setText("");

        try {
            if(gridMap == null) {
                errorLabel.setText("Choose a picture");
                return;
            }
            if(numberOfThreadsTextField.getText().isEmpty()) {
                errorLabel.setText("Input number of Threads");
                return;
            }

            int numberOfThreads = Integer.parseInt(numberOfThreadsTextField.getText());
            if(numberOfThreads<=0) {
                errorLabel.setText("Number of Threads must be positive");
                return;
            }

            int scale = squaresPerSide / numberOfThreads;
            int rest = squaresPerSide%numberOfThreads;        // rest from division
            int actualHeight = 0;
            int nextHeight = 0;

            threads = new GameOfLifeThread[numberOfThreads];

            for (int i = 0; i < threads.length; i++) {

                if ((nextHeight += scale) > squaresPerSide)
                    nextHeight = squaresPerSide;
                if(rest>0) {
                    nextHeight++;
                    rest--;
                }

                threads[i] = new GameOfLifeThread(
                        this,
                        gridMap.getMap(),
                        squaresPerSide,
                        actualHeight,
                        nextHeight,
                        sleepTimeSlider.getValue()
                );
                actualHeight = nextHeight;
            }

        } catch (NumberFormatException e) {
            errorLabel.setText("Number of Threads must be a number");
        }

        for(Thread thread: threads)
            thread.start();

        beginSimulationButton.setDisable(true);
    }

    @FXML
    private void stopThreads() {
        if(threads!=null) {
            beginSimulationButton.setDisable(false);

            for(GameOfLifeThread thread: threads)
                thread.stopThread();
        }
    }
 }