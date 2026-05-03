package org.example.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LogoLexerTest {
    @Test
    void testNumberToken() {
        LogoLexer lexer = new LogoLexer("123");
        List<LogoToken> tokens = lexer.tokenize();

        assertEquals(1, tokens.size());
        assertEquals(LogoTokenType.NUMBER, tokens.get(0).type);
        assertEquals("123", tokens.get(0).text);
    }

    @Test
    void testKeywordTo(){
        LogoLexer lexer = new LogoLexer("to");
        List<LogoToken> tokens = lexer.tokenize();

        assertEquals(1, tokens.size());
        assertEquals(LogoTokenType.TO, tokens.get(0).type);

    }

    @Test
    void testProcedure() {
        LogoLexer lexer = new LogoLexer("square");
        List<LogoToken> tokens = lexer.tokenize();

        assertEquals(1, tokens.size());
        assertEquals(LogoTokenType.IDENTIFIER, tokens.get(0).type);
        assertEquals("square", tokens.get(0).text);

    }

    @Test
    void testVariable() {
        LogoLexer lexer = new LogoLexer(":size");

        List<LogoToken> tokens = lexer.tokenize();

        assertEquals(1, tokens.size());
        assertEquals(LogoTokenType.VARIABLE, tokens.get(0).type);
        assertEquals(":size", tokens.get(0).text);
    }


    @Test
    void testProgram() {
        String code = """
        to square :size
          forward :size
          right 90
        end
        """;

        LogoLexer lexer = new LogoLexer(code);

        List<LogoToken> tokens = lexer.tokenize();

        assertTrue(tokens.size() > 0);

        assertTrue(tokens.stream().anyMatch(t -> t.type == LogoTokenType.TO));
        assertTrue(tokens.stream().anyMatch(t -> t.type == LogoTokenType.FORWARD));
        assertTrue(tokens.stream().anyMatch(t -> t.type == LogoTokenType.END));
    }
    @Test
    void testUnknownChar() {
        LogoLexer lexer = new LogoLexer("@");

        List<LogoToken> tokens = lexer.tokenize();

        assertEquals(LogoTokenType.UNKNOWN, tokens.get(0).type);
    }

    @Test
    void testNewLine() {
        LogoLexer lexer = new LogoLexer("to square\nend");

        List<LogoToken> tokens = lexer.tokenize();

        boolean hasNewline = tokens.stream()
                .anyMatch(t -> t.type == LogoTokenType.NEWLINE);

        assertTrue(hasNewline);
    }

}
