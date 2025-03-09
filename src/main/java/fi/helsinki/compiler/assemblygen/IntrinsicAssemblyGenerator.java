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
    }

    public static void generateIntrinsicAssemblyLines(List<String> assemblyLines, String operator, List<String> argRefs, String resultRegister) {
        intrinsicMap.get(operator).generate(assemblyLines, argRefs, resultRegister);
    }
}

abstract class Intrinsic {

    public abstract void generate(List<String> assemblyLines, List<String> argRefs, String resultRegister);
}

class AdditionIntrinsic extends Intrinsic {
    public void generate(List<String> assemblyLines, List<String> argRefs, String resultRegister) {
        if (!resultRegister.equals(argRefs.get(0))) {
            assemblyLines.add("movq " + argRefs.get(0) + ", " + resultRegister);
        }
        assemblyLines.add("addq " + argRefs.get(1) + ", " + resultRegister);
    }
}

class SubtractionIntrinsic extends Intrinsic {
    public void generate(List<String> assemblyLines, List<String> argRefs, String resultRegister) {
        if (!resultRegister.equals(argRefs.get(0))) {
            assemblyLines.add("movq " + argRefs.get(0) + ", " + resultRegister);
        }
        assemblyLines.add("subq " + argRefs.get(1) + ", " + resultRegister);
    }
}

class MultiplyIntrinsic extends Intrinsic {
    public void generate(List<String> assemblyLines, List<String> argRefs, String resultRegister) {
        if (!resultRegister.equals(argRefs.get(0))) {
            assemblyLines.add("movq " + argRefs.get(0) + ", " + resultRegister);
        }
        assemblyLines.add("imulq " + argRefs.get(1) + ", " + resultRegister);
    }
}

class DivisionIntrinsic extends Intrinsic {
    public void generate(List<String> assemblyLines, List<String> argRefs, String resultRegister) {
        assemblyLines.add("movq " + argRefs.get(0) + ", %rax");
        assemblyLines.add("cqto");
        assemblyLines.add("idivq " + argRefs.get(1));
        if (!resultRegister.equals("%rax")) {
            assemblyLines.add("movq %rax, " + resultRegister);
        }
    }
}
