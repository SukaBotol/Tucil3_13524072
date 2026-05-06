package stima;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class App extends Application{
    
    // public static void main(String[] args) throws Exception{
    //     // Scanner input = new Scanner(System.in);
    //     // String in_string;
    //     // System.out.println(">> Masukan file input (base dir ./data): ");
    //     // in_string = input.nextLine();
    //     // matrix base = the_io.read_file("./data/"+in_string);
    //     // System.out.println(">> Pilih algoritma: (UCS/GBFS/A*)");
    //     // in_string = input.nextLine();
    //     // long cost=0;
    //     // if(in_string.equals("A*")){
    //     //   
    //     // }
    //     // else if(in_string.equals("UCS")){

    //     // }
    //     // else if(in_string.equals("GBFS")){
    //     //     ArrayList<matrix.direction> path = base.do_gbfs();
    //     //     for(matrix.direction dir : path){
    //     //         System.out.println(dir.toString());
    //     //         cost += base.move(base.simulate_move(dir, base.player, base.visited_numbers));
    //     //         base.print_matrix(0);
    //     //     }
    //     // }

    //     matrix base = the_io.read_file("./data/test_3.txt");
    //     long cost =0;
    //     ArrayList<matrix.direction> path = base.search(2);
    //     for(matrix.direction dir : path){
    //         System.out.println(dir.toString());
    //         cost += base.move(base.simulate_move(dir, base.player, base.visited_numbers).end_pos);
    //         base.print_matrix(0);
    //     }

    //     System.out.println("__________________\nres:\n");
    //     base.print_matrix(0);
    //     System.out.println("cost: "+cost);
    // }

    @Override
    public void start(Stage stage) throws Exception {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Parent root = loader.load();
            controller controll = loader.getController();
            Scene scene = new Scene(root,Color.DIMGRAY);

            scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                
                @Override
                public void handle(KeyEvent event){
                    controll.handle_buttons(event);
                }
            });

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