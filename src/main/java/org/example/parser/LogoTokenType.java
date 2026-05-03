package org.example.parser;

public enum LogoTokenType {
    //KEYWORDS
    TO, END,
    // BUILT-INS
    FORWARD, RIGHT, LEFT,
    // USER PROCEDURES
    IDENTIFIER,
    //VARIABLES
    VARIABLE,
    // LITERALS
    NUMBER,
    //SYMBOLS
    NEWLINE,
    // FALLBACK
    UNKNOWN
}
