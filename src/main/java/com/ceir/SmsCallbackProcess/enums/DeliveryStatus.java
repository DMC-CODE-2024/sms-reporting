package com.ceir.SmsCallbackProcess.enums;

public enum DeliveryStatus {
    NO_UPDATE(2),
    DELIVERED(1),
    FAILED(0);

    private final int value;

    DeliveryStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
