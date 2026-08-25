package de.robv.android.xposed;

/** Compile-time stub. */
public abstract class XC_MethodReplacement extends XC_MethodHook {
    public XC_MethodReplacement() {
        super(0);
    }

    public XC_MethodReplacement(int priority) {
        super(priority);
    }

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        param.setResult(replaceHookedMethod(param));
    }

    protected abstract Object replaceHookedMethod(MethodHookParam param) throws Throwable;
}