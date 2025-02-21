package fi.helsinki.compiler.irgenerator;

import fi.helsinki.compiler.common.expressions.Expression;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IRGeneratorTests {

    @Test
    public void testBasicAddition() throws ParserException, TypeCheckerException, IRGenerationException {
        Parser parser = new Parser(new Tokenizer().tokenize("var x = 5; var y = 12; if y > 12 then {x = x + 4} else {x = x - y}", "TestFile.dl"));
        TypeChecker typeChecker = new TypeChecker();
        Expression expression = parser.parse();
        typeChecker.checkType(expression);
        IRGenerator irGenerator = new IRGenerator();
        List<Instruction> instructions = irGenerator.generateIR(expression);
//        assertEquals(instructions.size(), 1);
    }
}
