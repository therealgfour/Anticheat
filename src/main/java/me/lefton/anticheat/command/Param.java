package me.lefton.anticheat.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark command methods.
 * Methods annotated with this will be registered as subcommands.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Param {
    String name();

    String[] aliases() default {};

    String description() default "";

    String permission() default "";

    int minArgs() default 0;

    String usage() default "";
}
