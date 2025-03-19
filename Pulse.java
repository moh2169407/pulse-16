package Emulator;

import java.io.File;
import java.util.Scanner;


public class Pulse {
    Registers register = new Registers();       // Emulator the registers, also contains the pc and sp pointers
    Memory memory = new Memory(register.hp);    // Memory of the processor
    String currentInstructions;
    int line = 0;
    ISA isa = new ISA(line);
    Control control = new Control();            // Controls the instruction, decides what to do

    // Each instruction could possibly have one of component
    String RT = null;
    String RD = null;
    String RS = null;
    String Source = null;
    String Opcode = null;
    String func = null;
    String Constant = null;
    String RD_Dec = null;
    String RT_DEC = null;
    String type = null;
    int branchOffset = 0;

    public Pulse() throws Exception {
        System.out.println("--------------------------");
        File file = new File("/Users/amiinmohamud/IdeaProjects/2024Projects/src/Emulator/Test/Binary.txt");
        Scanner scan = new Scanner(file);
        int index = 0;
        while (scan.hasNext()) {
            String c = scan.nextLine();
            String c1 = c.substring(0, 8);
            String c2 = c.substring(8, 16);
            memory.fillProgram((byte) toDecimal(c1), index);
            memory.fillProgram((byte) toDecimal(c2), index + 1);
            index = index + 2;
        }

        while (!control.halt) {
            System.out.println("Line: " + register.pc);
            register.printRegister();
            System.out.println("------------------------");
            fetch(branchOffset, control.branch);
            Decode();
            EXE();
            MEM();
            WB();

        }
        register.printRegister();
        System.out.println("------------------------");
        memory.printMemory();
        System.exit(0);

    }


    public void fetch(int branchOffSet, Boolean branch) throws Exception {
        if (!branch) {
            int index = register.pc;
            currentInstructions = memory.getWordProgram(index);
            System.out.println("Current Instruction: " + currentInstructions);
            register.pc = register.pc + 2;
        } else {
            int x = branchOffSet;
            register.pc = (register.pc + x);
            int index = register.pc;
            currentInstructions = memory.getWordProgram(index);
            System.out.println("Current Instruction: " + currentInstructions);
            register.pc = register.pc + 2;
        }
    }

    public void Decode() throws Exception {
        control.reset();
        String opcode = currentInstructions.substring(0, 4);
        type = isa.getType(isa.getInstrInv(opcode));
        System.out.println(isa.getInstrInv(opcode));


        if (opcode.equals("1111")) {
            String f = currentInstructions.substring(11, 16);
            int d = toDecimal(f);

            switch (d) {
                case 0:            // PUSH
                    RS = "" + register.getRegister(toDecimal(currentInstructions.substring(4, 7)));
                    memory.setStack(Short.parseShort(RS), register.sp);
                    break;
                case 1:            // POP
                    register.setRegister((short) Short.parseShort(memory.getStack(register.sp)), toDecimal(currentInstructions.substring(4, 7)));

                    break;
                case 2:            // CALL
                    memory.CALL(register.sp, register.pc);
                    break;
                case 3:            // RET
                    int[] x = memory.RET();
                    register.pc = x[0];
                    register.sp = x[1];
                    break;
                case 4:            // HALT
                    control.halt = true;
                    break;
                case 5:            // PRINT
                    RS = "" + register.getRegister(toDecimal(currentInstructions.substring(4, 7)));
                    System.out.println(RS);
                    break;
                case 6:            // MALLOC
                    register.setRegister((short) memory.MALLOC(register.getRegister(toDecimal(currentInstructions.substring(4, 7)))), toDecimal(currentInstructions.substring(4, 7)));

                    break;
                case 7:            // DEALLOC
                    memory.DEALLOC(register.getRegister(toDecimal(currentInstructions.substring(4, 7))));
                    break;
            }
        }

        RT = null;
        RD = null;
        RS = null;
        Source = null;
        Opcode = null;
        func = null;
        Constant = null;
        control.branch = false;
        switch (type) {
            case "R":
                control.ALU = true;
                control.WB = true;


                Opcode = currentInstructions.substring(0, 4);
                RS = "" + register.getRegister(toDecimal(currentInstructions.substring(4, 7)));
                RD = String.valueOf(toDecimal(currentInstructions.substring(7, 10)));
                RT_DEC = "" + register.getRegister(toDecimal(currentInstructions.substring(10, 13)));
                RT = String.valueOf(toDecimal(currentInstructions.substring(10, 13)));
                func = currentInstructions.substring(13, 16);
                break;
            case "I":
                Opcode = currentInstructions.substring(0, 4);
                if (Opcode.equals("1011")) {                // Load word instruction
                    control.RTDst = true;
                    control.ALU = true;
                    control.WB = true;
                    control.MemeRead = true;

                    RS = "" + register.getRegister(toDecimal(currentInstructions.substring(4, 7)));
                    RT = "" + currentInstructions.substring(7, 10);
                    Constant = currentInstructions.substring(10, 16);

                } else if (Opcode.equals("1100")) {          // Store word instruction
                    control.ALU = true;
                    control.MemWrite = true;
                    control.RTDst = true;

                    RS = "" + register.getRegister(toDecimal(currentInstructions.substring(4, 7)));
                    RT = "" + register.getRegister(toDecimal(currentInstructions.substring(7, 10)));
                    Constant = currentInstructions.substring(10, 16);
                } else {
                    control.ALU = true;
                    control.WB = true;
                    control.RTDst = true;


                    RS = "" + register.getRegister(toDecimal(currentInstructions.substring(4, 7)));
                    RT = "" + currentInstructions.substring(7, 10);
                    Constant = currentInstructions.substring(10, 16);
                }
                break;
            case "B":
                control.ALU = true;


                Opcode = currentInstructions.substring(0, 4);
                RS = "" + register.getRegister(toDecimal(currentInstructions.substring(4, 7)));
                RT_DEC = "" + register.getRegister(toDecimal(currentInstructions.substring(7, 10)));
                Constant = currentInstructions.substring(10, 16);
                break;


        }

    }

