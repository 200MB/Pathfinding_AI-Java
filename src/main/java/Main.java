import AI.opener.FxmlOpener;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    //todo: reset grid 3 to 1. update if statement for better branch checking
    public static void main(String[] args)  {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        FxmlOpener.open("MapCreator.fxml", new Stage());
    }
}
