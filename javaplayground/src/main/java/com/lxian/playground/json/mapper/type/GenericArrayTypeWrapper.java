package com.lxian.playground.json.mapper.type;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

public class GenericArrayTypeWrapper implements GenericArrayType {

    private GenericArrayType genericArrayType;

    private Type resolvedGenericComponentType;

    public GenericArrayTypeWrapper(GenericArrayType genericArrayType) {
        this.genericArrayType = genericArrayType;
    }

    Type getResolvedGenericComponentType() {
        return resolvedGenericComponentType;
    }

    void setResolvedGenericComponentType(Type resolvedGenericComponentType) {
        this.resolvedGenericComponentType = resolvedGenericComponentType;
    }

    @Override
    public Type getGenericComponentType() {
        return resolvedGenericComponentType != null ? resolvedGenericComponentType : genericArrayType.getGenericComponentType();
    }

    @Override
    public String getTypeName() {
        return genericArrayType.getTypeName();
    }
}
