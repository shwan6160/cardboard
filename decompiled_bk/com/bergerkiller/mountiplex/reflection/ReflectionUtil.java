/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.asm.ASMUtil;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.signature.MethodSignature;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ReflectionUtil {
    public static String filterGenerics(String input) {
        int genEnd = input.indexOf(62);
        if (genEnd == -1) {
            return input;
        }
        int genStart = input.lastIndexOf(60, genEnd);
        if (genStart == -1) {
            return input;
        }
        return ReflectionUtil.filterGenerics(input.substring(0, genStart) + input.substring(genEnd + 1));
    }

    public static String getTypeName(Class<?> type) {
        int numArrays = 0;
        while (type.isArray()) {
            type = type.getComponentType();
            ++numArrays;
        }
        String name = type.isPrimitive() || BoxedType.getUnboxedType(type) != null ? type.getSimpleName() : MPLType.getName(type);
        for (int i = 0; i < numArrays; ++i) {
            name = name + "[]";
        }
        return name;
    }

    public static String getAccessibleTypeName(Class<?> type) {
        if (Resolver.isPublic(type)) {
            String name = ReflectionUtil.getTypeName(type);
            if (!Resolver.resolveClassPath(name).equals(name)) {
                name = "MPL_NOREMAP$" + name;
            }
            return name;
        }
        return "Object";
    }

    public static String getAccessibleTypeCast(Class<?> type) {
        if (type != Object.class && Resolver.isPublic(type)) {
            String name = ReflectionUtil.getTypeName(type);
            if (Resolver.resolveClassPath(name).equals(name)) {
                return '(' + name + ')';
            }
            name = "(MPL_NOREMAP$" + name + ')';
            return name;
        }
        return "";
    }

    public static String getCastString(Class<?> type) {
        StringBuilder body = new StringBuilder();
        body.append('(');
        body.append(ReflectionUtil.getTypeName(type));
        body.append(')');
        return body.toString();
    }

    public static int parseModifiers(String[] parts, int count) {
        int modifiers = 0;
        for (int i = 0; i < count; ++i) {
            if (parts[i].equals("public")) {
                modifiers |= 1;
                continue;
            }
            if (parts[i].equals("private")) {
                modifiers |= 2;
                continue;
            }
            if (parts[i].equals("protected")) {
                modifiers |= 4;
                continue;
            }
            if (parts[i].equals("final")) {
                modifiers |= 0x10;
                continue;
            }
            if (parts[i].equals("static")) {
                modifiers |= 8;
                continue;
            }
            if (parts[i].equals("volatile")) {
                modifiers |= 0x40;
                continue;
            }
            if (!parts[i].equals("abstract")) continue;
            modifiers |= 0x400;
        }
        return modifiers;
    }

    public static boolean compareModifiers(int m1, int m2) {
        return Modifier.isPrivate(m1) == Modifier.isPrivate(m2) && Modifier.isPublic(m1) == Modifier.isPublic(m2) && Modifier.isProtected(m1) == Modifier.isProtected(m2) && Modifier.isStatic(m1) == Modifier.isStatic(m2) && Modifier.isFinal(m1) == Modifier.isFinal(m2);
    }

    public static Stream<Class<?>> getAllDeclaringClasses(Class<?> clazz) {
        return MountiplexUtil.iterateNullTerminated(clazz, Class::getDeclaringClass);
    }

    public static Stream<Class<?>> getAllClasses(Class<?> clazz) {
        return MountiplexUtil.iterateNullTerminated(clazz, Class::getSuperclass);
    }

    public static Stream<Class<?>> getAllClassesAndInterfaces(Class<?> clazz) {
        return Stream.concat(ReflectionUtil.getAllClasses(clazz), ReflectionUtil.getAllClasses(clazz).flatMap(ReflectionUtil::discoverAllInterfaces).distinct());
    }

    private static Stream<Class<?>> discoverAllInterfaces(Class<?> type) {
        Class<?>[] interfaces = type.getInterfaces();
        return Stream.concat(Stream.of(interfaces), Stream.of(interfaces).flatMap(ReflectionUtil::discoverAllInterfaces));
    }

    public static Stream<Method> getDeclaredMethods(Class<?> clazz) {
        return Stream.of(ReflectionUtil.sortSyntheticMethods(clazz.getDeclaredMethods()));
    }

    public static Stream<Method> getMethods(Class<?> clazz) {
        return Stream.of(ReflectionUtil.sortSyntheticMethods(clazz.getMethods()));
    }

    private static Method[] sortSyntheticMethods(Method[] methods) {
        Arrays.sort(methods, 0, methods.length, (m1, m2) -> {
            int mods = m1.getModifiers() | m2.getModifiers();
            if (Modifier.isPrivate(mods) || Modifier.isStatic(mods) || !MPLType.getName(m1).equals(MPLType.getName(m2)) || !Arrays.equals(m1.getParameterTypes(), m2.getParameterTypes()) || m1.getReturnType().equals(m2.getReturnType())) {
                return 0;
            }
            if (m1.getReturnType().isAssignableFrom(m2.getReturnType())) {
                return 1;
            }
            return -1;
        });
        return methods;
    }

    public static Stream<Method> getAllMethods(Class<?> clazz) {
        return ReflectionUtil.getAllClassesAndInterfaces(clazz).flatMap(ReflectionUtil::getDeclaredMethods).filter(ReflectionUtil.createDuplicateMethodFilter());
    }

    public static Predicate<Method> createDuplicateMethodFilter() {
        HashSet filtered = new HashSet();
        return method -> Modifier.isStatic(method.getModifiers()) || filtered.add(new MethodSignature((Method)method));
    }

    public static Stream<Field> getAllFields(Class<?> clazz) {
        return MountiplexUtil.iterateNullTerminated(clazz, Class::getSuperclass).flatMap(t -> Stream.of(t.getDeclaredFields()));
    }

    public static Stream<Field> getAllNonStaticFields(Class<?> clazz) {
        return ReflectionUtil.getAllFields(clazz).filter(m -> !Modifier.isStatic(m.getModifiers()));
    }

    public static Stream<Field> getAllStaticFields(Class<?> clazz) {
        return ReflectionUtil.getAllFields(clazz).filter(m -> Modifier.isStatic(m.getModifiers()));
    }

    public static String stringifyType(Class<?> type) {
        return type == null ? "[null]" : type.getSimpleName();
    }

    public static String stringifyMethodSignature(Method method) {
        String str = Modifier.toString(method.getModifiers() & 0xFFFFFFBF);
        str = str + " " + ReflectionUtil.stringifyType(method.getReturnType());
        str = str + " " + MPLType.getName(method);
        str = str + "(";
        boolean first = true;
        for (Class<?> param : method.getParameterTypes()) {
            if (first) {
                first = false;
            } else {
                str = str + ", ";
            }
            str = str + ReflectionUtil.stringifyType(param);
        }
        str = str + ")";
        return str;
    }

    private static boolean hasMethod(Class<?> type, Method method) {
        try {
            String name = MPLType.getName(method);
            return MPLType.getDeclaredMethod(type, name, method.getParameterTypes()) != null;
        }
        catch (Throwable t) {
            return false;
        }
    }

    public static Class<?> findMethodClass(Method method) {
        Class<?> type = method.getDeclaringClass();
        for (Class<?> iif : type.getInterfaces()) {
            if (!ReflectionUtil.hasMethod(iif, method)) continue;
            return iif;
        }
        Class<?> lowestSubClass = type;
        while ((type = type.getSuperclass()) != null) {
            if (!ReflectionUtil.hasMethod(type, method)) continue;
            lowestSubClass = type;
        }
        return lowestSubClass;
    }

    public static <A extends Annotation, V> V recurseFindAnnotationValue(Class<?> type, Class<A> annotationClass, Function<A, V> method, V defaultValue) {
        return ReflectionUtil.getAllDeclaringClasses(type).map(t -> t.getAnnotation(annotationClass)).filter(Objects::nonNull).map(method).findFirst().orElse(defaultValue);
    }

    public static RuntimeException fixMethodInvokeException(Method method, Object instance, Object[] args, Throwable ex) {
        RuntimeException result = null;
        if (ex instanceof InvocationTargetException) {
            ex = ReflectionUtil.getCleanCause(ex);
        } else {
            if (ex instanceof IllegalAccessException) {
                ReflectionUtil.filterInvokeTraceElements(ex, 0);
                result = new RuntimeException("Failed to invoke method " + ReflectionUtil.stringifyMethodSignature(method), ex);
                result.setStackTrace(new StackTraceElement[0]);
                return result;
            }
            if (Modifier.isStatic(method.getModifiers())) {
                if (instance != null) {
                    result = new IllegalArgumentException("Method " + ReflectionUtil.stringifyMethodSignature(method) + " is static and can not be invoked on instance of type " + ReflectionUtil.stringifyType(instance.getClass()));
                }
            } else if (instance == null) {
                result = new IllegalArgumentException("Method " + ReflectionUtil.stringifyMethodSignature(method) + " is not static and requires an instance passed in (instance is null)");
            } else {
                Class<?> m_type = ReflectionUtil.findMethodClass(method);
                if (!m_type.isInstance(instance)) {
                    result = new IllegalArgumentException("Method " + ReflectionUtil.stringifyMethodSignature(method) + " is declared in class " + ReflectionUtil.stringifyType(m_type) + " and can not be invoked on object of type " + ReflectionUtil.stringifyType(instance.getClass()));
                }
            }
            if (result == null) {
                Class<?>[] m_params = method.getParameterTypes();
                if (args.length != m_params.length) {
                    result = new IllegalArgumentException("Method " + ReflectionUtil.stringifyMethodSignature(method) + " Illegal number of arguments provided. Expected " + m_params.length + ", but got " + args.length);
                } else {
                    for (int i = 0; i < m_params.length; ++i) {
                        Object arg = args[i];
                        if (m_params[i].isPrimitive() && arg == null) {
                            result = new IllegalArgumentException("Method " + ReflectionUtil.stringifyMethodSignature(method) + " Passed in null for primitive type parameter #" + (i + 1));
                            break;
                        }
                        if (arg == null || BoxedType.tryBoxType(m_params[i]).isInstance(arg)) continue;
                        result = new IllegalArgumentException("Method " + ReflectionUtil.stringifyMethodSignature(method) + " Passed in wrong type for parameter #" + (i + 1) + " (" + ReflectionUtil.stringifyType(m_params[i]) + " expected, but was " + ReflectionUtil.stringifyType(arg.getClass()) + ")");
                        break;
                    }
                }
            }
            if (result == null && ex instanceof IllegalArgumentException) {
                result = (IllegalArgumentException)ex;
            }
            if (result != null) {
                result.setStackTrace(ex.getStackTrace());
                ReflectionUtil.filterInvokeTraceElements(result, 0);
                ReflectionUtil.addMethodTraceElement(result, method);
                return result;
            }
        }
        if (ex instanceof ExceptionInInitializerError) {
            ExceptionInInitializerError e = (ExceptionInInitializerError)ex;
            ReflectionUtil.filterCause(e);
            ReflectionUtil.filterInvokeTraceElements(e, 0);
        }
        if (ex instanceof Error) {
            throw (Error)ex;
        }
        if (ex instanceof RuntimeException) {
            result = (RuntimeException)ex;
        } else {
            result = new RuntimeException("Failed to invoke method " + ReflectionUtil.stringifyMethodSignature(method), ex);
            result.setStackTrace(new StackTraceElement[0]);
        }
        return result;
    }

    private static Throwable getCleanCause(Throwable ex) {
        Throwable cause = ex.getCause();
        int index = cause.getStackTrace().length - ex.getStackTrace().length;
        ReflectionUtil.filterInvokeTraceElements(cause, index);
        return cause;
    }

    private static void filterCause(Throwable t) {
        if (t.getCause() != null) {
            ReflectionUtil.filterCause(t.getCause());
            ArrayList<StackTraceElement> newElem = new ArrayList<StackTraceElement>(Arrays.asList(t.getCause().getStackTrace()));
            StackTraceElement[] parentElem = t.getStackTrace();
            for (int i = parentElem.length - 1; i >= 0 && ((StackTraceElement)newElem.get(newElem.size() - 1)).equals(parentElem[i]); --i) {
                newElem.remove(newElem.size() - 1);
            }
            t.getCause().setStackTrace(newElem.toArray(new StackTraceElement[newElem.size()]));
        }
    }

    private static void filterInvokeTraceElements(Throwable t, int offset) {
        ArrayList<StackTraceElement> stack_trace = new ArrayList<StackTraceElement>(Arrays.asList(t.getStackTrace()));
        if (offset >= 0 && offset < stack_trace.size()) {
            StackTraceElement e;
            String c;
            ListIterator iter = stack_trace.listIterator(offset);
            while (!(!iter.hasNext() || (c = (e = (StackTraceElement)iter.next()).getClassName()).equals("java.lang.reflect.Method") && e.getMethodName().equals("invoke") || !c.startsWith("java.lang.reflect.") && !c.startsWith("sun.reflect."))) {
                iter.remove();
            }
            t.setStackTrace(stack_trace.toArray(new StackTraceElement[stack_trace.size()]));
        }
    }

    private static void addMethodTraceElement(Throwable t, Method m) {
        StackTraceElement[] oldTrace = t.getStackTrace();
        StackTraceElement[] newTrace = new StackTraceElement[oldTrace.length + 1];
        System.arraycopy(oldTrace, 0, newTrace, 1, oldTrace.length);
        newTrace[0] = ASMUtil.findMethodDetails(m);
        t.setStackTrace(newTrace);
    }
}

