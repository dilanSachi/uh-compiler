package fi.helsinki.compiler.assemblygen;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Timestamp;
import java.util.Date;

public class Assembler {

    public void assemble(String assemblyCode, String outputFile, String workDirectory, String tempfileBasename,
                         boolean linkWithC, String[] extraLibraries) throws IOException, InterruptedException {
        byte[] assembledCode = assemble(assemblyCode, workDirectory, tempfileBasename, linkWithC, extraLibraries);
        Files.write(Path.of(outputFile), assembledCode);
    }

    public byte[] assembleAndGetExecutable(String assemblyCode, String workDirectory, String tempfileBasename,
                         boolean linkWithC, String[] extraLibraries) throws IOException, InterruptedException {
        return assemble(assemblyCode, workDirectory, tempfileBasename, linkWithC, extraLibraries);
    }

    private byte[] assemble(String assemblyCode, String workDirectory, String tempfileBasename,
                                      boolean linkWithC, String[] extraLibraries) throws IOException, InterruptedException {
        if (workDirectory != null) {
            workDirectory = Path.of(workDirectory).toAbsolutePath().toString();
        } else {
            File file = new File(System.getProperty("java.io.tmpdir"), "compiler_" + System.currentTimeMillis());
            file.mkdir();
            workDirectory = file.getAbsolutePath();
        }
        Path stdlibAsm = Path.of(workDirectory, "stdlib.s");
        String stdlibObj = Path.of(workDirectory, "stdlib.o").toString();
        Path programAsm = Path.of(workDirectory, tempfileBasename + ".s");
        String programObj = Path.of(workDirectory, tempfileBasename + ".o").toString();
        String outputFile = Path.of(workDirectory, "executable.out").toString();
        String finalStdlibAsmCode;
        if (linkWithC) {
            finalStdlibAsmCode = stdlibAsmCode.split("# BEGIN START")[0] + stdlibAsmCode.split("# END START")[1];
        } else {
            finalStdlibAsmCode = stdlibAsmCode;
        }
        Files.write(stdlibAsm, finalStdlibAsmCode.getBytes(StandardCharsets.UTF_8));
        Files.write(programAsm, assemblyCode.getBytes(StandardCharsets.UTF_8));
        ProcessBuilder pb1 = new ProcessBuilder("as", "-g", "-o", stdlibObj, stdlibAsm.toString());
        Process p1 = pb1.start();
        p1.waitFor();
        ProcessBuilder pb2 = new ProcessBuilder("as", "-g", "-o", programObj, programAsm.toString());
        Process p2 = pb2.start();
        p2.waitFor();
        String[] linkerFlags = new String[extraLibraries.length + 5];
        if (linkWithC) {
            linkerFlags[0] = "cc";
        } else {
            linkerFlags[0] = "ld";
        }
        linkerFlags[1] = "-o" + outputFile;
        linkerFlags[2] = "-static";
        for (int i = 0; i < extraLibraries.length; i++) {
            linkerFlags[i + 3] = "-" + extraLibraries[i];
        }
        linkerFlags[linkerFlags.length - 2] = stdlibObj;
        linkerFlags[linkerFlags.length - 1] = programObj;
        ProcessBuilder pb3 = new ProcessBuilder(linkerFlags);
        Process p3 = pb3.start();
        p3.waitFor();
        return Files.readAllBytes(Path.of(outputFile));
    }

