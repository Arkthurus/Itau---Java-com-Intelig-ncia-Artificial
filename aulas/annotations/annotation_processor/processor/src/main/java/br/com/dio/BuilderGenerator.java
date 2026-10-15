package br.com.dio;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.type.TypeMirror;
import java.util.Map;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

public class BuilderGenerator {

    public TypeSpec create(final String packageName, final String className,
                           final String builderName, final Map<String, TypeMirror> fields) {
        var generatedBuilderClass= TypeSpec.classBuilder(builderName)
                .addModifiers(PUBLIC);

        fields.forEach((k, v) -> generatedBuilderClass.addField(TypeName.get(v), k, PRIVATE));
        fields.forEach((k, v) -> generatedBuilderClass.addMethod(genBuilderSetter(
                packageName,
                builderName,
                k,
                TypeName.get(v))
            )
        );

        var buildMethod = MethodSpec.methodBuilder("build")
                .addModifiers(PUBLIC)
                .returns(ClassName.get(packageName, className))
                .addStatement("var target = new $N()", className);
        fields.keySet().forEach(f -> buildMethod.addStatement(
                "target.set$N($N)",
                f.substring(0, 1).toUpperCase() + f.substring(1),
                f
        ));

        buildMethod.addStatement("return target");

        return generatedBuilderClass.addMethod(buildMethod.build())
                .build();
    }

    private MethodSpec genBuilderSetter(final String packageName, final String name,
                                        final String param, final TypeName type){
        return MethodSpec.methodBuilder(param)
                .addModifiers(PUBLIC)
                .returns(ClassName.get(packageName, name))
                .addParameter(type, param, FINAL)
                .addStatement("this.$N = $N", param, param)
                .addStatement("return this")
                .build();
    }

}
