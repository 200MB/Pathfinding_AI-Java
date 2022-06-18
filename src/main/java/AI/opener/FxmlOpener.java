package AI.opener;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public final class FxmlOpener {
    public static void open(String fxml, Stage stage) {
        Parent parent = null;
        try {
            parent = FXMLLoader.load(Objects.requireNonNull(FxmlOpener.class.getResource("/%s".formatted(fxml))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
