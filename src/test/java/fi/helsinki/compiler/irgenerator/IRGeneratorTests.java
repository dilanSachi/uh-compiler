package fi.helsinki.compiler.irgenerator;

import fi.helsinki.compiler.common.expressions.Expression;
import fi.helsinki.compiler.common.types.Type;
import fi.helsinki.compiler.common.types.UnitType;
import fi.helsinki.compiler.exceptions.IRGenerationException;
import fi.helsinki.compiler.exceptions.ParserException;
import fi.helsinki.compiler.exceptions.TypeCheckerException;
import fi.helsinki.compiler.irgenerator.instructions.Instruction;
import fi.helsinki.compiler.parser.Parser;
import fi.helsinki.compiler.tokenizer.Tokenizer;
import fi.helsinki.compiler.typechecker.TypeChecker;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    public void testWhileBlock() throws ParserException, TypeCheckerException, IRGenerationException {
        Parser parser = new Parser(new Tokenizer().tokenize("while 3 > 12 do {1 + 1}", "TestFile.dl"));
        TypeChecker typeChecker = new TypeChecker();
        Expression expression = parser.parse();
        typeChecker.checkType(expression);
        IRGenerator irGenerator = new IRGenerator();
        List<Instruction> instructions = irGenerator.generateIR(expression);
        assertEquals(instructions.size(), 12);
        assertEquals(instructions.get(0).toString(), "Label(start)");
        assertEquals(instructions.get(1).toString(), "Label(while_start)");
        assertEquals(instructions.get(2).toString(), "LoadIntConst(3,x2)");
        assertEquals(instructions.get(3).toString(), "LoadIntConst(12,x3)");
        assertEquals(instructions.get(4).toString(), "Call(>,[x2, x3],x4)");
        assertEquals(instructions.get(5).toString(), "CondJump(x4,Label(do),Label(end))");
        assertEquals(instructions.get(6).toString(), "Label(do)");
        assertEquals(instructions.get(7).toString(), "LoadIntConst(1,x5)");
        assertEquals(instructions.get(8).toString(), "LoadIntConst(1,x6)");
        assertEquals(instructions.get(9).toString(), "Call(+,[x5, x6],x7)");
        assertEquals(instructions.get(10).toString(), "Jump(Label(while_start))");
        assertEquals(instructions.get(11).toString(), "Label(end)");
    }

    @Test @Disabled
    public void testCodeBlock() throws ParserException, TypeCheckerException, IRGenerationException {
        Parser parser = new Parser(new Tokenizer().tokenize("var a = 30; var b = 2; var c = -2; var d = 2; var e = 221;while a > 2 do {\n" +
                "    if b <= 14 then {\n" +
                "         while c - 2 < 3 do {\n" +
                "               d = d * 3;\n" +
                "               c = c + 1;\n" +
                "         }\n" +
                "    } else {\n" +
                "         e = e % 10;\na = a - 1;b = b + 1;" +
                "    }\n" +
                "}print_int(a);print_int(b);print_int(c);print_int(d);print_int(e);", "TestFile.dl"));
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
