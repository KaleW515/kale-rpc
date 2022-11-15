package com.kalew515.common.enums;

public enum LoadbalancerEnum {

    RANDOM("random"),

    MIN_CALL("min-call"),

    MIN_CONN("min-conn");

    private final String lbName;

    LoadbalancerEnum (String lbName) {
        this.lbName = lbName;
    }

    public String getLbName () {
        return lbName;
    }
}
