package Emulator;

import java.util.Arrays;

/**
 * This is class emulators the operations of a registers inside a processor
 */
public class Registers {
    short[] register;
    int pc, sp, hp;

    /**
     * The constructor of the register class, creates an array of "registers"
     */
    public Registers() {
        register = new short[8];
        pc = 0;
        sp = 49152;
        hp = 16384;

    }

    /**
     * The method to setting values inside the registers
     *
     * @param value, The short value that's going inside the register
     * @param index, The register that you want to change
     * @throws Exception, Throws an exception if register zero is changed, the zero register value should always remain zero
     */
    public void setRegister(short value, int index) throws Exception {
        if (index == 0 && value != 0) {
            throw new Exception("You can't change the zero register value");
        } else {
            register[index] = value;
        }

    }

    /**
     * The method to getting a value from a register
     *
     * @param index, You choose the regiser with the index, 0-7
     * @return, Returns the short value contain inside the register
     */
    public short getRegister(int index) {
        return register[index];
    }
// Helper Methods

    /**
     * Clears all the registers values
     */
    public void clearRegister() {
        Arrays.fill(register, (short) 0);
    }

    /**
     * Prints all the register values
     */
    public void printRegister() {
        for (int i = 0; i < register.length; i++) {
            System.out.println("Register " + i + ": " + register[i]);
        }
    }


}
