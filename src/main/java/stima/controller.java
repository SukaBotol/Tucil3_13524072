package stima;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class controller implements Initializable{

    @FXML
    private GridPane gridpane;

    @FXML
    private Button pause_button;

    @FXML
    private Button stop_button;

    @FXML
    private Button press_button;

    @FXML
    private Button save_button;

    @FXML
    private Button solve_button;
    
    @FXML
    private Label solution_label;

    @FXML
    private Label solution_text;
    
    @FXML
    private Label cost_label;

    @FXML
    private Label iteration_label;

    @FXML
    private Label time_label;

    @FXML
    private Label shortcuts;

    @FXML
    private Slider speed_slider;

    @FXML
    private ChoiceBox<String> method;
    private String[] choices = {"UCS", "GBFS", "A*"};
    

    String wall_color = "#deddce";
    String normal_color = "#474747";
    String goal_color = "#7bf759";
    String player_color = "#5499de";
    String[] number_colors = {
        "#ff1100", "#ff9900", "#fff700", "#00ff22", "#00ffae", 
        "#0037ff", "#7300ff", "#ff00ea", "#ffffff", "#944646"};


    matrix current;
    matrix.result result = null;
    ArrayList<matrix.position> player_pos_to_end = new ArrayList<>();

    @Override
    public void initialize(URL arg0, ResourceBundle arg1){
        speed_slider.setValue(0);
        method.getItems().addAll(choices);
        save_button.setDisable(true);
        pause_button.setDisable(true);
        stop_button.setDisable(true);
        speed_slider.setDisable(true);
        speed_slider.setShowTickMarks(true);
        speed_slider.setShowTickLabels(true);
        speed_slider.setMajorTickUnit(1);
        speed_slider.setMinorTickCount(0);
        speed_slider.setBlockIncrement(1);
        speed_slider.setSnapToTicks(true);
        shortcuts.setText("Shortcuts: \nJ: Decrease speed\nK: Pause/Play\nL: Increase speed\nM: Stop\n<: Back\n >: Forward");
        method.setValue(choices[0]);
    }

    // file IO
    FileChooser filechooser = new FileChooser();
    File file;
    public void selectFile(ActionEvent e){
        filechooser.setInitialDirectory(new File("../Tucil3_13524072/data"));
        if(board_thread!=null){
            board_thread.interrupt();
        }
        filechooser.setTitle("Open a .txt file");
        filechooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        file = filechooser.showOpenDialog(new Stage());
        if (file==null) return;
        try{
            current = the_i.read_file(file.getPath());
        } catch (Exception r){
            r.printStackTrace();
        }
        speed_slider.setValue(0);
        save_button.setDisable(true);
        pause_button.setDisable(true);
        stop_button.setDisable(true);
        speed_slider.setDisable(true);
        pause_button.setText(" ▶ ");
        cost_label.setText("cost: ");
        iteration_label.setText("total iterations: ");
        time_label.setText("time taken: ");
        solution_label.setText("");
        solution_label.setWrapText(true);

        boarding();
    }


    // WIP
    @FXML
    void save(ActionEvent event){
        filechooser.setInitialDirectory(new File("../Tucil3_13524072/test"));
        filechooser.setTitle("Save to .txt file");
        filechooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = filechooser.showSaveDialog(new Stage());
        filechooser.setTitle("Save to .txt file");
        if(file != null){
            StringBuilder stringgy = new StringBuilder();
            matrix.position temp = null;
            stringgy.append(current.matrix_to_string(temp));
            stringgy.append("----------- Result ----------\n");
            stringgy.append(time_label.getText()+"\n");
            stringgy.append(iteration_label.getText()+"\n\n");

            int i=1;
            for(matrix.State st : result.iterations){
                stringgy.append("Iteration "+i+":\n");
                stringgy.append(current.matrix_to_string(st.pos));
                stringgy.append("\n");
                i++;
            }

            saveSystem(file, stringgy.toString());
        }
    }

    public void saveSystem(File file, String content){
        try {
            if(content.equals(null)){
                throw new IllegalArgumentException("There's nothing in the result Area");
            }
            else {
                PrintWriter writer = new PrintWriter(file);
                writer.write(content);
                writer.close();
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
    }

    // board creation
    public void boarding(){
        solution_text.setText("found solution: ");
        gridpane.getChildren().clear();
        gridpane.getRowConstraints().clear();
        gridpane.getColumnConstraints().clear();
        for(int i=0;i<current.row;i++){
            RowConstraints temprow = new RowConstraints();
            temprow.setPercentHeight(100.0/current.row);
            gridpane.getRowConstraints().add(temprow);
        }
        for(int i=0;i<current.col;i++){
            ColumnConstraints tempcol = new ColumnConstraints();
            tempcol.setPercentWidth(100.0/current.col);
            gridpane.getColumnConstraints().add(tempcol);
        }
        for(int i=0;i<current.row;i++){
            for(int j=0;j<current.col;j++){                
                StackPane cell = new StackPane();
                if(current.player.r==i && current.player.c==j){
                    cell.setBackground(new Background(new BackgroundFill(Color.web(player_color),CornerRadii.EMPTY,Insets.EMPTY)));
                }
                else if(current.c[i][j]=='*'){
                    cell.setBackground(new Background(new BackgroundFill(Color.web(normal_color),CornerRadii.EMPTY,Insets.EMPTY)));
                }
                else if(current.c[i][j]=='0' || current.c[i][j]=='1' || current.c[i][j]=='2' || current.c[i][j]=='3' || current.c[i][j]=='4'
                    || current.c[i][j]=='5' || current.c[i][j]=='6' || current.c[i][j]=='7' || current.c[i][j]=='8' || current.c[i][j]=='9'){
                    cell.setBackground(new Background(new BackgroundFill(Color.web(normal_color),CornerRadii.EMPTY,Insets.EMPTY)));
                    Label number_label = new Label(String.valueOf(current.c[i][j]));
                    number_label.setStyle("-fx-font-size: 20;");
                    number_label.setTextFill(Color.web(number_colors[current.c[i][j]-'0']));
                    cell.getChildren().add(number_label);
                }
                else if(current.c[i][j]=='L'){
                    try {
                        Image lava = new Image(getClass().getResourceAsStream("/lava.png"));
                        BackgroundImage lava_back = new BackgroundImage(lava, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, false));
                        cell.setBackground(new Background(lava_back));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if(current.c[i][j]=='X'){
                    cell.setBackground(new Background(new BackgroundFill(Color.web(wall_color),CornerRadii.EMPTY,Insets.EMPTY)));
                }
                else if(current.c[i][j]=='O'){
                    cell.setBackground(new Background(new BackgroundFill(Color.web(goal_color),CornerRadii.EMPTY,Insets.EMPTY)));
                }
                cell.setStyle("-fx-border-width:1; -fx-border-color:#000000;");
                gridpane.add(cell,j,i);
            }
        }
    }

    // solve
    public void solve(){
        result=null;
        player_pos_to_end.clear();
        current_index=0;
        boarding();
        double start=System.nanoTime();
        if(method.getValue().equals("UCS")){
            result = current.search(0);
        }
        else if(method.getValue().equals("GBFS")){
            result = current.search(1);
        }   
        else if(method.getValue().equals("A*")){
            result = current.search(2);
        }
        
        StringBuilder build = new StringBuilder("");
        if(result.path==null){
            solution_text.setText("found solution: no lmao");
        }
        else{
            cost_label.setText("cost: "+(int)result.cost);
            pause_button.setDisable(false);
            stop_button.setDisable(false);
            speed_slider.setDisable(false);
            matrix.position temp = current.player;
            for(matrix.direction dir : result.path){
                player_pos_to_end.add(current.simulate_move(dir, temp, current.visited_numbers).end_pos);
                temp = current. simulate_move(dir, temp, current.visited_numbers).end_pos;
                switch (dir) {
                    case UP:
                        build.append("U ");
                        break;
                    case DOWN:
                        build.append("D ");
                        break;
                    case LEFT:
                        build.append("L ");
                        break;
                    case RIGHT:
                        build.append("R ");
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        save_button.setDisable(false);
        iteration_label.setText("total iterations: "+result.iterations.size());
        solution_label.setText(build.toString());
        time_label.setText(String.format("time taken: %fms", (System.nanoTime()-start)/1_000_000));
    }

    // ▶
    private volatile int current_index=0;
    private volatile boolean board_running = false;
    private volatile boolean should_stop = false;
    private Thread board_thread = null;
    double prev_speed=0;
    public void handle_pause_play(){
        if(pause_button.getText().equals(" ▶ ")){
            if(speed_slider.getValue()!=0){
                prev_speed=speed_slider.getValue();
            }
            if(prev_speed==0){
                speed_slider.setValue(1);
            }
            else{
                speed_slider.setValue(prev_speed);
            }
            pause_button.setText("⏸");
            if(board_thread==null){
                should_stop = false;
                board_running=true;
                play_result();
            }
            else{
                synchronized (this) {
                    board_running = true;
                    this.notifyAll();
                }
            }
        }
        else{
            prev_speed=speed_slider.getValue();
            speed_slider.setValue(0);
            pause_button.setText(" ▶ ");
            board_running=false;
        }
    }
    public void handle_stop(){
        pause_button.setText(" ▶ ");
        prev_speed=0;
        current_index=0;
        speed_slider.setValue(0);
        board_running = false;
        should_stop = true;
        if(board_thread != null){
            board_thread.interrupt();
        }
        board_thread = null;
        boarding();
    }

    public void play_result(){
        if(player_pos_to_end.isEmpty() || player_pos_to_end==null){return;}
        board_thread=new Thread(() ->{
            while(!should_stop){

                synchronized (this) {
                    while(!board_running && !should_stop){
                        try {
                            this.wait();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                if(current_index<0 || current_index>=result.path.size()){continue;}
                else if(speed_slider.getValue()>0){
                    final matrix.position old_pos = (current_index == 0) ? current.player : player_pos_to_end.get(current_index-1);
                    final matrix.position new_pos = player_pos_to_end.get(current_index);

                    javafx.application.Platform.runLater(()->{
                        update_board(old_pos.r, old_pos.c, normal_color);
                        update_board(new_pos.r, new_pos.c, player_color);
                    });
                    
                    current_index++;
                    if(current_index==result.path.size()){
                        current_index--;
                    }
                    try{
                        long sleepy = (long) (1000/speed_slider.getValue());
                        Thread.sleep(sleepy);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
                else if(speed_slider.getValue()<0){
                    if(current_index>=0){
                        final matrix.position new_pos = (current_index == 0) ? current.player : player_pos_to_end.get(current_index-1);
                        final matrix.position old_pos = player_pos_to_end.get(current_index);
    
                        javafx.application.Platform.runLater(()->{
                            if(old_pos.c==current.goal.c && old_pos.r==current.goal.r){
                                update_board(old_pos.r, old_pos.c, goal_color);
                            }
                            else{
                                update_board(old_pos.r, old_pos.c, normal_color);
                            }
                            update_board(new_pos.r, new_pos.c, player_color);
                        });
                        
                        current_index--;
                    }
                    
                    if(current_index<0){
                        current_index=0;
                    }
                    try{
                        long sleepy = (long) (1000/Math.abs(speed_slider.getValue()));
                        Thread.sleep(sleepy);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        board_thread.start();
    }

    private void update_board(int i, int j, String color){
        gridpane.getChildren().stream()
                .filter(node -> GridPane.getRowIndex(node)==i &&GridPane.getColumnIndex(node)==j)
                .forEach(node -> {
                    StackPane cell = (StackPane) node;
                    cell.setBackground(new Background(new BackgroundFill(Color.web(color), CornerRadii.EMPTY, Insets.EMPTY)));
                });
    }


    public void handle_buttons(KeyEvent event){
        if(!speed_slider.isDisabled()){
            switch(event.getCode()){
                case K:
                    handle_pause_play();
                    break;
                case J:
                    if(speed_slider.getValue()!=-5){
                        speed_slider.setValue(speed_slider.getValue()-1);
                    }
                    break;
                case L:
                    if(speed_slider.getValue()!=5){
                        speed_slider.setValue(speed_slider.getValue()+1);
                    }
                    break;
                case M:
                    handle_stop();
                    break;
                case PERIOD:
                    if(pause_button.getText().equals("⏸")){
                        handle_pause_play();
                    }
                    if(current_index<player_pos_to_end.size()){
                        final matrix.position old_pos = (current_index == 0) ? current.player : player_pos_to_end.get(current_index-1);
                        final matrix.position new_pos = player_pos_to_end.get(current_index);

                        javafx.application.Platform.runLater(()->{
                            update_board(old_pos.r, old_pos.c, normal_color);
                            update_board(new_pos.r, new_pos.c, player_color);
                        });
                        current_index++;
                    }    
                    break;
                case COMMA:
                    if(pause_button.getText().equals("⏸")){
                        handle_pause_play();
                    }
                    if(current_index>0){
                        final matrix.position new_pos = (current_index == 1) ? current.player : player_pos_to_end.get(current_index-2);
                        final matrix.position old_pos = player_pos_to_end.get(current_index-1);
                        
                        javafx.application.Platform.runLater(()->{
                            if(old_pos.c==current.goal.c && old_pos.r==current.goal.r){
                                update_board(old_pos.r, old_pos.c, goal_color);
                            }
                            else{
                                update_board(old_pos.r, old_pos.c, normal_color);
                            }
                            update_board(new_pos.r, new_pos.c, player_color);
                        });
                        current_index--;
                    }
                    break;
                default: break;
            }
        }
    }
}
