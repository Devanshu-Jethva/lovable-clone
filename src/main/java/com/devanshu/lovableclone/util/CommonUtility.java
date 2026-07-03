package com.devanshu.lovableclone.util;

import java.time.Instant;

public final class CommonUtility {

    private CommonUtility() {
    }

    public static Instant toInstant(Long epoch) {
        return epoch != null ? Instant.ofEpochSecond(epoch) : null;
    }
}
