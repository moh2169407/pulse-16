package Emulator;

import java.util.HashMap;

public class ISA {
    private final BiMaps registers;         // The multi-directional hashmap containing the registers
    private final BiMaps instr;             // The multi-directional hashmap containing the instruction
    private final HashMap<String, String> types;    // The hashmap that contains the
    int line;                                       // Contain the line being read, used for debugging
    BiMaps stack;                               //  The multi-directional hashmap containing the stack
    BiMaps sysc;                                //  The multi-directional hashmap containing the syscall instructions

    /**
     * Fills the class with constant that used to read instruction
     * @param line
     */
    public ISA(int line) {
        this.line = line;
        // Filling in the hash with the registers name and their binary values
        registers = new BiMaps();
        registers.insert("$r0", "000");
        registers.insert("$r1", "001");
        registers.insert("$r2", "010");
        registers.insert("$r3", "011");
        registers.insert("$r4", "100");
        registers.insert("$r5", "101");
        registers.insert("$r6", "110");
        registers.insert("$r7", "111");

        // Filling the hash with instruction name and their binary values
        instr = new BiMaps();
        // R-Type
        instr.insert("ADD", "0000");
        instr.insert("SUB", "0001");
        instr.insert("AND", "0010");
        instr.insert("OR", "0011");
        instr.insert("MUL", "0100");
        instr.insert("SLT", "0101");
        instr.insert("SLL", "0110");
        instr.insert("SRL", "0111");

        // I-Type
        instr.insert("ADDI", "1000");
        instr.insert("ANDI", "1001");
        instr.insert("ORI", "1010");
        instr.insert("LW", "1011");
        instr.insert("SW", "1100");

        // B-Type
        instr.insert("BEQ", "1101");
        instr.insert("BNE", "1110");

        // S-Type (distinguish via function fields)
        instr.insert("PUSH", "1111");
        instr.insert("POP", "1111");
        instr.insert("CALL", "1111");
        instr.insert("RET", "1111");


        // SYS-Type
        instr.insert("HALT", "1111");
        instr.insert("PRINT", "1111");
        instr.insert("MALLOC", "1111");
        instr.insert("DEALLOC", "1111");

        // Filling in the hash with the different types of instructions

        types = new HashMap<>();
        // R-Type
        types.put("ADD", "R");
        types.put("SUB", "R");
        types.put("AND", "R");
        types.put("OR", "R");
        types.put("MUL", "R");
        types.put("SLT", "R");
        types.put("SLL", "R");
        types.put("SRL", "R");

        // I-Type
        types.put("ADDI", "I");
        types.put("ANDI", "I");
        types.put("ORI", "I");
        types.put("LW", "I");
        types.put("SW", "I");

        // B-Type
        types.put("BEQ", "B");
        types.put("BNE", "B");

        // S-Type (distinguish via function fields)
        types.put("PUSH", "S");
        types.put("POP", "S");
        types.put("CALL", "S");
        types.put("RET", "S");

        // SYS-Type
        types.put("HALT", "SYS");
        types.put("PRINT", "SYS");
        types.put("MALLOC", "SYS");
        types.put("DEALLOC", "SYS");

        // The function code for the Stack instruction
        stack = new BiMaps();
        stack.insert("PUSH", "000000000");
        stack.insert("POP", "000000001");
        stack.insert("CALL", "000000000010");
        stack.insert("RET", "000000000011");

        // The function code the system calls instructions
        sysc = new BiMaps();
        sysc.insert("HALT", "000000000100");
        sysc.insert("PRINT", "000000101");
        sysc.insert("MALLOC", "000000110");
        sysc.insert("DEALLOC", "000000111");
    }

    /**
     * This method gets the Binary for the function for the syscall instruction
     * @param b: B is the instruction opcode
     * @return: return the function field in binary
     */
    public String getSYS(String b) {
        return sysc.getBin(b);
    }

    /**
     * This method gets the assembly for the function for the syscall instruction
     * @param b: B is the binary for the function
     * @return: Returns the assembly
     */
    public String getSYSInv(String b) {
        return sysc.getOpc(b);
    }

    /**
     * This method takes an assembly opcode and returns the binary function field
     * @param b: assembly opcode
     * @return:  binary function field
     */
    public String getStack(String b) {
        return stack.getBin(b);
    }

    /**
     * This method takes a binary function field and returns the assembly opcode
     * @param b: binary function field
     * @return: assembly opcode
     */
    public String getStackInv(String b) {
        return stack.getOpc(b);
    }

    /**
     *  This method takes in an assembly opcode and return binary
     * @param b: is the assembly opcode
     * @return: returns the binary
     * @throws Exception: if the instruction isn't valid, or can't be found inside the hash
     */
    public String getInstr(String b) throws Exception {
        if (instr.map.containsKey(b)) {
            return instr.getBin(b);
        } else throw new Exception("Invalid Instruction at line " + line);
    }
    /**
     *  This method takes in binary and return assembly opcode
     * @param b: binary for the opcode
     * @return: returns the assembly opcode
     * @throws Exception: if the instruction isn't valid, or can't be found inside the hash
     */
    public String getInstrInv(String b) throws Exception {
        if (instr.reverse.containsKey(b)) {
            return instr.getOpc(b);
        } else throw new Exception("Invalid Instruction at line " + line);
    }

    /**
     * This method takes in the assembly register and returns the binary
     * @param b: assembly register
     * @return: Returns the binary
     * @throws Exception: If the register can't be found
     */
    public String getReg(String b) throws Exception {
        if (registers.map.containsKey(b)) {
            return registers.getBin(b);
        } else throw new Exception("Invalid Instruction at line " + line);
    }
    /**
     * This method takes in the binary and returns the assembly register
     * @param b: binary register
     * @return: Returns the assembly register
     * @throws Exception: If the register can't be found
     */
    public String getRegInv(String b) throws Exception {
        if (registers.reverse.containsKey(b)) {
            return registers.getOpc(b);
        } else throw new Exception("Invalid Instruction at line " + line);
    }

    /**
     * This method takes in the opcode of an instruction and returns the type
     * @param key: key is the assembly opcode
     * @return: Retuns the types
     * @throws Exception: Throws an exception if the opcode can't be found
     */
    public String getType(String key) throws Exception {
        if (types.containsKey(key)) {
            return types.get(key);
        } else {
            throw new Exception("Invalid Instruction at line " + line);
        }
    }


}

