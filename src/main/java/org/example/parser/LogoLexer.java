package org.example.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LogoLexer {
    private final String input;
    private int line = 0;
    private int column = 0;
    private int position = 0;

    private static final Map<String, LogoTokenType> KEYWORDS = Map.ofEntries(
            Map.entry("to", LogoTokenType.TO),
            Map.entry("end", LogoTokenType.END),
            Map.entry("stop", LogoTokenType.PROCEDURE_KEYWORD),
            Map.entry("output", LogoTokenType.PROCEDURE_KEYWORD),
            Map.entry("op", LogoTokenType.PROCEDURE_KEYWORD),
            Map.entry("define", LogoTokenType.PROCEDURE_COMMAND),
            Map.entry("def", LogoTokenType.PROCEDURE_COMMAND),
            Map.entry("repeat", LogoTokenType.LOOP_KEYWORD),
            Map.entry("while", LogoTokenType.LOOP_KEYWORD),
            Map.entry("for", LogoTokenType.LOOP_KEYWORD),
            Map.entry("dotimes", LogoTokenType.LOOP_KEYWORD),
            Map.entry("do.while", LogoTokenType.LOOP_KEYWORD),
            Map.entry("do.until", LogoTokenType.LOOP_KEYWORD),
            Map.entry("until", LogoTokenType.LOOP_KEYWORD),
            Map.entry("repcount", LogoTokenType.LOOP_KEYWORD),
            Map.entry("if", LogoTokenType.CONDITION_KEYWORD),
            Map.entry("ifelse", LogoTokenType.CONDITION_KEYWORD),
            Map.entry("test", LogoTokenType.CONDITION_KEYWORD),
            Map.entry("iftrue", LogoTokenType.CONDITION_KEYWORD),
            Map.entry("iffalse", LogoTokenType.CONDITION_KEYWORD),
            Map.entry("wait", LogoTokenType.CONTROL_COMMAND),
            Map.entry("bye", LogoTokenType.CONTROL_COMMAND),
            Map.entry("make", LogoTokenType.DEFINITION_KEYWORD),
            Map.entry("local", LogoTokenType.DEFINITION_KEYWORD),
            Map.entry("name", LogoTokenType.VARIABLE_COMMAND),
            Map.entry("localmake", LogoTokenType.VARIABLE_COMMAND),
            Map.entry("thing", LogoTokenType.VARIABLE_COMMAND),
            Map.entry("forward", LogoTokenType.POSITION_COMMAND),
            Map.entry("fd", LogoTokenType.POSITION_COMMAND),
            Map.entry("back", LogoTokenType.POSITION_COMMAND),
            Map.entry("backward", LogoTokenType.POSITION_COMMAND),
            Map.entry("bk", LogoTokenType.POSITION_COMMAND),
            Map.entry("right", LogoTokenType.POSITION_COMMAND),
            Map.entry("rt", LogoTokenType.POSITION_COMMAND),
            Map.entry("left", LogoTokenType.POSITION_COMMAND),
            Map.entry("lt", LogoTokenType.POSITION_COMMAND),
            Map.entry("home", LogoTokenType.POSITION_COMMAND),
            Map.entry("set", LogoTokenType.POSITION_COMMAND),
            Map.entry("setpos", LogoTokenType.POSITION_COMMAND),
            Map.entry("setxy", LogoTokenType.POSITION_COMMAND),
            Map.entry("setx", LogoTokenType.POSITION_COMMAND),
            Map.entry("sety", LogoTokenType.POSITION_COMMAND),
            Map.entry("setheading", LogoTokenType.POSITION_COMMAND),
            Map.entry("seth", LogoTokenType.POSITION_COMMAND),
            Map.entry("sh", LogoTokenType.POSITION_COMMAND),
            Map.entry("pos", LogoTokenType.TURTLE_QUERY),
            Map.entry("xcor", LogoTokenType.TURTLE_QUERY),
            Map.entry("ycor", LogoTokenType.TURTLE_QUERY),
            Map.entry("heading", LogoTokenType.TURTLE_QUERY),
            Map.entry("towards", LogoTokenType.TURTLE_QUERY),
            Map.entry("penup", LogoTokenType.PEN_COMMAND),
            Map.entry("pu", LogoTokenType.PEN_COMMAND),
            Map.entry("pendown", LogoTokenType.PEN_COMMAND),
            Map.entry("pd", LogoTokenType.PEN_COMMAND),
            Map.entry("setcolor", LogoTokenType.PEN_COMMAND),
            Map.entry("setpencolor", LogoTokenType.PEN_COMMAND),
            Map.entry("setpc", LogoTokenType.PEN_COMMAND),
            Map.entry("pencolor", LogoTokenType.PEN_COMMAND),
            Map.entry("pc", LogoTokenType.PEN_COMMAND),
            Map.entry("setwidth", LogoTokenType.PEN_COMMAND),
            Map.entry("setpensize", LogoTokenType.PEN_COMMAND),
            Map.entry("pensize", LogoTokenType.PEN_COMMAND),
            Map.entry("changeshape", LogoTokenType.PEN_COMMAND),
            Map.entry("csh", LogoTokenType.PEN_COMMAND),
            Map.entry("pendownp", LogoTokenType.PEN_QUERY),
            Map.entry("pendown?", LogoTokenType.PEN_QUERY),
            Map.entry("clearscreen", LogoTokenType.DRAWING_COMMAND),
            Map.entry("cs", LogoTokenType.DRAWING_COMMAND),
            Map.entry("clean", LogoTokenType.DRAWING_COMMAND),
            Map.entry("hideturtle", LogoTokenType.DRAWING_COMMAND),
            Map.entry("ht", LogoTokenType.DRAWING_COMMAND),
            Map.entry("showturtle", LogoTokenType.DRAWING_COMMAND),
            Map.entry("st", LogoTokenType.DRAWING_COMMAND),
            Map.entry("arc", LogoTokenType.DRAWING_COMMAND),
            Map.entry("circle", LogoTokenType.DRAWING_COMMAND),
            Map.entry("dot", LogoTokenType.DRAWING_COMMAND),
            Map.entry("ellipse", LogoTokenType.DRAWING_COMMAND),
            Map.entry("fill", LogoTokenType.DRAWING_COMMAND),
            Map.entry("filled", LogoTokenType.DRAWING_COMMAND),
            Map.entry("label", LogoTokenType.DRAWING_COMMAND),
            Map.entry("setlabelheight", LogoTokenType.DRAWING_COMMAND),
            Map.entry("wrap", LogoTokenType.WINDOW_COMMAND),
            Map.entry("window", LogoTokenType.WINDOW_COMMAND),
            Map.entry("fence", LogoTokenType.WINDOW_COMMAND),
            Map.entry("shownp", LogoTokenType.WINDOW_QUERY),
            Map.entry("shown?", LogoTokenType.WINDOW_QUERY),
            Map.entry("labelsize", LogoTokenType.WINDOW_QUERY),
            Map.entry("print", LogoTokenType.OUTPUT_COMMAND),
            Map.entry("pr", LogoTokenType.OUTPUT_COMMAND),
            Map.entry("show", LogoTokenType.OUTPUT_COMMAND),
            Map.entry("readword", LogoTokenType.RECEIVER_COMMAND),
            Map.entry("readlist", LogoTokenType.RECEIVER_COMMAND),
            Map.entry("random", LogoTokenType.MATH_COMMAND),
            Map.entry("sum", LogoTokenType.MATH_COMMAND),
            Map.entry("minus", LogoTokenType.MATH_COMMAND),
            Map.entry("difference", LogoTokenType.MATH_COMMAND),
            Map.entry("product", LogoTokenType.MATH_COMMAND),
            Map.entry("quotient", LogoTokenType.MATH_COMMAND),
            Map.entry("remainder", LogoTokenType.MATH_COMMAND),
            Map.entry("modulo", LogoTokenType.MATH_COMMAND),
            Map.entry("sqrt", LogoTokenType.MATH_COMMAND),
            Map.entry("sin", LogoTokenType.MATH_COMMAND),
            Map.entry("cos", LogoTokenType.MATH_COMMAND),
            Map.entry("tan", LogoTokenType.MATH_COMMAND),
            Map.entry("round", LogoTokenType.MATH_COMMAND),
            Map.entry("abs", LogoTokenType.MATH_COMMAND),
            Map.entry("power", LogoTokenType.MATH_COMMAND),
            Map.entry("and", LogoTokenType.LOGIC_COMMAND),
            Map.entry("or", LogoTokenType.LOGIC_COMMAND),
            Map.entry("not", LogoTokenType.LOGIC_COMMAND),
            Map.entry("equalp", LogoTokenType.LOGIC_COMMAND),
            Map.entry("equal?", LogoTokenType.LOGIC_COMMAND),
            Map.entry("notequalp", LogoTokenType.LOGIC_COMMAND),
            Map.entry("notequal?", LogoTokenType.LOGIC_COMMAND),
            Map.entry("lessp", LogoTokenType.LOGIC_COMMAND),
            Map.entry("less?", LogoTokenType.LOGIC_COMMAND),
            Map.entry("greaterp", LogoTokenType.LOGIC_COMMAND),
            Map.entry("greater?", LogoTokenType.LOGIC_COMMAND),
            Map.entry("true", LogoTokenType.LOGIC_COMMAND),
            Map.entry("false", LogoTokenType.LOGIC_COMMAND),
            Map.entry("word", LogoTokenType.LIST_COMMAND),
            Map.entry("list", LogoTokenType.LIST_COMMAND),
            Map.entry("array", LogoTokenType.LIST_COMMAND),
            Map.entry("sentence", LogoTokenType.LIST_COMMAND),
            Map.entry("se", LogoTokenType.LIST_COMMAND),
            Map.entry("first", LogoTokenType.LIST_COMMAND),
            Map.entry("last", LogoTokenType.LIST_COMMAND),
            Map.entry("butfirst", LogoTokenType.LIST_COMMAND),
            Map.entry("bf", LogoTokenType.LIST_COMMAND),
            Map.entry("butlast", LogoTokenType.LIST_COMMAND),
            Map.entry("bl", LogoTokenType.LIST_COMMAND),
            Map.entry("item", LogoTokenType.LIST_COMMAND),
            Map.entry("pick", LogoTokenType.LIST_COMMAND),
            Map.entry("count", LogoTokenType.LIST_COMMAND),
            Map.entry("emptyp", LogoTokenType.LIST_COMMAND),
            Map.entry("empty?", LogoTokenType.LIST_COMMAND),
            Map.entry("memberp", LogoTokenType.LIST_COMMAND),
            Map.entry("member?", LogoTokenType.LIST_COMMAND),
            Map.entry("wordp", LogoTokenType.PREDICATE_COMMAND),
            Map.entry("word?", LogoTokenType.PREDICATE_COMMAND),
            Map.entry("listp", LogoTokenType.PREDICATE_COMMAND),
            Map.entry("list?", LogoTokenType.PREDICATE_COMMAND),
            Map.entry("arrayp", LogoTokenType.PREDICATE_COMMAND),
            Map.entry("array?", LogoTokenType.PREDICATE_COMMAND),
            Map.entry("numberp", LogoTokenType.PREDICATE_COMMAND),
            Map.entry("number?", LogoTokenType.PREDICATE_COMMAND),
            Map.entry("beforep", LogoTokenType.PREDICATE_COMMAND),
            Map.entry("before?", LogoTokenType.PREDICATE_COMMAND),
            Map.entry("substringp", LogoTokenType.PREDICATE_COMMAND),
            Map.entry("substring?", LogoTokenType.PREDICATE_COMMAND)
    );

    public LogoLexer(String input) {
        this.input = input;
    }

    public List<LogoToken> tokenize(){

        List<LogoToken> tokens = new ArrayList<>();
        while(position < input.length()){
            char c = input.charAt(position);
            if(Character.isWhitespace(c)){
                if(c == '\n'){
                    tokens.add(new LogoToken(LogoTokenType.NEWLINE,"\\n", line, column));
                    line++;
                    column = 0;
                }else{
                    column++;
                }
                position++;
                continue;
            }
            if(c == ';'){
                tokens.add(comment());
                continue;
            }
            if(c == '"'){
                tokens.add(string());
                continue;
            }
            if(Character.isDigit(c) || (c == '-' && position + 1 < input.length() && Character.isDigit(input.charAt(position+1)))){
                tokens.add(number());
                continue;
            }
            if(c == ':'){
                tokens.add(variable());
                continue;
            }
            if (isWordStart(c)){
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
            tokens.add(new LogoToken(LogoTokenType.UNKNOWN, String.valueOf(c),line, column));
            position++;
            column++;
        }
        return tokens;
    }

    private LogoToken variable() {
        int startPosition = position;
        int startColumnPosition = column;
        position++; //skip :
        column++;

        while(position < input.length() && isWordPart(input.charAt(position))){
            position++;
            column++;
        }
        String text = input.substring(startPosition,position).toLowerCase();
        return new LogoToken(LogoTokenType.VARIABLE, text, line, startColumnPosition);

    }

    private LogoToken word() {
        int startPosition = position;
        int startColumnPosition = column;
        while( position < input.length() && isWordPart(input.charAt(position))){
            position++;
            column++;
        }
        String text = input.substring(startPosition,position);
        return new LogoToken(keywordType(text), text, line, startColumnPosition);

    }

    private LogoTokenType keywordType(String word){
        return KEYWORDS.getOrDefault(word.toLowerCase(), LogoTokenType.IDENTIFIER);
    }

    private LogoToken number() {
        int startPositon = position;
        int startColumnPosition = column;

        if (input.charAt(position) == '-') {
            position++;
            column++;
        }
        while(position < input.length() && Character.isDigit(input.charAt(position))){
            position++;
            column++;
        }
        if (position < input.length() && input.charAt(position) == '.'
                && position + 1 < input.length() && Character.isDigit(input.charAt(position + 1))) {
            position++;
            column++;
            while(position < input.length() && Character.isDigit(input.charAt(position))){
                position++;
                column++;
            }
        }
        String text = input.substring(startPositon,position);
        return new LogoToken(LogoTokenType.NUMBER,text,line, startColumnPosition);

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
        while(position < input.length() && input.charAt(position) != '\r' && input.charAt(position) != '\n'){
            position++;
            column++;
        }
        String text = input.substring(startPosition, position);
        return new LogoToken(LogoTokenType.COMMENT, text, line, startColumnPosition);
    }
    private LogoToken string() {
        int startPosition = position;
        int startColumnPosition = column;
        position++;
        column++;
        while(position < input.length() && !Character.isWhitespace(input.charAt(position)) && input.charAt(position) != '[' && input.charAt(position) != ']'){
            position++;
            column++;
        }
        String text = input.substring(startPosition, position);
        return new LogoToken(LogoTokenType.STRING, text, line, startColumnPosition);
    }

}
