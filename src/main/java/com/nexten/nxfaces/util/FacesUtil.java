package com.nexten.nxfaces.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author Jaques Claudino
 */
public class FacesUtil {

    private static final String BUNDLE_BASE_NAME = "Bundle";

    private FacesUtil() {
    }
    
    public static void addMsg(String clientId, FacesMessage.Severity severity, String summary, String detail) {
        if (detail == null) {
            detail = "";
        }
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(severity, summary, detail));
    }

    public static void addMsg(FacesMessage.Severity severity, String summary, String detail) {
        addMsg(null, severity, summary, detail);
    }

    public static void addMsgInfoBundle(String bundleKey) {
        addMsg(FacesMessage.SEVERITY_INFO, getBundleString(bundleKey), "");
    }

    public static void addMsgInfoBundle(String bundleKey, Object... args) {
        addMsg(FacesMessage.SEVERITY_INFO, String.format(getBundleString(bundleKey), args), "");
    }
    
    public static void addMsgErrorBundle(String bundleKey) {
        addMsg(FacesMessage.SEVERITY_ERROR, getBundleString(bundleKey), "");
    }

    public static void addMsgWarningBundle(String bundleKey) {
        addMsg(FacesMessage.SEVERITY_WARN, getBundleString(bundleKey), "");
    }

    public static String[] getBundleStrings(String bundleKey) {
    	return getBundleString(bundleKey).split(";");
    }
    
    public static String getBundleString(String bundleKey, Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
        try {
            return bundle.getString(bundleKey); 
        } catch (MissingResourceException e) {
            return "???" + bundleKey + "???";
        }
    }
    
    public static String getBundleString(String bundleKey) {
        return getBundleString(bundleKey, FacesContext.getCurrentInstance().getViewRoot().getLocale());
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
    
    public static ValueExpression createValueExpression(String expression) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Application app = facesContext.getApplication();
        ExpressionFactory elFactory = app.getExpressionFactory();
        ELContext elContext = facesContext.getELContext();
        return elFactory.createValueExpression(elContext, expression, Object.class);
    }
    
    public static UIComponent findComponent(final String id) {
        FacesContext context = FacesContext.getCurrentInstance(); 
        UIViewRoot root = context.getViewRoot();
        VisitContext visitContext = VisitContext.createVisitContext(context);
        final UIComponent[] found = new UIComponent[1];

        root.visitTree(visitContext, (VisitContext context1, UIComponent component) -> {
            if (id.equals(component.getId())) {
                found[0] = component;
                return VisitResult.COMPLETE;              
            }
            return VisitResult.ACCEPT;
        });

        return found[0];
    }
    
}
