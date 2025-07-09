package dev.drvzs.anticheat.process;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessorInfo {
    String name();
}
