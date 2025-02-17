package fi.helsinki.compiler.interpreter.functions;

import fi.helsinki.compiler.interpreter.FunctionDefinition;
import fi.helsinki.compiler.interpreter.IntValue;
import fi.helsinki.compiler.interpreter.Value;
import fi.helsinki.compiler.exceptions.InterpreterException;

public class PrintIntFunction extends FunctionDefinition {

    public Value invoke(Value... values) throws InterpreterException {
        if (values.length > 1 || !(values[0] instanceof IntValue)) {
            throw new InterpreterException("Invalid input type found");
        }
        System.out.println(((IntValue) values[0]).getIntValue() + "\n");
        return null;
    }

    @Override
    public String getType() {
        return "PrintIntFunction";
    }
}
