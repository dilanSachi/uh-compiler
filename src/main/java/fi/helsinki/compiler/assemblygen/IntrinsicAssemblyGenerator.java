package fi.helsinki.compiler.assemblygen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntrinsicAssemblyGenerator {

    private static final Map<String, Intrinsic> intrinsicMap = new HashMap<>();

    static {
        intrinsicMap.put("+", new AdditionIntrinsic());
        intrinsicMap.put("-", new SubtractionIntrinsic());
        intrinsicMap.put("*", new MultiplyIntrinsic());
        intrinsicMap.put("/", new DivisionIntrinsic());
        intrinsicMap.put("%", new RemainderIntrinsic());
        intrinsicMap.put("==", new EqualsIntrinsic());
        intrinsicMap.put("!=", new NotEqualsIntrinsic());
        intrinsicMap.put("<", new LessThanIntrinsic());
        intrinsicMap.put("<=", new LessThanOrEqualIntrinsic());
        intrinsicMap.put(">", new GreaterThanIntrinsic());
        intrinsicMap.put(">=", new GreaterThanOrEqualIntrinsic());
        intrinsicMap.put("unary_-", new UnaryNegationIntrinsic());
        intrinsicMap.put("unary_not", new UnaryNotIntrinsic());
        intrinsicMap.put("print_int", new PrintIntIntrinsic());
        intrinsicMap.put("print_bool", new PrintBooleanIntrinsic());
        intrinsicMap.put("read_int", new ReadIntIntrinsic());
    }

    public static void generateIntrinsicAssemblyLines(List<String> assemblyLines, String operator, List<String> argRefs, String resultRegister) {
        intrinsicMap.get(operator).generate(assemblyLines, argRefs, resultRegister);
    }
}

interface Intrinsic {
    void generate(List<String> assemblyLines, List<String> argRefs, String resultRegister);
}

abstract class IntComparison {
    void compareAndSet(List<String> assemblyLines, List<String> argRefs, String resultRegister, String setCCIns) {
        assemblyLines.add("xor %rax, %rax");
        assemblyLines.add("movq " + argRefs.get(0) + ", %rdx");
        assemblyLines.add("cmpq " + argRefs.get(1) + ", %rdx");
        assemblyLines.add(setCCIns + " %al");
        if (!resultRegister.equals("%rax")) {
            assemblyLines.add("movq %rax, " + resultRegister);
        }
    }
}

class AdditionIntrinsic implements Intrinsic {
    public void generate(List<String> assemblyLines, List<String> argRefs, String resultRegister) {
        if (!resultRegister.equals(argRefs.get(0))) {
            assemblyLines.add("movq " + argRefs.get(0) + ", " + resultRegister);
        }
        assemblyLines.add("addq " + argRefs.get(1) + ", " + resultRegister);
    }
}

class SubtractionIntrinsic implements Intrinsic {
    public void generate(List<String> assemblyLines, List<String> argRefs, String resultRegister) {
        if (!resultRegister.equals(argRefs.get(0))) {
            assemblyLines.add("movq " + argRefs.get(0) + ", " + resultRegister);
        }
        assemblyLines.add("subq " + argRefs.get(1) + ", " + resultRegister);
    }
}

class MultiplyIntrinsic implements Intrinsic {
    public void generate(List<String> assemblyLines, List<String> argRefs, String resultRegister) {
        if (!resultRegister.equals(argRefs.get(0))) {
            assemblyLines.add("movq " + argRefs.get(0) + ", " + resultRegister);
        }
        assemblyLines.add("imulq " + argRefs.get(1) + ", " + resultRegister);
    }
}

class DivisionIntrinsic implements Intrinsic {
    public void generate(List<String> assemblyLines, List<String> argRefs, String resultRegister) {
        assemblyLines.add("movq " + argRefs.get(0) + ", %rax");
        assemblyLines.add("cqto");
        assemblyLines.add("idivq " + argRefs.get(1));
        if (!resultRegister.equals("%rax")) {
            assemblyLines.add("movq %rax, " + resultRegister);
        }
    }
}

class RemainderIntrinsic implements Intrinsic {
    public void generate(List<String> assemblyLines, List<String> argRefs, String resultRegister) {
        assemblyLines.add("movq " + argRefs.get(0) + ", %rax");
        assemblyLines.add("cqto");
        assemblyLines.add("idivq " + argRefs.get(1));
        if (!resultRegister.equals("%rax")) {
            assemblyLines.add("movq %rdx, " + resultRegister);
        }
    }
}

class EqualsIntrinsic extends IntComparison implements Intrinsic {
    public void generate(List<String> assemblyLines, List<String> argRefs, String resultRegister) {
        compareAndSet(assemblyLines, argRefs, resultRegister, "sete");
    }
}

class NotEqualsIntrinsic extends IntComparison implements Intrinsic {
    public void generate(List<String> assemblyLines, List<String> argRefs, String resultRegister) {
        compareAndSet(assemblyLines, argRefs, resultRegister, "setne");
    }
}

class LessThanIntrinsic extends IntComparison implements Intrinsic {
    public void generate(List<String> assemblyLines, List<String> argRefs, String resultRegister) {
        compareAndSet(assemblyLines, argRefs, resultRegister, "setl");
    }
}

class LessThanOrEqualIntrinsic extends IntComparison implements Intrinsic {
    public void generate(List<String> assemblyLines, List<String> argRefs, String resultRegister) {
        compareAndSet(assemblyLines, argRefs, resultRegister, "setle");
    }
}

class GreaterThanIntrinsic extends IntComparison implements Intrinsic {
    public void generate(List<String> assemblyLines, List<String> argRefs, String resultRegister) {
        compareAndSet(assemblyLines, argRefs, resultRegister, "setg");
    }
}

class GreaterThanOrEqualIntrinsic extends IntComparison implements Intrinsic {
    public void generate(List<String> assemblyLines, List<String> argRefs, String resultRegister) {
        compareAndSet(assemblyLines, argRefs, resultRegister, "setge");
    }
}

class UnaryNotIntrinsic extends IntComparison implements Intrinsic {
    public void generate(List<String> assemblyLines, List<String> argRefs, String resultRegister) {
        assemblyLines.add("movq " + argRefs.get(0) + ", " + resultRegister);
        assemblyLines.add("xorq $1," + resultRegister);
    }
}

class UnaryNegationIntrinsic extends IntComparison implements Intrinsic {
    public void generate(List<String> assemblyLines, List<String> argRefs, String resultRegister) {
        assemblyLines.add("movq " + argRefs.get(0) + ", " + resultRegister);
        assemblyLines.add("negq " + resultRegister);
    }
}

class PrintIntIntrinsic implements Intrinsic {
    @Override
    public void generate(List<String> assemblyLines, List<String> argRefs, String resultRegister) {
        assemblyLines.add("movq " + argRefs.get(0) + ", %rdi");
        assemblyLines.add("callq print_int");
//        assemblyLines.add("movq %rax, " + resultRegister);
    }
}

class PrintBooleanIntrinsic implements Intrinsic {
    @Override
    public void generate(List<String> assemblyLines, List<String> argRefs, String resultRegister) {
        assemblyLines.add("movq " + argRefs.get(0) + ", %rdi");
        assemblyLines.add("callq print_bool");
//        assemblyLines.add("movq %rax, " + resultRegister);
    }
}

class ReadIntIntrinsic implements Intrinsic {
    @Override
    public void generate(List<String> assemblyLines, List<String> argRefs, String resultRegister) {
        assemblyLines.add("callq read_int");
//        assemblyLines.add("movq %rax, " + resultRegister);
    }
}
