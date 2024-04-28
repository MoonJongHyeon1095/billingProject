package com.github.domain.calculator;
public enum AdRangeEnum {
    RANGE_0(0),
    RANGE_1(1),
    RANGE_2(2),
    RANGE_3(3);

    private int rangeCode;

    AdRangeEnum(final int rangeCode) {
        this.rangeCode = rangeCode;
    }

    private int getRangeCode() {
        return rangeCode;
    }

    public static int getRange(final int views) {
        if (views >= 0 && views <= 9999) {
            return RANGE_0.getRangeCode();
        } else if (views >= 10000 && views <= 499999) {
            return RANGE_1.getRangeCode();
        } else if (views >= 500000 && views <= 9999999) {
            return RANGE_2.getRangeCode();
        } else {
            return RANGE_3.getRangeCode();
        }
    }
}
