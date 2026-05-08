package org.example.analysis;

public record ProcedureCall(String name, int line, int column) {
    @Override
    public String toString() {
        return "Procedure call: {name = " + name + ", position: (" + line + "," + column + ")}";
    }
}
