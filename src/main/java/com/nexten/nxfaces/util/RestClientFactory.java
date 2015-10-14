package com.nexten.nxfaces.util;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

/**
 *
 * @author Jaques Claudino
 */
public class RestClientFactory {
    
    public static WebTarget createWebTargetApacheCXF(String baseAddress, int timeout) {
        return ClientBuilder
                .newBuilder()
                .property("javax.xml.ws.client.connectionTimeout", timeout) //http://cxf.apache.org/javadoc/latest/constant-values.html
                .property("javax.xml.ws.client.receiveTimeout", timeout)
                .build()
                .target(baseAddress);
    }
    
    public static WebTarget createWebTargetJersey(String baseAddress, int timeout) {
        return ClientBuilder
                .newBuilder()
                .property("jersey.config.client.connectTimeout", timeout)
                .property("jersey.config.client.readTimeout", timeout)
                //.property("jersey.config.client.proxy.uri", "192.168.2.254:3128")
                .build()
                .target(baseAddress);
        
    }
    
}
