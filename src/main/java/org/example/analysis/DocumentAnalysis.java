package org.example.analysis;

import org.example.parser.LogoToken;
import org.example.parser.LogoTokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentAnalysis {
    private List<LogoToken> tokens = new ArrayList<>();
    private final Map<String, ProcedureDefinition> procedures = new HashMap<>();
    private final List<ProcedureCall> procedureCalls = new ArrayList<>();
    private final List<VariableDefinition> variableDefinitions = new ArrayList<>();
    //List<String> globalVariableDeclarations = new ArrayList<>();

    public void analyze(List<LogoToken> tokens){
        this.tokens = tokens;
        procedures.clear();
        procedureCalls.clear();
        variableDefinitions.clear();

        ProcedureContext currentProcedure = null;
        for(int i = 0; i < tokens.size(); i++){
            LogoToken token = tokens.get(i);
            if (token.type == LogoTokenType.TO){ // procedure definition
                if( i + 1 < tokens.size()){
                    LogoToken procedureName = tokens.get(i + 1);
                    if(procedureName.type == LogoTokenType.IDENTIFIER){
                        procedures.put(procedureName.text, new ProcedureDefinition(procedureName.text, procedureName.line, procedureName.column));
                        currentProcedure = new ProcedureContext(procedureName.text, token.line, token.column);
                    }
                }
            }
            if (token.type == LogoTokenType.END && currentProcedure != null) {
                currentProcedure.endLine = token.line;
                currentProcedure.endColumn = token.column;
                currentProcedure = null;
                continue;
            }
            if (token.type == LogoTokenType.IDENTIFIER){
                if (i > 0 && tokens.get(i-1).type == LogoTokenType.TO) continue; // definition
                procedureCalls.add(new ProcedureCall(token.text, token.line, token.column)); // procedure call
            }

            if (token.type == LogoTokenType.VARIABLE && isProcedureParameter(i)) {
                variableDefinitions.add(localVariable(token, currentProcedure));
                continue;
            }

            if (token.type == LogoTokenType.STRING && i > 0 && isVariableDefinitionCommand(tokens.get(i - 1))) {
                variableDefinitions.add(declaredVariable(token, tokens.get(i - 1), currentProcedure));
            }
        }
    }

    public LogoToken findTokenAt(int line, int column){
        for (LogoToken token: tokens){
            if (token.line == line && column < token.column + token.text.length() && column >= token.column){
                return token;
            }
        }
        return null;
    }

    public ProcedureDefinition findProcedure(String text) {
        return procedures.get(text);
    }

    public VariableDefinition findVariable(String name, int line, int column){
        String normalizedName = VariableDefinition.normalizeName(name);
        ProcedureContext procedure = procedureAt(line, column);
        if (procedure != null) {
            for (VariableDefinition variable : variableDefinitions) {
                if (variable.scope == VariableDefinition.Scope.LOCAL
                        && variable.name.equals(normalizedName)
                        && procedure.name.equals(variable.procedureName)
                        && isAtOrBefore(variable, line, column)) {
                    return variable;
                }
            }
        }

        for (VariableDefinition variable : variableDefinitions) {
            if (variable.scope == VariableDefinition.Scope.GLOBAL
                    && variable.name.equals(normalizedName)
                    && isAtOrBefore(variable, line, column)) {
                return variable;
            }
        }
        return null;
    }

    private boolean isProcedureParameter(int index) {
        int line = tokens.get(index).line;
        for (int i = index - 1; i >= 0 && tokens.get(i).line == line; i--) {
            if (tokens.get(i).type == LogoTokenType.TO) {
                return true;
            }
        }
        return false;
    }

    private boolean isVariableDefinitionCommand(LogoToken token) {
        if (token.type == LogoTokenType.DEFINITION_KEYWORD) {
            return true;
        }
        return token.type == LogoTokenType.VARIABLE_COMMAND && token.text.equalsIgnoreCase("localmake"); // odvojiti u dva slucaja??
    }

    private VariableDefinition declaredVariable(LogoToken token, LogoToken command, ProcedureContext currentProcedure) {
        if (currentProcedure != null && (command.text.equalsIgnoreCase("local") || command.text.equalsIgnoreCase("localmake"))) {
            return localVariable(token, currentProcedure);
        }
        return new VariableDefinition(token.text, token.line, token.column, VariableDefinition.Scope.GLOBAL, null);
    }

    private VariableDefinition localVariable(LogoToken token, ProcedureContext currentProcedure) {
        String procedureName = currentProcedure == null ? null : currentProcedure.name;
        return new VariableDefinition(token.text, token.line, token.column, VariableDefinition.Scope.LOCAL, procedureName);
    }

    private ProcedureContext procedureAt(int line, int column) {
        ProcedureContext active = null;
        for (int i = 0; i < tokens.size(); i++) {
            LogoToken token = tokens.get(i);
            if (token.type == LogoTokenType.TO && i + 1 < tokens.size() && tokens.get(i + 1).type == LogoTokenType.IDENTIFIER) {
                active = new ProcedureContext(tokens.get(i + 1).text, token.line, token.column);
            } else if (token.type == LogoTokenType.END && active != null) {
                active.endLine = token.line;
                active.endColumn = token.column;
                if (active.contains(line, column)) {
                    return active;
                }
                active = null;
            }
        }
        if (active != null && active.contains(line, column)) {
            return active;
        }
        return null;
    }

    private boolean isAtOrBefore(VariableDefinition variable, int line, int column) {
        return variable.line < line || (variable.line == line && variable.column <= column);
    }


}

