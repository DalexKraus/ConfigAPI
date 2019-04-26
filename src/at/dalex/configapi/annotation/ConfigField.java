package at.dalex.configapi.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigField {

    String location() default "UNDEFINED";
}
