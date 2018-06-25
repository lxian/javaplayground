package com.lxian.playground.json.mapper;

import com.lxian.playground.json.mapper.error.JsonDeserializationError;
import com.lxian.playground.json.mapper.error.JsonDeserializationTypeMissMatchError;
import com.lxian.playground.json.mapper.field.DefaultFieldGetterSetterResolver;
import com.lxian.playground.json.mapper.field.FieldGetterSetterResolver;
import com.lxian.playground.json.mapper.field.FieldNameResolver;
import com.lxian.playground.json.mapper.type.TypeResolver;
import com.lxian.playground.json.mapper.type.TypeResolvingError;
import com.lxian.playground.json.parser.JsonParser;
import com.lxian.playground.json.parser.error.InvalidJsonError;
import com.lxian.playground.json.parser.object.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.*;

public class DefaultDeserializer extends AbstractJsonDeserializer {

    private JsonParser parser;

    private FieldNameResolver fieldNameResolver;

    private FieldGetterSetterResolver fieldGetterSetterResolver;

    public DefaultDeserializer(JsonParser parser, FieldNameResolver fieldNameResolver) {
        this.parser = parser;
        this.fieldNameResolver = fieldNameResolver;
        this.fieldGetterSetterResolver = new DefaultFieldGetterSetterResolver(fieldNameResolver);
    }

    public <T> T read(InputStream in, Class<T> targetClazz) throws IOException, JsonDeserializationError {

        JsObject jsObject = parseJson(in);

        return createTargetObject(targetClazz, jsObject);
    }

    private <T> T createTargetObject(Class<T> targetClazz, JsObject jsObject) throws JsonDeserializationError {
        if (targetClazz.isPrimitive()) {
            throw new JsonDeserializationTypeMissMatchError("target class is primitive");
        }

        T target = instantiateTargetObject(targetClazz);

        populateField(target, targetClazz, jsObject);
        return target;
    }

    private Object createTargetObject(ParameterizedType parameterizedType, JsObject jsObject) throws JsonDeserializationError {
        Class rawType = (Class) parameterizedType.getRawType();
        if (rawType.isInterface()) {
            throw new JsonDeserializationTypeMissMatchError(String.format("class %s is interface", rawType));
        }

        Object target = instantiateTargetObject(rawType);

        populateField(target, parameterizedType, jsObject);

        return target;
    }

    private void populateField(Object target, Type type, JsObject jsObject) throws JsonDeserializationError {
        Class clazz = target.getClass();
        Method[] methods = clazz.getMethods();

        for (Method method: methods) {
            // deal with method with exactly one parameter only
            if (method.getParameterCount() != 1) {
                continue;
            }

            // guess the input property name from the method (as a setter) name
            String propertyName = fieldGetterSetterResolver.fromSetterName(method.getName());
            if (propertyName == null) {
                continue;
            }

            // match the property name
            if (!jsObject.containsKey(propertyName)) {
                throw new JsonDeserializationError(
                        String.format("%s field %s(%s) is missing in input", target.getClass().getName(), propertyName, method.getName()));
            }

            Type paramType;
            try {
                paramType = TypeResolver.resolver.resolveParam(type, method)[0];
            } catch (TypeResolvingError typeResolvingError) {
                throw new JsonDeserializationError(typeResolvingError);
            }

            JsValue value = jsObject.get(propertyName);
            Object targetValue;
            try {
                targetValue = createValue(paramType, value);
            } catch (JsonDeserializationTypeMissMatchError e) {
                throw new JsonDeserializationTypeMissMatchError(
                        String.format("%s field %s(%s) expected type %s, input %s", target.getClass().getName(), propertyName, method.getName(), paramType, value), e);
            }

            try {
                method.invoke(target, targetValue);
            } catch (Exception e) {
                throw new JsonDeserializationError(e);
            }
        }
    }

    private Object createValue(Type targetType, JsValue value) throws JsonDeserializationError {
        if (targetType instanceof TypeVariable) {
            throw new JsonDeserializationError(String.format("Generic type %s cannot be bound to a concrete class", targetType));
        } else if (targetType instanceof WildcardType) {
            throw new JsonDeserializationError(String.format("Wildcard type %s is not supported yet", targetType));
        } else if (targetType instanceof Class) {
            return createValue((Class) targetType, value);
        } else if (targetType instanceof GenericArrayType) {
            return createArray(((GenericArrayType) targetType).getGenericComponentType(), value);
        } else if (targetType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) targetType;

            if (parameterizedType.getRawType() instanceof Class) {

                Class rawClass = (Class) parameterizedType.getRawType();

                if (rawClass.isAssignableFrom(List.class)) {
                    return createList(parameterizedType.getActualTypeArguments()[0], value);
                } else if (rawClass.isAssignableFrom(Set.class)) {
                    return createSet(parameterizedType.getActualTypeArguments()[0], value);
                } else if (rawClass.isAssignableFrom(Map.class)) {
                    Type keyType = parameterizedType.getActualTypeArguments()[0];
                    if (keyType != String.class) {
                        throw new JsonDeserializationTypeMissMatchError();
                    }
                    Type valueType = parameterizedType.getActualTypeArguments()[1];
                    return createMap(valueType, value);
                } else {
                    return createTargetObject(parameterizedType, (JsObject) value);
                }
            }
        }

