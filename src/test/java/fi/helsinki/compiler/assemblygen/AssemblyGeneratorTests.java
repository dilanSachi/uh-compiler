package fi.helsinki.compiler.assemblygen;

import fi.helsinki.compiler.common.CommonStatics;
import fi.helsinki.compiler.common.expressions.Expression;
import fi.helsinki.compiler.exceptions.IRGenerationException;
import fi.helsinki.compiler.exceptions.ParserException;
import fi.helsinki.compiler.exceptions.TypeCheckerException;
import fi.helsinki.compiler.irgenerator.IRGenerator;
import fi.helsinki.compiler.irgenerator.instructions.Instruction;
import fi.helsinki.compiler.parser.Parser;
import fi.helsinki.compiler.tokenizer.Tokenizer;
import fi.helsinki.compiler.typechecker.TypeChecker;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssemblyGeneratorTests {

    @Test
    public void testBasicAddition() throws Exception {
        List<Instruction> instructions = generateInstructions("1 + 2;");
        AssemblyGenerator assemblyGenerator = new AssemblyGenerator();
        String assemblyCode = assemblyGenerator.generateAssembly(instructions);
        assertEquals(".extern print_int\n" +
                ".extern print_bool\n" +
                ".extern read_int\n" +
                ".section .text\n" +
                ".global main\n" +
                ".type main, @function\n" +
                "main:\n" +
                "pushq %rbp\n" +
                "movq %rsp, %rbp\n" +
                "subq $32, %rsp\n" +
                "#LoadIntConst(1,x12)\n" +
                "movq $1, -8(%rbp)\n" +
                "#LoadIntConst(2,x13)\n" +
                "movq $2, -16(%rbp)\n" +
                "#Call(+,[x12, x13],x14)\n" +
                "movq -8(%rbp), %rax\n" +
                "addq -16(%rbp), %rax\n" +
                "movq %rax, -32(%rbp)\n" +
                "movq $0, %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret", assemblyCode);
    }

    @Test
    public void testBasicAnd() throws Exception {
        List<Instruction> instructions = generateInstructions("true and false;");
        AssemblyGenerator assemblyGenerator = new AssemblyGenerator();
        String assemblyCode = assemblyGenerator.generateAssembly(instructions);
        assertEquals(".extern print_int\n" +
                ".extern print_bool\n" +
                ".extern read_int\n" +
                ".section .text\n" +
                ".global main\n" +
                ".type main, @function\n" +
                "main:\n" +
                "pushq %rbp\n" +
                "movq %rsp, %rbp\n" +
                "subq $24, %rsp\n" +
                "#LoadBoolConst(true,x12)\n" +
                "movq $1, -8(%rbp)\n" +
                "#CondJump(x12,Label(and_right),Label(and_skip))\n" +
                "cmpq $0, -8(%rbp)\n" +
                "jne .Land_right\n" +
                "jmp .Land_skip\n" +
                "#Label(and_right)\n" +
                "\n" +
                ".Land_right:\n" +
                "#LoadBoolConst(false,x13)\n" +
                "movq $0, -16(%rbp)\n" +
                "#Copy(x13,x14)\n" +
                "movq -16(%rbp), %rax\n" +
                "movq %rax, -24(%rbp)\n" +
                "#Jump(Label(and_end))\n" +
                "jmp .Land_end\n" +
                "#Label(and_skip)\n" +
                "\n" +
                ".Land_skip:\n" +
                "#LoadBoolConst(false,x14)\n" +
                "movq $0, -24(%rbp)\n" +
                "#Jump(Label(and_end))\n" +
                "jmp .Land_end\n" +
                "#Label(and_end)\n" +
                "\n" +
                ".Land_end:\n" +
                "movq $0, %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret", assemblyCode);
    }

    @Test
    public void testBasicAdditionWithMultiplication() throws Exception {
        List<Instruction> instructions = generateInstructions("(1+2)*3");
        AssemblyGenerator assemblyGenerator = new AssemblyGenerator();
        String assemblyCode = assemblyGenerator.generateAssembly(instructions);
        assertEquals(".extern print_int\n" +
                ".extern print_bool\n" +
                ".extern read_int\n" +
                ".section .text\n" +
                ".global main\n" +
                ".type main, @function\n" +
                "main:\n" +
                "pushq %rbp\n" +
                "movq %rsp, %rbp\n" +
                "subq $72, %rsp\n" +
                "#LoadIntConst(1,x12)\n" +
                "movq $1, -8(%rbp)\n" +
                "#LoadIntConst(2,x13)\n" +
                "movq $2, -16(%rbp)\n" +
                "#Call(+,[x12, x13],x14)\n" +
                "movq -8(%rbp), %rax\n" +
                "addq -16(%rbp), %rax\n" +
                "movq %rax, -32(%rbp)\n" +
                "#LoadIntConst(3,x15)\n" +
                "movq $3, -40(%rbp)\n" +
                "#Call(*,[x14, x15],x16)\n" +
                "movq -32(%rbp), %rax\n" +
                "imulq -40(%rbp), %rax\n" +
                "movq %rax, -56(%rbp)\n" +
                "#Call(print_int,[x16],x18)\n" +
                "movq -56(%rbp), %rdi\n" +
                "callq print_int\n" +
                "movq %rax, -72(%rbp)\n" +
                "movq $0, %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret", assemblyCode);
    }

    @Test
    public void testBasicAdditionWithPrint() throws Exception {
        List<Instruction> instructions = generateInstructions("1 + 2");
        AssemblyGenerator assemblyGenerator = new AssemblyGenerator();
        String assemblyCode = assemblyGenerator.generateAssembly(instructions);
        assertEquals(".extern print_int\n" +
                ".extern print_bool\n" +
                ".extern read_int\n" +
                ".section .text\n" +
                ".global main\n" +
                ".type main, @function\n" +
                "main:\n" +
                "pushq %rbp\n" +
                "movq %rsp, %rbp\n" +
                "subq $48, %rsp\n" +
                "#LoadIntConst(1,x12)\n" +
                "movq $1, -8(%rbp)\n" +
                "#LoadIntConst(2,x13)\n" +
                "movq $2, -16(%rbp)\n" +
                "#Call(+,[x12, x13],x14)\n" +
                "movq -8(%rbp), %rax\n" +
                "addq -16(%rbp), %rax\n" +
                "movq %rax, -32(%rbp)\n" +
                "#Call(print_int,[x14],x16)\n" +
                "movq -32(%rbp), %rdi\n" +
                "callq print_int\n" +
                "movq %rax, -48(%rbp)\n" +
                "movq $0, %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret", assemblyCode);
    }

    @Test
    public void testConditionalOperation() throws Exception {
        List<Instruction> instructions = generateInstructions("{ var x = true; if x then {1} else {2}; }");
        AssemblyGenerator assemblyGenerator = new AssemblyGenerator();
        String assemblyCode = assemblyGenerator.generateAssembly(instructions);
        assertEquals(".extern print_int\n" +
                ".extern print_bool\n" +
                ".extern read_int\n" +
                ".section .text\n" +
                ".global main\n" +
                ".type main, @function\n" +
                "main:\n" +
                "pushq %rbp\n" +
                "movq %rsp, %rbp\n" +
                "subq $40, %rsp\n" +
                "#LoadBoolConst(true,x12)\n" +
                "movq $1, -8(%rbp)\n" +
                "#Copy(x12,x13)\n" +
                "movq -8(%rbp), %rax\n" +
                "movq %rax, -16(%rbp)\n" +
                "#CondJump(x13,Label(then),Label(else))\n" +
                "cmpq $0, -16(%rbp)\n" +
                "jne .Lthen\n" +
                "jmp .Lelse\n" +
                "#Label(then)\n" +
                "\n" +
                ".Lthen:\n" +
                "#LoadIntConst(1,x16)\n" +
                "movq $1, -24(%rbp)\n" +
                "#Copy(x16,x15)\n" +
                "movq -24(%rbp), %rax\n" +
                "movq %rax, -32(%rbp)\n" +
                "#Jump(Label(end))\n" +
                "jmp .Lend\n" +
                "#Label(else)\n" +
                "\n" +
                ".Lelse:\n" +
                "#LoadIntConst(2,x17)\n" +
                "movq $2, -40(%rbp)\n" +
                "#Copy(x17,x15)\n" +
                "movq -40(%rbp), %rax\n" +
                "movq %rax, -32(%rbp)\n" +
                "#Label(end)\n" +
                "\n" +
                ".Lend:\n" +
                "movq $0, %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret", assemblyCode);
    }

    @Test
    public void testConditionalOperationWithoutParenthesis() throws Exception {
        List<Instruction> instructions = generateInstructions("{ if true then 1 else 2 }");
        AssemblyGenerator assemblyGenerator = new AssemblyGenerator();
        String assemblyCode = assemblyGenerator.generateAssembly(instructions);
        assertEquals(".extern print_int\n" +
                ".extern print_bool\n" +
                ".extern read_int\n" +
                ".section .text\n" +
                ".global main\n" +
                ".type main, @function\n" +
                "main:\n" +
                "pushq %rbp\n" +
                "movq %rsp, %rbp\n" +
                "subq $48, %rsp\n" +
                "#LoadBoolConst(true,x12)\n" +
                "movq $1, -8(%rbp)\n" +
                "#CondJump(x12,Label(then),Label(else))\n" +
                "cmpq $0, -8(%rbp)\n" +
                "jne .Lthen\n" +
                "jmp .Lelse\n" +
                "#Label(then)\n" +
                "\n" +
                ".Lthen:\n" +
                "#LoadIntConst(1,x14)\n" +
                "movq $1, -16(%rbp)\n" +
                "#Copy(x14,x13)\n" +
                "movq -16(%rbp), %rax\n" +
                "movq %rax, -24(%rbp)\n" +
                "#Jump(Label(end))\n" +
                "jmp .Lend\n" +
                "#Label(else)\n" +
                "\n" +
                ".Lelse:\n" +
                "#LoadIntConst(2,x15)\n" +
                "movq $2, -32(%rbp)\n" +
                "#Copy(x15,x13)\n" +
                "movq -32(%rbp), %rax\n" +
                "movq %rax, -24(%rbp)\n" +
                "#Label(end)\n" +
                "\n" +
                ".Lend:\n" +
                "#Call(print_int,[x13],x17)\n" +
                "movq -24(%rbp), %rdi\n" +
                "callq print_int\n" +
                "movq %rax, -48(%rbp)\n" +
                "movq $0, %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret", assemblyCode);
    }

    @Test
    public void testMinIntPrint() throws Exception {
        List<Instruction> instructions = generateInstructions("print_int(-9223372036854775808);");
        AssemblyGenerator assemblyGenerator = new AssemblyGenerator();
        String assemblyCode = assemblyGenerator.generateAssembly(instructions);
        assertEquals(".extern print_int\n" +
                ".extern print_bool\n" +
                ".extern read_int\n" +
                ".section .text\n" +
                ".global main\n" +
                ".type main, @function\n" +
                "main:\n" +
                "pushq %rbp\n" +
                "movq %rsp, %rbp\n" +
                "subq $40, %rsp\n" +
                "#LoadIntConst(-9223372036854775808,x12)\n" +
                "movabsq $9223372036854775808, %rax\n" +
                "movq %rax, -8(%rbp)\n" +
                "#Call(unary_-,[x12],x13)\n" +
                "movq -8(%rbp), %rax\n" +
                "negq %rax\n" +
                "movq %rax, -24(%rbp)\n" +
                "#Call(print_int,[x13],x15)\n" +
                "movq -24(%rbp), %rdi\n" +
                "callq print_int\n" +
                "movq %rax, -40(%rbp)\n" +
                "movq $0, %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret", assemblyCode);
    }

    @Test
    public void testWhileIf() throws Exception {
        List<Instruction> instructions = generateInstructions("var i = 0;\n" +
                "while i <= 3 do {\n" +
                "        if i % 2 == 1 then {\n" +
                "            print_int(i);\n" +
                "        }\n" +
                "        i = i + 1;\n" +
                "    }");
        AssemblyGenerator assemblyGenerator = new AssemblyGenerator();
        String assemblyCode = assemblyGenerator.generateAssembly(instructions);
        assertEquals(".extern print_int\n" +
                ".extern print_bool\n" +
                ".extern read_int\n" +
                ".section .text\n" +
                ".global main\n" +
                ".type main, @function\n" +
                "main:\n" +
                "pushq %rbp\n" +
                "movq %rsp, %rbp\n" +
                "subq $128, %rsp\n" +
                "#LoadIntConst(0,x12)\n" +
                "movq $0, -8(%rbp)\n" +
                "#Copy(x12,x13)\n" +
                "movq -8(%rbp), %rax\n" +
                "movq %rax, -16(%rbp)\n" +
                "#Label(while_start)\n" +
                "\n" +
                ".Lwhile_start:\n" +
                "#LoadIntConst(3,x15)\n" +
                "movq $3, -24(%rbp)\n" +
                "#Call(<=,[x13, x15],x16)\n" +
                "xor %rax, %rax\n" +
                "movq -16(%rbp), %rdx\n" +
                "cmpq -24(%rbp), %rdx\n" +
                "setle %al\n" +
                "movq %rax, -40(%rbp)\n" +
                "#CondJump(x16,Label(do),Label(end))\n" +
                "cmpq $0, -40(%rbp)\n" +
                "jne .Ldo\n" +
                "jmp .Lend\n" +
                "#Label(do)\n" +
                "\n" +
                ".Ldo:\n" +
                "#LoadIntConst(2,x17)\n" +
                "movq $2, -48(%rbp)\n" +
                "#Call(%,[x13, x17],x18)\n" +
                "movq -16(%rbp), %rax\n" +
                "cqto\n" +
                "idivq -48(%rbp)\n" +
                "movq %rdx, %rax\n" +
                "movq %rax, -64(%rbp)\n" +
                "#LoadIntConst(1,x19)\n" +
                "movq $1, -72(%rbp)\n" +
                "#Call(==,[x18, x19],x20)\n" +
                "xor %rax, %rax\n" +
                "movq -64(%rbp), %rdx\n" +
                "cmpq -72(%rbp), %rdx\n" +
                "sete %al\n" +
                "movq %rax, -88(%rbp)\n" +
                "#CondJump(x20,Label(then),Label(end0))\n" +
                "cmpq $0, -88(%rbp)\n" +
                "jne .Lthen\n" +
                "jmp .Lend0\n" +
                "#Label(then)\n" +
                "\n" +
                ".Lthen:\n" +
                "#Call(print_int,[x13],x22)\n" +
                "movq -16(%rbp), %rdi\n" +
                "callq print_int\n" +
                "movq %rax, -104(%rbp)\n" +
                "#Label(end0)\n" +
                "\n" +
                ".Lend0:\n" +
                "#LoadIntConst(1,x25)\n" +
                "movq $1, -112(%rbp)\n" +
                "#Call(+,[x13, x25],x26)\n" +
                "movq -16(%rbp), %rax\n" +
                "addq -112(%rbp), %rax\n" +
                "movq %rax, -128(%rbp)\n" +
                "#Copy(x26,x13)\n" +
                "movq -128(%rbp), %rax\n" +
                "movq %rax, -16(%rbp)\n" +
                "#Jump(Label(while_start))\n" +
                "jmp .Lwhile_start\n" +
                "#Label(end)\n" +
                "\n" +
                ".Lend:\n" +
                "movq $0, %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret", assemblyCode);
    }

    @Test
    public void testBlockStatement() throws Exception {
        List<Instruction> instructions = generateInstructions("{ true; 1 + 2 } + 3");
        AssemblyGenerator assemblyGenerator = new AssemblyGenerator();
        String assemblyCode = assemblyGenerator.generateAssembly(instructions);
        assertEquals(".extern print_int\n" +
                ".extern print_bool\n" +
                ".extern read_int\n" +
                ".section .text\n" +
                ".global main\n" +
                ".type main, @function\n" +
                "main:\n" +
                "pushq %rbp\n" +
                "movq %rsp, %rbp\n" +
                "subq $72, %rsp\n" +
                "#LoadBoolConst(true,x12)\n" +
                "movq $1, -8(%rbp)\n" +
                "#LoadIntConst(1,x13)\n" +
                "movq $1, -16(%rbp)\n" +
                "#LoadIntConst(2,x14)\n" +
                "movq $2, -24(%rbp)\n" +
                "#Call(+,[x13, x14],x15)\n" +
                "movq -16(%rbp), %rax\n" +
                "addq -24(%rbp), %rax\n" +
                "movq %rax, -40(%rbp)\n" +
                "#LoadIntConst(3,x16)\n" +
                "movq $3, -48(%rbp)\n" +
                "#Call(+,[x15, x16],x17)\n" +
                "movq -40(%rbp), %rax\n" +
                "addq -48(%rbp), %rax\n" +
                "movq %rax, -56(%rbp)\n" +
                "#Call(print_int,[x17],x19)\n" +
                "movq -56(%rbp), %rdi\n" +
                "callq print_int\n" +
                "movq %rax, -72(%rbp)\n" +
                "movq $0, %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret", assemblyCode);
    }

    @Test
    public void testBreakStatement() throws Exception {
        List<Instruction> instructions = generateInstructions("while true do {\n" +
                "        break\n" +
                "}");
        AssemblyGenerator assemblyGenerator = new AssemblyGenerator();
        String assemblyCode = assemblyGenerator.generateAssembly(instructions);
        assertEquals(".extern print_int\n" +
                ".extern print_bool\n" +
                ".extern read_int\n" +
                ".section .text\n" +
                ".global main\n" +
                ".type main, @function\n" +
                "main:\n" +
                "pushq %rbp\n" +
                "movq %rsp, %rbp\n" +
                "subq $8, %rsp\n" +
                "#Label(while_start)\n" +
                "\n" +
                ".Lwhile_start:\n" +
                "#LoadBoolConst(true,x12)\n" +
                "movq $1, -8(%rbp)\n" +
                "#CondJump(x12,Label(do),Label(end))\n" +
                "cmpq $0, -8(%rbp)\n" +
                "jne .Ldo\n" +
                "jmp .Lend\n" +
                "#Label(do)\n" +
                "\n" +
                ".Ldo:\n" +
                "#Jump(Label(end))\n" +
                "jmp .Lend\n" +
                "#Jump(Label(while_start))\n" +
                "jmp .Lwhile_start\n" +
                "#Label(end)\n" +
                "\n" +
                ".Lend:\n" +
                "movq $0, %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret", assemblyCode);
    }

    @Test
    public void testFunctionDefinition() throws Exception {
        List<Instruction> instructions = generateInstructions("fun f(x:Bool, y:Int):Int {\n" +
            " if (x) then {\n" +
                    "  return y\n" +
                    " } else {\n" +
                    "  return 2\n" +
                    " }\n" +
                    "}\n" +
                    "\n" +
                    "f(true, 5)");
        AssemblyGenerator assemblyGenerator = new AssemblyGenerator();
        String assemblyCode = assemblyGenerator.generateAssembly(instructions);
        assertEquals(".extern print_int\n" +
                ".extern print_bool\n" +
                ".extern read_int\n" +
                ".section .text\n" +
                ".global f\n" +
                ".type f, @function\n" +
                "f:\n" +
                "pushq %rbp\n" +
                "movq %rsp, %rbp\n" +
                "movq %rdi, -8(%rbp)\n" +
                "movq %rsi, -16(%rbp)\n" +
                "subq $32, %rsp\n" +
                "#CondJump(x,Label(then),Label(else))\n" +
                "cmpq $0, -8(%rbp)\n" +
                "jne .Lthen\n" +
                "jmp .Lelse\n" +
                "#Label(then)\n" +
                "\n" +
                ".Lthen:\n" +
                "#Return(y)\n" +
                "movq -16(%rbp), %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret\n" +
                "#Copy(y,x12)\n" +
                "movq -16(%rbp), %rax\n" +
                "movq %rax, -24(%rbp)\n" +
                "#Jump(Label(end))\n" +
                "jmp .Lend\n" +
                "#Label(else)\n" +
                "\n" +
                ".Lelse:\n" +
                "#LoadIntConst(2,x13)\n" +
                "movq $2, -32(%rbp)\n" +
                "#Return(x13)\n" +
                "movq -32(%rbp), %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret\n" +
                "#Copy(x13,x12)\n" +
                "movq -32(%rbp), %rax\n" +
                "movq %rax, -24(%rbp)\n" +
                "#Label(end)\n" +
                "\n" +
                ".Lend:\n" +
                "movq $0, %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret\n" +
                ".global main\n" +
                ".type main, @function\n" +
                "main:\n" +
                "pushq %rbp\n" +
                "movq %rsp, %rbp\n" +
                "subq $48, %rsp\n" +
                "#LoadBoolConst(true,x14)\n" +
                "movq $1, -8(%rbp)\n" +
                "#LoadIntConst(5,x15)\n" +
                "movq $5, -16(%rbp)\n" +
                "#Call(f,[x14, x15],x16)\n" +
                "movq -8(%rbp), %rdi\n" +
                "movq -16(%rbp), %rsi\n" +
                "callq f\n" +
                "movq %rax, -32(%rbp)\n" +
                "#Call(print_int,[x16],x18)\n" +
                "movq -32(%rbp), %rdi\n" +
                "callq print_int\n" +
                "movq %rax, -48(%rbp)\n" +
                "movq $0, %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret", assemblyCode);
    }

    @Test
    public void testFunctionWithMultiplication() throws Exception {
        List<Instruction> instructions = generateInstructions("fun square(x: Int): Int {\n" +
                "    return x * x;\n" +
                "}\n" +
                "\n" +
                "square(3)\n");
        AssemblyGenerator assemblyGenerator = new AssemblyGenerator();
        String assemblyCode = assemblyGenerator.generateAssembly(instructions);
        assertEquals(".extern print_int\n" +
                ".extern print_bool\n" +
                ".extern read_int\n" +
                ".section .text\n" +
                ".global square\n" +
                ".type square, @function\n" +
                "square:\n" +
                "pushq %rbp\n" +
                "movq %rsp, %rbp\n" +
                "movq %rdi, -16(%rbp)\n" +
                "subq $24, %rsp\n" +
                "#Call(*,[x, x],x12)\n" +
                "movq -16(%rbp), %rax\n" +
                "imulq -16(%rbp), %rax\n" +
                "movq %rax, -24(%rbp)\n" +
                "#Return(x12)\n" +
                "movq -24(%rbp), %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret\n" +
                "movq $0, %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret\n" +
                ".global main\n" +
                ".type main, @function\n" +
                "main:\n" +
                "pushq %rbp\n" +
                "movq %rsp, %rbp\n" +
                "subq $40, %rsp\n" +
                "#LoadIntConst(3,x14)\n" +
                "movq $3, -8(%rbp)\n" +
                "#Call(square,[x14],x15)\n" +
                "movq -8(%rbp), %rdi\n" +
                "callq square\n" +
                "movq %rax, -24(%rbp)\n" +
                "#Call(print_int,[x15],x17)\n" +
                "movq -24(%rbp), %rdi\n" +
                "callq print_int\n" +
                "movq %rax, -40(%rbp)\n" +
                "movq $0, %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret", assemblyCode);
    }

    @Test
    public void testFunctionDefinitionWithSemicolon() throws Exception {
        List<Instruction> instructions = generateInstructions("fun f(x:Bool, y:Int):Int {\n" +
                " if (x) then {\n" +
                "  return y;\n" +
                " } else {\n" +
                "  return 2;\n" +
                " }\n" +
                "}\n" +
                "\n" +
                "f(true, 5)");
        AssemblyGenerator assemblyGenerator = new AssemblyGenerator();
        String assemblyCode = assemblyGenerator.generateAssembly(instructions);
        assertEquals(".extern print_int\n" +
                ".extern print_bool\n" +
                ".extern read_int\n" +
                ".section .text\n" +
                ".global f\n" +
                ".type f, @function\n" +
                "f:\n" +
                "pushq %rbp\n" +
                "movq %rsp, %rbp\n" +
                "movq %rdi, -8(%rbp)\n" +
                "movq %rsi, -16(%rbp)\n" +
                "subq $48, %rsp\n" +
                "#CondJump(x,Label(then),Label(else))\n" +
                "cmpq $0, -8(%rbp)\n" +
                "jne .Lthen\n" +
                "jmp .Lelse\n" +
                "#Label(then)\n" +
                "\n" +
                ".Lthen:\n" +
                "#Return(y)\n" +
                "movq -16(%rbp), %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret\n" +
                "#Copy(x13,x12)\n" +
                "movq -24(%rbp), %rax\n" +
                "movq %rax, -32(%rbp)\n" +
                "#Jump(Label(end))\n" +
                "jmp .Lend\n" +
                "#Label(else)\n" +
                "\n" +
                ".Lelse:\n" +
                "#LoadIntConst(2,x14)\n" +
                "movq $2, -40(%rbp)\n" +
                "#Return(x14)\n" +
                "movq -40(%rbp), %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret\n" +
                "#Copy(x15,x12)\n" +
                "movq -48(%rbp), %rax\n" +
                "movq %rax, -32(%rbp)\n" +
                "#Label(end)\n" +
                "\n" +
                ".Lend:\n" +
                "movq $0, %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret\n" +
                ".global main\n" +
                ".type main, @function\n" +
                "main:\n" +
                "pushq %rbp\n" +
                "movq %rsp, %rbp\n" +
                "subq $48, %rsp\n" +
                "#LoadBoolConst(true,x16)\n" +
                "movq $1, -8(%rbp)\n" +
                "#LoadIntConst(5,x17)\n" +
                "movq $5, -16(%rbp)\n" +
                "#Call(f,[x16, x17],x18)\n" +
                "movq -8(%rbp), %rdi\n" +
                "movq -16(%rbp), %rsi\n" +
                "callq f\n" +
                "movq %rax, -32(%rbp)\n" +
                "#Call(print_int,[x18],x20)\n" +
                "movq -32(%rbp), %rdi\n" +
                "callq print_int\n" +
                "movq %rax, -48(%rbp)\n" +
                "movq $0, %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret", assemblyCode);
    }

    @Test
    public void testMultipleFunctionDefinition() throws Exception {
        List<Instruction> instructions = generateInstructions("fun f(x: Int): Int {\n" +
                "    return g(x + 1);\n" +
                "}\n" +
                "fun g(x: Int): Int {\n" +
                "    print_int(x);\n" +
                "    if x < 5 then {\n" +
                "        return f(x);\n" +
                "    } else {\n" +
                "        return x;\n" +
                "    }\n" +
                "}\n" +
                "f(1) * 100");
        AssemblyGenerator assemblyGenerator = new AssemblyGenerator();
        String assemblyCode = assemblyGenerator.generateAssembly(instructions);
        assertEquals(".extern print_int\n" +
                ".extern print_bool\n" +
                ".extern read_int\n" +
                ".section .text\n" +
                ".global f\n" +
                ".type f, @function\n" +
                "f:\n" +
                "pushq %rbp\n" +
                "movq %rsp, %rbp\n" +
                "movq %rdi, -24(%rbp)\n" +
                "subq $48, %rsp\n" +
                "#LoadIntConst(1,x12)\n" +
                "movq $1, -8(%rbp)\n" +
                "#Call(+,[x, x12],x13)\n" +
                "movq -24(%rbp), %rax\n" +
                "addq -8(%rbp), %rax\n" +
                "movq %rax, -32(%rbp)\n" +
                "#Call(g,[x13],x14)\n" +
                "movq -32(%rbp), %rdi\n" +
                "callq g\n" +
                "movq %rax, -48(%rbp)\n" +
                "#Return(x14)\n" +
                "movq -48(%rbp), %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret\n" +
                "movq $0, %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret\n" +
                ".global g\n" +
                ".type g, @function\n" +
                "g:\n" +
                "pushq %rbp\n" +
                "movq %rsp, %rbp\n" +
                "movq %rdi, -16(%rbp)\n" +
                "subq $88, %rsp\n" +
                "#Call(print_int,[x],x16)\n" +
                "movq -16(%rbp), %rdi\n" +
                "callq print_int\n" +
                "movq %rax, -24(%rbp)\n" +
                "#LoadIntConst(5,x17)\n" +
                "movq $5, -32(%rbp)\n" +
                "#Call(<,[x, x17],x18)\n" +
                "xor %rax, %rax\n" +
                "movq -16(%rbp), %rdx\n" +
                "cmpq -32(%rbp), %rdx\n" +
                "setl %al\n" +
                "movq %rax, -48(%rbp)\n" +
                "#CondJump(x18,Label(then),Label(else))\n" +
                "cmpq $0, -48(%rbp)\n" +
                "jne .Lthen\n" +
                "jmp .Lelse\n" +
                "#Label(then)\n" +
                "\n" +
                ".Lthen:\n" +
                "#Call(f,[x],x20)\n" +
                "movq -16(%rbp), %rdi\n" +
                "callq f\n" +
                "movq %rax, -64(%rbp)\n" +
                "#Return(x20)\n" +
                "movq -64(%rbp), %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret\n" +
                "#Copy(x21,x19)\n" +
                "movq -72(%rbp), %rax\n" +
                "movq %rax, -80(%rbp)\n" +
                "#Jump(Label(end))\n" +
                "jmp .Lend\n" +
                "#Label(else)\n" +
                "\n" +
                ".Lelse:\n" +
                "#Return(x)\n" +
                "movq -16(%rbp), %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret\n" +
                "#Copy(x22,x19)\n" +
                "movq -88(%rbp), %rax\n" +
                "movq %rax, -80(%rbp)\n" +
                "#Label(end)\n" +
                "\n" +
                ".Lend:\n" +
                "movq $0, %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret\n" +
                ".global main\n" +
                ".type main, @function\n" +
                "main:\n" +
                "pushq %rbp\n" +
                "movq %rsp, %rbp\n" +
                "subq $64, %rsp\n" +
                "#LoadIntConst(1,x23)\n" +
                "movq $1, -8(%rbp)\n" +
                "#Call(f,[x23],x24)\n" +
                "movq -8(%rbp), %rdi\n" +
                "callq f\n" +
                "movq %rax, -24(%rbp)\n" +
                "#LoadIntConst(100,x25)\n" +
                "movq $100, -32(%rbp)\n" +
                "#Call(*,[x24, x25],x26)\n" +
                "movq -24(%rbp), %rax\n" +
                "imulq -32(%rbp), %rax\n" +
                "movq %rax, -48(%rbp)\n" +
                "#Call(print_int,[x26],x28)\n" +
                "movq -48(%rbp), %rdi\n" +
                "callq print_int\n" +
                "movq %rax, -64(%rbp)\n" +
                "movq $0, %rax\n" +
                "movq %rbp, %rsp\n" +
                "popq %rbp\n" +
                "ret", assemblyCode);
    }

    private List<Instruction> generateInstructions(String sourceCode) throws ParserException, TypeCheckerException,
            IRGenerationException {
        Parser parser = new Parser(new Tokenizer().tokenize(sourceCode, "TestFile.dl"));
        TypeChecker typeChecker = new TypeChecker();
        Expression expression = parser.parse();
        typeChecker.checkType(expression);
        IRGenerator irGenerator = new IRGenerator(new CommonStatics());
        return irGenerator.generateIR(expression);
    }
}
