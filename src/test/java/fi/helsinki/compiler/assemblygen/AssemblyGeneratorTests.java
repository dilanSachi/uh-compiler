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

public class AssemblyGeneratorTests {

    @Test
    public void testBasic() throws ParserException, TypeCheckerException, IRGenerationException, ClassNotFoundException, IllegalAccessException {
        List<Instruction> instructions = generateInstructions("1 + 2");
        AssemblyGenerator assemblyGenerator = new AssemblyGenerator();
        assemblyGenerator.generateAssembly(instructions);
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
