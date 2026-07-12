/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.bases;

import com.bergerkiller.bukkit.common.bases.CheckedSupplier;

public final class CheckedDeferredSupplier<T>
implements CheckedSupplier<T> {
    private ErrorHandlingCheckedSupplier<T> supplier;

    private CheckedDeferredSupplier(CheckedSupplier<T> supplier) {
        this.supplier = new InitializingSupplier(supplier);
    }

    private CheckedDeferredSupplier(ErrorHandlingCheckedSupplier<T> supplier) {
        this.supplier = supplier;
    }

    public boolean isInitialized() {
        return this.supplier.isInitialized();
    }

    public boolean hasError() {
        return this.supplier.hasError();
    }

    public Throwable getError() {
        return this.supplier.getError();
    }

    @Override
    public T get() throws Throwable {
        return this.supplier.get();
    }

    public static <T> CheckedDeferredSupplier<T> of(CheckedSupplier<T> supplier) {
        return new CheckedDeferredSupplier<T>(supplier);
    }

    public static <T> CheckedDeferredSupplier<T> call(CheckedSupplier<T> supplier) {
        try {
            T result = supplier.get();
            return new CheckedDeferredSupplier<T>(new ResultSupplier<T>(result));
        }
        catch (Throwable t) {
            return new CheckedDeferredSupplier(new ErrorSupplier(t));
        }
    }

    private static interface ErrorHandlingCheckedSupplier<T>
    extends CheckedSupplier<T> {
        public boolean isInitialized();

        public boolean hasError();

        public Throwable getError();
    }

    private final class InitializingSupplier
    implements ErrorHandlingCheckedSupplier<T> {
        private final CheckedSupplier<T> baseSupplier;

        public InitializingSupplier(CheckedSupplier<T> baseSupplier) {
            this.baseSupplier = baseSupplier;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public T get() throws Throwable {
            CheckedDeferredSupplier checkedDeferredSupplier = CheckedDeferredSupplier.this;
            synchronized (checkedDeferredSupplier) {
                if (CheckedDeferredSupplier.this.supplier == this) {
                    try {
                        Object result = this.baseSupplier.get();
                        CheckedDeferredSupplier.this.supplier = new ResultSupplier(result);
                        return result;
                    }
                    catch (Throwable t) {
                        CheckedDeferredSupplier.this.supplier = new ErrorSupplier(t);
                        throw t;
                    }
                }
            }
            return CheckedDeferredSupplier.this.supplier.get();
        }

        @Override
        public boolean isInitialized() {
            return false;
        }

        @Override
        public boolean hasError() {
            try {
                this.get();
                return false;
            }
            catch (Throwable t) {
                return true;
            }
        }

        @Override
        public Throwable getError() {
            try {
                this.get();
                return null;
            }
            catch (Throwable t) {
                return t;
            }
        }
    }

    private static final class ResultSupplier<T>
    implements ErrorHandlingCheckedSupplier<T> {
        public final T value;

        public ResultSupplier(T value) {
            this.value = value;
        }

        @Override
        public T get() throws Throwable {
            return this.value;
        }

        @Override
        public boolean isInitialized() {
            return true;
        }

        @Override
        public boolean hasError() {
            return false;
        }

        @Override
        public Throwable getError() {
            return null;
        }
    }

    private static final class ErrorSupplier<T>
    implements ErrorHandlingCheckedSupplier<T> {
        public final Throwable error;

        public ErrorSupplier(Throwable error) {
            this.error = error;
        }

        @Override
        public T get() throws Throwable {
            throw this.error;
        }

        @Override
        public boolean isInitialized() {
            return true;
        }

        @Override
        public boolean hasError() {
            return true;
        }

        @Override
        public Throwable getError() {
            return this.error;
        }
    }
}

