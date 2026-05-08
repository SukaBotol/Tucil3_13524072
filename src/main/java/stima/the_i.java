package stima;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class the_i {

    public static matrix read_file(String source) throws Exception{
        File file = new File(source);
        matrix res=null;
        try(Scanner scanner = new Scanner(file)){
            String line=scanner.nextLine();
            String[] size = line.split("\\s+");
            if(size.length>2){
                throw new IllegalArgumentException("Too many size parameters\n");
            }else if(size.length<2){
                throw new IllegalArgumentException("How tf am i supposed to build a matrix with this\n");
            }
            int row=Integer.parseInt(size[0]),col=Integer.parseInt(size[1]),i=0;
            res = new matrix(row,col);

            while(scanner.hasNextLine() &&  i<row){
                line = scanner.nextLine();
                for(int j=0;j<col;j++){
                    res.c[i][j]=line.charAt(j);
                }
                i++;
            }
            i=0;
            while(scanner.hasNextLine()){
                line = scanner.nextLine();
                String costs_string[] = line.split("\\s+");
                for(int j=0;j<col;j++){
                    res.cost[i][j]=Integer.parseInt(costs_string[j]);
                }
                i++;
            }
        }
        catch(FileNotFoundException e){
            System.out.println("file not found");
            e.printStackTrace();
        }
        if(res==null){
            throw new IllegalArgumentException("matrix failed to construct\n");
        }
        res.initialize_positions();
        return res;
    }

}
