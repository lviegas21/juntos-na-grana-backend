package com.noxius.juntosnagrana.domain;

import static com.noxius.juntosnagrana.domain.DailyMissionTestSamples.*;
import static com.noxius.juntosnagrana.domain.MissionStatusRecordTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.noxius.juntosnagrana.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MissionStatusRecordTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MissionStatusRecord.class);
        MissionStatusRecord missionStatusRecord1 = getMissionStatusRecordSample1();
        MissionStatusRecord missionStatusRecord2 = new MissionStatusRecord();
        assertThat(missionStatusRecord1).isNotEqualTo(missionStatusRecord2);

        missionStatusRecord2.setId(missionStatusRecord1.getId());
        assertThat(missionStatusRecord1).isEqualTo(missionStatusRecord2);

        missionStatusRecord2 = getMissionStatusRecordSample2();
        assertThat(missionStatusRecord1).isNotEqualTo(missionStatusRecord2);
    }

    @Test
    void missionTest() {
        MissionStatusRecord missionStatusRecord = getMissionStatusRecordRandomSampleGenerator();
        DailyMission dailyMissionBack = getDailyMissionRandomSampleGenerator();

        missionStatusRecord.setMission(dailyMissionBack);
        assertThat(missionStatusRecord.getMission()).isEqualTo(dailyMissionBack);

        missionStatusRecord.mission(null);
        assertThat(missionStatusRecord.getMission()).isNull();
    }
}
