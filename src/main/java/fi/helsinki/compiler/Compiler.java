package fi.helsinki.compiler;

import fi.helsinki.compiler.assemblygen.Assembler;
import fi.helsinki.compiler.assemblygen.AssemblyGenerator;
import fi.helsinki.compiler.common.expressions.Expression;
import fi.helsinki.compiler.exceptions.CompilationException;
import fi.helsinki.compiler.exceptions.IRGenerationException;
import fi.helsinki.compiler.exceptions.ParserException;
import fi.helsinki.compiler.exceptions.TypeCheckerException;
import fi.helsinki.compiler.irgenerator.IRGenerator;
import fi.helsinki.compiler.irgenerator.instructions.Instruction;
import fi.helsinki.compiler.parser.Parser;
import fi.helsinki.compiler.tokenizer.Tokenizer;
import fi.helsinki.compiler.typechecker.TypeChecker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Compiler {
    public static void main(String[] args) throws CompilationException, IOException, ParserException, TypeCheckerException, IRGenerationException, ClassNotFoundException, IllegalAccessException {
        String command = null;
        String inputFile = null;
        String outputFile = null;
        String host;
        int port;
        for (String arg : args) {
            if (arg.startsWith("--output=")) {
                outputFile = arg.substring(8);
            } else if (arg.startsWith("--host=")) {
                host = arg.substring(6);
            } else if (arg.startsWith("--port=")) {
                port = Integer.valueOf(arg.substring(6));
            } else if (arg.startsWith("-")) {
                throw new CompilationException("Unknown argument: "  + arg);
            } else if (command == null) {
                command = arg;
            } else if (inputFile == null) {
                inputFile = arg;
            } else {
                throw new CompilationException("Multiple input files not supported");
            }
        }
        if (command == null) {
            throw new CompilationException("Command argument missing");
        }
        if (command.equals("compile")) {
            String sourceCode = readSourceCode(inputFile);
            if (outputFile == null) {
                throw new CompilationException("Output file flag --output=... required");
            }
            byte[] executable = startCompilation(sourceCode);
        } else if (command.equals("serve")) {

        } else {
            throw new CompilationException("Invalid compilation command: " + command);
        }

    }

    private static byte[]  startCompilation(String sourceCode) throws ParserException, TypeCheckerException, IRGenerationException, ClassNotFoundException, IllegalAccessException {
        Parser parser = new Parser(new Tokenizer().tokenize(sourceCode, "TestFile.dl"));
        TypeChecker typeChecker = new TypeChecker();
        Expression expression = parser.parse();
        typeChecker.checkType(expression);
        IRGenerator irGenerator = new IRGenerator();
        List<Instruction> instructions = irGenerator.generateIR(expression);
        AssemblyGenerator assemblyGenerator = new AssemblyGenerator();
        String assemblyCode = assemblyGenerator.generateAssembly(instructions);
        return new Assembler().assemble(assemblyCode);
    }

    private static String readSourceCode(String inputFilePath) throws IOException {
        if (inputFilePath != null) {
            return Files.readString(Path.of(inputFilePath));
        } else {
            Scanner stdScanner = new Scanner(System.in);
            return stdScanner.next();
        }
    }
}
