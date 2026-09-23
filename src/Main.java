import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        AnnotatedClass target = new AnnotatedClass();

        Method[] methods = target.getClass().getDeclaredMethods();

        Arrays.sort(methods, Comparator.comparing(Method::getName));

        for (Method method : methods) {
            if (!method.isAnnotationPresent(Repeat.class)) {
                continue; // метод не аннотирован пропускаем
            }

            int mod = method.getModifiers();
            if (!Modifier.isProtected(mod) && !Modifier.isPrivate(mod)) {
                continue; //метод публичный пропускаем
            }

            int times = method.getAnnotation(Repeat.class).value();
            System.out.println("Метод " + method.getName()
                    + " (" + Modifier.toString(mod) + "), вызовов: " + times);

            // доступ к priv/prot
            method.setAccessible(true);

            // создаем аргументы
            Object[] arguments = buildArguments(method.getParameterTypes());

            for (int i = 0; i < times; i++) {
                try {
                    Object result = method.invoke(target, arguments);
                    if (method.getReturnType() != void.class) {
                        System.out.println("    результат: " + result);
                    }
                } catch (IllegalAccessException e) {
                    System.out.println("    нет доступа: " + e.getMessage());
                } catch (InvocationTargetException e) {
                    System.out.println("    метод выбросил исключение: " + e.getCause());
                }
            }
            System.out.println();
        }
    }

    private static Object[] buildArguments(Class<?>[] types) {
        Object[] args = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            args[i] = defaultValue(types[i]);
        }
        return args;
    }

    private static Object defaultValue(Class<?> type) {
        if (type == int.class)
            return 2;
        if (type == long.class )
            return 2L;
        if (type == double.class)
            return 1.5;
        if (type == float.class)
            return 1.5f;
        if (type == short.class)
            return (short) 1;
        if (type == byte.class)
            return (byte) 1;
        if (type == boolean.class)
            return true;
        if (type == char.class)
            return 'A';

        if (type == String.class)
            return "abc";

        if (type.isArray())
            return Array.newInstance(type.getComponentType(), 0);

        throw new IllegalArgumentException(
                "Не удалось создать значение для типа " + type.getName());
    }
}