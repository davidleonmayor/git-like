package com.david.study.impl;

import com.david.study.interfaces.HashGenerator;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1HashGenerator implements HashGenerator {
    
    @Override
    public String generateHash(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] sha1Bytes = md.digest(input);
            return bytesToHex(sha1Bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not found", e);
        }
    }

    @Override
    public String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
