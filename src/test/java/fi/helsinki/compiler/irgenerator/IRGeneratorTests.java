package fi.helsinki.compiler.irgenerator;

import fi.helsinki.compiler.common.expressions.Expression;
import fi.helsinki.compiler.exceptions.IRGenerationException;
import fi.helsinki.compiler.exceptions.ParserException;
import fi.helsinki.compiler.exceptions.TypeCheckerException;
import fi.helsinki.compiler.irgenerator.instructions.Instruction;
import fi.helsinki.compiler.parser.Parser;
import fi.helsinki.compiler.tokenizer.Tokenizer;
import fi.helsinki.compiler.typechecker.TypeChecker;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IRGeneratorTests {

    @Test
    public void testBasicAddition() throws ParserException, TypeCheckerException, IRGenerationException {
        Parser parser = new Parser(new Tokenizer().tokenize("1 + 2", "TestFile.dl"));
        TypeChecker typeChecker = new TypeChecker();
        Expression expression = parser.parse();
        typeChecker.checkType(expression);
        IRGenerator irGenerator = new IRGenerator();
        List<Instruction> instructions = irGenerator.generateIR(expression);
        assertEquals(instructions.size(), 5);
        assertEquals(instructions.get(0).toString(), "Label(start)");
        assertEquals(instructions.get(1).toString(), "LoadIntConst(1,x2)");
        assertEquals(instructions.get(2).toString(), "LoadIntConst(2,x3)");
        assertEquals(instructions.get(3).toString(), "Call(+,[x2, x3],x4)");
        assertEquals(instructions.get(4).toString(), "Call(print_int,[x4],x6)");
    }

    @Test
    public void testIfElseBlock() throws ParserException, TypeCheckerException, IRGenerationException {
        Parser parser = new Parser(new Tokenizer().tokenize("if 3 > 12 then {1 + 1} else {2 + 3}", "TestFile.dl"));
        TypeChecker typeChecker = new TypeChecker();
        Expression expression = parser.parse();
        typeChecker.checkType(expression);
        IRGenerator irGenerator = new IRGenerator();
        List<Instruction> instructions = irGenerator.generateIR(expression);
        assertEquals(instructions.size(), 18);
        assertEquals(instructions.get(0).toString(), "Label(start)");
        assertEquals(instructions.get(1).toString(), "LoadIntConst(3,x2)");
        assertEquals(instructions.get(2).toString(), "LoadIntConst(12,x3)");
        assertEquals(instructions.get(3).toString(), "Call(>,[x2, x3],x4)");
        assertEquals(instructions.get(4).toString(), "CondJump(x4,Label(then),Label(else))");
        assertEquals(instructions.get(5).toString(), "Label(then)");
        assertEquals(instructions.get(6).toString(), "LoadIntConst(1,x6)");
        assertEquals(instructions.get(7).toString(), "LoadIntConst(1,x7)");
        assertEquals(instructions.get(8).toString(), "Call(+,[x6, x7],x8)");
        assertEquals(instructions.get(9).toString(), "Copy(x8,x5)");
        assertEquals(instructions.get(10).toString(), "Jump(Label(end))");
        assertEquals(instructions.get(11).toString(), "Label(else)");
        assertEquals(instructions.get(12).toString(), "LoadIntConst(2,x9)");
        assertEquals(instructions.get(13).toString(), "LoadIntConst(3,x10)");
        assertEquals(instructions.get(14).toString(), "Call(+,[x9, x10],x11)");
        assertEquals(instructions.get(15).toString(), "Copy(x11,x5)");
        assertEquals(instructions.get(16).toString(), "Label(end)");
        assertEquals(instructions.get(17).toString(), "Call(print_int,[x5],x13)");
    }

    @Test
    public void testIfElseBlockWithoutReturn() throws ParserException, TypeCheckerException, IRGenerationException {
        Parser parser = new Parser(new Tokenizer().tokenize("if 3 > 12 then {1 + 1;} else {2 + 3;}", "TestFile.dl"));
        TypeChecker typeChecker = new TypeChecker();
        Expression expression = parser.parse();
        typeChecker.checkType(expression);
        IRGenerator irGenerator = new IRGenerator();
        List<Instruction> instructions = irGenerator.generateIR(expression);
        assertEquals(instructions.size(), 17);
        assertEquals(instructions.get(0).toString(), "Label(start)");
        assertEquals(instructions.get(1).toString(), "LoadIntConst(3,x2)");
        assertEquals(instructions.get(2).toString(), "LoadIntConst(12,x3)");
        assertEquals(instructions.get(3).toString(), "Call(>,[x2, x3],x4)");
        assertEquals(instructions.get(4).toString(), "CondJump(x4,Label(then),Label(else))");
        assertEquals(instructions.get(5).toString(), "Label(then)");
        assertEquals(instructions.get(6).toString(), "LoadIntConst(1,x6)");
        assertEquals(instructions.get(7).toString(), "LoadIntConst(1,x7)");
        assertEquals(instructions.get(8).toString(), "Call(+,[x6, x7],x8)");
        assertEquals(instructions.get(9).toString(), "Copy(x9,x5)");
        assertEquals(instructions.get(10).toString(), "Jump(Label(end))");
        assertEquals(instructions.get(11).toString(), "Label(else)");
        assertEquals(instructions.get(12).toString(), "LoadIntConst(2,x10)");
        assertEquals(instructions.get(13).toString(), "LoadIntConst(3,x11)");
        assertEquals(instructions.get(14).toString(), "Call(+,[x10, x11],x12)");
        assertEquals(instructions.get(15).toString(), "Copy(x13,x5)");
        assertEquals(instructions.get(16).toString(), "Label(end)");
    }

    @Test
    public void testIfBlock() throws ParserException, TypeCheckerException, IRGenerationException {
        Parser parser = new Parser(new Tokenizer().tokenize("if 3 > 12 then {1 + 1}", "TestFile.dl"));
        TypeChecker typeChecker = new TypeChecker();
        Expression expression = parser.parse();
        typeChecker.checkType(expression);
        IRGenerator irGenerator = new IRGenerator();
        List<Instruction> instructions = irGenerator.generateIR(expression);
        assertEquals(instructions.size(), 10);
        assertEquals(instructions.get(0).toString(), "Label(start)");
        assertEquals(instructions.get(1).toString(), "LoadIntConst(3,x2)");
        assertEquals(instructions.get(2).toString(), "LoadIntConst(12,x3)");
        assertEquals(instructions.get(3).toString(), "Call(>,[x2, x3],x4)");
        assertEquals(instructions.get(4).toString(), "CondJump(x4,Label(then),Label(end))");
        assertEquals(instructions.get(5).toString(), "Label(then)");
        assertEquals(instructions.get(6).toString(), "LoadIntConst(1,x5)");
        assertEquals(instructions.get(7).toString(), "LoadIntConst(1,x6)");
        assertEquals(instructions.get(8).toString(), "Call(+,[x5, x6],x7)");
        assertEquals(instructions.get(9).toString(), "Label(end)");
    }
}
