package org.example.analysis;

public class VariableDefinition {

    public enum Scope {
        GLOBAL,
        LOCAL
    }

    public final String name;
    public final int line;
    public final int column;
    public final Scope scope; // add global or local scope
    public final String procedureName;

    public VariableDefinition(String text, int line, int column) {
        this(text, line, column, Scope.GLOBAL, null);
    }

    public VariableDefinition(String text, int line, int column, Scope scope, String procedureName) {
        this.name = normalizeName(text);
        this.line = line;
        this.column = column;
        this.scope = scope;
        this.procedureName = procedureName;
    }
    // skip : or "
    public static String normalizeName(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        if (text.charAt(0) == ':' || text.charAt(0) == '"') {
            return text.substring(1).toLowerCase();
        }
        return text.toLowerCase();
    }

    @Override
    public String toString() {
        return "Variable{name='" + name + "', scope=" + scope + ", procedureName='" + procedureName + "', position: (" + line + "," + column + ")}";
    }
}
