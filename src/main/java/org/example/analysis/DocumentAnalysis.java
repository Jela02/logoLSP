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
    private final Map<String, List<VariableDefinition>> globalVariables = new HashMap<>();
    private final Map<String, Map<String, List<VariableDefinition>>> localVariablesByProcedure = new HashMap<>();

    private final List<LogoToken> undefinedVariables = new ArrayList<>();
    private final List<LogoToken> undefinedProcedureCalls = new ArrayList<>();

    public List<LogoToken> getUndefinedVariables(){
        return undefinedVariables;
    }

    public List<LogoToken> getUndefinedProcedureCalls(){
        return undefinedProcedureCalls;
    }

    public Map<String, ProcedureDefinition> getProcedures() {
        return procedures;
    }

    public List<LogoToken> getTokens() {
        return tokens;
    }

    public List<ProcedureCall> getProcedureCalls() {
        return procedureCalls;
    }

    public List<VariableDefinition> getVariableDefinitions() {
        return variableDefinitions;
    }

    public void analyze(List<LogoToken> tokens){
        this.tokens = tokens;
        procedures.clear();
        procedureCalls.clear();
        variableDefinitions.clear();
        globalVariables.clear();
        localVariablesByProcedure.clear();
        undefinedVariables.clear();
        undefinedProcedureCalls.clear();

        ProcedureDefinition currentProcedure = null;
        for(int i = 0; i < tokens.size(); i++){ // find all global and local variables and procedure definitions
            LogoToken token = tokens.get(i);
            if (token.type == LogoTokenType.TO){ // procedure definition
                if( i + 1 < tokens.size()){
                    LogoToken procedureName = tokens.get(i + 1);
                    if(procedureName.type == LogoTokenType.IDENTIFIER){
                        currentProcedure = new ProcedureDefinition(procedureName.text, procedureName.line, procedureName.column);
                        procedures.put(procedureName.text, currentProcedure);
                    }
                }
            }
            if (token.type == LogoTokenType.END && currentProcedure != null) {
                currentProcedure.setEndPosition(token.line, token.column);
                currentProcedure = null;
                continue;
            }
            if (token.type == LogoTokenType.IDENTIFIER){
                if (i > 0 && tokens.get(i-1).type == LogoTokenType.TO) continue; // definition
                procedureCalls.add(new ProcedureCall(token.text, token.line, token.column)); // procedure call
            }

            if (token.type == LogoTokenType.VARIABLE && isProcedureParameter(i)) {
                registerVariable(localVariable(token, currentProcedure));
                continue;
            }

            if (isLoopVariableDeclaration(i)) {
                registerVariable(localVariable(token, currentProcedure));
                continue;
            }

            if (token.type == LogoTokenType.STRING && i > 0 && isVariableDefinitionCommand(tokens.get(i - 1))) {
                registerVariable(declaredVariable(token, tokens.get(i - 1), currentProcedure));
            }

        }
        for(int i = 0; i < tokens.size(); i++) { //procedure can be defined later => second loop is necessary
            LogoToken token = tokens.get(i);

            if (token.type == LogoTokenType.IDENTIFIER){
                if (i > 0 && tokens.get(i-1).type == LogoTokenType.TO) continue; // definition
                if (isVariableDeclaration(i)) continue;
                if (findProcedure(token.text) == null) {
                    undefinedProcedureCalls.add(token);
                }
            }

            if (token.type == LogoTokenType.VARIABLE && !isProcedureParameter(i) && findVariable(token.text, token.line, token.column) == null) {
                undefinedVariables.add(token);
                continue;
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
        ProcedureDefinition procedure = procedureAt(line, column);

        if (procedure != null) {
            VariableDefinition localVariable = findLatestAtOrBefore(
                    localVariablesByProcedure
                            .getOrDefault(procedure.name, Map.of())
                            .getOrDefault(normalizedName, List.of()),
                    line,
                    column
            );
            if (localVariable != null) {
                return localVariable;
            }
        }

        return findLatestAtOrBefore(
                globalVariables.getOrDefault(normalizedName, List.of()),
                line,
                column
        );
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

    public boolean isVariableDeclaration(int index) {
        LogoToken token = tokens.get(index);
        return (token.type == LogoTokenType.VARIABLE && isProcedureParameter(index))
                || isLoopVariableDeclaration(index)
                || (token.type == LogoTokenType.STRING && index > 0 && isVariableDefinitionCommand(tokens.get(index - 1)));
    }

    public boolean isVariableReference(LogoToken token) {
        return token.type == LogoTokenType.VARIABLE
                || (token.type == LogoTokenType.IDENTIFIER && findVariable(token.text, token.line, token.column) != null);
    }

    private boolean isLoopVariableDeclaration(int index) {
        return tokens.get(index).type == LogoTokenType.IDENTIFIER
                && index >= 2
                && tokens.get(index - 1).type == LogoTokenType.SYMBOL
                && tokens.get(index - 1).text.equals("[")
                && tokens.get(index - 2).type == LogoTokenType.LOOP_KEYWORD
                && (tokens.get(index - 2).text.equalsIgnoreCase("dotimes") || tokens.get(index - 2).text.equalsIgnoreCase("for"));
    }

    private boolean isVariableDefinitionCommand(LogoToken token) {
        if (token.type == LogoTokenType.DEFINITION_KEYWORD) {
            return true;
        }
        return token.type == LogoTokenType.VARIABLE_COMMAND && token.text.equalsIgnoreCase("localmake");
    }

    private VariableDefinition declaredVariable(LogoToken token, LogoToken command, ProcedureDefinition currentProcedure) {
        if (currentProcedure != null && (command.text.equalsIgnoreCase("local") || command.text.equalsIgnoreCase("localmake"))) {
            return localVariable(token, currentProcedure);
        }
        return new VariableDefinition(token.text, token.line, token.column);
    }

    private VariableDefinition localVariable(LogoToken token, ProcedureDefinition currentProcedure) {
        String procedureName = currentProcedure == null ? null : currentProcedure.name;
        return new VariableDefinition(token.text, token.line, token.column, procedureName);
    }

    private ProcedureDefinition procedureAt(int line, int column) {
        for (ProcedureDefinition procedure : procedures.values()) {
            if (procedure.contains(line, column)) {
                return procedure;
            }
        }
        return null;
    }

    private void registerVariable(VariableDefinition variable) {
        variableDefinitions.add(variable);
        if (variable.isGlobal()) {
            globalVariables.computeIfAbsent(variable.name, name -> new ArrayList<>()).add(variable);
            return;
        }
        localVariablesByProcedure
                .computeIfAbsent(variable.procedureName, name -> new HashMap<>())
                .computeIfAbsent(variable.name, name -> new ArrayList<>())
                .add(variable);
    }

    private VariableDefinition findLatestAtOrBefore(List<VariableDefinition> definitions, int line, int column) {
        VariableDefinition latest = null;
        for (VariableDefinition variable : definitions) {
            if (isAtOrBefore(variable, line, column)) {
                latest = variable;
            }
        }
        return latest;
    }

    private boolean isAtOrBefore(VariableDefinition variable, int line, int column) {
        return variable.line < line || (variable.line == line && variable.column <= column);
    }

}
