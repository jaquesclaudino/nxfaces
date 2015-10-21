package com.nexten.nxfaces.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author jaques
 */
public class DateUtil {
 
    private static final SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static final Calendar calendar = Calendar.getInstance();
    
    public static Date getFirstTime(Date date) {
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
                
        return calendar.getTime();
    }
    
    public static Date getLastTime(Date date) {
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
                
        return calendar.getTime();
    }
    
    public static Date getDate(int incDays) {
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, incDays);
        return calendar.getTime();
    }
    
    public static int getMinutesBetween(Date inicio, Date fim, boolean clearSeconds) {
        if (clearSeconds) {
            inicio = clearSeconds(inicio);
            fim = clearSeconds(fim);
        }
        return (int) ((fim.getTime() - inicio.getTime()) / 1000 / 60);
    }
    
    public static int getSecondsBetween(Date inicio, Date fim) {
        return (int) ((fim.getTime() - inicio.getTime()) / 1000);
    }
    
    public static boolean isWeekend(Date date) {
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ||
                calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
    }
    
    public static boolean isSameDay(Date date1, Date date2) {
        calendar.setTime(date1);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int ano = calendar.get(Calendar.YEAR);
        
        calendar.setTime(date2);
        return (calendar.get(Calendar.DAY_OF_MONTH) == dia && calendar.get(Calendar.YEAR) == ano);
    }
    
    public static Date addDays(Date date, int days) {
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }
    
    public static Date clearSeconds(Date date) {
        calendar.setTime(date);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    
    public static Date joinTime(Date date, Date time) {
        calendar.setTime(time);
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int minuto = calendar.get(Calendar.MINUTE);
        int segundo = calendar.get(Calendar.SECOND);
                
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hora);
        calendar.set(Calendar.MINUTE, minuto);
        calendar.set(Calendar.SECOND, segundo);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    
    public static String formatMinutes(int minutes) {   
        return String.format("%02d:%02d", minutes / 60, minutes % 60);
    }
    
    public static String formatDate(Date date) {
        return sdfDate.format(date);
    }
    
    public static String formatDateTime(Date date) {
        return sdfDateTime.format(date);
    }
    
}
