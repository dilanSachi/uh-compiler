package fi.helsinki.compiler.Interpreter;

import fi.helsinki.compiler.exceptions.InterpreterException;

public class PrintIntFunction extends FunctionDefinition {

    public void invoke(Value... values) throws InterpreterException {
        if (values.length > 1 || !(values[0] instanceof IntValue)) {
            throw new InterpreterException("Invalid input type found");
        }
        System.out.println(((IntValue) values[0]).getIntValue() + "\n");
    }

    @Override
    public String getType() {
        return "PrintIntFunction";
    }
}
