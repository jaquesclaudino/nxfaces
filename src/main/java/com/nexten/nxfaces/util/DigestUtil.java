package com.nexten.nxfaces.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/**
 *
 * @author Jaques Claudino
 */
public class DigestUtil {

    private static final Logger LOG = Logger.getLogger(DigestUtil.class.getName());

    private DigestUtil() {
    }

    public static String md5(String s) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5"); // NOSONAR
            m.update(s.getBytes(), 0, s.length());
            BigInteger i = new BigInteger(1, m.digest());
            return String.format("%1$032x", i);
        } catch (NoSuchAlgorithmException ex) {
            LOG.severe(ex.toString());
        }
        return null;
    }

    public static String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256"); // NOSONAR
            byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            LOG.severe(ex.toString());
        }
        return null;
    }
    
}
