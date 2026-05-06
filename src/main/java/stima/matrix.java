package stima;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;

// stores everything basically
public class matrix {
    
    public class position {
        int r;
        int c;
        public position(int row, int col){
            this.r=row;
            this.c=col;
        }
    }

    enum direction{
        UP, DOWN, LEFT, RIGHT
    }

    public int row;
    public int col;
    public position player;
    public position goal;
    public int number;
    public ArrayList<Integer> visited_numbers;
    public char[][] c;
    public int [][] cost;

    public matrix(int i, int j){
        if (i<=0 || j<=0){
            throw new IllegalArgumentException("invalid matrix!\n");
        }
        this.row=i;
        this.col=j;
        this.c= new char[i][j];
        this.cost = new int [i][j];
        for(int it=0;it<i;it++){
            for(int jt=0;jt<j;jt++){
                this.c[it][jt] = '\0';
                this.cost[it][jt] = -1;
            }
        }
        this.visited_numbers = new ArrayList<Integer>();
    }

    public void print_matrix(int mode){
        for(int i=0;i<this.row;i++){
            for(int j=0;j<this.col;j++){
                if(mode==0){
                    if(i==this.player.r &&  j ==this.player.c){
                        System.out.print('Z');
                    }
                    else{
                        System.out.print(this.c[i][j]);
                    }
                }
                else if(mode==1){
                    System.out.print(this.cost[i][j]);
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    public void copy(matrix source){
        this.col = source.col;
        this.row = source.row;
        this.c = new char[row][col];
        this.cost = new int[row][col];
        for(int i=0;i<source.row;i++){
            for(int j=0;j<source.col;j++){
                this.c[i][j] = source.c[i][j];
                this.cost[i][j] = source.cost[i][j];
            }
        }
        this.player = new position(source.player.r, source.player.c);
        this.goal = new position(source.goal.r, source.goal.c);
        this.visited_numbers = new ArrayList<>(source.visited_numbers);
    }

    public void initialize_positions(){
        ArrayList<Character> temp = new ArrayList<>();
        for(int i=0;i<this.row;i++){
            for(int j=0;j<this.col;j++){
                if(this.c[i][j]=='O'){
                    this.goal = new position(i, j);
                }
                else if(this.c[i][j]=='Z'){
                    this.player = new position(i,j);
                    this.c[i][j] = '*';
                }
                else if(this.c[i][j]=='0' || this.c[i][j]=='1' || this.c[i][j]=='2' || this.c[i][j]=='3' || this.c[i][j]=='4'
                || this.c[i][j]=='5' || this.c[i][j]=='6' || this.c[i][j]=='7' || this.c[i][j]=='8' || this.c[i][j]=='9'){
                    temp.add(this.c[i][j]);
                }
            }
        }
        number = temp.size();
    }

    private boolean prev_is_in(int current, ArrayList<Integer> visited){
        if(visited.contains(current)){
            return true;
        }
        else{
            if(current==0){
                visited.add(0);
                return true;
            }
            else{
                if(visited.contains(current-1)){
                    visited.add(current);
                }
                else return false;
            }
        }
        return true;
    }

    public long move(position end_pos){
        long cost=0;
        int i=this.player.r,j=this.player.c;
        while(true){
            if(end_pos.r<i){
                i--;
            }
            else if(end_pos.r>i){
                i++;
            }
            else if(end_pos.c<j){
                j--;
            }
            else if(end_pos.c>j){
                j++;
            }
            cost += this.cost[i][j];

            if(i==end_pos.r && j==end_pos.c){
                break;
            }
        }
        this.player.r=i;
        this.player.c=j;
        return cost;
    }

    public class simulate_move_result{
        public position end_pos;
        public long cost;
        public simulate_move_result(position end_pos, long cost){
            this.end_pos=end_pos;
            this.cost=cost;
        }
    }

    public simulate_move_result simulate_move(direction dir, position start, ArrayList<Integer> visited){
        int i=start.r,j=start.c;
        long cost=0;
        while(true){
            if(dir==direction.UP){
                i--;
            }
            else if(dir==direction.DOWN){
                i++;
            }
            else if(dir==direction.LEFT){
                j--;
            }
            else if(dir==direction.RIGHT){
                j++;
            }
            if(i==this.row || i<0 || j==this.col || j<0 || this.c[i][j]=='L'){
                return null;
            }
            if(this.c[i][j]=='*' || this.c[i][j]=='O'){
                cost+=this.cost[i][j];
            }
            else if(this.c[i][j]=='0' || this.c[i][j]=='1' || this.c[i][j]=='2' || this.c[i][j]=='3' || this.c[i][j]=='4'
                || this.c[i][j]=='5' || this.c[i][j]=='6' || this.c[i][j]=='7' || this.c[i][j]=='8' || this.c[i][j]=='9'){
                    if(!prev_is_in(Character.getNumericValue(this.c[i][j]), visited)){
                        return null;
                    }
                    else cost+=this.cost[i][j];
                }
            else if(this.c[i][j]=='X'){
                if(dir==direction.UP){
                    i++;
                }
                else if(dir==direction.DOWN){
                    i--;
                }
                else if(dir==direction.LEFT){
                    j++;
                }
                else if(dir==direction.RIGHT){
                    j--;
                }
                break;
            }
        }
        return new simulate_move_result(new position(i, j), cost);
    }

    public class State{
        public position pos;
        public ArrayList<direction> path;
        public ArrayList<Integer> visited;
        public double cost;

        public State(position pos, ArrayList<direction> path, ArrayList<Integer> visited, double cost){
            this.pos=pos;
            this.path=path;
            this.visited=visited;
            this.cost=cost;
        }
        public String getKey(){
            return pos.r + "," + pos.c + "," + visited.toString(); 
        }
    }

    private double calculate_line_distance(int r1, int c1){
        return Math.sqrt(Math.pow(r1-this.goal.r, 2) + Math.pow(c1-this.goal.c, 2));
    }

    public class result{
        public int iterations;
        public ArrayList<direction> path;
        public double cost;

        public result(int iter, ArrayList<direction> path, double cost){
            this.iterations=iter;
            this.path=path;
            this.cost=cost;
        }
    }

    public result search(int mode){   // mode: algorithm
        HashSet<String> set = new HashSet<>(); // so we dont overlap/redo paths
        PriorityQueue<State> que = new PriorityQueue<>();
        int iter=0;
        // ucs
        if(mode==0){
            que = new PriorityQueue<>((a,b) -> Double.compare(a.cost, b.cost));
        }
        //gbfs
        else if(mode==1){
            que = new PriorityQueue<>((a,b) -> Double.compare(calculate_line_distance(a.pos.r, a.pos.c), calculate_line_distance(b.pos.r, b.pos.c)));
        }
        // a*
        else if(mode==2){
            que = new PriorityQueue<>((a,b) -> Double.compare(calculate_line_distance(a.pos.r, a.pos.c)+a.cost, calculate_line_distance(b.pos.r, b.pos.c)+b.cost));
        }
        
        State start = new State(new position(this.player.r, this.player.c), new ArrayList<>(), new ArrayList<>(),0);
        que.add(start);
        while(!que.isEmpty()){
            iter++;
            State current = que.poll();
            if(set.contains(current.getKey())) continue;
            if(current.pos.r==this.goal.r && current.pos.c==this.goal.c && current.visited.size()==number){
                return new result(iter, current.path, current.cost);
            }

            set.add(current.getKey());

            for(direction dir : direction.values()){
                ArrayList<Integer> copy_visited = new ArrayList<>(current.visited);
                simulate_move_result result = simulate_move(dir, current.pos, copy_visited);
                if(result==null) continue;

                ArrayList<direction> new_path = new ArrayList<>(current.path);
                new_path.add(dir);
                State next_state = new State(result.end_pos, new_path, copy_visited, current.cost+result.cost);

                if(!set.contains(next_state.getKey())){
                    que.add(next_state);
                }

            }
        }

        return null; // not found
    }
}
