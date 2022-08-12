package AI.controller.fxmlController;

import AI.opener.FxmlOpener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.stream.IntStream;

public class MapCreatorController extends MainMenuController {
    @FXML
    private AnchorPane mapGrid;
    @FXML
    private TextField rowTxt;
    @FXML
    private TextField colTxt;

    private GridPane mapCreatorGrid = new GridPane();


    private final HashMap<String, Integer> mapCreatorGridData = new HashMap<>();

    //makes sure the grid stays fit and generates
    public void onGenerateBtnPressed() {
        mapGrid.getChildren().clear();
        mapCreatorGrid = new GridPane();
        mapCreatorGrid.setHgap(1);
        mapCreatorGrid.setVgap(1);
        mapCreatorGrid.setGridLinesVisible(true);
        mapCreatorGrid.setBackground(new Background(new BackgroundFill(Color.rgb(220, 220, 220), new CornerRadii(2.5), new Insets(-1.0))));
        int rows = Integer.parseInt(rowTxt.getText());
        int cols = Integer.parseInt(colTxt.getText());
        for (int i = 0; i < cols; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(mapGrid.getWidth() / cols);
            mapCreatorGrid.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < rows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(mapGrid.getHeight() / rows);
            mapCreatorGrid.getRowConstraints().add(rowConst);
        }
        mapCreatorGrid.setPrefSize(mapGrid.getWidth(), mapGrid.getHeight());
        setOnActionGrid(rows, cols);
        mapGrid.getChildren().add(mapCreatorGrid);
    }

    //-1 starting point, 0 barrier, 1 path, 2 finish
    private void setOnActionGrid(int rows, int cols) {
        IntStream.range(1, rows * cols + 1).forEach(e -> mapCreatorGridData.put(String.valueOf(e), 0));
        int count = 1;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                StackPane stack = new StackPane();
                stack.setStyle("-fx-background-color: gray;");
                stack.setId(String.valueOf(count));
                stack.getChildren().add(new Text(String.valueOf(count)));
                stack.setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.isShiftDown()
                            && !mapCreatorGridData.containsValue(2)) {
                        stack.getChildren().clear();
                        stack.setStyle("-fx-background-color: yellow;");
                        stack.getChildren().add(new Text("Finish"));
                        mapCreatorGridData.put(stack.getId(), 2);
                        finish = stack.getId();
                        System.out.println("changed finish");
                    } else if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                        stack.getChildren().clear();
                        stack.setStyle("-fx-background-color: green;");
                        mapCreatorGridData.put(stack.getId(), 1);
                    } else if (mouseEvent.getButton() == MouseButton.SECONDARY && mouseEvent.isShiftDown()
                            && !mapCreatorGridData.containsValue(-1)) {
                        stack.getChildren().clear();
                        stack.setStyle("-fx-background-color: orange;");
                        stack.getChildren().add(new Text("Starting Point"));
                        mapCreatorGridData.put(stack.getId(), -1);
                        startingPoint = stack.getId();
                        System.out.println("changed startingPoint");
                    } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                        stack.getChildren().clear();
                        stack.setStyle("-fx-background-color: gray;");
                        mapCreatorGridData.put(stack.getId(), 0);
                    }
                });
                mapCreatorGrid.add(stack, j, i);
                count++;
            }
        }
    }

    public void save() {
        savedGrid = mapCreatorGrid;
        gridData = mapCreatorGridData;
    }

    public void goBack() {
        FxmlOpener.open("MainMenu.fxml", (Stage) mapGrid.getScene().getWindow());
    }

}
