package org.example.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LogoLexerTest {

    @Test
    void tokenizesToAndEndAsSeparateTokenTypes() {
        LogoLexer lexer = new LogoLexer("to square :size\nend");

        List<LogoToken> tokens = lexer.tokenize();

        assertEquals(LogoTokenType.TO, tokens.get(0).type());
        assertEquals("to", tokens.get(0).text());
        assertEquals(LogoTokenType.IDENTIFIER, tokens.get(1).type());
        assertEquals("square", tokens.get(1).text());
        assertEquals(LogoTokenType.VARIABLE, tokens.get(2).type());
        assertEquals(":size", tokens.get(2).text());
        assertEquals(LogoTokenType.END, tokens.get(4).type());
        assertEquals("end", tokens.get(4).text());
    }

    @Test
    void tokenizesBuiltInCommandsAsCommand() {
        assertTokenType("pos", LogoTokenType.COMMAND);
        assertTokenType("xcor", LogoTokenType.COMMAND);
        assertTokenType("set", LogoTokenType.COMMAND);
        assertTokenType("setpos", LogoTokenType.COMMAND);
        assertTokenType("ellipse", LogoTokenType.COMMAND);
        assertTokenType("wrap", LogoTokenType.COMMAND);
        assertTokenType("shown?", LogoTokenType.COMMAND);
        assertTokenType("setcolor", LogoTokenType.COMMAND);
        assertTokenType("pendown?", LogoTokenType.COMMAND);
        assertTokenType("define", LogoTokenType.COMMAND);
        assertTokenType("def", LogoTokenType.COMMAND);
        assertTokenType("localmake", LogoTokenType.VARIABLE_COMMAND);
        assertTokenType("thing", LogoTokenType.VARIABLE_COMMAND);
        assertTokenType("dotimes", LogoTokenType.LOOP_KEYWORD);
        assertTokenType("do.while", LogoTokenType.LOOP_KEYWORD);
        assertTokenType("iftrue", LogoTokenType.CONDITION_KEYWORD);
        assertTokenType("wait", LogoTokenType.COMMAND);
        assertTokenType("readword", LogoTokenType.COMMAND);
        assertTokenType("minus", LogoTokenType.COMMAND);
        assertTokenType("power", LogoTokenType.COMMAND);
        assertTokenType("array", LogoTokenType.COMMAND);
        assertTokenType("number?", LogoTokenType.COMMAND);
        assertTokenType("substringp", LogoTokenType.COMMAND);
    }

    @Test
    void tokenizesDecimalNumbersAndCommas() {
        LogoLexer lexer = new LogoLexer("setcolor [50.5,100,-25.25]");

        List<LogoToken> tokens = lexer.tokenize();

        assertEquals(LogoTokenType.NUMBER, tokens.get(2).type());
        assertEquals("50.5", tokens.get(2).text());
        assertEquals(LogoTokenType.SYMBOL, tokens.get(3).type());
        assertEquals(",", tokens.get(3).text());
        assertEquals(LogoTokenType.NUMBER, tokens.get(4).type());
        assertEquals("100", tokens.get(4).text());
        assertEquals(LogoTokenType.NUMBER, tokens.get(6).type());
        assertEquals("-25.25", tokens.get(6).text());
    }

    private void assertTokenType(String text, LogoTokenType tokenType) {
        LogoLexer lexer = new LogoLexer(text);
        List<LogoToken> tokens = lexer.tokenize();

        assertEquals(1, tokens.size());
        assertEquals(tokenType, tokens.getFirst().type());
    }

}
