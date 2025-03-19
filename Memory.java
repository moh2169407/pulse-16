package Emulator;

import java.util.ArrayList;
import java.util.List;

/**
 * This class emulator the operator of memory inside a computer
 */
public class Memory {
    public static final int programStart = 0;                   // The start of the program memory
    public static final int programEND = 16384;                // The end of the program memory
    public static final int heapDataStart = 16384;          // The start of the heap data memory
    public int hp;                                           // The end of the static data memory
    public static final int stackStart = 49152;             // The start of the stack and the stack grows dynamically downwards
    // Free Memory from 49152 to 65536
    private byte[] memory;
    List<AllocatedBlock> allocatedBlocks = new ArrayList<>();
    List<Pointer> pointer = new ArrayList<>();


    /**
     * Create a memory with a size of 64 KB
     */
    public Memory(int hp) {
        memory = new byte[65536];   // 64 KB
        this.hp = hp;
    }

    /**
     * This method takes data for the program memory and store it into memory
     *
     * @param Byte:    Byte is the data, 8 bits of information, this processor is 16 bits,
     *                 split each instruction in half, and store them in each byte
     *                 ;address starts: 16384, ends: 32768
     * @param address: Address is where the data is going to be stored
     * @throws Exception: Address must be word aligned or multiples of two
     */
    public void fillProgram(byte Byte, int address) throws Exception {
        if (address < programStart || address >= programEND) {
            throw new Exception("Address out of program memory range");
        }
        memory[address] = Byte;
    }

    /**
     * This method takes an address as an index and return the information store in two byte
     * address starts: 16384, ends: 32768
     *
     * @param index: Is the address where information is stored
     * @throws Exception: Address must be word aligned
     * @return: Return a word aligned, two byte string
     */
    public String getWordProgram(int index) throws Exception {
        if (index < programStart || index >= programEND) {
            throw new Exception("Address out of program memory range");
        }
        if (index % 2 != 0) {
            throw new Exception("Memory must be word aligned");
        }
        String s = toTwosComplement((int) memory[index], 8);
        String s1 = toTwosComplement((int) memory[index + 1], 8);
        String s2 = s + s1;
        return s2;
    }

    /**
     * The method takes data and adds into the stack, this method automatically increments sp
     *
     * @param val: Is the data that getting stored inside the stack
     * @param sp:  Is the address of the stack pointer,
     * @throws Exception: SP needs to be word aligned and the address must be valid,
     *                    stack starts at 32768, and grows downwards until 65536
     */
    public void setStack(short val, int sp) throws Exception {
        if (sp < stackStart || sp >= memory.length) {
            throw new Exception("Address out of program memory range");
        }
        if (sp % 2 != 0) {
            throw new Exception("Memory must be word aligned");
        }
        String s = toTwosComplement(val, 16);
        String s1 = s.substring(0, 8);
        String s2 = s.substring(8, 16);
        memory[sp - 1] = (byte) Integer.parseInt(s1);
        memory[sp] = (byte) Integer.parseInt(s2);
        sp = sp - 2;
    }

    /**
     * This method takes an address and returns a word aligned bytes
     *
     * @param sp: The address of the data, must be words aligned and
     *            fit inside the stack bounds
     * @throws Exception: SP must be word aligned and fit inside 32768, and grows downwards until 65536
     * @return: The data inside the stack, returns two byte as string
     */
    public String getStack(int sp) throws Exception {
        if (sp < stackStart || sp >= memory.length) {
            throw new Exception("Address out of program memory range");
        }
        if (sp % 2 != 0) {
            throw new Exception("Memory must be word aligned");
        }

        String s = toTwosComplement(memory[sp - 1], 8);
        String s1 = toTwosComplement(memory[sp], 8);
        memory[sp - 1] = 0;
        memory[sp] = 0;
        sp = sp + 2;
        return String.valueOf(toDecimal(s + s1));
    }

    /**
     * This method save the current pc to the stack, and saves the address to a pointer
     * @param sp: Stack pointer is the address to the current address
     * @param pc: Program counter is the address of the current program in memory
     */
    public void CALL(int sp, int pc) {
        Pointer pointer1 = new Pointer(pc, sp);
        pointer.add(pointer1);
        System.out.println("PC: " + pc);
        String s = toTwosComplement(pc, 16);
        String s1 = s.substring(0, 8);
        String s2 = s.substring(8, 16);

        memory[sp - 1] = (byte) toDecimal(s1);
        memory[sp] = (byte) toDecimal(s2);

        sp -= 2;
    }

    /**
     * This method ret the pc from the stack and return it, the address the pc is saved to the
     * stack is saved inside the pointer
     * @return: PC(program counter)
     * @throws Exception: Throws an exception if the RET method is called without a call method
     * or if there's no pc inside the stack
     */
    public int[] RET() throws Exception {
        if (pointer.isEmpty()) {
            throw new Exception("Empty Return PC Address");
        } else {
            Pointer pointer2 = pointer.get(pointer.size() - 1);


            for (int i = pointer2.stackAddress; i < stackStart - 2; i++) {
                memory[i] = memory[i + 2];
            }

            // Clear the last two positions in the stack
            memory[stackStart - 2] = 0;
            memory[stackStart - 1] = 0;

            pointer2.stackAddress += 2;

            int[] x = {pointer2.baseAddress, pointer2.stackAddress};
            return x;
        }
    }

