import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class Main {

    public static void main(String[] args)
    {
        AnnotatedClass target = new AnnotatedClass();
        ArgumentFactory factory = new ArgumentFactory();

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

            // создаем аргументы любых типов
            Object[] arguments;
            try {
                arguments = factory.buildArguments(method.getParameterTypes());
            } catch (IllegalArgumentException e) {
                System.out.println("    не удалось подготовить аргументы: " + e.getMessage());
                System.out.println();
                continue;
            }

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
}
