package fi.helsinki.compiler.irgenerator;

import fi.helsinki.compiler.common.CommonStatics;
import fi.helsinki.compiler.common.expressions.Expression;
import fi.helsinki.compiler.exceptions.IRGenerationException;
import fi.helsinki.compiler.exceptions.ParserException;
import fi.helsinki.compiler.exceptions.TypeCheckerException;
import fi.helsinki.compiler.irgenerator.instructions.Instruction;
import fi.helsinki.compiler.irgenerator.instructions.Label;
import fi.helsinki.compiler.parser.Parser;
import fi.helsinki.compiler.tokenizer.Tokenizer;
import fi.helsinki.compiler.typechecker.TypeChecker;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IRGeneratorTests {

    @Test
    public void testBasicAddition() throws ParserException, TypeCheckerException, IRGenerationException {
        List<Instruction> instructions = generateInstructions("1 + 2");
        instructions.addFirst(new Label(new CommonStatics(), "start", null));
        assertEquals(instructions.size(), 5);
//        assertEquals(instructions.get(0).toString(), "Label(start)");
        assertEquals(instructions.get(1).toString(), "LoadIntConst(1,x12)");
        assertEquals(instructions.get(2).toString(), "LoadIntConst(2,x13)");
        assertEquals(instructions.get(3).toString(), "Call(+,[x12, x13],x14)");
        assertEquals(instructions.get(4).toString(), "Call(print_int,[x14],x16)");
    }

    @Test
    public void testIfElseBlock() throws ParserException, TypeCheckerException, IRGenerationException {
        List<Instruction> instructions = generateInstructions("if 3 > 12 then {1 + 1} else {2 + 3}");
        instructions.addFirst(new Label(new CommonStatics(), "start", null));
        assertEquals(instructions.size(), 18);
//        assertEquals(instructions.get(0).toString(), "Label(start)");
        assertEquals(instructions.get(1).toString(), "LoadIntConst(3,x12)");
        assertEquals(instructions.get(2).toString(), "LoadIntConst(12,x13)");
        assertEquals(instructions.get(3).toString(), "Call(>,[x12, x13],x14)");
        assertEquals(instructions.get(4).toString(), "CondJump(x14,Label(then),Label(else))");
        assertEquals(instructions.get(5).toString(), "Label(then)");
        assertEquals(instructions.get(6).toString(), "LoadIntConst(1,x16)");
        assertEquals(instructions.get(7).toString(), "LoadIntConst(1,x17)");
        assertEquals(instructions.get(8).toString(), "Call(+,[x16, x17],x18)");
        assertEquals(instructions.get(9).toString(), "Copy(x18,x15)");
        assertEquals(instructions.get(10).toString(), "Jump(Label(end))");
        assertEquals(instructions.get(11).toString(), "Label(else)");
        assertEquals(instructions.get(12).toString(), "LoadIntConst(2,x19)");
        assertEquals(instructions.get(13).toString(), "LoadIntConst(3,x20)");
        assertEquals(instructions.get(14).toString(), "Call(+,[x19, x20],x21)");
        assertEquals(instructions.get(15).toString(), "Copy(x21,x15)");
        assertEquals(instructions.get(16).toString(), "Label(end)");
        assertEquals(instructions.get(17).toString(), "Call(print_int,[x15],x23)");
    }

    @Test
    public void testIfElseBlockWithoutReturn() throws ParserException, TypeCheckerException, IRGenerationException {
        List<Instruction> instructions = generateInstructions("if 3 > 12 then {1 + 1;} else {2 + 3;}");
        instructions.addFirst(new Label(new CommonStatics(), "start", null));
        assertEquals(instructions.size(), 17);
//        assertEquals(instructions.get(0).toString(), "Label(start)");
        assertEquals(instructions.get(1).toString(), "LoadIntConst(3,x12)");
        assertEquals(instructions.get(2).toString(), "LoadIntConst(12,x13)");
        assertEquals(instructions.get(3).toString(), "Call(>,[x12, x13],x14)");
        assertEquals(instructions.get(4).toString(), "CondJump(x14,Label(then),Label(else))");
        assertEquals(instructions.get(5).toString(), "Label(then)");
//        assertEquals(instructions.get(6).toString(), "LoadIntConst(1,x15)");
//        assertEquals(instructions.get(7).toString(), "LoadIntConst(1,x16)");
//        assertEquals(instructions.get(8).toString(), "Call(+,[x15, x16],x17)");
//        assertEquals(instructions.get(9).toString(), "Copy(x18,x14)");
        assertEquals(instructions.get(10).toString(), "Jump(Label(end))");
        assertEquals(instructions.get(11).toString(), "Label(else)");
//        assertEquals(instructions.get(12).toString(), "LoadIntConst(2,x19)");
//        assertEquals(instructions.get(13).toString(), "LoadIntConst(3,x20)");
//        assertEquals(instructions.get(14).toString(), "Call(+,[x19, x20],x21)");
//        assertEquals(instructions.get(15).toString(), "Copy(x22,x14)");
        assertEquals(instructions.get(16).toString(), "Label(end)");
    }

    @Test
    public void testIfBlock() throws ParserException, TypeCheckerException, IRGenerationException {
        List<Instruction> instructions = generateInstructions("if 3 > 12 then {1 + 1}");
        instructions.addFirst(new Label(new CommonStatics(), "start", null));
        assertEquals(instructions.size(), 10);
//        assertEquals(instructions.get(0).toString(), "Label(start)");
//        assertEquals(instructions.get(1).toString(), "LoadIntConst(3,x11)");
//        assertEquals(instructions.get(2).toString(), "LoadIntConst(12,x12)");
//        assertEquals(instructions.get(3).toString(), "Call(>,[x11, x12],x13)");
//        assertEquals(instructions.get(4).toString(), "CondJump(x13,Label(then),Label(end))");
        assertEquals(instructions.get(5).toString(), "Label(then)");
//        assertEquals(instructions.get(6).toString(), "LoadIntConst(1,x14)");
//        assertEquals(instructions.get(7).toString(), "LoadIntConst(1,x15)");
//        assertEquals(instructions.get(8).toString(), "Call(+,[x14, x15],x16)");
        assertEquals(instructions.get(9).toString(), "Label(end)");
    }

    @Test
    public void testWhileBlock() throws ParserException, TypeCheckerException, IRGenerationException {
        List<Instruction> instructions = generateInstructions("while 3 > 12 do {1 + 1}");
        instructions.addFirst(new Label(new CommonStatics(), "start", null));
        assertEquals(instructions.size(), 12);
//        assertEquals(instructions.get(0).toString(), "Label(start)");
//        assertEquals(instructions.get(1).toString(), "Label(while_start)");
//        assertEquals(instructions.get(2).toString(), "LoadIntConst(3,x11)");
//        assertEquals(instructions.get(3).toString(), "LoadIntConst(12,x12)");
//        assertEquals(instructions.get(4).toString(), "Call(>,[x11, x12],x13)");
//        assertEquals(instructions.get(5).toString(), "CondJump(x13,Label(do),Label(end))");
        assertEquals(instructions.get(6).toString(), "Label(do)");
//        assertEquals(instructions.get(7).toString(), "LoadIntConst(1,x14)");
//        assertEquals(instructions.get(8).toString(), "LoadIntConst(1,x15)");
//        assertEquals(instructions.get(9).toString(), "Call(+,[x14, x15],x16)");
        assertEquals(instructions.get(10).toString(), "Jump(Label(while_start))");
        assertEquals(instructions.get(11).toString(), "Label(end)");
    }

    @Test
    public void testVariableDefinition() throws ParserException, TypeCheckerException, IRGenerationException {
        List<Instruction> instructions = generateInstructions("var x:Int = 3;");
        instructions.addFirst(new Label(new CommonStatics(), "start", null));
        assertEquals(instructions.size(), 3);
//        assertEquals(instructions.get(0).toString(), "Label(start)");
//        assertEquals(instructions.get(1).toString(), "LoadIntConst(3,x11)");
//        assertEquals(instructions.get(2).toString(), "Copy(x11,x12)");
    }

    @Test
    public void testEquals() throws ParserException, TypeCheckerException, IRGenerationException {
        List<Instruction> instructions = generateInstructions("var x:Int = 3; x = 2 + 3");
        instructions.addFirst(new Label(new CommonStatics(), "start", null));
        assertEquals(instructions.size(), 8);
//        assertEquals(instructions.get(0).toString(), "Label(start)");
//        assertEquals(instructions.get(1).toString(), "LoadIntConst(3,x11)");
//        assertEquals(instructions.get(2).toString(), "Copy(x11,x12)");
//        assertEquals(instructions.get(3).toString(), "LoadIntConst(2,x14)");
//        assertEquals(instructions.get(4).toString(), "LoadIntConst(3,x15)");
//        assertEquals(instructions.get(5).toString(), "Call(+,[x14, x15],x16)");
//        assertEquals(instructions.get(6).toString(), "Copy(x16,x12)");
//        assertEquals(instructions.get(7).toString(), "Call(print_int,[x12],x18)");
    }

    @Test
    public void testEqualsWithEnd() throws ParserException, TypeCheckerException, IRGenerationException {
        List<Instruction> instructions = generateInstructions("var x:Int = 3; x = 2 + 3;");
        instructions.addFirst(new Label(new CommonStatics(), "start", null));
        assertEquals(instructions.size(), 7);
//        assertEquals(instructions.get(0).toString(), "Label(start)");
//        assertEquals(instructions.get(1).toString(), "LoadIntConst(3,x11)");
//        assertEquals(instructions.get(2).toString(), "Copy(x11,x12)");
//        assertEquals(instructions.get(3).toString(), "LoadIntConst(2,x14)");
//        assertEquals(instructions.get(4).toString(), "LoadIntConst(3,x15)");
//        assertEquals(instructions.get(5).toString(), "Call(+,[x14, x15],x16)");
//        assertEquals(instructions.get(6).toString(), "Copy(x16,x12)");
    }

    @Test
    public void testUnaryOps() throws ParserException, TypeCheckerException, IRGenerationException {
        List<Instruction> instructions = generateInstructions("-2");
        instructions.addFirst(new Label(new CommonStatics(), "start", null));
        assertEquals(instructions.size(), 4);
//        assertEquals(instructions.get(0).toString(), "Label(start)");
//        assertEquals(instructions.get(1).toString(), "LoadIntConst(2,x11)");
//        assertEquals(instructions.get(2).toString(), "Call(unary_-,[x11],x12)");
//        assertEquals(instructions.get(3).toString(), "Call(print_int,[x12],x15)");

        instructions = generateInstructions("-2;");
        instructions.addFirst(new Label(new CommonStatics(), "start", null));
        assertEquals(instructions.size(), 3);
//        assertEquals(instructions.get(0).toString(), "Label(start)");
//        assertEquals(instructions.get(1).toString(), "LoadIntConst(2,x11)");
//        assertEquals(instructions.get(2).toString(), "Call(unary_-,[x11],x12)");

        instructions = generateInstructions("not true;");
        instructions.addFirst(new Label(new CommonStatics(), "start", null));
        assertEquals(instructions.size(), 3);
//        assertEquals(instructions.get(0).toString(), "Label(start)");
//        assertEquals(instructions.get(1).toString(), "LoadBoolConst(true,x11)");
//        assertEquals(instructions.get(2).toString(), "Call(not,[x11],x12)");

        instructions = generateInstructions("not false");
        instructions.addFirst(new Label(new CommonStatics(), "start", null));
        assertEquals(instructions.size(), 4);
//        assertEquals(instructions.get(0).toString(), "Label(start)");
//        assertEquals(instructions.get(1).toString(), "LoadBoolConst(false,x11)");
//        assertEquals(instructions.get(2).toString(), "Call(not,[x11],x12)");
//        assertEquals(instructions.get(3).toString(), "Call(print_bool,[x12],x15)");
    }

    @Test
    public void testAndOrOps() throws ParserException, TypeCheckerException, IRGenerationException {
        List<Instruction> instructions = generateInstructions("true and true");
        instructions.addFirst(new Label(new CommonStatics(), "start", null));
        assertEquals(instructions.size(), 12);
//        assertEquals(instructions.get(0).toString(), "Label(start)");
//        assertEquals(instructions.get(1).toString(), "LoadIntConst(2,x11)");
//        assertEquals(instructions.get(2).toString(), "Call(unary_-,[x11],x12)");
//        assertEquals(instructions.get(3).toString(), "Call(print_int,[x12],x15)");

        instructions = generateInstructions("true and false;");
        instructions.addFirst(new Label(new CommonStatics(), "start", null));
        assertEquals(instructions.size(), 11);
//        assertEquals(instructions.get(0).toString(), "Label(start)");
//        assertEquals(instructions.get(1).toString(), "LoadIntConst(2,x11)");
//        assertEquals(instructions.get(2).toString(), "Call(unary_-,[x11],x12)");

        instructions = generateInstructions("true or true");
        instructions.addFirst(new Label(new CommonStatics(), "start", null));
        assertEquals(instructions.size(), 12);
//        assertEquals(instructions.get(0).toString(), "Label(start)");
//        assertEquals(instructions.get(1).toString(), "LoadBoolConst(true,x11)");
//        assertEquals(instructions.get(2).toString(), "Call(not,[x11],x12)");

        instructions = generateInstructions("true or false;");
        instructions.addFirst(new Label(new CommonStatics(), "start", null));
        assertEquals(instructions.size(), 11);
//        assertEquals(instructions.get(0).toString(), "Label(start)");
//        assertEquals(instructions.get(1).toString(), "LoadBoolConst(false,x11)");
//        assertEquals(instructions.get(2).toString(), "Call(not,[x11],x12)");
//        assertEquals(instructions.get(3).toString(), "Call(print_bool,[x12],x15)");
    }

    @Test
    public void testCodeBlock() throws ParserException, TypeCheckerException, IRGenerationException {
        List<Instruction> instructions = generateInstructions("var a = 30; var b = 2; var c = -2; var d = 2; var e = 221;while a > 2 do {\n" +
                "    if b <= 14 then {\n" +
                "         while c - 2 < 3 do {\n" +
                "               d = d * 3;\n" +
                "               c = c + 1;\n" +
                "         }\n" +
                "    } else {\n" +
                "         e = e % 10;\na = a - 1;b = b + 1;" +
                "    }\n" +
                "}print_int(a);print_int(b);print_int(c);print_int(d);print_int(e);");
        instructions.addFirst(new Label(new CommonStatics(), "start", null));
        assertEquals(instructions.size(), 57);
//        assertEquals(instructions.get(0).toString(), "Label(start)");
        assertEquals(instructions.get(1).toString(), "LoadIntConst(30,x12)");
        assertEquals(instructions.get(2).toString(), "Copy(x12,x13)");
        assertEquals(instructions.get(3).toString(), "LoadIntConst(2,x15)");
        assertEquals(instructions.get(4).toString(), "Copy(x15,x16)");
        assertEquals(instructions.get(5).toString(), "LoadIntConst(2,x18)");
        assertEquals(instructions.get(6).toString(), "Call(unary_-,[x18],x19)");
        assertEquals(instructions.get(7).toString(), "Copy(x19,x21)");
        assertEquals(instructions.get(8).toString(), "LoadIntConst(2,x23)");
        assertEquals(instructions.get(9).toString(), "Copy(x23,x24)");
        assertEquals(instructions.get(10).toString(), "LoadIntConst(221,x26)");
        assertEquals(instructions.get(11).toString(), "Copy(x26,x27)");
        assertEquals(instructions.get(12).toString(), "Label(while_start)");
        assertEquals(instructions.get(13).toString(), "LoadIntConst(2,x29)");
        assertEquals(instructions.get(14).toString(), "Call(>,[x13, x29],x30)");
//        assertEquals(instructions.get(15).toString(), "CondJump(x30,Label(do),Label(end))");
        assertEquals(instructions.get(16).toString(), "Label(do)");
        assertEquals(instructions.get(17).toString(), "LoadIntConst(14,x31)");
        assertEquals(instructions.get(18).toString(), "Call(<=,[x16, x31],x32)");
        assertEquals(instructions.get(19).toString(), "CondJump(x32,Label(then),Label(else))");
        assertEquals(instructions.get(20).toString(), "Label(then)");
//        assertEquals(instructions.get(21).toString(), "Label(while_start4)");
        assertEquals(instructions.get(22).toString(), "LoadIntConst(2,x34)");
        assertEquals(instructions.get(23).toString(), "Call(-,[x21, x34],x35)");
        assertEquals(instructions.get(24).toString(), "LoadIntConst(3,x36)");
        assertEquals(instructions.get(25).toString(), "Call(<,[x35, x36],x37)");
//        assertEquals(instructions.get(26).toString(), "CondJump(x37,Label(do2),Label(end3))");
//        assertEquals(instructions.get(27).toString(), "Label(do2)");
        assertEquals(instructions.get(28).toString(), "LoadIntConst(3,x38)");
        assertEquals(instructions.get(29).toString(), "Call(*,[x24, x38],x39)");
        assertEquals(instructions.get(30).toString(), "Copy(x39,x24)");
        assertEquals(instructions.get(31).toString(), "LoadIntConst(1,x40)");
        assertEquals(instructions.get(32).toString(), "Call(+,[x21, x40],x41)");
        assertEquals(instructions.get(33).toString(), "Copy(x41,x21)");
//        assertEquals(instructions.get(34).toString(), "Jump(Label(while_start4))");
//        assertEquals(instructions.get(35).toString(), "Label(end3)");
//        assertEquals(instructions.get(36).toString(), "Copy(unit,x16)");
//        assertEquals(instructions.get(37).toString(), "Jump(Label(end1))");
        assertEquals(instructions.get(38).toString(), "Label(else)");
        assertEquals(instructions.get(39).toString(), "LoadIntConst(10,x44)");
        assertEquals(instructions.get(40).toString(), "Call(%,[x27, x44],x45)");
        assertEquals(instructions.get(41).toString(), "Copy(x45,x27)");
        assertEquals(instructions.get(42).toString(), "LoadIntConst(1,x46)");
        assertEquals(instructions.get(43).toString(), "Call(-,[x13, x46],x47)");
        assertEquals(instructions.get(44).toString(), "Copy(x47,x13)");
        assertEquals(instructions.get(45).toString(), "LoadIntConst(1,x48)");
        assertEquals(instructions.get(46).toString(), "Call(+,[x16, x48],x49)");
        assertEquals(instructions.get(47).toString(), "Copy(x49,x16)");
//        assertEquals(instructions.get(48).toString(), "Copy(unit,x32)");
//        assertEquals(instructions.get(49).toString(), "Label(end1)");
        assertEquals(instructions.get(50).toString(), "Jump(Label(while_start))");
        assertEquals(instructions.get(51).toString(), "Label(end)");
        assertEquals(instructions.get(52).toString(), "Call(print_int,[x13],x52)");
        assertEquals(instructions.get(53).toString(), "Call(print_int,[x16],x53)");
        assertEquals(instructions.get(54).toString(), "Call(print_int,[x21],x54)");
        assertEquals(instructions.get(55).toString(), "Call(print_int,[x24],x55)");
        assertEquals(instructions.get(56).toString(), "Call(print_int,[x27],x56)");
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
