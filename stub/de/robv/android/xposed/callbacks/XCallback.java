package de.robv.android.xposed.callbacks;

/** Compile-time stub. */
public abstract class XCallback implements Comparable<XCallback> {
    public XCallback(int priority) {
    }

    @Override
    public int compareTo(XCallback other) {
        return 0;
    }

    /** Stub inner class that LoadPackageParam extends. */
    public static class Param {
    }
}