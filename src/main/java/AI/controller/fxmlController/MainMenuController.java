package AI.controller.fxmlController;


import AI.controller.threadController.BranchThread;
import AI.directions.Directions;
import AI.opener.FxmlOpener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class MainMenuController implements Initializable {
    @FXML
    private AnchorPane mapGrid;
    @FXML
    private Button createBtn;

    protected static String startingPoint = "0";
    protected static String finish = "0";

    protected static HashMap<String, Integer> gridData = new HashMap<>(); //contains all the IDs

    protected static GridPane savedGrid = null; //instance of gridPane

    protected ArrayList<ArrayList<Integer>> paths = new ArrayList<>();

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
        removeTrails();

        int count = 0;
        try {
            Directions[] branches = getAllAvailable4Bit(Integer.parseInt(startingPoint));
            for (Directions direction : branches) { //create parent threads to branch off starting point
                if (direction == null) continue;
                BranchThread branch = new BranchThread();
                branch.setDirection(direction);
                branch.setIndex(move(Integer.parseInt(startingPoint), direction));
                Thread branchThread = new Thread(branch);
                branchThread.start();
            }
        } catch (StackOverflowError e) {
            System.err.println("true recursion level was " + count);
            System.err.println("reported recursion level was " +
                    e.getStackTrace().length);
        }

    }

    //3 means trailed. another thread already passed it.
    protected Directions[] getAllAvailable4Bit(int index) {
        Directions[] available = new Directions[4];
        int count = 0;
        for (Directions direction : Directions.values()) {
            if (isGreen(move(index, direction)) && !gridData.get(String.valueOf(index)).equals(3)) {
                available[count] = direction;
                count++;
            }
        }
        return available;
    }

    protected int getRowLocation(int index) {
        return index % savedGrid.getColumnCount() == 0 ?
                index / savedGrid.getColumnCount() : index / savedGrid.getColumnCount() + 1;
    }
    //working

    protected int getColLocation(int index) {
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

    protected int move(int currIndex, Directions direction) {
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

    protected boolean canMoveTo(int index) {
        if (index >= gridData.size() || index <= 0) return false;
        return gridData.get(String.valueOf(index)).equals(1) || gridData.get(String.valueOf(index)).equals(2)
                || gridData.get(String.valueOf(index)).equals(-1);
    }

    protected boolean isGreen(int index) {
        if (index >= gridData.size() || index <= 0) return false;
        return gridData.get(String.valueOf(index)).equals(1) ||gridData.get(String.valueOf(index)).equals(2);
    }

    protected void showPath(ArrayList<Integer> path) {
        for (Integer i : path) {
            savedGrid.getChildren().get(i).setStyle("-fx-background-color: red;");
        }
    }

    protected void displayShortest() {
        showPath(paths.stream().sorted(Comparator
                .comparing(ArrayList::size)).collect(Collectors.toList()).get(0));
    }

    private void removeTrails() {
        for (int i = 1; i < gridData.size(); i++) {
            if (gridData.get(String.valueOf(i)).equals(3)) gridData.put(String.valueOf(i),1);
        }
    }

    protected boolean isFinish(int index) {
        return gridData.get(String.valueOf(index)).equals(2);
    }

    protected void setTrailMark(int index) {
        gridData.put((String.valueOf(index)), 3);
    }


}
