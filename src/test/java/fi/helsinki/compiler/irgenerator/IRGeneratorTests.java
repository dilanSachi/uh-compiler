package fi.helsinki.compiler.irgenerator;

import fi.helsinki.compiler.common.expressions.Expression;
import fi.helsinki.compiler.common.types.IntType;
import fi.helsinki.compiler.common.types.Type;
import fi.helsinki.compiler.exceptions.IRGenerationException;
import fi.helsinki.compiler.exceptions.ParserException;
import fi.helsinki.compiler.exceptions.TypeCheckerException;
import fi.helsinki.compiler.irgenerator.instructions.Instruction;
import fi.helsinki.compiler.parser.Parser;
import fi.helsinki.compiler.tokenizer.Tokenizer;
import fi.helsinki.compiler.typechecker.TypeChecker;
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
        assertEquals(instructions.get(1).toString(), "LoadIntConst(1,x1)");
        assertEquals(instructions.get(2).toString(), "LoadIntConst(2,x2)");
        assertEquals(instructions.get(3).toString(), "Call(+,[x1, x2],x3)");
        assertEquals(instructions.get(4).toString(), "Call(print_int,[x3],x5)");
    }

    @Test
    public void testIfElseBlock() throws ParserException, TypeCheckerException, IRGenerationException {
        Parser parser = new Parser(new Tokenizer().tokenize("if 3 > 12 then {1 + 1;} else {2 + 3;}", "TestFile.dl"));
        TypeChecker typeChecker = new TypeChecker();
        Expression expression = parser.parse();
        typeChecker.checkType(expression);
        IRGenerator irGenerator = new IRGenerator();
        List<Instruction> instructions = irGenerator.generateIR(expression);
        assertEquals(instructions.size(), 14);
        assertEquals(instructions.get(0).toString(), "Label(start)");
        assertEquals(instructions.get(1).toString(), "LoadIntConst(1,x1)");
        assertEquals(instructions.get(2).toString(), "LoadIntConst(2,x2)");
        assertEquals(instructions.get(3).toString(), "Call(+,[x1, x2],x3)");
        assertEquals(instructions.get(4).toString(), "Call(print_int,[x3],x5)");
    }
}
