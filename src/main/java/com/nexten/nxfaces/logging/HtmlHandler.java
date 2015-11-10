package com.nexten.nxfaces.logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * @author Jaques Claudino
 * Aug 14, 2015
 */
public class HtmlHandler extends Handler {
    
    private static final int DEFAULT_MAX_LINES = 5000;    
    private static final Level DEFAULT_LEVEL = Level.INFO;
    private static HtmlHandler instance;
    
    private final List<String> list = Collections.synchronizedList(new ArrayList<String>());
    private int maxLines = DEFAULT_MAX_LINES;
    
    private HtmlHandler() {        
    }
    
   public static HtmlHandler getInstance() {
       if (instance == null) {
           instance = new HtmlHandler();
           instance.setFormatter(new HtmlFormatter());
           instance.setLevel(DEFAULT_LEVEL);
       }
       return instance;
   } 
    
    @Override
    public void publish(LogRecord record) {
        if (getLevel() == null || record.getLevel().intValue() >= getLevel().intValue()) {
            while (list.size() > maxLines) {
                list.remove(list.size()-1);
            }        
            list.add(0,getFormatter().format(record));
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

    public String getAsHtml() {
        StringBuilder sb = new StringBuilder();
        for (String line : list) {
            sb.append(line);        
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
    
}
