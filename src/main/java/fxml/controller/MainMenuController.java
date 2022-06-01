package fxml.controller;


import fxml.directions.Directions;
import fxml.opener.FxmlOpener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {
    @FXML
    private AnchorPane mapGrid;
    @FXML
    private Button createBtn;

    protected static String startingPoint = "0";
    protected static String finish = "0";

    protected static HashMap<String, Integer> gridData = new HashMap<>();

    protected static GridPane savedGrid = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (savedGrid != null) {
            mapGrid.setPrefSize(savedGrid.getWidth(), savedGrid.getHeight());
            mapGrid.getChildren().add(savedGrid);
        }
    }

    public void onCreateBtnPressed() {
        FxmlOpener.open("MapCreator.fxml", (Stage) createBtn.getScene().getWindow());
    }

    public void resetGrid() {
        mapGrid.getChildren().clear();
        gridData = new HashMap<>();
    }

    public void start() {
        int count = 0;
        try {
            String ind = startingPoint;
            while (!Objects.requireNonNull(ind).equalsIgnoreCase(finish)) {
                ind = decideMove(Integer.parseInt(ind));
                System.out.println();
                System.out.println("index At " + ind + "\n");
                count++;
            }
            System.out.println("-----------------------------");
            System.out.println("FOUND");
            System.out.println("TOTAL MOVES: " + count);
        } catch (StackOverflowError e) {
            System.err.println("true recursion level was " + count);
            System.err.println("reported recursion level was " +
                    e.getStackTrace().length);
        }

    }
    //this method is also may be used to tell if user made it impossible for AI to find path (all 4 ways are blocked)

    private String decideMove(int index) {
        int futureInd;
        Directions[] directions = decidePriority(index);
        for (Directions direction : directions) {
            futureInd = move(index, direction);
            if (canMoveTo(futureInd) && !isDeadEnd(futureInd)) {
                System.out.print("WENT " + direction + " ");
                index = futureInd;
                return String.valueOf(index);
            }
        }
        System.out.println("COULDN'T MOVE");
        return null;
    }
    //works as intended might have to rework it soon though

    private Directions[] decidePriority(int index) {
        int[] currInd = getMatrix(index);
        int[] finishMatrix = getMatrix(Integer.parseInt(finish));
        //go down
        if (currInd[0] < finishMatrix[0]) {
            if (currInd[1] > finishMatrix[1]) {
                System.out.println("DECIDER:FIRST OPTION[DOWN]");
                return new Directions[]{Directions.DOWN, Directions.LEFT, Directions.RIGHT, Directions.UP};
            }
            System.out.println("DECIDER:SECOND OPTION[DOWN]");
            return new Directions[]{Directions.DOWN, Directions.RIGHT, Directions.LEFT, Directions.UP};
        } else if (currInd[0] == finishMatrix[0]) {
            if (currInd[1] > finishMatrix[1]) {
                System.out.println("DECIDER:FIRST OPTION[EVEN]");
                return new Directions[]{Directions.LEFT, Directions.UP, Directions.DOWN, Directions.RIGHT};
            }
            System.out.println("DECIDER:SECOND OPTION[EVEN]");
            return new Directions[]{Directions.RIGHT, Directions.UP, Directions.DOWN, Directions.LEFT};
        }
        //go up
        else {
            if (currInd[1] > finishMatrix[1]) {
                System.out.println("DECIDER:FIRST OPTION[UP]");
                return new Directions[]{Directions.UP, Directions.LEFT, Directions.RIGHT, Directions.DOWN};
            }
            System.out.println("DECIDER:SECOND OPTION[UP]");
            return new Directions[]{Directions.UP, Directions.RIGHT, Directions.LEFT, Directions.DOWN};
        }

    }
    //working

    private int getRowLocation(int index) {
        return index % savedGrid.getColumnCount() == 0 ?
                index / savedGrid.getColumnCount() : index / savedGrid.getColumnCount() + 1;
    }
    //working

    private int getColLocation(int index) {
        return index % savedGrid.getColumnCount() == 0 ?
                savedGrid.getColumnCount() : index - (index / savedGrid.getColumnCount()) * savedGrid.getColumnCount();
    }
    //combines #getRowLocation and #GetColLocation

    private int[] getMatrix(int index) {
        int row = getRowLocation(index);
        int col = getColLocation(index);
        return new int[]{row, col};
    }
    //working

    private int move(int currIndex, Directions direction) {
        switch (direction) {
            case UP -> {
                return (currIndex - savedGrid.getColumnCount());
            }
            case DOWN -> {
                return (currIndex + savedGrid.getColumnCount());
            }
            case LEFT -> {
                return currIndex - 1;
            }
            case RIGHT -> {
                return currIndex + 1;
            }
            default -> {
                return -1; //never triggers so has no effect
            }
        }
    }
    //could be buggy

    private boolean canMoveTo(int index) {
        if (index >= gridData.size() || index <= 0) return false;
        return gridData.get(String.valueOf(index)).equals(1) || gridData.get(String.valueOf(index)).equals(2)
                || gridData.get(String.valueOf(index)).equals(-1);
    }
    //could be buggy

    private boolean isDeadEnd(int futureInd) {
        if (String.valueOf(futureInd).equals(startingPoint)) return false;
        if (String.valueOf(futureInd).equals(finish)) return false;
        int count = 0;
        Directions[] directions = decidePriority(futureInd);
        for (Directions direction : directions) {
            int potentialLastIndex = move(futureInd, direction);
            if (canMoveTo(potentialLastIndex)) {
                count++;
            }
        }
        return count <= 1;
    }
}
