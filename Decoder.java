package Emulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.PrintWriter;

/**
 * This class is an emulator for the decoder inside the processor
 */
public class Decoder {
    int line = 0;
    ISA isa = new ISA(line);

    /**
     * This is the constructor for this class, takes in an input of a file name that it needs to decoder into binary
     *
     * @param url: FileName that needs decoding, file needs to be in assembly
     * @param url2 Output file name for the binary code
     * @throws FileNotFoundException: If  the file isn't found
     */
    public Decoder(String url, String url2) throws Exception {
        // Creates a File using name given, and iterates tho
        File fileName = new File(url);          // Creates a file for the input assembly code
        File outFIle = new File(url2);          // Creates a file for the output binary code
        Scanner scan = new Scanner(fileName);   // Scans into the assembly file
        PrintWriter pw = new PrintWriter(outFIle);
        while (scan.hasNext()) {
            StringBuilder strb = new StringBuilder();       // Uses the string builder to append the instruction in binary
            // Breaks the string line into words, and then uses hash to find the binary equivalent
            String source = scan.nextLine();
            source = source.replace(",", "");
            String[] source1 = source.split(" ");           // Formats the instruction into words that's inside an array
            for (String s : source1) {                      // Iterates through the array and check the opcode and other cases
                System.out.println(s);
            }
            String type = isa.getType(source1[0]);         // The
            String opcodeB = null;
            String RD = null;
            String RT = null;
            String RS = null;
            String Constant = null;
            // Sort the instruction into types, and then splits
            switch (type) {
                case "R":
                    // R-Types instruction are all 4 length
                    if (source1.length != 4) {
                        throw new Exception("Instruction length is incorrect");
                    }
                    opcodeB = isa.getInstr(source1[0]);
                    RS = isa.getReg(source1[1]);
                    RT = isa.getReg(source1[2]);
                    RD = isa.getReg(source1[3]);
                    String zeros = "000";           // The function field for R-Type are always zero
                    strb.append(opcodeB).append(RS).append(RT).append(RD).append(zeros);
                    pw.println(strb);               // Prints it to the file
                    break;
                case "I":
                    // I-Type instruction length changes
                    if (source1[0].equals("SW") || source1[0].equals("LW")) {       // This part handles LW or SW instruction
                        if (source1.length == 3) {
                            opcodeB = isa.getInstr(source1[0]);
                            RS = isa.getReg(source1[1]);
                            source1[2] = source1[2].replace('(', ' ');
                            String[] a = source1[2].split(" ");
                            Constant = String.valueOf(toBinary(Integer.parseInt(a[0])));
                            a[1] = a[1].replace(")", "");
                            RT = isa.getReg(a[1]);
                            strb.append(opcodeB).append(RS).append(RT).append(Constant);
                            pw.println(strb);
                        } else {
                            throw new Exception("Instruction length is incorrect");
                        }
                    } else {
                        if (source1.length != 4) {
                            throw new Exception("Instruction length is incorrect");
                        }                                                             // This part handles all other I-Types instruction
                        opcodeB = isa.getInstr(source1[0]);
                        RS = isa.getReg(source1[1]);
                        RT = isa.getReg(source1[2]);
                        Constant = toBinary(Integer.parseInt(source1[3]));
                        strb.append(opcodeB).append(RS).append(RT).append(Constant);
                        pw.println(strb);
                    }
                    break;
                case "B":
                    // Branch instruction are always 4 long
                    if (source1.length != 4) {
                        throw new Exception("Instruction length is incorrect");
                    }
                    opcodeB = isa.getInstr(source1[0]);
                    RS = isa.getReg(source1[1]);
                    RT = isa.getReg(source1[2]);
                    Constant = toTwosComplement(Integer.parseInt(source1[3]), 6);
                    System.out.println("Constant: "  + Constant);
                    strb.append(opcodeB).append(RS).append(RT).append(Constant);
                    pw.println(strb);           // Print to the file
                    break;
                case "S":
                    if (source1[0].equals("CALL") || source1[0].equals("RET")) {
                        opcodeB = isa.getInstr(source1[0]);
                        String func = isa.getStack(source1[0]);
                        strb.append(opcodeB).append(func);
                        pw.println(strb);
                    } else {
                        if (source1.length != 2) {
                            throw new Exception("Instruction length is incorrect");
                        }
                        opcodeB = isa.getInstr(source1[0]);
                        RS = isa.getReg(source1[1]);
                        String func = isa.getStack(source1[0]);
                        strb.append(opcodeB).append(RS).append(func);
                        pw.println(strb);
                    }
                    break;

                case "SYS":
                    if (source1[0].equals("HALT")) {

                        opcodeB = isa.getInstr(source1[0]);
                        String funcm = isa.getSYS(source1[0]);
                        strb.append(opcodeB).append(funcm);
                        pw.println(strb);
                    } else if (source1[0].equals("PRINT")) {
                        opcodeB = isa.getInstr(source1[0]);
                        RS = isa.getReg(source1[1]);
                        String x = isa.getSYS(source1[0]);
                        strb.append(opcodeB).append(RS).append(x);
                        pw.println(strb);
                    } else if (source1[0].equals("MALLOC") || source1[0].equals("DEALLOC")) {
                        opcodeB = isa.getInstr(source1[0]);
                        RS = isa.getReg(source1[1]);
                        String x = isa.getSYS(source1[0]);
                        strb.append(opcodeB).append(RS).append(x);
                        pw.println(strb);
                    }
                    break;

            }


            line++;
        }
        pw.close();
    }
// Helper Method

    /**
     * This method is a helper that takes in an int and return the binary
     * @param val:
     * @return
     */
    public static String toBinary(int val) {
        String s = Integer.toBinaryString(val);
        if (s.length() > 6) {
            return s.substring(s.length() - 6);
        } else {
            return String.format("%06d", Integer.parseInt(s));
        }

    }
    public static String toTwosComplement(int number, int bits) {
        // For positive numbers or zero, just return the padded binary string
        if (number >= 0) {
            return String.format("%" + bits + "s", Integer.toBinaryString(number)).replace(' ', '0');
        }
        // For negative numbers, compute two's complement
        int twosComplement = (1 << bits) + number; // Add the negative number to 2^bits
        return Integer.toBinaryString(twosComplement).substring(Integer.toBinaryString(twosComplement).length() - bits);
    }


}