    private static final String stdlibAsmCode = ".global _start\n" +
            "    .global print_int\n" +
            "    .global print_bool\n" +
            "    .global read_int\n" +
            "    .extern main\n" +
            "    .section .text\n" +
            "\n" +
            "# BEGIN START (we skip this part when linking with C)\n" +
            "# ***** Function '_start' *****\n" +
            "# Calls function 'main' and halts the program\n" +
            "\n" +
            "_start:\n" +
            "    call main\n" +
            "    movq $60, %rax\n" +
            "    xorq %rdi, %rdi\n" +
            "    syscall\n" +
            "# END START\n" +
            "\n" +
            "# ***** Function 'print_int' *****\n" +
            "# Prints a 64-bit signed integer followed by a newline.\n" +
            "#\n" +
            "# We'll build up the digits to print on the stack.\n" +
            "# We generate the least significant digit first,\n" +
            "# and the stack grows downward, so that works out nicely.\n" +
            "#\n" +
            "# Algorithm:\n" +
            "#     push(newline)\n" +
            "#     if x < 0:\n" +
            "#         negative = true\n" +
            "#         x = -x\n" +
            "#     while x > 0:\n" +
            "#         push(digit for (x % 10))\n" +
            "#         x = x / 10\n" +
            "#     if negative:\n" +
            "#         push(minus sign)\n" +
            "#     syscall 'write' with pushed data\n" +
            "#     return the original argument\n" +
            "#\n" +
            "# Registers:\n" +
            "# - rdi = our input number, which we divide down as we go\n" +
            "# - rsp = stack pointer, pointing to the next character to emit.\n" +
            "# - rbp = pointer to one after the last byte of our output (which grows downward)\n" +
            "# - r9 = whether the number was negative\n" +
            "# - r10 = a copy of the original input, so we can return it\n" +
            "# - rax, rcx and rdx are used by intermediate computations\n" +
            "\n" +
            "print_int:\n" +
            "    pushq %rbp               # Save previous stack frame pointer\n" +
            "    movq %rsp, %rbp          # Set stack frame pointer\n" +
            "    movq %rdi, %r10          # Back up original input\n" +
            "    decq %rsp                # Point rsp at first byte of output\n" +
            "                             # TODO: this non-alignment confuses debuggers. Use a different register?\n" +
            "\n" +
            "    # Add newline as the last output byte\n" +
            "    movb $10, (%rsp)         # ASCII newline = 10\n" +
            "    decq %rsp\n" +
            "\n" +
            "    # Check for zero and negative cases\n" +
            "    xorq %r9, %r9\n" +
            "    xorq %rax, %rax\n" +
            "    cmpq $0, %rdi\n" +
            "    je .Ljust_zero\n" +
            "    jge .Ldigit_loop\n" +
            "    incq %r9  # If < 0, set %r9 to 1\n" +
            "\n" +
            ".Ldigit_loop:\n" +
            "    cmpq $0, %rdi\n" +
            "    je .Ldigits_done        # Loop done when input = 0\n" +
            "\n" +
            "    # Divide rdi by 10\n" +
            "    movq %rdi, %rax\n" +
            "    movq $10, %rcx\n" +
            "    cqto\n" +
            "    idivq %rcx               # Sets rax = quotient and rdx = remainder\n" +
            "\n" +
            "    movq %rax, %rdi          # The quotient becomes our remaining input\n" +
            "    cmpq $0, %rdx            # If the remainder is negative (because the input is), negate it\n" +
            "    jge .Lnot_negative\n" +
            "    negq %rdx\n" +
            ".Lnot_negative:\n" +
            "    addq $48, %rdx           # ASCII '0' = 48. Add the remainder to get the correct digit.\n" +
            "    movb %dl, (%rsp)         # Store the digit in the output\n" +
            "    decq %rsp\n" +
            "    jmp .Ldigit_loop\n" +
            "\n" +
            ".Ljust_zero:\n" +
            "    movb $48, (%rsp)         # ASCII '0' = 48\n" +
            "    decq %rsp\n" +
            "\n" +
            ".Ldigits_done:\n" +
            "\n" +
            "    # Add minus sign if negative\n" +
            "    cmpq $0, %r9\n" +
            "    je .Lminus_done\n" +
            "    movb $45, (%rsp)         # ASCII '-' = 45\n" +
            "    decq %rsp\n" +
            ".Lminus_done:\n" +
            "\n" +
            "    # Call syscall 'write'\n" +
            "    movq $1, %rax            # rax = syscall number for write\n" +
            "    movq $1, %rdi            # rdi = file handle for stdout\n" +
            "    # rsi = pointer to message\n" +
            "    movq %rsp, %rsi\n" +
            "    incq %rsi\n" +
            "    # rdx = number of bytes\n" +
            "    movq %rbp, %rdx\n" +
            "    subq %rsp, %rdx\n" +
            "    decq %rdx\n" +
            "    syscall\n" +
            "\n" +
            "    # Restore stack registers and return the original input\n" +
            "    movq %rbp, %rsp\n" +
            "    popq %rbp\n" +
            "    movq %r10, %rax\n" +
            "    ret\n" +
            "\n" +
            "\n" +
            "# ***** Function 'print_bool' *****\n" +
            "# Prints either 'true' or 'false', followed by a newline.\n" +
            "print_bool:\n" +
            "    pushq %rbp               # Save previous stack frame pointer\n" +
            "    movq %rsp, %rbp          # Set stack frame pointer\n" +
            "    movq %rdi, %r10          # Back up original input\n" +
            "\n" +
            "    cmpq $0, %rdi            # See if the argument is false (i.e. 0)\n" +
            "    jne .Ltrue\n" +
            "    movq $false_str, %rsi    # If so, set %rsi to the address of the string for false\n" +
            "    movq $false_str_len, %rdx       # and %rdx to the length of that string,\n" +
            "    jmp .Lwrite\n" +
            ".Ltrue:\n" +
            "    movq $true_str, %rsi     # otherwise do the same with the string for true.\n" +
            "    movq $true_str_len, %rdx\n" +
            "\n" +
            ".Lwrite:\n" +
            "    # Call syscall 'write'\n" +
            "    movq $1, %rax            # rax = syscall number for write\n" +
            "    movq $1, %rdi            # rdi = file handle for stdout\n" +
            "    # rsi = pointer to message (already set above)\n" +
            "    # rdx = number of bytes (already set above)\n" +
            "    syscall\n" +
            "    \n" +
            "    # Restore stack registers and return the original input\n" +
            "    movq %rbp, %rsp\n" +
            "    popq %rbp\n" +
            "    movq %r10, %rax\n" +
            "    ret\n" +
            "\n" +
            "true_str:\n" +
            "    .ascii \"true\n\"\n" +
            "true_str_len = . - true_str\n" +
            "false_str:\n" +
            "    .ascii \"false\n\"\n" +
            "false_str_len = . - false_str\n" +
            "\n" +
            "# ***** Function 'read_int' *****\n" +
            "# Reads an integer from stdin, skipping non-digit characters, until a newline.\n" +
            "#\n" +
            "# To avoid the complexity of buffering, it very inefficiently\n" +
            "# makes a syscall to read each byte.\n" +
            "#\n" +
            "# It crashes the program if input could not be read.\n" +
            "read_int:\n" +
            "    pushq %rbp           # Save previous stack frame pointer\n" +
            "    movq %rsp, %rbp      # Set stack frame pointer\n" +
            "    pushq %r12           # Back up r12 since it's callee-saved\n" +
            "    pushq $0             # Reserve space for input\n" +
            "                         # (we only write the lowest byte,\n" +
            "                         # but loading 64-bits at once is easier)\n" +
            "\n" +
            "    xorq %r9, %r9        # Clear r9 - it'll store the minus sign\n" +
            "    xorq %r10, %r10      # Clear r10 - it'll accumulate our output\n" +
            "                         # Skip r11 - syscalls destroy it\n" +
            "    xorq %r12, %r12      # Clear r12 - it'll count the number of input bytes read.\n" +
            "\n" +
            "    # Loop until a newline or end of input is encountered\n" +
            ".Lloop:\n" +
            "    # Call syscall 'read'\n" +
            "    xorq %rax, %rax      # syscall number for read = 0\n" +
            "    xorq %rdi, %rdi      # file handle for stdin = 0\n" +
            "    movq %rsp, %rsi      # rsi = pointer to buffer\n" +
            "    movq $1, %rdx        # rdx = buffer size\n" +
            "    syscall              # result in rax = number of bytes read,\n" +
            "                         # or 0 on end of input, -1 on error\n" +
            "\n" +
            "    # Check return value: either -1, 0 or 1.\n" +
            "    cmpq $0, %rax\n" +
            "    jg .Lno_error\n" +
            "    je .Lend_of_input\n" +
            "    jmp .Lerror\n" +
            "\n" +
            ".Lend_of_input:\n" +
            "    cmpq $0, %r12\n" +
            "    je .Lerror           # If we've read no input, it's an error.\n" +
            "    jmp .Lend            # Otherwise complete reading this input.\n" +
            "\n" +
            ".Lno_error:\n" +
            "    incq %r12            # Increment input byte counter\n" +
            "    movq (%rsp), %r8     # Load input byte to r8\n" +
            "\n" +
            "    # If the input byte is 10 (newline), exit the loop\n" +
            "    cmpq $10, %r8\n" +
            "    je .Lend\n" +
            "\n" +
            "    # If the input byte is 45 (minus sign), negate r9\n" +
            "    cmpq $45, %r8\n" +
            "    jne .Lnegation_done\n" +
            "    xorq $1, %r9\n" +
            ".Lnegation_done:\n" +
            "\n" +
            "    # If the input byte is not between 48 ('0') and 57 ('9')\n" +
            "    # then skip it as a junk character.\n" +
            "    cmpq $48, %r8\n" +
            "    jl .Lloop\n" +
            "    cmpq $57, %r8\n" +
            "    jg .Lloop\n" +
            "\n" +
            "    # Subtract 48 to get a digit 0..9\n" +
            "    subq $48, %r8\n" +
            "\n" +
            "    # Shift the digit onto the result\n" +
            "    imulq $10, %r10\n" +
            "    addq %r8, %r10\n" +
            "\n" +
            "    jmp .Lloop\n" +
            "\n" +
            ".Lend:\n" +
            "    # If it's a negative number, negate the result\n" +
            "    cmpq $0, %r9\n" +
            "    je .Lfinal_negation_done\n" +
            "    neg %r10\n" +
            ".Lfinal_negation_done:\n" +
            "    # Restore stack registers and return the result\n" +
            "    popq %r12\n" +
            "    movq %rbp, %rsp\n" +
            "    popq %rbp\n" +
            "    movq %r10, %rax\n" +
            "    ret\n" +
            "\n" +
            ".Lerror:\n" +
            "    # Write error message to stderr with syscall 'write'\n" +
            "    movq $1, %rax\n" +
            "    movq $2, %rdi\n" +
            "    movq $read_int_error_str, %rsi\n" +
            "    movq $read_int_error_str_len, %rdx\n" +
            "    syscall\n" +
            "\n" +
            "    # Exit the program\n" +
            "    movq $60, %rax      # Syscall number for exit = 60.\n" +
            "    movq $1, %rdi       # Set exit code 1.\n" +
            "    syscall\n" +
            "\n" +
            "read_int_error_str:\n" +
            "    .ascii \"Error: read_int() failed to read input\n\"\n" +
            "read_int_error_str_len = . - read_int_error_str";
}
