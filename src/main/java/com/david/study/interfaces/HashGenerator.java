package com.david.study.interfaces;

public interface HashGenerator {
    String generateHash(byte[] content);
    String bytesToHex(byte[] bytes);
}
