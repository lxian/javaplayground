package com.lxian.playground.json.mapper.field;

public class LowerSnakeCaseToCamelFieldNameResolver implements FieldNameResolver {

    public String toFieldName(String name) {
        StringBuilder sb = new StringBuilder();
        String[] tokens = name.split("_");
        for (int i = 0; i < tokens.length; i++) {
            if (i== 0) {
                sb.append(tokens[i]);
            } else {
                sb.append(Utils.capitalize(tokens[i]));
            }
        }
        return sb.toString();
    }

    public String fromFieldName(String fieldName) {
        StringBuilder sb = new StringBuilder();
        int tokStart = 0;
        for (int i = 0; i < fieldName.length(); i++) {
            if (fieldName.charAt(i) >= 'A' && fieldName.charAt(i) <= 'Z') {
                if (tokStart != 0) {
                    sb.append('_');
                }
                sb.append(fieldName.substring(tokStart, i).toLowerCase());
                tokStart = i;
            }
        }

        if (tokStart != 0) {
            sb.append('_');
        }
        sb.append(fieldName.substring(tokStart, fieldName.length()).toLowerCase());

        return sb.toString();
    }

}

