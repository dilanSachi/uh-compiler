package fi.helsinki.compiler.assemblygen;

import fi.helsinki.compiler.irgenerator.IRVariable;
import fi.helsinki.compiler.irgenerator.instructions.*;

import java.lang.reflect.Field;
import java.util.*;

public class AssemblyGenerator {

    private List<IRVariable> resultList = new ArrayList<>();
    private Set<IRVariable> resultSet = new HashSet<>();
    private Map<String, FunctionDefinitionIns> functionDefinitionInsMap = new HashMap<>();
    private final String[] PARAMETER_REGISTERS = new String[]{"%rdi", "%rsi", "%rdx", "%rcx", "%r8", "%r9"};

    public String generateAssembly(List<Instruction> instructions) throws ClassNotFoundException, IllegalAccessException {
        List<String> lines = new ArrayList<>();
        lines.add(".extern print_int");
        lines.add(".extern print_bool");
        lines.add(".extern read_int");
        lines.add(".section .text");
        if (!instructions.isEmpty() && instructions.getFirst() instanceof FunctionDefinitionIns) {
            for (FunctionDefinitionIns instruction : instructions.toArray(new FunctionDefinitionIns[]{})) {
                lines.addAll(generateAssemblyForFunction(instruction.getFunctionName(), instruction.getFunctionInstructions()));
                functionDefinitionInsMap.put(instruction.getFunctionName(), instruction);
            }
        } else {
            lines.addAll(generateAssemblyForFunction("main", instructions));
        }
        return String.join("\n", lines);
    }

    private List<String> generateAssemblyForFunction(String functionName, List<Instruction> instructions) throws ClassNotFoundException,
            IllegalAccessException {
        List<String> lines = new ArrayList<>();
        Locals locals = new Locals(getAllIRVariables(instructions));
        lines.add(".global " + functionName);
        lines.add(".type " + functionName + ", @function");
        lines.add(functionName+ ":");
        lines.add("pushq %rbp");
        lines.add("movq %rsp, %rbp");
        lines.add("subq $" + locals.getStackUsed() + ", %rsp");
        for (Instruction instruction : instructions) {
            lines.add("#" + instruction.toString());
            switch (instruction) {
                case Label labelIns: {
                    lines.add("");
                    lines.add(".L" + labelIns.getLabelName() + ":");
                    break;
                }
                case LoadIntConst intConstIns: {
                    if (Math.pow(-2, 31) <= intConstIns.getValue() && intConstIns.getValue() <= Math.pow(2, 31)) {
                        lines.add("movq $" + intConstIns.getValue().toString().replace("-", "") + ", " + locals.getRef(intConstIns.getDestination()));
                    } else {
                        lines.add("movabsq $" + intConstIns.getValue().toString().replace("-", "") + ", %rax");
                        lines.add("movq %rax, " + locals.getRef(intConstIns.getDestination()));
                    }
                    break;
                }
                case Jump jumpIns: {
                    lines.add("jmp .L" + jumpIns.getLabel().getLabelName());
                    break;
                }
                case LoadBoolConst boolConstIns: {
                    lines.add("movq $" + (boolConstIns.getValue() ? "1" : "0") + ", " + locals.getRef(boolConstIns.getDestination()));
                    break;
                }
                case Copy copyIns: {
                    lines.add("movq " + locals.getRef(copyIns.getSource()) + ", %rax");
                    lines.add("movq %rax, " + locals.getRef(copyIns.getDestination()));
                    break;
                }
                case CondJump condJumpIns: {
                    lines.add("cmpq $0, " + locals.getRef(condJumpIns.getCondition()));
                    lines.add("jne .L" + condJumpIns.getThenLabel().getLabelName());
                    lines.add("jmp .L" + condJumpIns.getElseLabel().getLabelName());
                    break;
                }
                case Call callIns: {
                    List<String> argRegisters = new ArrayList<>();
                    IRVariable[] irVariables = callIns.getArguments();
                    for (IRVariable irVariable : irVariables) {
                        argRegisters.add(locals.getRef(irVariable));
                    }
                    if (IntrinsicAssemblyGenerator.hasIntrinsic(callIns.getFunction().getType().getTypeStr())) {
                        IntrinsicAssemblyGenerator.generateIntrinsicAssemblyLines(lines,
                                callIns.getFunction().getType().getTypeStr(), argRegisters, "%rax");
                    } else {
                        FunctionDefinitionIns funDefIns = functionDefinitionInsMap.get(callIns.getFunction().getName());

                    }
                    lines.add("movq %rax, " + locals.getRef(callIns.getDestination()));
                    break;
                }
                case ReturnIns returnIns: {
                    lines.add("moveq " + locals.getRef(returnIns.getValue()) + ", %rax");
                    lines.add("movq %rbp, %rsp");
                    lines.add("popq %rbp");
                    lines.add("ret");

                }
                default: {}
            }
        }
        lines.add("movq $0, %rax");
        lines.add("movq %rbp, %rsp");
        lines.add("popq %rbp");
        lines.add("ret");
        return lines;
    }

    private List<IRVariable> getAllIRVariables(List<Instruction> instructions) throws ClassNotFoundException, IllegalAccessException {
        for (int i = 0; i < instructions.size(); i++) {
            Class instructionClass = Class.forName(instructions.get(i).getClass().getName());
            for (Field field : instructionClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.getType().getName().equals(IRVariable.class.getName())) {
                    add((IRVariable) field.get(instructions.get(i)));
                } else if (field.getType().getName().equals(List.class.getName())) {
                    List<?> variableList = (List<?>) field.get(instructions.get(i));
                    for (int j = 0; j < variableList.size(); j++) {
                        if (variableList.get(j) instanceof IRVariable) {
                            add((IRVariable) variableList.get(j));
                        }
                    }
                }
            }
        }
        return resultList;
    }

    private void add(IRVariable variable) {
        if (!resultSet.contains(variable)) {
            resultList.add(variable);
            resultSet.add(variable);
        }
    }
}
