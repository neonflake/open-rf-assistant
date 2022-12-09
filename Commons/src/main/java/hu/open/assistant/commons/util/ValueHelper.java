package hu.open.assistant.commons.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class which helps with storing default values for logical objects.
 */
public class ValueHelper {

    private final List<Object> values = new ArrayList<>();

    /**
     * Inits a new empty list of objects.
     */
    public ValueHelper() {

    }

    /**
     * Adds an array to the list of values.
     *
     * @param array to add
     * @return reference to this object
     */
    public ValueHelper add(Object[] array) {
        values.add(Arrays.toString(array));
        return this;
    }

    /**
     * Adds an object to the list of values.
     *
     * @param object to add
     * @return reference to this object
     */
    public ValueHelper add(Object object) {
        values.add(object);
        return this;
    }

    /**
     * Returns the values list.
     */
    public List<Object> getValues() {
        return values;
    }
}
