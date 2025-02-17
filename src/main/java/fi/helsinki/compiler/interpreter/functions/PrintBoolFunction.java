package fi.helsinki.compiler.interpreter.functions;

import fi.helsinki.compiler.interpreter.BooleanValue;
import fi.helsinki.compiler.interpreter.FunctionDefinition;
import fi.helsinki.compiler.interpreter.Value;
import fi.helsinki.compiler.exceptions.InterpreterException;

public class PrintBoolFunction extends FunctionDefinition {
    public void invoke(Value... values) throws InterpreterException {
        if (values.length > 1 || !(values[0] instanceof BooleanValue)) {
            throw new InterpreterException("Invalid input type found");
        }
        System.out.println(((BooleanValue) values[0]).getValue() + "\n");
    }

    @Override
    public String getType() {
        return "PrintBooleanFunction";
    }
}