    public void EXE() throws Exception {
        new ALU();

    }

    public void MEM() throws Exception {
        if (control.MemWrite) { // Handle SW
            int address = Integer.parseInt(RT_DEC); // Computed address from ALU
            memory.setHeapData((short) Short.parseShort(RT), address);
        } else if (control.MemeRead) { // Handle LW
            int address = Integer.parseInt(RT_DEC); // Computed address from ALU
            System.out.println(address);
            short value = Short.parseShort(memory.getHeapData(address));
            RT_DEC = String.valueOf(value);
            System.out.println("RT:" + RT_DEC);
        }
    }


    public void WB() throws Exception {
        if (type.equals("I") && control.WB) {

            register.setRegister(Short.parseShort(RT_DEC), toDecimal(RT));
        } else if (type.equals("R") && control.WB) {
            System.out.println("RD" + RD);
            register.setRegister(Short.parseShort(RD_Dec), Integer.parseInt(RD));
        }
        line++;
    }

    public int toDecimal(String binary) {
        int value = Integer.parseInt(binary, 2);
        // Check if the value is negative in 6-bit two's complement
        if (binary.length() == 6 && binary.charAt(0) == '1') {
            value -= (1 << 6); // Subtract 2^6 to get the correct signed value
        }
        return value;
    }

    private class ALU {
        public ALU() throws Exception {

            if (control.ALU) {
                short inputOne = Short.parseShort(RS);
                short inputTwo = control.RTDst ? Short.parseShort("" + toDecimal(Constant)) : Short.parseShort(RT_DEC);
                System.out.println("Input 1: " + inputOne);
                System.out.println("Input 2: " + inputTwo);
                switch (isa.getInstrInv(Opcode)) {
                    // R-Type
                    case "ADD":
                        System.out.println("Input one: " + inputOne);
                        System.out.println("Input two: " + inputTwo);
                        RD_Dec = String.valueOf(inputOne + inputTwo);
                        break;
                    case "SUB":
                        RD_Dec = String.valueOf(inputOne - inputTwo);
                        break;
                    case "AND":
                        RD_Dec = String.valueOf(inputOne & inputTwo);
                        break;
                    case "OR":
                        RD_Dec = String.valueOf(inputOne | inputTwo);
                        break;
                    case "MUL":
                        RD_Dec = String.valueOf(inputOne * inputTwo);
                        break;
                    case "SLT":
                        RD_Dec = inputOne < inputTwo ? "1" : "0";
                        break;
                    case "SLL":
                        RD_Dec = String.valueOf(inputOne << inputTwo);
                        break;
                    case "SRL":
                        RD_Dec = String.valueOf(inputOne >> inputTwo);
                        break;
                    // I-Type
                    case "ADDI":
                        RT_DEC = String.valueOf(inputOne + inputTwo);
                        break;
                    case "ANDI":
                        RT_DEC = String.valueOf(inputOne & inputTwo);
                        break;
                    case "ORI":
                        RT_DEC = String.valueOf(inputOne | inputTwo);
                        break;
                    case "LW":
                        RT_DEC = String.valueOf(inputOne + inputTwo);
                        break;
                    case "SW":
                        RT_DEC = String.valueOf(inputOne + inputTwo);
                        break;
                    // B-Type
                    case "BEQ":
                        RD_Dec = String.valueOf(inputOne - inputTwo);
                        if (RD_Dec.equals("0")) {
                            control.branch = true;
                            branchOffset = toDecimal(Constant);
                            System.out.println(branchOffset);
                        }
                        break;
                    case "BNE":
                        RD_Dec = String.valueOf(inputOne - inputTwo);
                        if (!RD_Dec.equals("0")) {
                            control.branch = true;
                            branchOffset = toDecimal(Constant);
                            System.out.println(branchOffset);
                        }
                        break;


                }
            }
        }
    }
}
