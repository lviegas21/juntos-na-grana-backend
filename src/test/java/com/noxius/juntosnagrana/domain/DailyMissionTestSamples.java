package com.noxius.juntosnagrana.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DailyMissionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static DailyMission getDailyMissionSample1() {
        return new DailyMission().id(1L).title("title1").description("description1").xpReward(1);
    }

    public static DailyMission getDailyMissionSample2() {
        return new DailyMission().id(2L).title("title2").description("description2").xpReward(2);
    }

    public static DailyMission getDailyMissionRandomSampleGenerator() {
        return new DailyMission()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .xpReward(intCount.incrementAndGet());
    }
}
