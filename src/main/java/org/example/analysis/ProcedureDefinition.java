package org.example.analysis;

public class ProcedureDefinition {
    public final String name;
    public final int line;
    public final int column;

    private int endLine = Integer.MAX_VALUE;
    private int endColumn = Integer.MAX_VALUE;

    public ProcedureDefinition(String text, int line, int column) {
        this.name = text;
        this.line = line;
        this.column = column;
    }

    public void setEndPosition(int line, int column) {
        this.endLine = line;
        this.endColumn = column;
    }

    public boolean contains(int line, int column) {
        boolean afterStart = line > this.line || (line == this.line && column >= this.column);
        boolean beforeEnd = line < endLine || (line == endLine && column <= endColumn);
        return afterStart && beforeEnd;
    }

    @Override
    public String toString() {
        return "Procedure: {name = "+ name + ", position: (" + line +","+ column +")}" ;
    }
}
