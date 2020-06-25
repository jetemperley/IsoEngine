import java.io.File;
import java.util.Scanner;

class Noise{
    String pie;

    Noise(){
        try{
        Scanner s = new Scanner(new File("pi.txt"));
        pie = s.next();
        // System.out.println(pie);
        s.close();
        } catch (Exception e){

        }
        
    }
}