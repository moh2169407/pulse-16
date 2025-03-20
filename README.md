# 16-Bit Custom Processor Emulator

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Instruction Set Architecture (ISA)](#instruction-set-architecture-isa)
    - [Instruction Types](#instruction-types)
    - [Instruction Formats](#instruction-formats)
    - [Example Instructions](#example-instructions)
- [Registers](#registers)
- [Memory Layout](#memory-layout)
- [System Calls](#system-calls)
- [Emulator Design](#emulator-design)
    - [Components](#components)
    - [Control Flow](#control-flow)
- [Usage](#usage)
    - [How to Run](#how-to-run)
    - [Example Program](#example-program)
---

## Overview
This project implements a 16-bit custom processor emulator in Java. It simulates the core components of a basic RISC-style CPU, including instruction decoding, arithmetic operations, and memory management. The emulator serves as both a learning tool and a platform for experimentation with low-level computing concepts.

---

## Features
- **16-bit instruction width** with flexible architecture.
- Support for **5 instruction types**: R-Type, I-Type, B-Type, S-Type, and SYS-Type.
- **8 general-purpose registers** and specialized registers for program control.
- Comprehensive **memory management**:
    - Program memory for instructions.
    - Stack memory for local variables and function calls.
    - Heap memory for dynamic allocation.
- Basic system calls (e.g., `PRINT`, `HALT`, `MALLOC`, `DEALLOC`).
- Implements essential operations: arithmetic, logical, branching, and memory access.
- Simple stack and heap management with basic pointer tracking.

---

## Instruction Set Architecture (ISA)

### Instruction Types
1. **R-Type** (Register-to-Register): Arithmetic and logical operations between registers.
2. **I-Type** (Immediate-to-Register): Operations using immediate values.
3. **B-Type** (Branching): Conditional branching based on comparisons.
4. **S-Type** (Stack): Stack-specific instructions (e.g., `PUSH`, `POP`, `CALL`, `RET`).
5. **SYS-Type** (System Calls): Special operations like `PRINT`, `HALT`, `MALLOC`, and `DEALLOC`.

### Instruction Formats
| **Field**              | **Bits** | **Description**                           |
|------------------------|----------|-------------------------------------------|
| **Opcode**             | 4        | Specifies the operation (e.g., `ADD`, `SUB`). |
| **RS** (Source)        | 3        | First source register.                    |
| **RT** (Target)        | 3        | Second source register or destination.    |
| **Immediate/Offset**   | 6        | Immediate value, offset, or function code.|

---

### Example Instructions
#### R-Type Example: `ADD`
| **Opcode** | **RS** | **RT** | **RD** | **Func** |
|------------|--------|--------|--------|----------|
| `0000`     | `001`  | `010`  | `011`  | `000`    |
- **Meaning**: Add `R1` and `R2`, store the result in `R3`.

#### I-Type Example: `ADDI`
| **Opcode** | **RS** | **RT** | **Immediate** |
|------------|--------|--------|---------------|
| `1000`     | `001`  | `010`  | `000100`      |
- **Meaning**: Add `R1` and `4`, store the result in `R2`.

#### B-Type Example: `BEQ`
| **Opcode** | **RS** | **RT** | **Offset** |
|------------|--------|--------|------------|
| `1111`     | `001`  | `010`  | `000010`   |
- **Meaning**: Branch to `PC + 2` if `R1 == R2`.

---

## Registers
### General-Purpose Registers
- **R0-R7**:
    - `R0`: Always contains the value `0` (read-only).
    - `R1-R7`: Usable for arithmetic, logical, and control operations.

### Special Registers
- **PC (Program Counter)**: Tracks the next instruction address.
- **SP (Stack Pointer)**: Tracks the top of the stack.
- **HP (Heap Pointer)**: Tracks the start of available heap memory.

---

## Memory Layout
| **Memory Range**    | **Purpose**                           |
|---------------------|---------------------------------------|
| `0x0000 - 0x1FFF`   | Program Memory (Instructions)         |
| `0x2000 - 0x3FFF`   | Static Data (Global Variables)        |
| `0x4000 - 0x5FFF`   | Heap (Dynamic Memory Allocation)      |
| `0x6000 - 0x7FFF`   | Stack (Local Variables, Return Addr.) |

---

## System Calls
- **`PRINT`**: Outputs the value of a specified register.
- **`HALT`**: Stops the program.
- **`MALLOC`**: Allocates a specified number of bytes in heap memory.
- **`DEALLOC`**: Frees a previously allocated block of memory.

---

## Emulator Design

### Components
1. **Instruction Fetch**: Retrieves the instruction from memory.
2. **Instruction Decode**: Parses the instruction to identify the operation and operands.
3. **Execution Unit (ALU)**: Performs arithmetic and logical operations.
4. **Memory Access**: Handles load/store operations.
5. **Write Back**: Stores results in registers or memory.

### Control Flow
1. Fetch the instruction at the address in `PC`.
2. Decode the instruction to determine its type.
3. Execute the operation in the ALU or memory unit.
4. Write results back to the appropriate register or memory location.
5. Update `PC` for the next instruction.

---

## Usage

### How to Run
1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/16-bit-emulator.git
   ```
2. Compile the source files:
   ```bash
   javac Emulator/*.java
   ```
3. Run the emulator:
   ```bash
   java Emulator.Pulse
   ```
4. Provide a binary program file for the emulator to execute.

### Example Program
#### Assembly Code:
```assembly
ADDI R1, R0, 10  # Load 10 into R1
ADDI R2, R0, 20  # Load 20 into R2
ADD  R3, R1, R2  # Add R1 and R2, store result in R3
PRINT R3         # Print the value of R3
HALT             # Stop execution
```
#### Expected Output:
```
R3: 30
Program halted.
```

---

#### Example Video:

You can find the example assembly code for calculating the factorial in the repository:

[Factorial.txt](https://github.com/moh2169407/pulse-16/blob/main/Test/Factorial.txt)

This is a 16-bit processor with a max value of 65536. Since factorials grow quickly, the maximum you can calculate is 8! before the program crashes due to overflow.

**Important Note:**
The value of the first register (`R1`) determines the factorial calculation. For example, if `R1` is set to 5, the program will compute 5!. You can adjust this value in the assembly code to calculate different factorials, but keep in mind the limit mentioned above.


