package com.nexten.nxfaces.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * @author Jaques Claudino
 * Aug 14, 2015
 */
public class HtmlFormatter extends Formatter {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
    private static final int DEFAULT_MAX_STACK_LENGTH = 500;    
    
    private static final String SPAN_RED = "<span style='color: red; font-weight: bold;'>";
    private static final String SPAN_ORANGE = "<span style='color: #FF8C00; font-weight: bold;'>";
    private static final String SPAN_GRAY = "<span style='color: gray;'>";    
    private static final String SPAN_CLOSE = "</span>";
    
    private int maxStackLength = DEFAULT_MAX_STACK_LENGTH;
    
    @Override
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        sb.append(SDF.format(new Date(record.getMillis())));
        sb.append(' ');
                
        String message = getMessage(record);
                
        boolean severe = record.getLevel().equals(Level.SEVERE);
        boolean warning = record.getLevel().equals(Level.WARNING);
        boolean fine = record.getLevel().intValue() <= Level.FINE.intValue();

        if (severe) {
            sb.append(SPAN_RED);
        } else if (warning) {
            sb.append(SPAN_ORANGE);
        } else if (fine) {
            sb.append(SPAN_GRAY);
        }
        
        sb.append(String.format("%s: %s ", record.getLevel().getName(), message));
        
        if (!fine) {
            if (severe || warning) {
                sb.append(SPAN_CLOSE);
            }
            sb.append(SPAN_GRAY);
        }
        
        if (record.getSourceMethodName() != null) {
            sb.append(String.format("[%s.%s]", getClassNameWithoutPackage(record.getSourceClassName()), record.getSourceMethodName()));
        }
        
        if (record.getThrown() != null) {           
            sb.append("<div style='padding-left: 20px; padding-bottom: 20px'>");            

            StringWriter sw = new StringWriter();
            try (PrintWriter pw = new PrintWriter(sw)) {
                record.getThrown().printStackTrace(pw);
            }
            String stack = sw.toString();
            boolean trunc = stack.length() > maxStackLength;
            if (trunc) {
                stack = stack.substring(0, maxStackLength);
            }
            sb.append(stack.replaceAll("\n", "<br> \t "));            
            if (trunc) {
                sb.append("...");
            }
            sb.append("</div>");
        } else {
            sb.append("<br>");
        }
        sb.append(SPAN_CLOSE);
        
        return sb.toString();
    }
       
    private String getMessage(LogRecord record) {
        String message = formatMessage(record); 
        String thrownMessage = null;
        if (record.getThrown() != null) {
            thrownMessage = record.getThrown().getMessage();
        }      
        if (message != null && thrownMessage != null) {
            message = String.format("%s (%s)", message, thrownMessage);
        } else if (thrownMessage != null) {
            message = thrownMessage;
        } else if (message == null) {
            message = "Unknown";
        }
        return message;
    }

    private String getClassNameWithoutPackage(String className) {
        if (className == null) {
            return null;
        }
        
        String[] items = className.split("\\.");
        if (items.length > 0) {
            return items[items.length-1];
        } else {
            return className;
        }
    }

    public void setMaxStackLength(int maxStackLength) {
        this.maxStackLength = maxStackLength;
    }
    
}
