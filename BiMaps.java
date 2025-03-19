package Emulator;

import java.util.HashMap;

/**
 *  This class is used for a multi-directional hashmaps, it used to store the opcode and return the binary equivalent, and vis-versa
 */
public class BiMaps {
    HashMap<String, String> map;
    HashMap<String, String> reverse;
    public BiMaps(){
        map = new HashMap<>();
        reverse = new HashMap<>();
    }

    /**
     * Methods for inserting opcode and binary into both hashs
     * @param opcode: The first few binary bits that tell you the operations
     * @param binary: The binary equivalent to opcode, the one and zeros and computer performs
     */
    public void insert(String opcode, String binary){
        map.put(opcode, binary);
        reverse.put(binary, opcode);
    }

    /**
     * The methods for getting the binary after inputting the opcode
     * @param opcode: The first few binary bits that tell you the operations
     * @return: The binary equivalent to opcode, the one and zeros and computer performs
     */
    public String getBin(String opcode){
        return map.get(opcode);
    }

    /**
     * The method for getting the opcode after inputting the binary
     * @param binary: The binary equivalent to opcode, the one and zeros and computer performs
     * @return: The first few binary bits that tell you the operations
     */
    public String getOpc(String binary){
        return reverse.get(binary);
    }
}
