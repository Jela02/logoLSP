package org.example.parser;

public class LogoToken {
    public final LogoTokenType type;
    public final String text;
    public final int line;
    public final int column;


    public LogoToken(LogoTokenType type, String text, int line, int column) {
        this.type = type;
        this.text = text;
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return type + " " + text + "(" + line + "," + column + ")";
    }
}
