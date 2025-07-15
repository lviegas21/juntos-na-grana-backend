package com.noxius.juntosnagrana.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FamilyTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Family getFamilySample1() {
        return new Family().id(1L).name("name1");
    }

    public static Family getFamilySample2() {
        return new Family().id(2L).name("name2");
    }

    public static Family getFamilyRandomSampleGenerator() {
        return new Family().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
