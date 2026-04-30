package stima;

import java.util.ArrayList;

public class App{
    
    public static void main(String[] args) throws Exception{
        // Scanner input = new Scanner(System.in);
        // String in_string;
        // System.out.println(">> Masukan file input (base dir ./data): ");
        // in_string = input.nextLine();
        // matrix base = the_io.read_file("./data/"+in_string);
        // System.out.println(">> Pilih algoritma: (UCS/GBFS/A*)");
        // in_string = input.nextLine();
        // long cost=0;
        // if(in_string.equals("A*")){
        //     System.out.println(">> Pilih Heuristic: (H1/H2/H3)");
        //     in_string = input.nextLine();
        // }
        // else if(in_string.equals("UCS")){

        // }
        // else if(in_string.equals("GBFS")){
        //     ArrayList<matrix.direction> path = base.do_gbfs();
        //     for(matrix.direction dir : path){
        //         System.out.println(dir.toString());
        //         cost += base.move(base.simulate_move(dir, base.player, base.visited_numbers));
        //         base.print_matrix(0);
        //     }
        // }

        matrix base = the_io.read_file("./data/test_2.txt");
        long cost =0;
        ArrayList<matrix.direction> path = base.search(2);
        for(matrix.direction dir : path){
            System.out.println(dir.toString());
            cost += base.move(base.simulate_move(dir, base.player, base.visited_numbers).end_pos);
            base.print_matrix(0);
        }

        System.out.println("__________________\nres:\n");
        base.print_matrix(0);
        System.out.println("cost: "+cost);
    }
    
}