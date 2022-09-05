package com.nexten.nxfaces.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

/**
 *
 * @author Jaques Claudino <jaques at nexten.cc>
 * Aug 3, 2017
 */
@Named
@ApplicationScoped
public class Sorter {

    public int sortByDouble(Object obj1, Object obj2) {
        Double value1 = Double.parseDouble((String) obj1);
        Double value2 = Double.parseDouble((String) obj2);
        return value1.compareTo(value2);
    }

}
