package stima;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class App extends Application{
    
    @Override
    public void start(Stage stage) throws Exception {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Image spiki = new Image(getClass().getResourceAsStream("/spiki.png"));
            Parent root = loader.load();
            controller controll = loader.getController();
            Scene scene = new Scene(root,Color.DIMGRAY);

            scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                
                @Override
                public void handle(KeyEvent event){
                    controll.handle_buttons(event);
                }
            });

            stage.getIcons().add(spiki);
            stage.setTitle("ice sliding puzzle solver");    
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch(Exception e){
            e.printStackTrace();
        }
        
    }
    public static void main(String[] args) throws Exception{
        launch(args);
    }
    
}