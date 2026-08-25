package de.robv.android.xposed.callbacks;

import de.robv.android.xposed.IXposedHookLoadPackage;

/**
 * Compile-time stub.
 * Real class lives in this package: de.robv.android.xposed.callbacks.
 * LoadPackageParam is a nested class inside it.
 */
public abstract class XC_LoadPackage extends XCallback implements IXposedHookLoadPackage {

    public XC_LoadPackage() {
        super(0);
    }

    public XC_LoadPackage(int priority) {
        super(priority);
    }

    public static final class LoadPackageParam extends XCallback.Param {
        public String packageName;
        public String processName;
        public ClassLoader classLoader;
        public boolean isFirstApplication;
    }

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
    }
}