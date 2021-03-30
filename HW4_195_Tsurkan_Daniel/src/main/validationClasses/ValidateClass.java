package validationClasses;

import static java.util.Objects.requireNonNull;

import annotationPackage.*;

import java.lang.reflect.*;
import java.util.*;

public class ValidateClass implements Validator {

    private final Set<ValidationError> errors = new HashSet<>();
    private String finalPath = "";

    public void setFinalPath(String path) {
        finalPath = path + ".";
    }

    /**
     * Main method, which keeps all logic of checking values in object.
     *
     * @param object the object we need to check
     * @return set of errors
     */
    @Override
    public Set<ValidationError> validate(Object object) {
        if (object == null || !object.getClass().isAnnotationPresent(Constrained.class))
            return errors;

        Class<?> c = requireNonNull(object).getClass();
        Field[] fields = c.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);

            findErrors(f, object);
        }
        return errors;
    }

    /**
     * Method finds errors in one field.
     *
     * @param f      our field
     * @param object object where our field situated
     */
    private void findErrors(Field f, Object object) {
        try {
            // Проверяем наше поле на наличие аннотаций и ошибок
            handleObjectOfList(f.getAnnotatedType(), f.get(object), f.getName());

            try {
                var typeOfField = Class.forName(f.getAnnotatedType().getType().getTypeName());
                if (!typeOfField.isAnnotationPresent(Constrained.class) && typeOfField.getPackageName().equals("forms"))
                    return;
                if (typeOfField.isAnnotationPresent(Constrained.class))
                    checkCustomClass(f.getAnnotatedType(), f.get(object), f.getName());
            } catch (ClassNotFoundException ignored) { }

            // Здесь только для листов
            if (f.get(object) instanceof List && f.getAnnotatedType() instanceof AnnotatedParameterizedType)
                recursiveHandle(f.getAnnotatedType(), f.get(object), f.getName());

        } catch (IllegalAccessException m) {
            errors.add(createError("Something wrong with access...", f.getName(), "unknown"));
        }
    }

    /**
     * Method checks one object in list.
     *
     * @param t    annotated type of our object in list
     * @param o    the value of the object we are checking
     * @param path path to our object
     */
    private void handleObjectOfList(AnnotatedType t, Object o, String path) {
        if (!t.isAnnotationPresent(NotNull.class) && o == null)
            return;
        if (t.isAnnotationPresent(NotNull.class) && o == null) {
            errors.add(createError("must not be null", path, "null"));
            return;
        }
        if (t.isAnnotationPresent(Negative.class))
            isNegativeRec(o, path);
        if (t.isAnnotationPresent(Positive.class))
            isPositiveRec(o, path);
        if (t.isAnnotationPresent(AnyOf.class))
            isAnyOfRec(o, t, path);
        if (t.isAnnotationPresent(NotEmpty.class) && o instanceof String)
            isEmptyRec(o, path);
        if (t.isAnnotationPresent(InRange.class))
            isInRangeRec(o, t, path);
        if (t.isAnnotationPresent(NotBlank.class))
            isBlankRec(o, path);
        if (t.isAnnotationPresent(Size.class))
            isRightSizeRec(o, t, path);
    }

    /**
     * Method creates an error if the object we are checking is negative.
     *
     * @param object the value we need to check
     * @param path   path to object
     */
    private void isPositiveRec(Object object, String path) {
        if (Long.parseLong(object.toString()) < 0)
            errors.add(createError("must be positive", path, object));
    }

    /**
     * Method creates an error if the object we are checking is positive.
     *
     * @param object the value we need to check
     * @param path   path to object
     */
    private void isNegativeRec(Object object, String path) {
        if (Long.parseLong(object.toString()) > 0)
            errors.add(createError("must be negative", path, object));
    }

    /**
     * Method creates an error if the object we are checking is blank.
     *
     * @param object the value we need to check
     * @param path   path to object
     */
    private void isBlankRec(Object object, String path) {
        if (object instanceof String && object.toString().isBlank())
            errors.add(createError("must not be blank", path, "\"\""));
    }

    /**
     * Method creates an error if the object we are checking is not in our ranges.
     *
     * @param object the value we need to check
     * @param t      annotated type of our object
     * @param path   path to object
     */
    private void isInRangeRec(Object object, AnnotatedType t, String path) {
        long max = t.getAnnotation(InRange.class).max();
        long min = t.getAnnotation(InRange.class).min();
        if (Long.parseLong(object.toString()) > max || Long.parseLong(object.toString()) < min)
            errors.add(createError("must be in range between " + min + " and " + max, path, object));
    }

    /**
     * Method creates an error if the object we are checking is not in our array in annotation.
     *
     * @param object the value we need to check
     * @param t      annotated type of our object
     * @param path   path to object
     */
    private void isAnyOfRec(Object object, AnnotatedType t, String path) {
        StringBuilder message = new StringBuilder("must be one of ");
        for (String str : t.getAnnotation(AnyOf.class).value())
            message.append("'").append(str).append("', ");
        if (Arrays.stream(t.getAnnotation(AnyOf.class).value()).noneMatch(x -> x.equals(object.toString())))
            errors.add(createError(message.substring(0, message.length() - 2), path, object.toString()));
    }

    /**
     * Method creates an error if the object we are checking is empty.
     *
     * @param collection the value we need to check
     * @param path       path to object
     */
    private void isEmptyRec(Object collection, String path) {
        if (collection instanceof String && ((String) collection).isEmpty())
            errors.add(createError("must not be empty", path, ""));
        else if (collection instanceof Collection<?> && ((Collection<?>) collection).isEmpty())
            errors.add(createError("must not be empty", path, collection));
        else if (collection instanceof Map && ((Map<?, ?>) collection).isEmpty())
            errors.add(createError("must not be empty", path, collection));
    }

    /**
     * Method creates an error if the object we are checking hasn't appropriate size.
     *
     * @param collection the value we need to check
     * @param t          annotated type of our object
     * @param path       path to object
     */
    private void isRightSizeRec(Object collection, AnnotatedType t, String path) {
        int max = t.getAnnotation(Size.class).max();
        int min = t.getAnnotation(Size.class).min();
        if (collection instanceof String && (collection.toString().length() > max || collection.toString().length() < min))
            errors.add(createError("must be in range between " + min + " and " + max, path, collection));
        else if (collection instanceof Collection<?> && (max < ((Collection<?>) collection).size() || min > ((Collection<?>) collection).size()))
            errors.add(createError("must be in range between " + min + " and " + max, path, ((Collection<?>) collection).size()));
        else if (collection instanceof Map && (max < ((Map<?, ?>) collection).size() || min > ((Map<?, ?>) collection).size()))
            errors.add(createError("must be in range between " + min + " and " + max, path, ((Map<?, ?>) collection).size()));
    }

    /**
     * Method recursively finds errors in Lists.
     *
     * @param value the value we need to check
     * @param t     annotated type of our object
     * @param fName path to object
     */
    private void recursiveHandle(AnnotatedType t, Object value, String fName) {
        String path;
        int index = -1;
        for (Object object : (Collection<?>) value) {
            path = fName + "[" + ++index + "]";
            AnnotatedType type = ((AnnotatedParameterizedType) t).getAnnotatedActualTypeArguments()[0];
            if (object instanceof Collection<?>) {
                if (type.isAnnotationPresent(Size.class))
                    isRightSizeRec(object, type, path);
                if (type.isAnnotationPresent(NotEmpty.class))
                    isEmptyRec(object, path);
                recursiveHandle(type, object, path);
            } else {
                handleObjectOfList(type, object, path);
                // Проверяем объекты в листе, если объектами являются классы созданные нами.
                checkCustomClass(type, object, path);
            }

        }
    }

    /**
     * Method creates and adds errors for fields which types correspond our custom classes.
     *
     * @param type      annotated type of our object
     * @param object    the value we need to check
     * @param path      path to object
     */
    private void checkCustomClass(AnnotatedType type, Object object, String path) {
        try {
            var typeOfField = Class.forName(type.getType().getTypeName());
            if (typeOfField.isAnnotationPresent(Constrained.class) /*&& typeOfField.getPackageName().equals("forms")*/) {
                var vcl = new ValidateClass();
                vcl.setFinalPath(this.finalPath + path);
                errors.addAll(vcl.validate(object));
            }
        } catch (ClassNotFoundException ignored) {
        }
    }

    /**
     * Method creates an error.
     *
     * @param m message for error
     * @param p path for error
     * @param v value of object with error
     */
    private ValidationError createError(String m, String p, Object v) {
        return new ValidationError() {
            @Override
            public String getMessage() {
                return m;
            }

            @Override
            public String getPath() {
                return finalPath + p;
            }

            @Override
            public Object getFailedValue() {
                return v;
            }
        };
    }
}