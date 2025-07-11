package com.noxius.juntosnagrana.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class MissionStatusRecordTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static MissionStatusRecord getMissionStatusRecordSample1() {
        return new MissionStatusRecord().id(1L);
    }

    public static MissionStatusRecord getMissionStatusRecordSample2() {
        return new MissionStatusRecord().id(2L);
    }

    public static MissionStatusRecord getMissionStatusRecordRandomSampleGenerator() {
        return new MissionStatusRecord().id(longCount.incrementAndGet());
    }
}
