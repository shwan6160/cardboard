/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations;

import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import java.util.LinkedList;
import java.util.Locale;

public class SourcePreprocessor {
    private final ClassResolver resolver;
    private StringBuilder result = new StringBuilder();
    private int disabledIfLevel = 0;
    private boolean disabledIfExpression = false;
    private LinkedList<String> selectStack = new LinkedList();
    private boolean isFirstSelectCase = false;

    public SourcePreprocessor(ClassResolver resolver) {
        this.resolver = resolver;
    }

    public String preprocess(String declaration) {
        int endIndex;
        int startIndex;
        while ((startIndex = declaration.lastIndexOf("/*")) != -1 && (endIndex = declaration.indexOf("*/", startIndex + 2)) != -1) {
            declaration = declaration.substring(0, startIndex) + declaration.substring(endIndex + 2);
        }
        for (String line : declaration.split("\\r?\\n")) {
            this.preprocessLine(line);
        }
        return this.result.toString();
    }

    public void preprocessLine(String line) {
        int statementEnd;
        int start;
        String lineTrimmed = line.trim();
        String lineLower = lineTrimmed.toLowerCase(Locale.ENGLISH);
        int selectCaseIndent = -1;
        if (lineLower.startsWith("#select ")) {
            int start2 = line.indexOf(35);
            if (start2 != -1) {
                this.selectStack.offer(line.substring(start2 + 8));
                this.isFirstSelectCase = true;
            }
            return;
        }
        if (lineLower.startsWith("#endselect")) {
            this.selectStack.pollLast();
            if (this.isFirstSelectCase) {
                this.isFirstSelectCase = false;
                return;
            }
            start = line.indexOf(35);
            if (start == -1) {
                return;
            }
            line = line.substring(0, start) + "#endif";
            lineTrimmed = line.trim();
            lineLower = lineTrimmed.toLowerCase(Locale.ENGLISH);
        } else if (lineLower.startsWith("#case")) {
            start = line.indexOf(35);
            if (start == -1 || this.selectStack.isEmpty()) {
                return;
            }
            String statement = line.substring(start + 5);
            String afterElse = null;
            String statementTrimmed = statement;
            int statementEnd2 = statement.indexOf(58);
            if (statementEnd2 != -1) {
                afterElse = statement.substring(statementEnd2 + 1);
                statementTrimmed = statement.substring(0, statementEnd2);
                selectCaseIndent = start + 5 + statementEnd2;
            }
            boolean isFinalElse = statementTrimmed.trim().equalsIgnoreCase("else");
            StringBuilder replacement = new StringBuilder();
            replacement.append(line.substring(0, start));
            if (!this.isFirstSelectCase && isFinalElse) {
                replacement.append("#else");
                if (afterElse != null) {
                    replacement.append(':').append(afterElse);
                }
            } else {
                replacement.append(this.isFirstSelectCase ? "#if " : "#elseif ");
                replacement.append(this.selectStack.peekLast()).append(statement);
            }
            line = replacement.toString();
            lineTrimmed = line.trim();
            lineLower = lineTrimmed.toLowerCase(Locale.ENGLISH);
            this.isFirstSelectCase = false;
        }
        if ((lineLower.startsWith("#if") || lineLower.startsWith("#elseif") || lineLower.startsWith("#else")) && (statementEnd = line.indexOf(58)) != -1) {
            this.preprocessLine(line.substring(0, statementEnd));
            StringBuilder statementStr = new StringBuilder();
            int numSpacesIndent = selectCaseIndent != -1 ? selectCaseIndent : statementEnd;
            for (int n = 0; n <= numSpacesIndent; ++n) {
                statementStr.append(' ');
            }
            statementStr.append(line.substring(statementEnd + 1));
            this.preprocessLine(statementStr.toString());
            return;
        }
        if (this.disabledIfLevel > 1) {
            if (lineLower.startsWith("#if")) {
                ++this.disabledIfLevel;
            } else if (lineLower.startsWith("#endif")) {
                --this.disabledIfLevel;
            }
            return;
        }
        if (this.disabledIfLevel == 1) {
            if (lineLower.startsWith("#if")) {
                ++this.disabledIfLevel;
            } else if (lineLower.startsWith("#endif")) {
                --this.disabledIfLevel;
            } else if (lineLower.startsWith("#else")) {
                int ifIdx = lineTrimmed.indexOf("if", 5);
                boolean evaluates = true;
                if (ifIdx != -1) {
                    String expr = lineTrimmed.substring(ifIdx + 2).trim();
                    evaluates = this.resolver.evaluateExpression(expr);
                }
                if (!this.disabledIfExpression && evaluates) {
                    --this.disabledIfLevel;
                }
            }
            return;
        }
        this.disabledIfExpression = false;
        if (lineLower.startsWith("#if")) {
            String expr = lineTrimmed.substring(3).trim();
            if (!this.resolver.evaluateExpression(expr)) {
                ++this.disabledIfLevel;
            }
            return;
        }
        if (lineLower.startsWith("#else")) {
            ++this.disabledIfLevel;
            this.disabledIfExpression = true;
            return;
        }
        if (lineLower.startsWith("#endif")) {
            return;
        }
        if (lineLower.startsWith("//")) {
            return;
        }
        this.result.append(line).append('\n');
        if (lineLower.startsWith("#set ")) {
            int nameEndIdx = (lineTrimmed = lineTrimmed.substring(5).trim()).indexOf(32);
            if (nameEndIdx == -1) {
                return;
            }
            String varName = lineTrimmed.substring(0, nameEndIdx);
            String varValue = lineTrimmed.substring(nameEndIdx + 1);
            while (varValue.length() > 0 && varValue.charAt(0) == ' ') {
                varValue = varValue.substring(1);
            }
            this.resolver.setVariable(varName, varValue);
            return;
        }
    }
}

