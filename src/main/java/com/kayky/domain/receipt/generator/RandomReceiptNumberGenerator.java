package com.kayky.domain.receipt.generator;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RandomReceiptNumberGenerator implements ReceiptNumberGenerator {

    @Override
    public String generate() {
        return  "RCT-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
    }
}