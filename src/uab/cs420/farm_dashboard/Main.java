package uab.cs420.farm_dashboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();

        Controller controller = Controller.getInstance();
        loader.setController(controller);
        controller.setMain(this);

        loader.setLocation(getClass().getResource("farm-dashboard.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Farm Dashboard");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}