        throw new JsonDeserializationTypeMissMatchError();
    }

    private Object createMap(Type valueType, JsValue value) throws JsonDeserializationError {
        if (!(value instanceof JsObject)) {
            throw new JsonDeserializationTypeMissMatchError();
        }

        JsObject jsObject = (JsObject) value;
        Map map = new HashMap();
        for (Map.Entry<String, JsValue> entry: jsObject.entrySet()) {
            String eKey = entry.getKey();
            JsValue eValue = entry.getValue();
            map.put(eKey, createValue(valueType, eValue));
        }

        return map;
    }

    private Object createValue(Class targetClass, JsValue value) throws JsonDeserializationError {
        targetClass = boxIfNeeded(targetClass);

        if (value instanceof JsBoolean) {
            if (targetClass == Boolean.class) {
                return ((JsBoolean) value).toBool();
            }
        } else if (value instanceof JsString) {
            if (targetClass == String.class) {
                return ((JsString) value).toJavaString();
            }
        } else if (value instanceof JsNull) {
            if (!targetClass.isPrimitive()) {
                return null;
            }
        } else if (value instanceof JsNumber) {
            return createNumber(targetClass, (JsNumber) value);
        } else if (value instanceof JsList) {
            if (targetClass.isArray()) {
                return createArray(targetClass.getComponentType(), value);
            }
        } else if (value instanceof JsObject) {
            if (!targetClass.isPrimitive()) {
                return createTargetObject(targetClass, (JsObject) value);
            }
        }

        throw new JsonDeserializationTypeMissMatchError();
    }

    private Set createSet(Type componentType, JsValue values) throws JsonDeserializationError {
        return new HashSet(createList(componentType, values));
    }

    private List createList(Type componentType, JsValue values) throws JsonDeserializationError {
        if (!(values instanceof JsList)) {
            throw new JsonDeserializationTypeMissMatchError();
        }

        List<JsValue> valueList = ((JsList) values).toList();
        List lst = new ArrayList(valueList.size());
        for (JsValue value : valueList) {
            lst.add(createValue(componentType, value));
        }
        return lst;
    }

    private Object createArray(Type componentClass, JsValue values) throws JsonDeserializationError {
        List lst = createList(componentClass, values);
        Object[] arr;
        if (componentClass instanceof Class) {
            arr = (Object[]) Array.newInstance((Class)componentClass, lst.size());
        } else if (componentClass instanceof ParameterizedType) {
            arr = (Object[]) Array.newInstance((Class)((ParameterizedType) componentClass).getRawType(), lst.size());
        } else {
            throw new JsonDeserializationError();
        }
        for (int i=0; i < lst.size(); i++) {
            arr[i] = lst.get(i);
        }
        return arr;
    }

    private Number createNumber(Class targetClass, JsNumber value) throws JsonDeserializationTypeMissMatchError {
        if (targetClass == Short.class) {
            return value.toShort();
        } else if (targetClass == Integer.class) {
            return value.toInteger();
        } else if (targetClass == Long.class) {
            return value.toLong();
        } else if (targetClass == Float.class) {
            return value.toFloat();
        } else if (targetClass == Double.class) {
            return value.toDouble();
        } else if (targetClass == BigDecimal.class) {
            return value.toBigDecimal();
        } else {
            throw new JsonDeserializationTypeMissMatchError();
        }
    }

    private JsObject parseJson(InputStream in) throws IOException, JsonDeserializationError {
        JsObject jsObject;
        try {
            jsObject = parser.parse(in);
        } catch (InvalidJsonError e) {
            throw new JsonDeserializationError(e);
        }
        return jsObject;
    }

    private <T> T instantiateTargetObject(Class<T> clazz) throws JsonDeserializationError {
        try {
            return clazz.getConstructor(new Class[0]).newInstance();
        } catch (IllegalAccessException e) {
            throw new JsonDeserializationError(String.format("public No-arg constructor is required for %s", clazz), e);
        } catch (NoSuchMethodException e) {
            throw new JsonDeserializationError(String.format("public No-arg constructor is required for %s", clazz), e);
        } catch (Exception e) {
            throw new JsonDeserializationError(e);
        }
    }

    private static Class boxIfNeeded(Class clazz) {
        if (!clazz.isPrimitive()) {
            return clazz;
        }

        if (clazz == boolean.class) {
            return Boolean.class;
        } else if (clazz == char.class) {
            return Character.class;
        } else if (clazz == byte.class) {
            return Byte.class;
        } else if (clazz == byte.class) {
            return Byte.class;
        } else if (clazz == short.class) {
            return Short.class;
        } else if (clazz == int.class) {
            return Integer.class;
        } else if (clazz == long.class) {
            return Long.class;
        } else if (clazz == float.class) {
            return Float.class;
        } else if (clazz == double.class) {
            return Double.class;
        } else {
            return Void.class;
        }
    }
}

