package br.com.dio;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.SOURCE;
import static java.lang.annotation.ElementType.TYPE;

@Target (TYPE)
@Retention (SOURCE)
public @interface Builder {

}
