package com.github.util;

public enum TimeUnit {
    MILLISECONDS {
        public long toMillis(long duration) { return duration; }
        public long toSeconds(long duration) { return duration / 1000; }
    },
    SECONDS {
        public long toMillis(long duration) { return duration * 1000; }
        public long toSeconds(long duration) { return duration; }
    };

    // 각 시간 단위를 밀리초로 변환
    public abstract long toMillis(long duration);

    // 각 시간 단위를 초로 변환
    public abstract long toSeconds(long duration);
}


