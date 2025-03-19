package Emulator;

public class Control {
    Boolean RTDst;
    Boolean ALU;
    Boolean Func;
    Boolean WB;
    Boolean MemWrite;
    Boolean MemeRead;
    Boolean halt;
    Boolean branch;


    public Control(){
        RTDst = false;
        ALU = false;
        Func = false;
        WB = false;
        MemWrite = false;
        MemeRead  = false;
        halt = false;
        branch = false;



    }
    public void reset(){
        RTDst = false;
        ALU = false;
        Func = false;
        WB = false;
        MemWrite = false;
        MemeRead  = false;
    }
}
