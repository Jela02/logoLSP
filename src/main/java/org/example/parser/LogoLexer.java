package org.example.parser;

import java.util.ArrayList;
import java.util.List;

public class LogoLexer {
    private final String input;
    private int line = 0;
    private int column = 0;
    private int position = 0;

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
            if(Character.isDigit(c)){
                tokens.add(number());
                continue;
            }
            if(c == ':'){
                tokens.add(variable());
                continue;
            }
            if(Character.isLetter(c)){
                tokens.add(word());
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

        while(position < input.length() && Character.isLetterOrDigit(input.charAt(position))){
            position++;
            column++;
        }
        String text = input.substring(startPosition,position).toLowerCase();
        return new LogoToken(LogoTokenType.VARIABLE, text, line, startColumnPosition);

    }

    private LogoToken word() {
        int startPosition = position;
        int startColumnPosition = column;
        while (position < input.length() && Character.isLetterOrDigit(input.charAt(position))){
            position++;
            column++;
        }

        String text = input.substring(startPosition, position);
        return new LogoToken(keywordType(text), text, line, startColumnPosition);
    }

    private LogoTokenType keywordType(String text) {
        return switch (text){
            case "to" -> LogoTokenType.TO;
            case "end" ->LogoTokenType.END;
            case "forward" -> LogoTokenType.FORWARD;
            case "right" -> LogoTokenType.RIGHT;
            case "left" -> LogoTokenType.LEFT;
            default -> LogoTokenType.IDENTIFIER;
        };
    }

    private LogoToken number() {
        int startPositon = position;
        int startColumnPosition = column;

        while(position < input.length() && Character.isDigit(input.charAt(position))){
            position++;
            column++;
        }
        String text = input.substring(startPositon,position);
        return new LogoToken(LogoTokenType.NUMBER,text,line, startColumnPosition);

    }

}
