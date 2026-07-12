/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.asm;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SourceFileProcessor {
    private static final Set<String> IGNORED_NAMES = new HashSet<String>(Arrays.asList("u", "i", "b", "p", "pre", "code"));

    public Map<String, String> process(File sourceFile) throws IOException {
        HashMap<String, String> variables = new HashMap<String, String>();
        String content = new String(Files.readAllBytes(sourceFile.toPath()), StandardCharsets.UTF_8);
        char curr_char = ' ';
        char prev_char = ' ';
        boolean in_string = false;
        boolean in_block_comment = false;
        boolean in_block_comment_contents = false;
        int start_indent = 0;
        int min_start_indent = Integer.MAX_VALUE;
        StringBuilder block_comment = new StringBuilder();
        for (int i = 0; i < content.length(); ++i) {
            prev_char = curr_char;
            curr_char = content.charAt(i);
            if (curr_char == '\r') continue;
            if (!in_block_comment) {
                if (curr_char == '\n') {
                    start_indent = 0;
                    continue;
                }
                if (curr_char == ' ') {
                    ++start_indent;
                    continue;
                }
                if (curr_char == '\t') {
                    start_indent += 4;
                    continue;
                }
                if (in_string && prev_char != '\\' && curr_char == '\"') {
                    in_string = false;
                    continue;
                }
                if (!in_string && curr_char == '\"') {
                    in_string = true;
                    continue;
                }
                if (in_string || prev_char != 47 || curr_char != 42) continue;
                in_block_comment = true;
                in_block_comment_contents = false;
                start_indent += 2;
                min_start_indent = Integer.MAX_VALUE;
                continue;
            }
            if (!in_block_comment_contents) {
                if (curr_char == '\n') {
                    block_comment.append('\n');
                    start_indent = 0;
                    continue;
                }
                if (curr_char == ' ' || curr_char == '*') {
                    ++start_indent;
                    continue;
                }
                if (curr_char == '\t') {
                    start_indent += 4;
                    continue;
                }
                in_block_comment_contents = true;
                this.appendSpaces(block_comment, start_indent);
            } else if (curr_char == '\n') {
                in_block_comment_contents = false;
                start_indent = 0;
                block_comment.append('\n');
                continue;
            }
            if (prev_char == '*' && curr_char == '/') {
                in_block_comment = false;
                in_block_comment_contents = false;
                this.cleanupSpaces(block_comment, min_start_indent);
                this.processBlockComment(block_comment, variables);
                block_comment.setLength(0);
                continue;
            }
            min_start_indent = Math.min(min_start_indent, start_indent);
            block_comment.append(curr_char);
        }
        return variables;
    }

    private void appendSpaces(StringBuilder str, int n) {
        while (--n >= 0) {
            str.append(' ');
        }
    }

    private void cleanupPreceedingSpaces(StringBuilder str) {
        int i = 0;
        while (i < str.length()) {
            char c = str.charAt(i);
            if (c == '\n') {
                str.replace(0, i + 1, "");
                i = 0;
                continue;
            }
            if (c != ' ') break;
            ++i;
        }
    }

    private void cleanupSpaces(StringBuilder str, int indent) {
        this.cleanupPreceedingSpaces(str);
        for (int i = str.length() - 1; i >= 0; --i) {
            char c = str.charAt(i);
            if (c == ' ' || c == '\n') {
                continue;
            }
            str.replace(i + 1, str.length(), "");
            break;
        }
        int remainingSpaces = indent;
        int numSkipped = 0;
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (c == ' ' && remainingSpaces > 0) {
                --remainingSpaces;
                ++numSkipped;
            } else if (numSkipped > 0) {
                str.replace(i - numSkipped, i, "");
                numSkipped = 0;
            }
            if (c != '\n') continue;
            remainingSpaces = indent;
        }
    }

    public void processBlockComment(StringBuilder str, Map<String, String> variables) {
        if (str.length() < 2 || str.charAt(0) != '<') {
            return;
        }
        int endIndex = str.indexOf(">", 1);
        if (endIndex == -1) {
            return;
        }
        int newLineIndex = str.indexOf("\n", 1);
        if (newLineIndex == -1 || newLineIndex < endIndex) {
            return;
        }
        String variableName = str.substring(1, endIndex).trim();
        if (IGNORED_NAMES.contains(variableName)) {
            return;
        }
        str.replace(0, endIndex + 1, "");
        this.cleanupPreceedingSpaces(str);
        String variableValue = str.toString();
        variables.put(variableName, variableValue);
    }
}

