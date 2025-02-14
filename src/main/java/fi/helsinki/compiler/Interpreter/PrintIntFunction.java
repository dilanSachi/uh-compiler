package fi.helsinki.compiler.Interpreter;

public class PrintIntFunction implements FunctionDefinition {

    public void invoke(IntValue intValue) {
        System.out.println(intValue.getIntValue() + "\n");
    }
}
