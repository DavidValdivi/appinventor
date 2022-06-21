package com.google.appinventor.server;

import java.io.IOException;
import java.io.PrintWriter;

import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.net.URLEncoder;
import java.security.*;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appinventor.server.storage.StorageIo;
import com.google.appinventor.server.storage.StorageIoInstanceHolder;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class CommunityLoginServlet extends OdeServlet {
    private static final String HMAC_SHA512 = "HmacSHA256";
    private final StorageIo storageIo = StorageIoInstanceHolder.getInstance();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        if (((request.getParameter("sso"))) != null) {
            // Verify payload integrity
            PrintWriter pw = response.getWriter();
            try {
                String sso = request.getParameter("sso");
                String sig = request.getParameter("sig");
                String decoded = URLDecoder.decode(sso, StandardCharsets.UTF_8.name());
                String hash = calculateHMAC(decoded, "myrandomstring");
                if(sig.equals(hash)) {
                    byte[] decodedString = Base64.getDecoder().decode(decoded);
                    String queryString = new String(decodedString);
                    Map<String, String> queries = getQueryMap(queryString);
                    System.out.println(queries.get("name") + " " + queries.get("email"));
                    String decodedEmail = URLDecoder.decode(queries.get("name"), StandardCharsets.US_ASCII.name());
                    System.out.println(decodedEmail);
                    storageIo.setUserCommunityLogin(userInfoProvider.getUserId(), true);
                    storageIo.setUserCommunityLoginEmail(userInfoProvider.getUserId(), decodedEmail);
                    pw.print("<h1>You have been logged in successfully. Go back to MIT App Inventor to continue.</h1>");
                } else {
                    pw.print("Something went wrong. please try to login again.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                pw.print("Error: " + e.getMessage());
            }
        } else {
            byte[] noncee = new byte[16];
	        new SecureRandom().nextBytes(noncee);
            String nonce = convertBytesToHex(noncee);
            String payload = "nonce="+nonce+"&return_sso_url=http://localhost:8888/community/login&sso_redirect_url=http://localhost:8888/community/login&destination_url=http://localhost:8888/community/login";
            String base64 = Base64.getEncoder().encodeToString(payload.getBytes());
            String URL_ENCODED_PAYLOAD = URLEncoder.encode(base64);
            try {
                String hmac = calculateHMAC(base64, "myrandomstring");
                String HEX_SIGNATURE = hmac.toLowerCase();
                response.sendRedirect("http://localhost:4200/session/sso_provider?sso="+URL_ENCODED_PAYLOAD+"&sig="+HEX_SIGNATURE);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    // util to print bytes in hex
    private static String convertBytesToHex(byte[] bytes) {
		StringBuilder result = new StringBuilder();
		for (byte temp : bytes) {
		    result.append(String.format("%02x", temp));
		}
		return result.toString();
    	}	
	public static String calculateHMAC(String data, String key)
	    throws SignatureException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
	{
	    SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), HMAC_SHA512);
	    Mac mac = Mac.getInstance(HMAC_SHA512);
	    mac.init(secretKeySpec);
	    return toHexString(mac.doFinal(data.getBytes()));
	}
	private static String toHexString(byte[] bytes) {
	    Formatter formatter = new Formatter();
	    for (byte b : bytes) {
		formatter.format("%02x", b);
	    }
	    return formatter.toString();
	}
    public static Map<String, String> getQueryMap(String query)  
    {  
        String[] params = query.split("&");  
        Map<String, String> map = new HashMap<String, String>();  
        for (String param : params)  
        {  
            String name = param.split("=")[0];  
            String value = param.split("=")[1];  
            map.put(name, value);  
        }  
        return map;  
    }  
}
