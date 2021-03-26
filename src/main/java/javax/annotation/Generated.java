package javax.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//https://docs.oracle.com/javase/7/docs/api/javax/annotation/Generated.html
@Retention(value=RetentionPolicy.SOURCE)
@Target(value={ElementType.PACKAGE,ElementType.TYPE,ElementType.ANNOTATION_TYPE,ElementType.METHOD,ElementType.CONSTRUCTOR,ElementType.FIELD,ElementType.LOCAL_VARIABLE,ElementType.PARAMETER})
public @interface Generated {
	String[] value();

	String date() default "";

	String comments() default "";
}
