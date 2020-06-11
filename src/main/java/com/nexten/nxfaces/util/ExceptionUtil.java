package com.nexten.nxfaces.util;

/**
 *
 * @author Jaques Claudino <jaques at nexten.cc>
 * Jun 11, 2020
 */
public class ExceptionUtil {

    public static String getCauseMessage(Throwable throwable) {
        if (throwable.getCause() != null) {
            return getCauseMessage(throwable.getCause());
        }
        return throwable.getMessage();
    }
    
}
