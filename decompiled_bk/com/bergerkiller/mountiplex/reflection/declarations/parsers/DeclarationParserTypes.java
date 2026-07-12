/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations.parsers;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.declarations.Declaration;
import com.bergerkiller.mountiplex.reflection.declarations.FieldDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Remapping;
import com.bergerkiller.mountiplex.reflection.declarations.Requirement;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.DeclarationParser;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.MacroDeclarationParser;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.MacroParser;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.MacroPathParser;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.ParserStringBuffer;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.context.ClassDeclarationParserContext;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.context.DeclarationParserContext;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.context.SourceDeclarationParserContext;
import com.bergerkiller.mountiplex.reflection.util.StringBuffer;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Level;

public final class DeclarationParserTypes {
    public static final DeclarationParser COMMENT = new DeclarationParser(){

        @Override
        public boolean detect(ParserStringBuffer buffer, DeclarationParserContext context) {
            return buffer.startsWith("//");
        }

        @Override
        public void parse(ParserStringBuffer buffer, DeclarationParserContext context) {
            buffer.trimLine();
        }
    };
    public static final DeclarationParser BOOTSTRAP = new MacroParser("#bootstrap"){

        @Override
        public void parse(ParserStringBuffer buffer, DeclarationParserContext context) {
            int code_end_index;
            super.parse(buffer, context);
            StringBuffer postfix = buffer.get();
            if (postfix.startsWith("{")) {
                code_end_index = -1;
                int depth = 0;
                for (int i = 1; i < postfix.length(); ++i) {
                    char c = postfix.charAt(i);
                    if (c == '{') {
                        ++depth;
                        continue;
                    }
                    if (c != '}' || depth-- > 0) continue;
                    code_end_index = i + 1;
                    break;
                }
            } else {
                code_end_index = postfix.indexOf('\n');
            }
            if (code_end_index == -1) {
                buffer.set(StringBuffer.EMPTY);
            } else {
                String code = postfix.substringToString(0, code_end_index);
                context.getResolver().addBootstrap(code);
                buffer.trimWhitespace(code_end_index);
            }
        }
    };
    public static final DeclarationParser RESOLVER = new MacroParser("#resolver"){

        @Override
        public void parse(ParserStringBuffer buffer, DeclarationParserContext context) {
            super.parse(buffer, context);
            context.getResolver().setClassDeclarationResolverName(buffer.trimLine());
        }
    };
    public static final DeclarationParser REQUIREMENT = new MacroDeclarationParser("#require", "requirement"){

        @Override
        public void runMacro(Declaration dec, DeclarationParserContext context) {
            String name = "unknown";
            if (dec instanceof MethodDeclaration) {
                name = ((MethodDeclaration)dec).name.real();
            } else if (dec instanceof FieldDeclaration) {
                name = ((FieldDeclaration)dec).name.real();
            }
            context.getResolver().storeRequirement(new Requirement(name, dec));
        }
    };
    public static final DeclarationParser REMAPPING = new MacroDeclarationParser("#remap", "remapping"){

        @Override
        public void runMacro(Declaration dec, DeclarationParserContext context) {
            Declaration resolved = dec.discover();
            if (resolved == null) {
                if (context.getResolver().getLogErrors()) {
                    MountiplexUtil.LOGGER.log(Level.WARNING, "Remapping declaration not found!");
                    dec.discoverAlternatives();
                }
                return;
            }
            if (resolved instanceof MethodDeclaration) {
                MethodDeclaration mDec = (MethodDeclaration)resolved;
                if (mDec.body != null && mDec.method == null) {
                    MountiplexUtil.LOGGER.log(Level.WARNING, "Method bodies for remapped methods are not supported");
                    MountiplexUtil.LOGGER.log(Level.WARNING, "Method: " + resolved.toString());
                    return;
                }
                context.getResolver().storeRemapping(new Remapping.MethodRemapping(mDec));
            } else if (resolved instanceof FieldDeclaration) {
                context.getResolver().storeRemapping(new Remapping.FieldRemapping((FieldDeclaration)resolved));
            }
        }
    };
    public static final DeclarationParser WARNING = new MacroParser("#warning"){

        @Override
        public void parse(ParserStringBuffer buffer, DeclarationParserContext context) {
            super.parse(buffer, context);
            context.addWarning(buffer.trimLine());
        }
    };
    public static final DeclarationParser ERROR = new MacroParser("#error"){

        @Override
        public void parse(ParserStringBuffer buffer, DeclarationParserContext context) {
            super.parse(buffer, context);
            context.addError(buffer.trimLine());
        }
    };
    public static final DeclarationParser PACKAGE = new MacroPathParser("package"){

        @Override
        public void runMacro(String path, DeclarationParserContext context) {
            context.getResolver().setPackage(path);
        }
    };
    public static final DeclarationParser IMPORT = new MacroPathParser("import"){

        @Override
        public void runMacro(String path, DeclarationParserContext context) {
            context.getResolver().addImport(path);
        }
    };
    public static final DeclarationParser INCLUDE = new MacroPathParser("#include"){

        private InputStream openResource(SourceDeclarationParserContext context, String path) {
            if (context.getCurrentDirectory() == null) {
                return context.getClassLoader().getResourceAsStream(path);
            }
            try {
                String includedPath = context.getCurrentDirectory().getAbsolutePath() + File.separator + path.replace("/", File.separator);
                return new FileInputStream(includedPath);
            }
            catch (FileNotFoundException fileNotFoundException) {
                return null;
            }
        }

        @Override
        public void runMacro(String path, DeclarationParserContext origContext) {
            String inclSourceStr;
            SourceDeclarationParserContext context = (SourceDeclarationParserContext)origContext;
            String templateFile = context.getCurrentTemplateFile();
            if (path.startsWith(".") || path.startsWith("/")) {
                int moveUpIdx;
                int lastPathIdx = templateFile.lastIndexOf(47);
                path = lastPathIdx != -1 ? templateFile.substring(0, lastPathIdx) + "/" + path : templateFile + "/" + path;
                while ((moveUpIdx = path.indexOf("/../")) != -1) {
                    int before = path.lastIndexOf(47, moveUpIdx - 1);
                    if (before == -1) {
                        path = path.substring(moveUpIdx + 4);
                        continue;
                    }
                    path = path.substring(0, before) + "/" + path.substring(moveUpIdx + 4);
                }
                path = path.replace("/./", "/").replace("//", "/");
            }
            try (InputStream is = this.openResource(context, path);){
                if (is == null) {
                    MountiplexUtil.LOGGER.warning("Could not resolve include while parsing template: " + path);
                    MountiplexUtil.LOGGER.warning("Template file: " + templateFile);
                    return;
                }
                try (ByteArrayOutputStream result = new ByteArrayOutputStream();){
                    int length;
                    byte[] buffer = new byte[1024];
                    while ((length = is.read(buffer)) != -1) {
                        result.write(buffer, 0, length);
                    }
                    inclSourceStr = result.toString("UTF-8");
                }
            }
            catch (Throwable t) {
                MountiplexUtil.LOGGER.log(Level.WARNING, "Failed to load template " + path, t);
                return;
            }
            StringBuilder subSource = new StringBuilder();
            subSource.append(context.getResolver().saveDeclaration()).append("\n");
            subSource.append("#setpath ").append(path).append("\n");
            subSource.append(inclSourceStr);
            context.includeSource(StringBuffer.of(subSource));
        }
    };
    public static final DeclarationParser SET_PATH = new MacroPathParser("#setpath"){

        @Override
        public void runMacro(String path, DeclarationParserContext origContext) {
            SourceDeclarationParserContext context = (SourceDeclarationParserContext)origContext;
            context.setCurrentTemplateFile(path);
        }
    };
    public static final DeclarationParser SET_VARIABLE = new MacroParser("#set"){

        @Override
        public void parse(ParserStringBuffer buffer, DeclarationParserContext context) {
            super.parse(buffer, context);
            int nameEndIdx = buffer.get().indexOf(' ');
            if (nameEndIdx == -1) {
                buffer.set(StringBuffer.EMPTY);
                return;
            }
            String varName = buffer.get().substringToString(0, nameEndIdx);
            String varValue = "";
            buffer.trimWhitespace(nameEndIdx + 1);
            StringBuffer postfix = buffer.get();
            for (int cidx = 0; cidx < postfix.length(); ++cidx) {
                char c = postfix.charAt(cidx);
                if (!MountiplexUtil.containsChar(c, ParserStringBuffer.INVALID_NAME_CHARACTERS)) continue;
                varValue = postfix.substringToString(0, cidx);
                break;
            }
            buffer.trimLine();
            context.getResolver().setVariable(varName, varValue);
        }
    };
    public static final DeclarationParser CODE_BLOCK = new DeclarationParser(){

        @Override
        public boolean detect(ParserStringBuffer buffer, DeclarationParserContext context) {
            return buffer.startsWith("<code>");
        }

        @Override
        public void parse(ParserStringBuffer buffer, DeclarationParserContext origContext) {
            ClassDeclarationParserContext context = (ClassDeclarationParserContext)origContext;
            int endIdx = buffer.get().indexOf("</code>");
            if (endIdx == -1) {
                buffer.trimWhitespace(6);
            } else {
                int statementEnd;
                int startIndex;
                String code = buffer.get().substringToString(7, endIdx);
                int searchStartIndex = 0;
                while ((startIndex = code.indexOf("import ", searchStartIndex)) != -1 && (statementEnd = code.indexOf(59, startIndex + 7)) != 1) {
                    int importLineEnd;
                    char c;
                    int lineEnd = code.indexOf(10, startIndex + 7);
                    if (lineEnd != -1 && lineEnd < statementEnd) {
                        searchStartIndex = lineEnd;
                        continue;
                    }
                    int importLineStart = startIndex;
                    boolean validImport = true;
                    int i = startIndex - 1;
                    while (i >= 0 && (c = code.charAt(i)) != '\n') {
                        if (c != ' ') {
                            validImport = false;
                            break;
                        }
                        importLineStart = i--;
                    }
                    if (!validImport) {
                        searchStartIndex = statementEnd + 1;
                        continue;
                    }
                    context.addCodeImport(code.substring(startIndex + 7, statementEnd).trim());
                    for (importLineEnd = statementEnd + 1; importLineEnd < code.length(); ++importLineEnd) {
                        c = code.charAt(importLineEnd);
                        if (c == ' ') {
                            continue;
                        }
                        if (c != '\n') break;
                        ++importLineEnd;
                        break;
                    }
                    code = code.substring(0, importLineStart) + code.substring(importLineEnd);
                    searchStartIndex = importLineStart;
                }
                context.appendCode(code);
                buffer.set(buffer.get().substring(endIdx + 7));
                buffer.trimLine();
            }
        }
    };
}

