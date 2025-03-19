package Emulator;

public class AssemblerMain{
    public static void main(String[]args) throws Exception{
       if(args.length != 2){
           System.out.println("Requires two arguments");
           System.out.println("Assembly input file and output file names");
           throw new Exception("Argument Mismatch");
       }
       else{
           new Decoder(args[0], args[1]);
       }
    }
}
