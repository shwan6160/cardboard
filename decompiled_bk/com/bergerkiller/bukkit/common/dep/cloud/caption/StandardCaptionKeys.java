/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.caption;

import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class StandardCaptionKeys {
    private static final Collection<Caption> RECOGNIZED_CAPTIONS = new LinkedList<Caption>();
    public static final Caption ARGUMENT_PARSE_FAILURE_BOOLEAN = StandardCaptionKeys.of("argument.parse.failure.boolean");
    public static final Caption ARGUMENT_PARSE_FAILURE_NUMBER = StandardCaptionKeys.of("argument.parse.failure.number");
    public static final Caption ARGUMENT_PARSE_FAILURE_CHAR = StandardCaptionKeys.of("argument.parse.failure.char");
    public static final Caption ARGUMENT_PARSE_FAILURE_STRING = StandardCaptionKeys.of("argument.parse.failure.string");
    public static final Caption ARGUMENT_PARSE_FAILURE_UUID = StandardCaptionKeys.of("argument.parse.failure.uuid");
    public static final Caption ARGUMENT_PARSE_FAILURE_ENUM = StandardCaptionKeys.of("argument.parse.failure.enum");
    public static final Caption ARGUMENT_PARSE_FAILURE_REGEX = StandardCaptionKeys.of("argument.parse.failure.regex");
    public static final Caption ARGUMENT_PARSE_FAILURE_FLAG_UNKNOWN_FLAG = StandardCaptionKeys.of("argument.parse.failure.flag.unknown");
    public static final Caption ARGUMENT_PARSE_FAILURE_FLAG_DUPLICATE_FLAG = StandardCaptionKeys.of("argument.parse.failure.flag.duplicate_flag");
    public static final Caption ARGUMENT_PARSE_FAILURE_FLAG_NO_FLAG_STARTED = StandardCaptionKeys.of("argument.parse.failure.flag.no_flag_started");
    public static final Caption ARGUMENT_PARSE_FAILURE_FLAG_MISSING_ARGUMENT = StandardCaptionKeys.of("argument.parse.failure.flag.missing_argument");
    public static final Caption ARGUMENT_PARSE_FAILURE_FLAG_NO_PERMISSION = StandardCaptionKeys.of("argument.parse.failure.flag.no_permission");
    public static final Caption ARGUMENT_PARSE_FAILURE_COLOR = StandardCaptionKeys.of("argument.parse.failure.color");
    public static final Caption ARGUMENT_PARSE_FAILURE_DURATION = StandardCaptionKeys.of("argument.parse.failure.duration");
    public static final Caption ARGUMENT_PARSE_FAILURE_AGGREGATE_MISSING_INPUT = StandardCaptionKeys.of("argument.parse.failure.aggregate.missing");
    public static final Caption ARGUMENT_PARSE_FAILURE_AGGREGATE_COMPONENT_FAILURE = StandardCaptionKeys.of("argument.parse.failure.aggregate.failure");
    public static final Caption ARGUMENT_PARSE_FAILURE_EITHER = StandardCaptionKeys.of("argument.parse.failure.either");
    public static final Caption EXCEPTION_UNEXPECTED = StandardCaptionKeys.of("exception.unexpected");
    public static final Caption EXCEPTION_INVALID_ARGUMENT = StandardCaptionKeys.of("exception.invalid_argument");
    public static final Caption EXCEPTION_NO_SUCH_COMMAND = StandardCaptionKeys.of("exception.no_such_command");
    public static final Caption EXCEPTION_NO_PERMISSION = StandardCaptionKeys.of("exception.no_permission");
    public static final Caption EXCEPTION_INVALID_SENDER = StandardCaptionKeys.of("exception.invalid_sender");
    public static final Caption EXCEPTION_INVALID_SENDER_LIST = StandardCaptionKeys.of("exception.invalid_sender_list");
    public static final Caption EXCEPTION_INVALID_SYNTAX = StandardCaptionKeys.of("exception.invalid_syntax");

    private StandardCaptionKeys() {
    }

    private static @NonNull Caption of(@NonNull String key) {
        Caption caption = Caption.of(key);
        RECOGNIZED_CAPTIONS.add(caption);
        return caption;
    }

    public static @NonNull Collection<@NonNull Caption> standardCaptionKeys() {
        return Collections.unmodifiableCollection(RECOGNIZED_CAPTIONS);
    }
}

