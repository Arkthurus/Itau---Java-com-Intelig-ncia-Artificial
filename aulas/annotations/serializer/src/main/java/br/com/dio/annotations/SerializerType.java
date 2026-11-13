package br.com.dio.annotations;

import java.lang.ProcessBuilder.Redirect.Type;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.TYPE)
public @interface SerializerType {
    
    FieldFormatEnum fieldFormat() default FieldFormatEnum.CAMEL_CASE;

    boolean prettify() default true;
}
