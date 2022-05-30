package com.nexten.nxfaces.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author jaques
 */
public class DateUtil {
 
    private static final String SDF_DATE_PATTERN = "dd/MM/yyyy";
    private static final String SDF_DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm:ss";

    private DateUtil() {
    }
    
    public static Date getFirstTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
                
        return cal.getTime();
    }
    
    public static Date getLastTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
                
        return cal.getTime();
    }
    
    public static Date getDate(int incDays) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, incDays);
        return cal.getTime();
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
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ||
                cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
    }
    
    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        int day = cal.get(Calendar.DAY_OF_YEAR);
        int year = cal.get(Calendar.YEAR);
        
        cal.setTime(date2);
        return cal.get(Calendar.DAY_OF_YEAR) == day && cal.get(Calendar.YEAR) == year;
    }
    
    public static boolean isSameMonth(Date date1, Date date2) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        
        cal.setTime(date2);
        return cal.get(Calendar.MONTH) == month && cal.get(Calendar.YEAR) == year;
    }
    
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }
    
    public static Date clearSeconds(Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    public static Date maxSeconds(Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
    
    public static Date joinTime(Date date, Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        int hora = cal.get(Calendar.HOUR_OF_DAY);
        int minuto = cal.get(Calendar.MINUTE);
        int segundo = cal.get(Calendar.SECOND);
                
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hora);
        cal.set(Calendar.MINUTE, minuto);
        cal.set(Calendar.SECOND, segundo);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    public static String formatMinutes(Integer minutes) {  
        if (minutes == null) {
            return "00:00";
        }
        return String.format("%02d:%02d", minutes / 60, minutes % 60);
    }
    
    public static String formatDate(Date date) {
        return new SimpleDateFormat(SDF_DATE_PATTERN).format(date);
    }
    
    public static String formatDateTime(Date date) {
        return new SimpleDateFormat(SDF_DATE_TIME_PATTERN).format(date);
    }
    
}
