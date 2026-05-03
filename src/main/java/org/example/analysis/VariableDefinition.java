package org.example.analysis;

public class VariableDefinition {

    public final String name;
    public final int line;
    public final int column;

    public VariableDefinition(String text, int line, int column) {
        this.name = text;
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return "Variable{name='" + name + ", position: (" + line +","+ column +")}" ;
    }
}
