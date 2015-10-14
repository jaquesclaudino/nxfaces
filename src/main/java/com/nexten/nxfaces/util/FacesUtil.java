package com.nexten.nxfaces.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author Jaques Claudino
 */
public class FacesUtil {

    private static final String BUNDLE_BASE_NAME = "Bundle";
    
    public static void addMsg(String clientId, FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(severity, summary, detail));
    }

    public static void addMsg(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public static void addMsgInfoBundle(String bundleKey) {
        addMsg(FacesMessage.SEVERITY_INFO, getBundleString(bundleKey), "");
    }

    public static void addMsgErrorBundle(String bundleKey) {
        addMsg(FacesMessage.SEVERITY_ERROR, getBundleString(bundleKey), "");
    }

    public static void addMsgWarningBundle(String bundleKey) {
        addMsg(FacesMessage.SEVERITY_WARN, getBundleString(bundleKey), "");
    }

    public static String getBundleString(String bundleKey) {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, FacesContext.getCurrentInstance().getViewRoot().getLocale());
        try {
            return bundle.getString(bundleKey); 
        } catch (MissingResourceException e) {
            return "???" + bundleKey + "???";
        }
    }

    public static ExternalContext getExternalContext() {
        return FacesContext.getCurrentInstance().getExternalContext();
    }
    
    public static String getApplicationURI() throws URISyntaxException {
        ExternalContext ext = getExternalContext();
        
        URI uri = new URI(
                ext.getRequestScheme(), 
                null, //userInfo
                ext.getRequestServerName(), 
                ext.getRequestServerPort(), 
                ext.getRequestContextPath(), 
                null,  //query
                null); //fragment
        
        return uri.toASCIIString();
    }    
    
}
