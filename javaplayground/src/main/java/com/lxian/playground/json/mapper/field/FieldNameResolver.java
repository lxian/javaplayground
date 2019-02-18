package com.lxian.playground.json.mapper.field;

/**
 * bi-directional name resolver for converting field name between two object
 */
public interface FieldNameResolver {

    /**
     * resolve a string to a field name based on some rules eg. snake case, camel...
     * @param name
     * @return
     */
    String toFieldName(String name);

    /**
     * convert the field name back to the original name, in the opposite direction of toFieldName
     * @param fieldName
     * @return
     */
    String fromFieldName(String fieldName);
}
