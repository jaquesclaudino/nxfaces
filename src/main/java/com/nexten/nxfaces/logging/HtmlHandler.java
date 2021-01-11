package com.nexten.nxfaces.logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author Jaques Claudino
 * Aug 14, 2015
 */
public class HtmlHandler extends Handler {
    
    private static final int DEFAULT_MAX_LINES = 5000;    
    private static final Level DEFAULT_LEVEL = Level.INFO;
    
    private final ClassLoader contextClassLoader; // to not share the handler between different web applications   
    private final List<String> list = Collections.synchronizedList(new ArrayList<>());
    private int maxLines = DEFAULT_MAX_LINES;
    private String filterRegex;    
    
    private HtmlHandler(ClassLoader contextClassLoader) {
        this.contextClassLoader = contextClassLoader;
    }

    public static HtmlHandler getInstance() {
        HtmlHandler instance = null;
        
        // find current instance:
        for (Handler handler : Logger.getLogger("").getHandlers()) {
            if (handler instanceof HtmlHandler) {
                HtmlHandler htmlHandler = (HtmlHandler) handler;
                if (Thread.currentThread().getContextClassLoader().equals(htmlHandler.getContextClassLoader())) {
                    instance = htmlHandler;
                    break;
                }
            }
        }
        
        // create the instance if not exists:
        if (instance == null) {
            instance = new HtmlHandler(Thread.currentThread().getContextClassLoader());
            instance.setFormatter(new HtmlFormatter());
            instance.setLevel(DEFAULT_LEVEL);
            Logger.getLogger("").addHandler(instance); // NOSONAR
        }
        
        return instance;
    }
        
    @Override
    public void publish(LogRecord record) {
        if (Thread.currentThread().getContextClassLoader().equals(contextClassLoader)) {
            if (getLevel() == null || record.getLevel().intValue() >= getLevel().intValue()) {
                synchronized(this) {
                    while (list.size() > maxLines) {
                        list.remove(list.size()-1);
                    }
                    String line = getFormatter().format(record);
                    if (filterRegex == null || Pattern.matches(filterRegex, line)) {
                        list.add(0, line);
                    }
                }
            }
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }

    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
    }

    public void setMaxStackLength(int maxStackLength) {
        if (getFormatter() instanceof HtmlFormatter) {
            ((HtmlFormatter) getFormatter()).setMaxStackLength(maxStackLength);
        }
    }
    
    public synchronized String getAsHtml(String filter) {
        StringBuilder sb = new StringBuilder();
        for (String line : list) {
            if (filter == null || line.toLowerCase().contains(filter.toLowerCase())) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    public void setLevelAsString(String level) {
        try {
            setLevel(Level.parse(level));
        } catch (Exception ex) {
            setLevel(DEFAULT_LEVEL);
        }
    }

    public ClassLoader getContextClassLoader() {
        return contextClassLoader;
    }

    public void setFilterRegex(String filterRegex) {
        this.filterRegex = filterRegex;
    }
    
}
