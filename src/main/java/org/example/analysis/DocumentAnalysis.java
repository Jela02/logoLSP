package org.example.analysis;

import org.example.parser.LogoToken;
import org.example.parser.LogoTokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentAnalysis {
    private List<LogoToken> tokens = new ArrayList<>();
    private Map<String, ProcedureDefinition> procedures = new HashMap<>();
    private List<ProcedureCall> procedureCalls = new ArrayList<>();
    private final List<VariableDefinition> variables = new ArrayList<>();
    private final List<VariableDefinition> variableDefinitions = new ArrayList<>();

    public void analyze(List<LogoToken> tokens){
        this.tokens = tokens;
        for(int i = 0; i < tokens.size(); i++){
            LogoToken token = tokens.get(i);
            if (token.type == LogoTokenType.TO){ // procedure definition
                if( i + 1 < tokens.size()){
                    LogoToken procedureName = tokens.get(i + 1);
                    if(procedureName.type == LogoTokenType.IDENTIFIER){
                        procedures.put(procedureName.text, new ProcedureDefinition(procedureName.text, procedureName.line, procedureName.column));
                    }
                }
            }
            if (token.type == LogoTokenType.IDENTIFIER){
                if (i > 0 && tokens.get(i-1).type == LogoTokenType.TO) continue; // defition
                procedureCalls.add(new ProcedureCall(token.text, token.line, token.column)); // procedure call
            }

            if(token.type == LogoTokenType.VARIABLE) {
                VariableDefinition variable = new VariableDefinition(token.text, token.line, token.column);
                variables.add(variable);
                if (isVariableDeclaration(i)) {
                    variableDefinitions.add(variable);
                }
            }
        }
    }
    public boolean isVariableDeclaration(int index){
        if((index < 0 || index > tokens.size())) return false;
        int line = tokens.get(index).line;
        for (int i = index - 1; i >= 0 && tokens.get(i).line == line; i--){
            if(tokens.get(i).type == LogoTokenType.TO){
                return true;
            }
        }
        return false;
    }

    public LogoToken findTokenAt(int line, int column){
        for (LogoToken token: tokens){
            if (token.line == line && column < token.column + token.text.length() && column >= token.column){
                return token;
            }
        }
        return null;
    }


}
