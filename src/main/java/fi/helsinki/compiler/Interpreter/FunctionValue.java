package fi.helsinki.compiler.Interpreter;

public class FunctionValue extends Value {

    public FunctionValue() {
    }


    @Override
    public String getType() {
        return "FunctionType";
    }
}
