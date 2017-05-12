package com.nexten.nxfaces.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author jaques
 */
public class DateUtil {
 
    private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat SDF_DATE_TIME = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static final Calendar CAL = Calendar.getInstance();
    
    public static Date getFirstTime(Date date) {
        CAL.setTime(date);
        CAL.set(Calendar.HOUR_OF_DAY, 0);
        CAL.set(Calendar.MINUTE, 0);
        CAL.set(Calendar.SECOND, 0);
        CAL.set(Calendar.MILLISECOND, 0);
                
        return CAL.getTime();
    }
    
    public static Date getLastTime(Date date) {
        CAL.setTime(date);
        CAL.set(Calendar.HOUR_OF_DAY, 23);
        CAL.set(Calendar.MINUTE, 59);
        CAL.set(Calendar.SECOND, 59);
        CAL.set(Calendar.MILLISECOND, 999);
                
        return CAL.getTime();
    }
    
    public static Date getDate(int incDays) {
        CAL.setTime(new Date());
        CAL.add(Calendar.DATE, incDays);
        return CAL.getTime();
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
        CAL.setTime(date);
        return CAL.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ||
                CAL.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
    }
    
    public static boolean isSameDay(Date date1, Date date2) {
        CAL.setTime(date1);
        int day = CAL.get(Calendar.DAY_OF_YEAR);
        int year = CAL.get(Calendar.YEAR);
        
        CAL.setTime(date2);
        return CAL.get(Calendar.DAY_OF_YEAR) == day && CAL.get(Calendar.YEAR) == year;
    }
    
    public static Date addDays(Date date, int days) {
        CAL.setTime(date);
        CAL.add(Calendar.DATE, days);
        return CAL.getTime();
    }
    
    public static Date clearSeconds(Date date) {
        CAL.setTime(date);
        CAL.set(Calendar.SECOND, 0);
        CAL.set(Calendar.MILLISECOND, 0);
        return CAL.getTime();
    }
    
    public static Date joinTime(Date date, Date time) {
        CAL.setTime(time);
        int hora = CAL.get(Calendar.HOUR_OF_DAY);
        int minuto = CAL.get(Calendar.MINUTE);
        int segundo = CAL.get(Calendar.SECOND);
                
        CAL.setTime(date);
        CAL.set(Calendar.HOUR_OF_DAY, hora);
        CAL.set(Calendar.MINUTE, minuto);
        CAL.set(Calendar.SECOND, segundo);
        CAL.set(Calendar.MILLISECOND, 0);
        return CAL.getTime();
    }
    
    public static String formatMinutes(int minutes) {   
        return String.format("%02d:%02d", minutes / 60, minutes % 60);
    }
    
    public static String formatDate(Date date) {
        return SDF_DATE.format(date);
    }
    
    public static String formatDateTime(Date date) {
        return SDF_DATE_TIME.format(date);
    }
    
}
