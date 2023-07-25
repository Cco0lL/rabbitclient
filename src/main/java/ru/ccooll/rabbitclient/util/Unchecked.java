package ru.ccooll.rabbitclient.util;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.concurrent.Callable;

public class Unchecked {

    public static final SilentInvoker SILENT_INVOKER;

    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            CallSite callSite = LambdaMetafactory.metafactory(
                    lookup,
                    "invoke",
                    MethodType.methodType(SilentInvoker.class),
                    SilentInvoker.SIGNATURE,
                    lookup.findVirtual(Callable.class, "call", MethodType.methodType(Object.class)),
                    SilentInvoker.SIGNATURE);
            SILENT_INVOKER = (SilentInvoker) callSite.getTarget().invokeExact();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T callUnchecked(Callable<T> callable) {
       return SILENT_INVOKER.invoke(callable);
    }

    public interface SilentInvoker {

        MethodType SIGNATURE = MethodType.methodType(Object.class, Callable.class);

        <T> T invoke(Callable<T> callable);
    }
}