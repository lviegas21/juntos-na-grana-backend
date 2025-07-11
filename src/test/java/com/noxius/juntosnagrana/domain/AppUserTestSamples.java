package com.noxius.juntosnagrana.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AppUserTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static AppUser getAppUserSample1() {
        return new AppUser().id(1L).username("username1").name("name1").avatar("avatar1").xpPoints(1).level(1);
    }

    public static AppUser getAppUserSample2() {
        return new AppUser().id(2L).username("username2").name("name2").avatar("avatar2").xpPoints(2).level(2);
    }

    public static AppUser getAppUserRandomSampleGenerator() {
        return new AppUser()
            .id(longCount.incrementAndGet())
            .username(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .avatar(UUID.randomUUID().toString())
            .xpPoints(intCount.incrementAndGet())
            .level(intCount.incrementAndGet());
    }
}
