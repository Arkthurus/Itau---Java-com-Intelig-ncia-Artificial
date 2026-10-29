package br.com.dio.annotations;

import java.util.function.Function;
import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_HYPHEN;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

/**
 * FieldFormatEnum
 */
public enum FieldFormatEnum {

    CAMEL_CASE (field ->  field),
    PASCAL_CASE(field ->  LOWER_CAMEL.to(UPPER_CAMEL,      field)),
    SNAKE_CASE (field ->  LOWER_CAMEL.to(LOWER_UNDERSCORE, field)),
    KEBAB_CASE (field ->  LOWER_CAMEL.to(LOWER_HYPHEN,     field));

    private final Function<String, String> format;

    public Function<String, String> getFormat() {
        return format;
    }

    private FieldFormatEnum(Function<String, String> format) {
        this.format = format;
    }
}
