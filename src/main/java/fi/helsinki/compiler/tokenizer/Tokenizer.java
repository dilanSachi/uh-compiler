package fi.helsinki.compiler.tokenizer;

import fi.helsinki.compiler.exceptions.TokenizeException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {

    private static final Pattern patternWhitespace = Pattern.compile("^ +");
    private static final Pattern patternTab = Pattern.compile("^\t+");
    private static final Pattern patternComment = Pattern.compile("^//.*?\n");
    private static final Pattern patternIdentifier = Pattern.compile("^(((?!var(?![a-zA-Z]))(?<![a-zA-Z])[a-z|A-Z|_]+[a-z|A-Z|_|0-9]*)|((var)[a-z|A-Z|_|0-9]+))");
    private static final Pattern patternOperator = Pattern.compile("^=| or | and |==|!=|<|<=|>|>=|\\+|-|\\*|\\/|%");
    private static final Pattern patternKeyword = Pattern.compile("^(if|while|function|var)( |\n)");
    private static final Pattern patternNewline = Pattern.compile("^\n");
    private static final Pattern patternStringLiteral = Pattern.compile("^\".*?\"");
    private static final Pattern patternIntegerLiteral = Pattern.compile("^[0-9]+");
    private static final Pattern patternBooleanLiteral = Pattern.compile("^true|false");

    public static void main(String[] args) {
        // ”hello”, ”hello!”, ”hello!!”, ”hello!!!”
        Pattern pattern = Pattern.compile("hello[0-9]");
        Matcher matcher = pattern.matcher("hello1 hello2 hello3 hello4 abcd hello5 there!");
        boolean matchFound = matcher.find();
        if(matchFound) {
            System.out.println("Match found");
        } else {
            System.out.println("Match not found");

        }
        String sourceCode = "var n: Int = read_int();\n" +
                "print_int(n);\n" +
                "// This is a while loop" +
                "while n > 1 do {\n" +
                "    if n % 2 == 0 then { // checking if n is even\n" +
                "        n = n / 2;\n" +
                "    } else {\n" +
                "        n = 3*n + 1;\n" +
                "    }\n" +
                "    print_int(n);\n" +
                "}";
//        List<String> tokens = tokenize(sourceCode);
//        System.out.print(String.join(", ", tokens));
    }

    public List<String> tokenize(String sourceCode) throws TokenizeException {
        Matcher matcher = patternComment.matcher(sourceCode);
        ArrayList<String> tokens = new ArrayList<>();
        while (true) {
            if (matcher.find()) {
                System.out.println("Ignoring comment : " + sourceCode.substring(0, matcher.end()));
                sourceCode = sourceCode.substring(matcher.end());
                continue;
            }
            matcher = patternWhitespace.matcher(sourceCode);
            if (matcher.find()) {
                System.out.println("Found whitespaces : " + sourceCode.substring(0, matcher.end()) + ".");
                sourceCode = sourceCode.substring(matcher.end());
                continue;
            }
            matcher = patternTab.matcher(sourceCode);
            if (matcher.find()) {
                System.out.println("Found tab : " + sourceCode.substring(0, matcher.end()) + ".");
                sourceCode = sourceCode.substring(matcher.end());
                continue;
            }
            matcher = patternNewline.matcher(sourceCode);
            if (matcher.find()) {
                String keyword = sourceCode.substring(0, matcher.end());
                System.out.println("Found newline : " + keyword);
                sourceCode = sourceCode.substring(matcher.end());
                continue;
            }
            matcher = patternKeyword.matcher(sourceCode);
            if (matcher.find()) {
                String keyword = sourceCode.substring(0, matcher.end() - 1);
                tokens.add(keyword);
                System.out.println("Found keyword : " + keyword);
                sourceCode = sourceCode.substring(matcher.end() - 1);
                continue;
            }
            matcher = patternBooleanLiteral.matcher(sourceCode);
            if (matcher.find()) {
                String keyword = sourceCode.substring(0, matcher.end());
                tokens.add(keyword);
                System.out.println("Found boolean literal : " + keyword);
                sourceCode = sourceCode.substring(matcher.end());
                continue;
            }
            matcher = patternIntegerLiteral.matcher(sourceCode);
            if (matcher.find()) {
                String keyword = sourceCode.substring(0, matcher.end());
                tokens.add(keyword);
                System.out.println("Found integer literal : " + keyword);
                sourceCode = sourceCode.substring(matcher.end());
                continue;
            }
            matcher = patternStringLiteral.matcher(sourceCode);
            if (matcher.find()) {
                String keyword = sourceCode.substring(0, matcher.end());
                tokens.add(keyword);
                System.out.println("Found string literal : " + keyword);
                sourceCode = sourceCode.substring(matcher.end());
                continue;
            }
//            matcher = patternOperator.matcher(sourceCode);
//            if (matcher.find()) {
//                String keyword = sourceCode.substring(0, matcher.end());
//                tokens.add(keyword);
//                System.out.println("Found operator : " + keyword);
//                sourceCode = sourceCode.substring(matcher.end());
//                continue;
//            }
            matcher = patternIdentifier.matcher(sourceCode);
            if (matcher.find()) {
                String keyword = sourceCode.substring(0, matcher.end());
                tokens.add(keyword);
                System.out.println("Found identifier : " + keyword);
                sourceCode = sourceCode.substring(matcher.end());
                continue;
            }
            if (matcher.hitEnd()) {
                return tokens;
            }
            throw new TokenizeException("Found invalid token");
        }
    }
}
