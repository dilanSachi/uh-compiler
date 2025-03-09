package fi.helsinki.compiler.assemblygen;

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
    public void testBasic() throws ParserException, TypeCheckerException, IRGenerationException, ClassNotFoundException, IllegalAccessException {
        List<Instruction> instructions = generateInstructions("{ var x = true; if x then {1} else {2}; }");
        AssemblyGenerator assemblyGenerator = new AssemblyGenerator();
        String assemblyCode = assemblyGenerator.generateAssembly(instructions);
        assertEquals(".extern print_int\n" +
                ".extern print_bool\n" +
                ".extern read_int\n" +
                ".global main\n" +
                ".type main, @function\n" +
                ".section .text\n" +
                "main:\n" +
                "pushq %rbp\n" +
                "movq %rsp, %rbp\n" +
                "subq $40, %rsp\n" +
                "#Label(start)\n" +
                "\n" +
                ".Lstart\n" +
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
                ".Lthen\n" +
                "#LoadIntConst(1,x16)\n" +
                "movq $1, -24(%rbp)\n" +
                "#Copy(x16,x15)\n" +
                "movq -24(%rbp), %rax\n" +
                "movq %rax, -32(%rbp)\n" +
                "#Jump(Label(end))\n" +
                "jmp .Lend\n" +
                "#Label(else)\n" +
                "\n" +
                ".Lelse\n" +
                "#LoadIntConst(2,x17)\n" +
                "movq $2, -40(%rbp)\n" +
                "#Copy(x17,x15)\n" +
                "movq -40(%rbp), %rax\n" +
                "movq %rax, -32(%rbp)\n" +
                "#Label(end)\n" +
                "\n" +
                ".Lend\n" +
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
        IRGenerator irGenerator = new IRGenerator();
        return irGenerator.generateIR(expression);
    }
}
