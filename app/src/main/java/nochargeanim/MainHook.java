package nochargeanim;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {

    private static final String CLS = "com.oplus.charge.viewmodel.OplusChargeAnimImpl";
    private static final String[] METHODS = {"createChargeAnim", "updateChargeAnimState"};

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!"com.android.systemui".equals(lpparam.packageName)) return;

        Class<?> clazz = XposedHelpers.findClassIfExists(CLS, lpparam.classLoader);
        if (clazz == null) {
            XposedBridge.log("NoChargeAnim: class not found: " + CLS);
            return;
        }

        for (final String name : METHODS) {
            for (Method m : clazz.getDeclaredMethods()) {
                if (!m.getName().equals(name)) continue;
                final Class<?> rt = m.getReturnType();
                XposedBridge.hookMethod(m, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        if (rt == void.class) param.setResult(null);
                        else if (rt == boolean.class) param.setResult(false);
                        else if (rt == int.class) param.setResult(0);
                        else if (rt == long.class) param.setResult(0L);
                        else if (rt == float.class) param.setResult(0f);
                        else if (rt == double.class) param.setResult(0d);
                        else if (!rt.isPrimitive()) param.setResult(null);
                    }
                });
                XposedBridge.log("NoChargeAnim: hooked " + m);
            }
        }
    }
}
