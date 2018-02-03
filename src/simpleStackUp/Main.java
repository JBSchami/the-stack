package simpleStackUp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Simple Stack");
        primaryStage.setScene(new Scene(root, 810, 300));//325 with top bar
        primaryStage.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream("images/Logo.png")));
        primaryStage.show();
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();

    }
    public static void main(String[] args) {
        launch(args);
    }
}