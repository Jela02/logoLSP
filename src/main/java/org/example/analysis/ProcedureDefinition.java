package org.example.analysis;

public class ProcedureDefinition {
    public final String name;
    public final int line;
    public final int column;
    public ProcedureDefinition(String text, int line, int column) {
        this.name = text;
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return "Procedure: {name = "+ name + ", position: (" + line +","+ column +")}" ;
    }
}
