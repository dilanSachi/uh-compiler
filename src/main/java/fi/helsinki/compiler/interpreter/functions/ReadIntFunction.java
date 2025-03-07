package fi.helsinki.compiler.interpreter.functions;

import fi.helsinki.compiler.exceptions.InterpreterException;
import fi.helsinki.compiler.interpreter.FunctionDefinition;
import fi.helsinki.compiler.interpreter.IntValue;
import fi.helsinki.compiler.interpreter.Value;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ReadIntFunction extends FunctionDefinition {

    public Value invoke(Value... values) throws InterpreterException {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        return new IntValue(Long.valueOf(input.replace("\n", "")));
    }

    @Override
    public String getType() {
        return "PrintIntFunction";
    }
}
