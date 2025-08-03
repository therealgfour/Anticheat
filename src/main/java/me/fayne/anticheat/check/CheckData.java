package me.fayne.anticheat.check;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CheckData {

     boolean enabled() default true;

     boolean experimental() default false;

     double punishmentVL() default 20.0;

     String name();

     String type() default "A";

     String description() default "detects cheats with packets :)";

}
