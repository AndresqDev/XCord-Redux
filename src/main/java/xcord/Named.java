package xcord;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import xcord.Qualifier;

@Qualifier
@Documented
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Named {
    public String value() default "";
}

