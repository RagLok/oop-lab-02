import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;


public class ArgumentFactory {

    // типы, которые сейчас создаются - защита от бесконечной рекурсии (A(B b), B(A a))
    private final Set<Class<?>> inProgress = new HashSet<>();

    public Object[] buildArguments(Class<?>[] types) {
        Object[] args = new Object[types.length];

        for (int i = 0; i < types.length; i++)
        {
            args[i] = create(types[i]);
        }

        return args;
    }

    public Object create(Class<?> type) {

        if (type == int.class || type == Integer.class)
            return 2;
        if (type == long.class || type == Long.class)
            return 2L;
        if (type == double.class || type == Double.class)
            return 1.5;
        if (type == float.class || type == Float.class)
            return 1.5f;
        if (type == short.class || type == Short.class)
            return (short) 1;
        if (type == byte.class || type == Byte.class)
            return (byte) 1;
        if (type == boolean.class || type == Boolean.class)
            return true;
        if (type == char.class || type == Character.class)
            return 'A';

        if (type == String.class || type == CharSequence.class || type == Object.class)
            return "abc";

        if (type.isArray())
        {
            // массив из одного элемента нужного типа
            Object array = Array.newInstance(type.getComponentType(), 1);
            Array.set(array, 0, create(type.getComponentType()));

            return array;
        }

        if (type.isEnum())
        {
            Object[] constants = type.getEnumConstants();

            if (constants.length == 0)
                throw new IllegalArgumentException("Перечисление " + type.getName() + " не имеет констант");

            return constants[0];
        }

        if (type.isInterface())
            return createForInterface(type);

        if (Modifier.isAbstract(type.getModifiers()))
            throw new IllegalArgumentException(
                    "Нельзя создать экземпляр абстрактного класса " + type.getName());

        return createByConstructor(type);
    }

    // пользовательский класс перебором конструкторов
    private Object createByConstructor(Class<?> type) {
        if (!inProgress.add(type))
            throw new IllegalArgumentException("Циклическая зависимость при создании " + type.getName());

        try {
            Constructor<?>[] constructors = type.getDeclaredConstructors();
            //сортировка с меньшим колвом параметров раньше
            Arrays.sort(constructors, Comparator.comparingInt(Constructor::getParameterCount));

            Exception lastError = null;
            for (Constructor<?> constructor : constructors)
            {
                try
                {
                    constructor.setAccessible(true);
                    Object[] args = buildArguments(constructor.getParameterTypes());

                    return constructor.newInstance(args);
                } catch (Exception e) {
                    lastError = e; // пробуем следующий конструктор
                }
            }
            throw new IllegalArgumentException("Не удалось создать значение для типа " + type.getName(), lastError);
        } finally {
            inProgress.remove(type);
        }
    }

    private Object createForInterface(Class<?> type) {
        if (type.isAssignableFrom(ArrayList.class))
            return new ArrayList<>();
        if (type.isAssignableFrom(HashSet.class))
            return new HashSet<>();
        if (type.isAssignableFrom(HashMap.class))
            return new HashMap<>();
        if (type.isAssignableFrom(ArrayDeque.class))
            return new ArrayDeque<>();

        // реализацию произвольного интерфейса по одному его типу не найти
        throw new IllegalArgumentException(
                "Нельзя создать экземпляр интерфейса " + type.getName());
    }
}
