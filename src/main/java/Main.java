import fxml.opener.FxmlOpener;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args)  {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        FxmlOpener.open("MapCreator.fxml", new Stage());
    }
}
