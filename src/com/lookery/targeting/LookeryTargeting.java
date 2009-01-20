//  This software code is made available "AS IS" without warranties of any
//  kind.  You may copy, display, modify and redistribute the software
//  code either by itself or as incorporated into your code; provided that
//  you do not remove any proprietary notices.  Your use of this software
//  code is at your own risk and you waive any claim against Amazon
//  Digital Services, Inc. or its affiliates with respect to your use of
//  this software code. (c) 2007-2009 Lookery.

package com.lookery.targeting;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.lookery.thirdparty.Base64;

public class LookeryTargeting {
    
    /**
     * HMAC/SHA1 Algorithm per RFC 2104.
     */
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    
    private static final String API_VERSION = "2";
    private static final String BASE_URL = "http://services.lookery.com/targeting?";

    private String apiKey;
    private String secretKey;
    
    public LookeryTargeting(String apiKey, String secretKey) {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }
    
    public String redirect(String url) {
        Map<String, String> emptyMap = Collections.emptyMap();
        return redirect(url, emptyMap);
    }
    
    public String redirect(String url, Map<String, String> params) {
        String time = Integer.toString((int) (System.currentTimeMillis() / 1000));
        String encSignature = encode(secretKey, time, true);
        StringBuffer requestUrl = new StringBuffer(BASE_URL);
        requestUrl.append("v=").append(API_VERSION);
        requestUrl.append("&api_key=").append(apiKey);
        requestUrl.append("&r_url=").append(urlencode(url));
        requestUrl.append("&timestamp=").append(time);
        requestUrl.append("&signature=").append(encSignature);
        
        for (Map.Entry<String, String> param : params.entrySet()) {
            requestUrl.append("&").append(urlencode(param.getKey()));
            requestUrl.append("=").append(urlencode(param.getValue()));
        }
        
        return requestUrl.toString();
    }

    /**
     * Calculate the HMAC/SHA1 on a string.
     * @param data Data to sign
     * @param passcode Passcode to sign it with
     * @return Signature
     * @throws NoSuchAlgorithmException If the algorithm does not exist.  Unlikely
     * @throws InvalidKeyException If the key is invalid.
     */
    private static String encode(String secretKey, String data,
                                boolean urlencode)
    {
        // Acquire an HMAC/SHA1 from the raw key bytes.
        SecretKeySpec signingKey =
            new SecretKeySpec(secretKey.getBytes(), HMAC_SHA1_ALGORITHM);

        // Acquire the MAC instance and initialize with the signing key.
        Mac mac = null;
        try {
            mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            // should not happen
            throw new RuntimeException("Could not find sha1 algorithm", e);
        }
        try {
            mac.init(signingKey);
        } catch (InvalidKeyException e) {
            // also should not happen
            throw new RuntimeException("Could not initialize the MAC algorithm", e);
        }

        // Compute the HMAC on the digest, and set it.
        String b64 = Base64.encodeBytes(mac.doFinal(data.getBytes()));

        if (urlencode) {
            return urlencode(b64);
        } else {
            return b64;
        }
    }
   
    static String urlencode(String unencoded) {
        try {
            return URLEncoder.encode(unencoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // should never happen
            throw new RuntimeException("Could not url encode to UTF-8", e);
        }
    }


}
