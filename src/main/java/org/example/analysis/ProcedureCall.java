package org.example.analysis;

public class ProcedureCall {
    public final String name;
    public final int line;
    public final int column;

    public ProcedureCall(String name, int line, int column) {
        this.name = name;
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return "Procedure call: {name = "+ name + ", position: (" + line +","+ column +")}" ;
    }

}
