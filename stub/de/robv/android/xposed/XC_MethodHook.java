package de.robv.android.xposed;

import de.robv.android.xposed.callbacks.XCallback;

/** Compile-time stub. */
public abstract class XC_MethodHook extends XCallback {
    public XC_MethodHook() {
        super(0);
    }

    public XC_MethodHook(int priority) {
        super(priority);
    }

    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
    }

    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
    }

    public static class MethodHookParam {
        public Object thisObject;
        public Object[] args;
        public Object getResult() {
            return null;
        }
        public void setResult(Object result) {
        }
        public Object getResultOrThrowable() {
            return null;
        }
        public Throwable getThrowable() {
            return null;
        }
        public void setThrowable(Throwable throwable) {
        }
        public boolean hasThrowable() {
            return false;
        }
        public Object getResultOrDefault(Object defaultValue) {
            return null;
        }
    }

    public static class Unhook {
        public void unhook() {
        }
        public boolean isUnhooked() {
            return false;
        }
    }
}