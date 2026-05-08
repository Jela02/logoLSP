package org.example.analysis;

import org.example.parser.LogoLexer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DocumentAnalysisTest {

    @Test
    void localProcedureParameterShadowsGlobalVariable() {
        DocumentAnalysis analysis = analyze("""
                make "x 1
                to demo :x
                print :x
                end
                print :x
                """);

        VariableDefinition local = analysis.findVariable(":x", 2, 7);
        VariableDefinition global = analysis.findVariable(":x", 4, 7);

        assertNotNull(local);
        assertEquals("demo", local.procedureName);
        assertEquals(1, local.line);
        assertEquals(8, local.column);

        assertNotNull(global);
        assertNull(global.procedureName);
        assertEquals(0, global.line);
        assertEquals(5, global.column);
    }

    @Test
    void localmakeCreatesLocalDefinitionInsideProcedure() {
        DocumentAnalysis analysis = analyze("""
                make "x 1
                to demo
                localmake "x 2
                print :x
                end
                """);

        VariableDefinition variable = analysis.findVariable(":x", 3, 7);

        assertNotNull(variable);
        assertEquals("demo", variable.procedureName);
        assertEquals(2, variable.line);
        assertEquals(10, variable.column);
    }

    @Test
    void laterLocalDeclarationDoesNotCaptureEarlierReference() {
        DocumentAnalysis analysis = analyze("""
                make "x 1
                to demo
                print :x
                local "x
                end
                """);

        VariableDefinition variable = analysis.findVariable(":x", 2, 7);

        assertNotNull(variable);
        assertNull(variable.procedureName);
        assertEquals(0, variable.line);
        assertEquals(5, variable.column);
    }

    @Test
    void reportsUndefinedVariables() {
        DocumentAnalysis analysis = analyze("""
                to demo
                print :missing
                end
                """);

        assertEquals(1, analysis.getUndefinedVariables().size());
        assertEquals(":missing", analysis.getUndefinedVariables().get(0).text);
    }

    @Test
    void reportsUndefinedProcedureCalls() {
        DocumentAnalysis analysis = analyze("""
                missingProcedure

                to definedProcedure
                end
                """);

        assertEquals(1, analysis.getUndefinedProcedureCalls().size());
        assertEquals("missingProcedure", analysis.getUndefinedProcedureCalls().get(0).text);
    }

    @Test
    void dotimesCounterIsLocalVariable() {
        DocumentAnalysis analysis = analyze("""
                to demo :lines
                dotimes [ i :lines ] [
                  fd :i
                ]
                end
                """);

        VariableDefinition counter = analysis.findVariable(":i", 2, 6);

        assertNotNull(counter);
        assertEquals("demo", counter.procedureName);
        assertEquals(1, counter.line);
        assertEquals(10, counter.column);
        assertEquals(0, analysis.getUndefinedVariables().size());
        assertEquals(0, analysis.getUndefinedProcedureCalls().size());
    }

    private DocumentAnalysis analyze(String text) {
        DocumentAnalysis analysis = new DocumentAnalysis();
        analysis.analyze(new LogoLexer(text).tokenize());
        return analysis;
    }
}
