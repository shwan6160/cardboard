/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.yaml.YamlPath
 */
package com.bergerkiller.bukkit.tc.properties.standard.type;

import com.bergerkiller.bukkit.common.config.yaml.YamlPath;
import com.bergerkiller.bukkit.tc.Localization;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TrainNameFormat {
    public static final TrainNameFormat DEFAULT = new TrainNameFormat("train", "", false);
    private static final Pattern NAME_GUESS_PATTERN = Pattern.compile("^(.*?)\\d+([^\\d]*)$");
    private final String _prefix;
    private final String _postfix;
    private final boolean _optionalNumber;

    private TrainNameFormat(String prefix, String postfix, boolean optionalNumber) {
        this._prefix = prefix;
        this._postfix = postfix;
        this._optionalNumber = optionalNumber;
    }

    public boolean hasOptionalNumber() {
        return this._optionalNumber;
    }

    public String generate(int number) {
        if (this._optionalNumber && number <= 1) {
            return this._prefix;
        }
        StringBuilder str = new StringBuilder(this._prefix.length() + this._postfix.length() + 5);
        str.append(this._prefix);
        str.append(number);
        str.append(this._postfix);
        return str.toString();
    }

    public String search(Predicate<String> filter) {
        int number = 1;
        String name;
        while (!filter.test(name = this.generate(number))) {
            ++number;
        }
        return name;
    }

    public boolean matches(String trainName) {
        if (this._optionalNumber && trainName.equals(this._prefix)) {
            return true;
        }
        if (!trainName.startsWith(this._prefix) || !trainName.endsWith(this._postfix)) {
            return false;
        }
        int end = trainName.length() - this._postfix.length();
        if (end == this._prefix.length()) {
            return false;
        }
        for (int i = this._prefix.length(); i < end; ++i) {
            if (Character.isDigit(trainName.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public VerifyResult verify() {
        if (this._optionalNumber && this._prefix.isEmpty() && this._postfix.isEmpty()) {
            return VerifyResult.ERR_EMPTY;
        }
        VerifyResult result = TrainNameFormat.verify(this._prefix);
        if (result != VerifyResult.OK && result != VerifyResult.ERR_EMPTY) {
            return result;
        }
        result = TrainNameFormat.verify(this._postfix);
        if (result != VerifyResult.OK && result != VerifyResult.ERR_EMPTY) {
            return result;
        }
        return VerifyResult.OK;
    }

    public static VerifyResult verify(String name) {
        if (name.isEmpty()) {
            return VerifyResult.ERR_EMPTY;
        }
        YamlPath path = YamlPath.create((String)name);
        if (path.depth() != 1 || path.isListElement()) {
            return VerifyResult.ERR_INVALID_CHAR;
        }
        return VerifyResult.OK;
    }

    public static TrainNameFormat parse(String format) {
        int lastHashIndex = format.lastIndexOf(35);
        if (lastHashIndex == -1) {
            return new TrainNameFormat(format, "", true);
        }
        return new TrainNameFormat(format.substring(0, lastHashIndex), format.substring(lastHashIndex + 1), false);
    }

    public static TrainNameFormat guess(String trainName) {
        Matcher matcher = NAME_GUESS_PATTERN.matcher(trainName);
        if (matcher.find()) {
            return new TrainNameFormat(matcher.group(1), matcher.group(2), false);
        }
        return new TrainNameFormat(trainName, "", true);
    }

    public boolean equals(Object o) {
        if (o instanceof TrainNameFormat) {
            TrainNameFormat other = (TrainNameFormat)o;
            return this._prefix.equals(other._prefix) && this._postfix.equals(other._postfix) && this._optionalNumber == other._optionalNumber;
        }
        return false;
    }

    public String toString() {
        if (this._optionalNumber) {
            return this._prefix + this._postfix;
        }
        return this._prefix + "#" + this._postfix;
    }

    public static enum VerifyResult {
        OK(null, null),
        ERR_EMPTY(Localization.COMMAND_INPUT_NAME_EMPTY, Localization.COMMAND_MODEL_CONFIG_INPUT_NAME_EMPTY),
        ERR_INVALID_CHAR(Localization.COMMAND_INPUT_NAME_INVALID, Localization.COMMAND_MODEL_CONFIG_INPUT_NAME_INVALID);

        private final Localization message;
        private final Localization modelMessage;

        private VerifyResult(Localization message, Localization modelMessage) {
            this.message = message;
            this.modelMessage = modelMessage;
        }

        public Localization getMessage() {
            return this.message;
        }

        public Localization getModelMessage() {
            return this.modelMessage;
        }
    }
}

