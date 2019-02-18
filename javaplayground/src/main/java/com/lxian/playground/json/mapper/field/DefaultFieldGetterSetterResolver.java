package com.lxian.playground.json.mapper.field;

import static com.lxian.playground.json.mapper.field.Utils.capitalize;

public class DefaultFieldGetterSetterResolver implements FieldGetterSetterResolver {

    private FieldNameResolver fieldNameResolver;

    public DefaultFieldGetterSetterResolver(FieldNameResolver fieldNameResolver) {
        this.fieldNameResolver = fieldNameResolver;
    }

    public String toSetterName(String name) {
        return "set" + capitalize(fieldNameResolver.toFieldName(name));
    }

    public String fromSetterName(String setterName) {
        int found = setterName.indexOf("set");
        if (found != 0) {
            return null;
        }
        return fieldNameResolver.fromFieldName(setterName.substring(3, setterName.length()));
    }

    public String fromGetterName(String getterName) {
        int found = getterName.indexOf("get");
        if (found != 0) {
            return null;
        }
        return fieldNameResolver.fromFieldName(getterName.substring(3, getterName.length()));
    }

    public String toGetterName(String name) {
        return "get" + capitalize(fieldNameResolver.toFieldName(name));
    }
}

