package com.noxius.juntosnagrana.domain;

import static com.noxius.juntosnagrana.domain.DailyMissionTestSamples.*;
import static com.noxius.juntosnagrana.domain.FamilyTestSamples.*;
import static com.noxius.juntosnagrana.domain.MissionStatusRecordTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.noxius.juntosnagrana.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DailyMissionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DailyMission.class);
        DailyMission dailyMission1 = getDailyMissionSample1();
        DailyMission dailyMission2 = new DailyMission();
        assertThat(dailyMission1).isNotEqualTo(dailyMission2);

        dailyMission2.setId(dailyMission1.getId());
        assertThat(dailyMission1).isEqualTo(dailyMission2);

        dailyMission2 = getDailyMissionSample2();
        assertThat(dailyMission1).isNotEqualTo(dailyMission2);
    }

    @Test
    void statusRecordsTest() {
        DailyMission dailyMission = getDailyMissionRandomSampleGenerator();
        MissionStatusRecord missionStatusRecordBack = getMissionStatusRecordRandomSampleGenerator();

        dailyMission.addStatusRecords(missionStatusRecordBack);
        assertThat(dailyMission.getStatusRecords()).containsOnly(missionStatusRecordBack);
        assertThat(missionStatusRecordBack.getMission()).isEqualTo(dailyMission);

        dailyMission.removeStatusRecords(missionStatusRecordBack);
        assertThat(dailyMission.getStatusRecords()).doesNotContain(missionStatusRecordBack);
        assertThat(missionStatusRecordBack.getMission()).isNull();

        dailyMission.statusRecords(new HashSet<>(Set.of(missionStatusRecordBack)));
        assertThat(dailyMission.getStatusRecords()).containsOnly(missionStatusRecordBack);
        assertThat(missionStatusRecordBack.getMission()).isEqualTo(dailyMission);

        dailyMission.setStatusRecords(new HashSet<>());
        assertThat(dailyMission.getStatusRecords()).doesNotContain(missionStatusRecordBack);
        assertThat(missionStatusRecordBack.getMission()).isNull();
    }

    @Test
    void familyTest() {
        DailyMission dailyMission = getDailyMissionRandomSampleGenerator();
        Family familyBack = getFamilyRandomSampleGenerator();

        dailyMission.setFamily(familyBack);
        assertThat(dailyMission.getFamily()).isEqualTo(familyBack);

        dailyMission.family(null);
        assertThat(dailyMission.getFamily()).isNull();
    }
}
