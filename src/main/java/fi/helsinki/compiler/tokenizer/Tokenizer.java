package fi.helsinki.compiler.tokenizer;

import fi.helsinki.compiler.common.Location;
import fi.helsinki.compiler.exceptions.TokenizeException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {

    private static final Pattern patternWhitespace = Pattern.compile("^ +");
    private static final Pattern patternTab = Pattern.compile("^\t+");
    private static final Pattern patternComment = Pattern.compile("^((\\/\\/|#).*?(\\n|$))");
    private static final Pattern patternIdentifier = Pattern.compile("^(((?!var(?![a-zA-Z]))(?<![a-zA-Z])[a-z|A-Z|_]+[a-z|A-Z|_|0-9]*)|((var)[a-z|A-Z|_|0-9]+))");
    private static final Pattern patternOperator = Pattern.compile("^(==|or |and |=|!=|<=|>=|>|<|\\+|-|\\*|\\/|%)");
    private static final Pattern patternKeyword = Pattern.compile("^(if|while|function|var|do|then|else)( |\n|$)");
    private static final Pattern patternNewline = Pattern.compile("^\n");
    private static final Pattern patternStringLiteral = Pattern.compile("^\".*?\"");
    private static final Pattern patternIntegerLiteral = Pattern.compile("^[0-9]+");
    private static final Pattern patternBooleanLiteral = Pattern.compile("^(true|false)");
    private static final Pattern patternPunctuation = Pattern.compile("^(\\{|}|\\(|\\)|,|;|:)");

    public List<Token> tokenize(String sourceCode, String filename) throws TokenizeException {
        Matcher matcher;
        ArrayList<Token> tokens = new ArrayList<>();
        int line = 0;
        int column = 0;
        while (true) {
            matcher = patternComment.matcher(sourceCode);
            if (matcher.find()) {
                sourceCode = sourceCode.substring(matcher.end());
                line += 1;
                column = 0;
                continue;
            }
            matcher = patternWhitespace.matcher(sourceCode);
            if (matcher.find()) {
                sourceCode = sourceCode.substring(matcher.end());
                column += matcher.end() - matcher.start();
                continue;
            }
            matcher = patternTab.matcher(sourceCode);
            if (matcher.find()) {
                sourceCode = sourceCode.substring(matcher.end());
                column += matcher.end() - matcher.start();
                continue;
            }
            matcher = patternNewline.matcher(sourceCode);
            if (matcher.find()) {
                sourceCode = sourceCode.substring(matcher.end());
                line += 1;
                column = 0;
                continue;
            }
            matcher = patternKeyword.matcher(sourceCode);
            if (matcher.find()) {
                String keyword;
                if (matcher.hitEnd()) {
                    keyword = sourceCode.substring(0, matcher.end()).strip();
                    sourceCode = sourceCode.substring(matcher.end());
                } else {
                    keyword = sourceCode.substring(0, matcher.end() - 1);
                    sourceCode = sourceCode.substring(matcher.end() - 1);
                }
                tokens.add(new Token(keyword, TokenType.KEYWORD, new Location(filename, line, column)));
                if (matcher.hitEnd()) {
                    column += matcher.end() - matcher.start();
                } else {
                    column += matcher.end() - matcher.start() - 1;
                }
                continue;
            }
            matcher = patternBooleanLiteral.matcher(sourceCode);
            if (matcher.find()) {
                String keyword = sourceCode.substring(0, matcher.end()).strip();
                tokens.add(new Token(keyword, TokenType.BOOLEAN_LITERAL, new Location(filename, line, column)));
                column += matcher.end() - matcher.start();
                sourceCode = sourceCode.substring(matcher.end());
                continue;
            }
            matcher = patternIntegerLiteral.matcher(sourceCode);
            if (matcher.find()) {
                String keyword = sourceCode.substring(0, matcher.end()).strip();
                tokens.add(new Token(keyword, TokenType.INTEGER_LITERAL, new Location(filename, line, column)));
                column += matcher.end() - matcher.start();
                sourceCode = sourceCode.substring(matcher.end());
                continue;
            }
            matcher = patternStringLiteral.matcher(sourceCode);
            if (matcher.find()) {
                String keyword = sourceCode.substring(0, matcher.end()).strip();
                tokens.add(new Token(keyword, TokenType.STRING_LITERAL, new Location(filename, line, column)));
                column += matcher.end() - matcher.start();
                sourceCode = sourceCode.substring(matcher.end());
                continue;
            }
            matcher = patternOperator.matcher(sourceCode);
            if (matcher.find()) {
                String keyword = sourceCode.substring(0, matcher.end()).strip();
                tokens.add(new Token(keyword, TokenType.OPERATOR, new Location(filename, line, column)));
                column += matcher.end() - matcher.start();
                sourceCode = sourceCode.substring(matcher.end());
                continue;
            }

            matcher = patternPunctuation.matcher(sourceCode);
            if (matcher.find()) {
                String keyword = sourceCode.substring(0, matcher.end()).strip();
                tokens.add(new Token(keyword, TokenType.PUNCTUATION, new Location(filename, line, column)));
                column += matcher.end() - matcher.start();
                sourceCode = sourceCode.substring(matcher.end());
                continue;
            }
            matcher = patternIdentifier.matcher(sourceCode);
            if (matcher.find()) {
                String keyword = sourceCode.substring(0, matcher.end()).strip();
                tokens.add(new Token(keyword, TokenType.IDENTIFIER, new Location(filename, line, column)));
                column += matcher.end() - matcher.start();
                sourceCode = sourceCode.substring(matcher.end());
                continue;
            }
            if (matcher.hitEnd()) {
                return tokens;
            }
            throw new TokenizeException("Found invalid token: '" + sourceCode.substring(0, 1)
                    + "' at line: " + line + " and column: " + column);
        }
    }
}
