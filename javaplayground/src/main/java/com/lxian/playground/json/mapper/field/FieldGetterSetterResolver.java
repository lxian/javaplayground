package com.lxian.playground.json.mapper.field;

public interface FieldGetterSetterResolver {

    String toSetterName(String name);

    String fromSetterName(String name);

    String toGetterName(String name);

    String fromGetterName(String name);
}
