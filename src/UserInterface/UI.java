/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UserInterface;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTabPane;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sun.applet.Main;

/**
 *
 * @author gbjohnson
 */
public class UI extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        //*
        JFXTabPane tabPane = new JFXTabPane();
        tabPane.setPrefSize(600, 800);

        Tab homeTab = new Tab();
        homeTab.setText("Home");
        VBox homeBox = new VBox();
        homeBox.getChildren().addAll(
                new JFXCheckBox("Button 1"),
                new JFXCheckBox("Button 2"),
                new JFXCheckBox("Button 3"));
        homeBox.setSpacing(30.0);
        homeBox.setManaged(false);
        homeBox.setLayoutY(10.0);
        homeTab.setContent(homeBox);
        
        tabPane.getTabs().add(homeTab);

        Tab editTab = new Tab();
        editTab.setText("Edit Content");
        editTab.setContent(new Label("Content"));
        tabPane.getTabs().add(editTab);

        Tab changesTab = new Tab();
        changesTab.setText("Write Changes");
        changesTab.setContent(new Label("Content"));
        tabPane.getTabs().add(changesTab);

        StackPane root = new StackPane();
        root.getChildren().add(tabPane);

        Scene scene = new Scene(root, 600, 800);

        primaryStage.setTitle("Course Tool");
        primaryStage.setScene(scene);
        primaryStage.show();
        /*/
        
            StackPane page = (StackPane) FXMLLoader.load(Main.class.getResource("FXML.fxml"));
            Scene scene = new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.setTitle("FXML is Simple");
            primaryStage.show();
        //*/
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
