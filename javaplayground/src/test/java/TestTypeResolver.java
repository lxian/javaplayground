import com.lxian.playground.json.mapper.type.TypeResolver;
import com.lxian.playground.json.mapper.type.TypeResolvingError;
import static org.junit.Assert.*;
import org.junit.Test;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class TestTypeResolver {

    public static class Foo<T, U> {
        public void foo(T t, List<U> ulist, U[] uarray, Float f, double d) {}
    }

    public static class Bar<R> extends Foo<String, R> {}

    public static class FooBar extends Bar<Bar<Integer>> {}

    @Test
    public void testGenericClass() throws TypeResolvingError {
        Type[] resolvedTypes = TypeResolver.resolver.resolveParam(FooBar.class, getMethodByName(FooBar.class, "foo"));

        // String
        assertEquals(resolvedTypes[0], String.class);

        // List<Bar<Integer>>
        assertEquals(((ParameterizedType) ((ParameterizedType) resolvedTypes[1]).getActualTypeArguments()[0]).getRawType(), Bar.class);
        assertEquals(((ParameterizedType) ((ParameterizedType) resolvedTypes[1]).getActualTypeArguments()[0]).getActualTypeArguments()[0], Integer.class);

        // Bar<Integer>[]
        assertEquals(((ParameterizedType) ((GenericArrayType) resolvedTypes[2]).getGenericComponentType()).getRawType(), Bar.class);
        assertEquals(((ParameterizedType) ((GenericArrayType) resolvedTypes[2]).getGenericComponentType()).getActualTypeArguments()[0], Integer.class);

        // Float
        assertEquals(resolvedTypes[3], Float.class);

        // double
        assertEquals(resolvedTypes[4], double.class);

    }

    public static interface Foo1<T> {
        void foo(T t);
    }

    public static interface Bar1<U> extends Foo1<String> {
        void bar(U u);
    }

    public static abstract class AbcFooBar1<T> implements Bar1<T> {
        @Override
        public void foo(String s) {
        }
    }

    public static class FooBar1 extends AbcFooBar1<Integer> {

        @Override
        public void bar(Integer integer) {
        }
    }

    @Test
    public void testGenericInterface() throws TypeResolvingError {
        Type[] resolvedBarParamTypes = TypeResolver.resolver.resolveParam(FooBar1.class, getMethodByName(FooBar1.class, "bar"));
        assertEquals(resolvedBarParamTypes[0], Integer.class);

        Type[] resolvedFooParamTypes = TypeResolver.resolver.resolveParam(FooBar1.class, getMethodByName(FooBar1.class, "foo"));
        assertEquals(resolvedFooParamTypes[0], String.class);
    }

    private Method getMethodByName(Class clazz, String methodName) {
        for (Method method: clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }
}
