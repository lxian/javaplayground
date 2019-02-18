package com.lxian.playground.json.mapper.type;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

public class TypeResolver {

    public static TypeResolver resolver = new TypeResolver();

    private Map<Class, GenericBindings> bindingCache = new HashMap<>();

    public Type[] resolveParam(Type type, Method method) throws TypeResolvingError {
        if (type instanceof Class) {
            return resolveParam((Class) type, method);
        } else if (type instanceof ParameterizedType) {
            return resolveParam((ParameterizedType) type, method);
        } else {
            throw new TypeResolvingError(String.format("type %s is not supported", type));
        }
    }

    public Type[] resolveParam(Class clazz, Method method) throws TypeResolvingError {

        validation(clazz, method);

        // method declared in current class, no need for generic resolving
        if (method.getDeclaringClass() == clazz) {
            return method.getGenericParameterTypes();
        }

        GenericBindings genericBindings = resolveClassGenerics(clazz);

        return getParamTypes(method, genericBindings);
    }

    public Type[] resolveParam(ParameterizedType parameterizedType, Method method) throws TypeResolvingError {
        if (!(parameterizedType.getRawType() instanceof Class)) {
            throw new TypeResolvingError(String.format("Unrecognized %s", parameterizedType));
        }

        Class rawClass = (Class )parameterizedType.getRawType();
        validation(rawClass, method);

        GenericBindings bindings = new GenericBindings();
        Type[] actualTypes = parameterizedType.getActualTypeArguments();
        TypeVariable[] typeVariables = rawClass.getTypeParameters();
        for (int i = 0; i < typeVariables.length ; i++) {
            bindings.add(typeVariables[i], actualTypes[i]);
        }

        // method declared in current class
        if (method.getDeclaringClass() == rawClass) {
            return getParamTypes(method, bindings);
        }

        resolveClassGenerics(rawClass, bindings);
        return getParamTypes(method, bindings);
    }

    private Type[] getParamTypes(Method method, GenericBindings bindings) throws TypeResolvingError {
        Type[] genericParamTypes = method.getGenericParameterTypes();
        Type[] resolvedParamTypes = new Type[method.getParameterCount()];
        for (int i = 0; i < method.getParameterCount(); i++) {
            resolvedParamTypes[i] = resolveGenericType(genericParamTypes[i], bindings);
        }
        return resolvedParamTypes;
    }

    private void validation(Class rawClass, Method method) throws TypeResolvingError {
        if (rawClass.isInterface()) {
            throw new TypeResolvingError(String.format("Interface %s", rawClass));
        }
        if (rawClass.isPrimitive()) {
            throw new TypeResolvingError(String.format("Primitive class %s", rawClass));
        }
        if (Modifier.isAbstract(method.getModifiers())) {
            throw new TypeResolvingError(String.format("Method %s is abstract", method));
        }
    }

    private Type resolveGenericType(Type type, GenericBindings genericBindings) throws TypeResolvingError {
        if (type instanceof Class) {
            return type;
        } else if (type instanceof WildcardType) {
            throw new TypeResolvingError(String.format("Wildcard type %s is not supported yet", type));
        } else if (type instanceof GenericArrayType) {
            return resolveGenericArray((GenericArrayType) type, genericBindings);
        } else if (type instanceof TypeVariable) {
            return resolveTypeVariable((TypeVariable) type, genericBindings);
        } else if (type instanceof ParameterizedType) {
            return resolveParameterizedType((ParameterizedType) type, genericBindings);
        } else {
            throw new TypeResolvingError(String.format("Unrecognized type %s", type));
        }
    }

    private ParameterizedType resolveParameterizedType(ParameterizedType paramType, GenericBindings genericBindings) throws TypeResolvingError {
        Type[] innerTypes = paramType.getActualTypeArguments();
        Type[] resolvedInnerTypes = new Type[innerTypes.length];
        for (int i = 0; i < innerTypes.length; i++ ) {
            resolvedInnerTypes[i] = resolveGenericType(innerTypes[i], genericBindings);
        }
        ParameterizedTypeWrapper wrapper = new ParameterizedTypeWrapper(paramType);
        wrapper.setResolvedActualTypes(resolvedInnerTypes);
        return wrapper;
    }

    private Type resolveTypeVariable(TypeVariable paramType, GenericBindings genericBindings) {
        return genericBindings.get(paramType);
    }

    private GenericArrayType resolveGenericArray(GenericArrayType paramType, GenericBindings genericBindings) throws TypeResolvingError {
        GenericArrayTypeWrapper wrapper = new GenericArrayTypeWrapper(paramType);
        wrapper.setResolvedGenericComponentType(resolveGenericType(paramType.getGenericComponentType(), genericBindings));
        return wrapper;
    }

    private GenericBindings resolveClassGenerics(Class clazz) throws TypeResolvingError {
        if (clazz.isPrimitive()) {
            throw new TypeResolvingError(String.format("Primitive class %s", clazz));
        }

        if (bindingCache.containsKey(clazz)) {
            bindingCache.get(clazz);
        }

        GenericBindings genericBindings = new GenericBindings();
        resolveClassGenerics(clazz, genericBindings);
        bindingCache.put(clazz, genericBindings);
        return genericBindings;
    }

    private void resolveClassGenerics(Class clazz, GenericBindings bindings) throws TypeResolvingError {
        Type superType = clazz.getGenericSuperclass();
        if (superType == null || superType == Object.class) {
            return;
        }
        if (superType instanceof Class) {
            resolveClassGenerics((Class) superType, bindings);
            return;
        }

        ParameterizedType parameterizedSuperType = (ParameterizedType) superType;
        Type[] actualTypes = parameterizedSuperType.getActualTypeArguments();
        TypeVariable[] typeVariables = ((Class) parameterizedSuperType.getRawType()).getTypeParameters();
        if (actualTypes.length == 0 || typeVariables.length == 0) {
            return;
        }

        for (int i=0; i < typeVariables.length; i++) {
            bindings.add(typeVariables[i], actualTypes[i]);
        }
        resolveClassGenerics(clazz.getSuperclass(), bindings);
    }

    public static class GenericBindings {
        Map<TypeVariable, Type> bindings = new HashMap<>();

        public void add(TypeVariable typeVariable, Type type) {
            bindings.put(typeVariable, type);
        }

        public Type get(TypeVariable typeVariable) {
            Type type = bindings.get(typeVariable);
            if (type instanceof TypeVariable && bindings.containsKey(type)) {
                return get((TypeVariable) type);
            }

            return type;
        }

    }

}