    /**
     * This method takes in data and an index, and saves the data dynamically
     * @param data: Data is a short data (16 bits), the method takes this a split it into bytes(8 bits)
     * @param index: Index is the address, must be worded aligned and be bounded inside the heap memory
     * @throws Exception: Throws an exception if the address isn't valid
     */

    public void setHeapData(short data, int index) throws Exception {
        if (index < heapDataStart || index > hp) {
            throw new Exception("Address out of program memory range");
        }
        if (index % 2 != 0) {
            throw new Exception("Memory must be word aligned");
        }
        String s = toTwosComplement(data, 16);
        String s1 = s.substring(0, 8);
        String s2 = s.substring(8, 16);
        memory[index] = (byte) toDecimal(s1);
        memory[index + 1] = (byte) toDecimal(s2);
    }

    /**
     * This method takes in an address and return the value in that word bound
     * @param index: Index is the address in memory
     * @return: Return a word, 16 bits from memory
     * @throws Exception: Throws an exception if address isn't valid
     */
    public String getHeapData(int index) throws Exception {
        if (index < heapDataStart || index > hp) {
            throw new Exception("Address out of program memory range Address: " + index + " SP: " + hp);
        }
        if (index % 2 != 0) {
            throw new Exception("Memory must be word aligned");
        }
        String x = String.valueOf(memory[index]);
        String y = String.valueOf(memory[index + 1]);

        return x + y;
    }

    /**
     * The method allocates saves for memory inside the heap
     * @param size: Size is the space in bytes you want to create
     * @return: It returns to address of the heap to the register containing the size
     */
    public int MALLOC(int size) {
        allocatedBlocks.add(new AllocatedBlock(hp, size));
        int x = hp;
        hp = hp + size;
        return x;
    }

    /**
     * This method deallocates space for the heap memory
     * @param address: The base address in the heap to removes, removes the allocated saves as well
     * @throws Exception: Throws an exception if the address isn't valid
     */
    public void DEALLOC(int address) throws Exception {
        AllocatedBlock blockToRemove = null;

        // Find the block with the given base address
        for (AllocatedBlock block : allocatedBlocks) {
            if (block.baseAddress == address) {
                blockToRemove = block;

                // Zero out memory for the block
                for (int i = 0; i < block.size; i++) {
                    memory[block.baseAddress + i] = 0;
                }
                break;
            }
        }

        // Handle case where address doesn't exist
        if (blockToRemove == null) {
            throw new Exception("Invalid base address: " + address);
        }

        // Remove the block from the list of allocated blocks
        allocatedBlocks.remove(blockToRemove);
    }


// Helper Methods

    /**
     * This helper method takes in an integer and splits into a 2 comp binary number
     * @param number: An integer to getting a converted
     * @param bits: the number of bits you need
     * @return: A two comp number
     */
    public static String toTwosComplement(int number, int bits) {
        // For positive numbers or zero, just return the padded binary string
        if (number >= 0) {
            return String.format("%" + bits + "s", Integer.toBinaryString(number)).replace(' ', '0');
        }
        // For negative numbers, compute two's complement
        int twosComplement = (1 << bits) + number; // Add the negative number to 2^bits
        return Integer.toBinaryString(twosComplement).substring(Integer.toBinaryString(twosComplement).length() - bits);
    }

    /**
     * Takes a binary string and convert it into integer
     * @param binary: A string contain binary number
     * @return: Return the deciaml version
     */
    public int toDecimal(String binary) {
        int value = Integer.parseInt(binary, 2);
        // Check if the value is negative in 6-bit two's complement
        if (binary.length() == 6 && binary.charAt(0) == '1') {
            value -= (1 << 6); // Subtract 2^6 to get the correct signed value
        }
        return value;
    }

    /**
     * This method prints the memory containing values
     */
    public void printMemory() {
        for (int i = 0; i < memory.length; i += 1) {
            if (memory[i] != 0) {
                System.out.println("Address " + i + ": " + memory[i]);

            }

        }


    }

    /**
     * This class is used to store the block in the heap
     */
    private class AllocatedBlock {
        int baseAddress;
        int size;

        AllocatedBlock(int baseAddress, int size) {
            this.baseAddress = baseAddress;
            this.size = size;
        }
    }

    /**
     * This class is used to track the pc inside the stack
     */
    private class Pointer {
        int baseAddress;
        int stackAddress;

        Pointer(int baseAddress, int stackAddress) {
            this.baseAddress = baseAddress;
            this.stackAddress = stackAddress;
            System.out.println(baseAddress);
            System.out.println(stackAddress);
        }
    }

}

