package com.lxian.playground.json.mapper.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ParameterizedTypeWrapper implements ParameterizedType {

    private ParameterizedType parameterizedType;

    private Type[] resolvedActualTypes;

    public ParameterizedTypeWrapper(ParameterizedType parameterizedType) {
        this.parameterizedType = parameterizedType;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return resolvedActualTypes != null ? resolvedActualTypes : parameterizedType.getActualTypeArguments();
    }

    Type[] getResolvedActualTypes() {
        return resolvedActualTypes;
    }

    void setResolvedActualTypes(Type[] resolvedActualTypes) {
        this.resolvedActualTypes = resolvedActualTypes;
    }

    @Override
    public Type getRawType() {
        return parameterizedType.getRawType();
    }

    @Override
    public Type getOwnerType() {
        return parameterizedType.getOwnerType();
    }

    @Override
    public String getTypeName() {
        return parameterizedType.getTypeName();
    }
}
