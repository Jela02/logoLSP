package org.example.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LogoLexer {
    private final String input;
    private int line = 0;
    private int column = 0;
    private int position = 0;

    private static final Set<String> PROCEDURE_KEYWORDS = Set.of(
            "stop", "output", "op"
    );
    private static final Set<String> LOOP_KEYWORDS = Set.of(
            "repeat", "while", "for", "dotimes", "do.while", "do.until", "until", "repcount"
    );
    private static final Set<String> CONDITION_KEYWORDS = Set.of(
            "if", "ifelse", "test", "iftrue", "iffalse"
    );
    private static final Set<String> DEFINITION_KEYWORDS = Set.of(
            "make", "local"
    );

    private static final Set<String> VARIABLE_COMMANDS = Set.of(
            "localmake", "thing", "name"
    );

    private static final Set<String> COMMANDS = Set.of(
            "define", "def",
            "wait", "bye",
            "fd", "forward", "bk", "back", "backward",
            "rt", "right", "lt", "left",
            "home", "setpos", "setxy", "setx", "sety",
            "set", "setheading", "seth", "sh",
            "pos", "xcor", "ycor", "heading", "towards",
            "penup", "pu", "pendown", "pd", "setcolor", "setpencolor", "setpc",
            "pencolor", "pc", "setwidth", "setpensize", "pensize", "changeshape", "csh",
            "pendownp", "pendown?",
            "clearscreen", "cs", "clean", "hideturtle", "ht", "showturtle", "st",
            "arc", "circle", "dot", "ellipse", "fill", "filled", "label", "setlabelheight",
            "wrap", "window", "fence", "shownp", "shown?", "labelsize",
            "print", "pr", "show",
            "readword", "readlist",
            "random",
            "sum", "minus", "difference", "product", "quotient",
            "remainder", "modulo", "sqrt", "sin", "cos", "tan", "round", "abs", "power",
            "and", "or", "not", "equalp", "equal?", "notequalp", "notequal?",
            "lessp", "less?", "greaterp", "greater?", "true", "false",
            "list", "word", "array", "sentence", "se", "first", "last",
            "butfirst", "bf", "butlast", "bl", "item", "pick", "count",
            "emptyp", "empty?", "memberp", "member?",
            "wordp", "word?", "listp", "list?", "arrayp", "array?",
            "numberp", "number?", "beforep", "before?", "substringp", "substring?"
    );

    public LogoLexer(String input) {
        this.input = input;
    }

    public List<LogoToken> tokenize() {

        List<LogoToken> tokens = new ArrayList<>();
        while (position < input.length()) {
            char c = input.charAt(position);
            if (Character.isWhitespace(c)) {
                if (c == '\n') {
                    tokens.add(new LogoToken(LogoTokenType.NEWLINE, "\\n", line, column));
                    line++;
                    column = 0;
                } else {
                    column++;
                }
                position++;
                continue;
            }
            if (c == ';') {
                tokens.add(comment());
                continue;
            }
            if (c == '"') {
                tokens.add(string());
                continue;
            }
            if (Character.isDigit(c) || (c == '-' && position + 1 < input.length() && Character.isDigit(input.charAt(position + 1)))) {
                tokens.add(number());
                continue;
            }
            if (c == ':') {
                tokens.add(variable());
                continue;
            }
            if (isWordStart(c)) {
                tokens.add(word());
                continue;
            }
            if (c == '[') {
                tokens.add(singleCharacterToken(LogoTokenType.SYMBOL));
                continue;
            }
            if (c == ']') {
                tokens.add(singleCharacterToken(LogoTokenType.SYMBOL));
                continue;
            }
            if (c == '(') {
                tokens.add(singleCharacterToken(LogoTokenType.SYMBOL));
                continue;
            }
            if (c == ')') {
                tokens.add(singleCharacterToken(LogoTokenType.SYMBOL));
                continue;
            }
            if (c == ',') {
                tokens.add(singleCharacterToken(LogoTokenType.SYMBOL));
                continue;
            }
            if (isOperator(c)) {
                tokens.add(singleCharacterToken(LogoTokenType.OPERATOR));
                continue;
            }
            tokens.add(new LogoToken(LogoTokenType.UNKNOWN, String.valueOf(c), line, column));
            position++;
            column++;
        }
        return tokens;
    }

    private LogoToken variable() {
        int startPosition = position;
        int startColumnPosition = column;

        do {
            position++;
            column++;
        } while (position < input.length() && isWordPart(input.charAt(position)));
        String text = input.substring(startPosition, position).toLowerCase();
        return new LogoToken(LogoTokenType.VARIABLE, text, line, startColumnPosition);

    }

    private LogoToken word() {
        int startPosition = position;
        int startColumnPosition = column;
        while (position < input.length() && isWordPart(input.charAt(position))) {
            position++;
            column++;
        }
        String text = input.substring(startPosition, position);
        return new LogoToken(keywordType(text), text, line, startColumnPosition);

    }

    private LogoTokenType keywordType(String word) {
        String lower = word.toLowerCase();
        if (lower.equals("to")) return LogoTokenType.TO;
        if (lower.equals("end")) return LogoTokenType.END;
        if (PROCEDURE_KEYWORDS.contains(lower)) {
            return LogoTokenType.PROCEDURE_KEYWORD;
        }
        if (LOOP_KEYWORDS.contains(lower)) {
            return LogoTokenType.LOOP_KEYWORD;
        }
        if (CONDITION_KEYWORDS.contains(lower)) {
            return LogoTokenType.CONDITION_KEYWORD;
        }
        if (DEFINITION_KEYWORDS.contains(lower)) {
            return LogoTokenType.DEFINITION_KEYWORD;
        }
        if (VARIABLE_COMMANDS.contains(lower)) {
            return LogoTokenType.VARIABLE_COMMAND;
        }
        if (COMMANDS.contains(lower)) {
            return LogoTokenType.COMMAND;
        }

        return LogoTokenType.IDENTIFIER;

    }

    private LogoToken number() {
        int startPositon = position;
        int startColumnPosition = column;

        if (input.charAt(position) == '-') {
            position++;
            column++;
        }
        while (position < input.length() && Character.isDigit(input.charAt(position))) {
            position++;
            column++;
        }
        if (position < input.length() && input.charAt(position) == '.'
                && position + 1 < input.length() && Character.isDigit(input.charAt(position + 1))) {
            do {
                position++;
                column++;
            } while (position < input.length() && Character.isDigit(input.charAt(position)));
        }
        String text = input.substring(startPositon, position);
        return new LogoToken(LogoTokenType.NUMBER, text, line, startColumnPosition);

    }

    private boolean isWordStart(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private boolean isWordPart(char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == '?' || c == '.';
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '=' || c == '<' || c == '>';
    }

    private LogoToken singleCharacterToken(LogoTokenType tokenType) {
        String text = String.valueOf(input.charAt(position));
        LogoToken token = new LogoToken(tokenType, text, line, column);
        position++;
        column++;
        return token;
    }

    private LogoToken comment() {
        int startPosition = position;
        int startColumnPosition = column;
        while (position < input.length() && input.charAt(position) != '\r' && input.charAt(position) != '\n') {
            position++;
            column++;
        }
        String text = input.substring(startPosition, position);
        return new LogoToken(LogoTokenType.COMMENT, text, line, startColumnPosition);
    }

    private LogoToken string() {
        int startPosition = position;
        int startColumnPosition = column;
        do {
            position++;
            column++;
        } while (position < input.length() && !Character.isWhitespace(input.charAt(position)) && input.charAt(position) != '[' && input.charAt(position) != ']');
        String text = input.substring(startPosition, position);
        return new LogoToken(LogoTokenType.STRING, text, line, startColumnPosition);
    }

}
