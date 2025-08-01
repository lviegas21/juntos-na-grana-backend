package com.noxius.juntosnagrana.domain;

import static com.noxius.juntosnagrana.domain.AssertUtils.zonedDataTimeSameInstant;
import static org.assertj.core.api.Assertions.assertThat;

public class DailyMissionAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertDailyMissionAllPropertiesEquals(DailyMission expected, DailyMission actual) {
        assertDailyMissionAutoGeneratedPropertiesEquals(expected, actual);
        assertDailyMissionAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertDailyMissionAllUpdatablePropertiesEquals(DailyMission expected, DailyMission actual) {
        assertDailyMissionUpdatableFieldsEquals(expected, actual);
        assertDailyMissionUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertDailyMissionAutoGeneratedPropertiesEquals(DailyMission expected, DailyMission actual) {
        assertThat(actual)
            .as("Verify DailyMission auto generated properties")
            .satisfies(a -> assertThat(a.getId()).as("check id").isEqualTo(expected.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertDailyMissionUpdatableFieldsEquals(DailyMission expected, DailyMission actual) {
        assertThat(actual)
            .as("Verify DailyMission relevant properties")
            .satisfies(a -> assertThat(a.getTitle()).as("check title").isEqualTo(expected.getTitle()))
            .satisfies(a -> assertThat(a.getDescription()).as("check description").isEqualTo(expected.getDescription()))
            .satisfies(a ->
                assertThat(a.getStartDate())
                    .as("check startDate")
                    .usingComparator(zonedDataTimeSameInstant)
                    .isEqualTo(expected.getStartDate())
            )
            .satisfies(a ->
                assertThat(a.getEndDate()).as("check endDate").usingComparator(zonedDataTimeSameInstant).isEqualTo(expected.getEndDate())
            )
            .satisfies(a -> assertThat(a.getType()).as("check type").isEqualTo(expected.getType()))
            .satisfies(a -> assertThat(a.getTargetAmount()).as("check targetAmount").isEqualTo(expected.getTargetAmount()))
            .satisfies(a -> assertThat(a.getCategory()).as("check category").isEqualTo(expected.getCategory()))
            .satisfies(a -> assertThat(a.getXpReward()).as("check xpReward").isEqualTo(expected.getXpReward()))
            .satisfies(a ->
                assertThat(a.getCreatedAt())
                    .as("check createdAt")
                    .usingComparator(zonedDataTimeSameInstant)
                    .isEqualTo(expected.getCreatedAt())
            );
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertDailyMissionUpdatableRelationshipsEquals(DailyMission expected, DailyMission actual) {
        assertThat(actual)
            .as("Verify DailyMission relationships")
            .satisfies(a -> assertThat(a.getFamily()).as("check family").isEqualTo(expected.getFamily()));
    }
}
