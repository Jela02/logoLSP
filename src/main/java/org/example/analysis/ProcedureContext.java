package org.example.analysis;

public class ProcedureContext {

    final String name;
    private final int startLine;
    private final int startColumn;
    int endLine = Integer.MAX_VALUE;
    int endColumn = Integer.MAX_VALUE;

    ProcedureContext(String name, int startLine, int startColumn) {
        this.name = name;
        this.startLine = startLine;
        this.startColumn = startColumn;
    }

    boolean contains(int line, int column) {
        boolean afterStart = line > startLine || (line == startLine && column >= startColumn);
        boolean beforeEnd = line < endLine || (line == endLine && column <= endColumn);
        return afterStart && beforeEnd;
    }

}
