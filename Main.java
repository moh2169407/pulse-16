package Emulator;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("One argument required");
            System.out.println("Binary file path");
            throw new Exception("File mismatch");
        }
        else{
            new Pulse(args[0]);
        }

    }
}