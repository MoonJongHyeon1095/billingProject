package com.github.util.range;

public enum VideoRangeEnum {
    RANGE_0(0),
    RANGE_1(1),
    RANGE_2(2),
    RANGE_3(3);

    private int rangeCode;

    VideoRangeEnum(final int rangeCode) {
        this.rangeCode = rangeCode;
    }

    private int getRangeCode() {
        return rangeCode;
    }

    public static int getRange(final int views) {
        if (views < 100_000) {
            return RANGE_0.getRangeCode();
        } else if (views < 500_000) {
            return RANGE_1.getRangeCode();
        } else if (views < 1_000_000) {
            return RANGE_2.getRangeCode();
        } else {
            return RANGE_3.getRangeCode();
        }

    }
}