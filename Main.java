package Emulator;

public class Main {
    public static void main(String[] args) throws Exception {
        Decoder decoder = new Decoder("/Users/amiinmohamud/IdeaProjects/2024Projects/src/Emulator/Test/Assembly.txt", "/Users/amiinmohamud/IdeaProjects/2024Projects/src/Emulator/Test/Binary.txt");
        new Pulse();
    }

}