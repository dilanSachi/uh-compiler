package fi.helsinki.compiler.parser;

public class Literal implements Expression {

    private Integer value;

    public Literal(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
