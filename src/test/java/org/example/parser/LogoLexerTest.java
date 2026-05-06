package org.example.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LogoLexerTest {

    @Test
    void tokenizesToAndEndAsSeparateTokenTypes() {
        LogoLexer lexer = new LogoLexer("to square :size\nend");

        List<LogoToken> tokens = lexer.tokenize();

        assertEquals(LogoTokenType.TO, tokens.get(0).type);
        assertEquals("to", tokens.get(0).text);
        assertEquals(LogoTokenType.IDENTIFIER, tokens.get(1).type);
        assertEquals("square", tokens.get(1).text);
        assertEquals(LogoTokenType.VARIABLE, tokens.get(2).type);
        assertEquals(":size", tokens.get(2).text);
        assertEquals(LogoTokenType.END, tokens.get(4).type);
        assertEquals("end", tokens.get(4).text);
    }

    @Test
    void tokenizesTurtleAcademyCommandsByFeatureGroup() {
        assertTokenType("pos", LogoTokenType.TURTLE_QUERY);
        assertTokenType("xcor", LogoTokenType.TURTLE_QUERY);
        assertTokenType("set", LogoTokenType.POSITION_COMMAND);
        assertTokenType("setpos", LogoTokenType.POSITION_COMMAND);
        assertTokenType("ellipse", LogoTokenType.DRAWING_COMMAND);
        assertTokenType("wrap", LogoTokenType.WINDOW_COMMAND);
        assertTokenType("shown?", LogoTokenType.WINDOW_QUERY);
        assertTokenType("setcolor", LogoTokenType.PEN_COMMAND);
        assertTokenType("pendown?", LogoTokenType.PEN_QUERY);
        assertTokenType("define", LogoTokenType.PROCEDURE_COMMAND);
        assertTokenType("def", LogoTokenType.PROCEDURE_COMMAND);
        assertTokenType("localmake", LogoTokenType.VARIABLE_COMMAND);
        assertTokenType("thing", LogoTokenType.VARIABLE_COMMAND);
        assertTokenType("dotimes", LogoTokenType.LOOP_KEYWORD);
        assertTokenType("do.while", LogoTokenType.LOOP_KEYWORD);
        assertTokenType("iftrue", LogoTokenType.CONDITION_KEYWORD);
        assertTokenType("wait", LogoTokenType.CONTROL_COMMAND);
        assertTokenType("readword", LogoTokenType.RECEIVER_COMMAND);
        assertTokenType("minus", LogoTokenType.MATH_COMMAND);
        assertTokenType("power", LogoTokenType.MATH_COMMAND);
        assertTokenType("array", LogoTokenType.LIST_COMMAND);
        assertTokenType("number?", LogoTokenType.PREDICATE_COMMAND);
        assertTokenType("substringp", LogoTokenType.PREDICATE_COMMAND);
    }

    @Test
    void tokenizesDecimalNumbersAndCommas() {
        LogoLexer lexer = new LogoLexer("setcolor [50.5,100,-25.25]");

        List<LogoToken> tokens = lexer.tokenize();

        assertEquals(LogoTokenType.NUMBER, tokens.get(2).type);
        assertEquals("50.5", tokens.get(2).text);
        assertEquals(LogoTokenType.SYMBOL, tokens.get(3).type);
        assertEquals(",", tokens.get(3).text);
        assertEquals(LogoTokenType.NUMBER, tokens.get(4).type);
        assertEquals("100", tokens.get(4).text);
        assertEquals(LogoTokenType.NUMBER, tokens.get(6).type);
        assertEquals("-25.25", tokens.get(6).text);
    }

    private void assertTokenType(String text, LogoTokenType tokenType) {
        LogoLexer lexer = new LogoLexer(text);
        List<LogoToken> tokens = lexer.tokenize();

        assertEquals(1, tokens.size());
        assertEquals(tokenType, tokens.get(0).type);
    }

}
