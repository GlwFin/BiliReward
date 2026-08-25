package io.nebula.bilireward;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Version-agnostic Bilibili rewarded-ad bypass module.
 *
 * Discovery is done by type signatures, not hard-coded names, so that
 * obfuscated class/method/field names (which change every release) do not
 * break the module.  It targets any Activity that holds a RewardAdListener
 * field and neutralizes the "watch / click / jump" gates.
 *
 * Heuristics:
 *   1. find an Activity subclass whose declared field type is
 *      "com.bilibili.gripper.api.ad.reward.RewardAdListener"
 *      -> that is the reward-ad activity.
 *   2. after onCreate: set all boolean fields to true.
 *   3. every no-arg boolean method -> return true (the gate checks).
 *   4. every method taking a RewardAdCloseFrom parameter
 *      -> set all boolean args and fields to true (the close/grant paths).
 *   5. finish(): set all boolean fields to true.
 */
public final class BiliReward implements IXposedHookLoadPackage {

    private static final String TAG = "BiliReward";
    private static final String TARGET = "tv.danmaku.bili";
    private static final String LISTENER_TYPE =
            "com.bilibili.gripper.api.ad.reward.RewardAdListener";
    private static final String CLOSE_FROM_TYPE =
            "com.bilibili.gripper.api.ad.reward.RewardAdCloseFrom";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lp) {
        if (!TARGET.equals(lp.packageName)) return;
        try {
            Class<?> target = findTarget(lp.classLoader);
            if (target == null) {
                log("target not found");
                return;
            }
            log("target: " + target.getName());
            install(target, lp.classLoader);
            log("installed");
        } catch (Throwable t) {
            log("setup failed: " + t);
        }
    }

    // ---------------------------------------------------------------
    // Discovery: find the activity holding a RewardAdListener field.
    // ---------------------------------------------------------------
    private Class<?> findTarget(ClassLoader cl) {
        String[] candidates = {
                "com.bilibili.ad.reward.activity.BaseRewardAdActivity",
                "com.bilibili.ad.reward.RewardAdActivity",
        };
        for (String name : candidates) {
            try {
                Class<?> c = XposedHelpers.findClassIfExists(name, cl);
                if (c != null) {
                    for (Field f : c.getDeclaredFields()) {
                        if (f.getType().getName().equals(LISTENER_TYPE)) {
                            return c;
                        }
                    }
                }
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    // ---------------------------------------------------------------
    // Install: hook by type signature, not by name.
    // ---------------------------------------------------------------
    private void install(Class<?> target, ClassLoader cl) {
        // 1. onCreate: set all boolean fields to true.
        for (Method m : target.getDeclaredMethods()) {
            if (!"onCreate".equals(m.getName())) continue;
            XposedBridge.hookMethod(m, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam p) {
                    setAllBooleans(p.thisObject, target);
                    log("onCreate done");
                }
            });
            break;
        }

        // 2. All no-arg boolean methods -> return true.
        for (Method m : target.getDeclaredMethods()) {
            if (m.getParameterCount() != 0) continue;
            if (m.getReturnType() != boolean.class) continue;
            if ("onCreate".equals(m.getName())) continue;
            if ("finish".equals(m.getName())) continue;
            XposedBridge.hookMethod(m, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam p) {
                    return Boolean.TRUE;
                }
            });
        }

        // 3. Methods taking RewardAdCloseFrom -> set args & fields.
        Class<?> closeFromType = null;
        try {
            closeFromType = XposedHelpers.findClass(CLOSE_FROM_TYPE, cl);
        } catch (Throwable ignored) {
        }
        if (closeFromType != null) {
            for (Method m : target.getDeclaredMethods()) {
                boolean has = false;
                for (Class<?> pt : m.getParameterTypes()) {
                    if (pt == closeFromType) { has = true; break; }
                }
                if (!has) continue;
                XposedBridge.hookMethod(m, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam p) {
                        for (int i = 0; i < p.args.length; i++) {
                            if (p.args[i] instanceof Boolean) {
                                p.args[i] = Boolean.TRUE;
                            }
                        }
                        setAllBooleans(p.thisObject, target);
                        log("close-path handled");
                    }
                });
            }
        }

        // 4. finish(): set all boolean fields.
        for (Method m : target.getDeclaredMethods()) {
            if (!"finish".equals(m.getName())) continue;
            XposedBridge.hookMethod(m, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam p) {
                    setAllBooleans(p.thisObject, target);
                    log("finish handled");
                }
            });
            break;
        }

        // Sanity log.
        int b = 0, n = 0, c = 0;
        for (Method m : target.getDeclaredMethods()) {
            if (m.getReturnType() == boolean.class && m.getParameterCount() == 0
                    && !"onCreate".equals(m.getName()) && !"finish".equals(m.getName()))
                n++;
            if (closeFromType != null) {
                for (Class<?> pt : m.getParameterTypes())
                    if (pt == closeFromType) { c++; break; }
            }
        }
        for (Field f : target.getDeclaredFields())
            if (f.getType() == boolean.class) b++;
        log(String.format("ok: %d fields, %d gate methods, %d close methods", b, n, c));
    }

    // ---------------------------------------------------------------
    // Helper: set all boolean fields to true.
    // ---------------------------------------------------------------
    private static void setAllBooleans(Object obj, Class<?> target) {
        if (obj == null) return;
        for (Field f : target.getDeclaredFields()) {
            if (f.getType() != boolean.class) continue;
            try {
                f.setAccessible(true);
                f.setBoolean(obj, true);
            } catch (Throwable ignored) {
            }
        }
    }

    private static void log(String msg) {
        XposedBridge.log(TAG + ": " + msg);
    }
}
