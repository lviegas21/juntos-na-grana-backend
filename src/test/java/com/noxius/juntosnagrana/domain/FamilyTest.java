package com.noxius.juntosnagrana.domain;

import static com.noxius.juntosnagrana.domain.AppUserTestSamples.*;
import static com.noxius.juntosnagrana.domain.FamilyTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.noxius.juntosnagrana.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class FamilyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Family.class);
        Family family1 = getFamilySample1();
        Family family2 = new Family();
        assertThat(family1).isNotEqualTo(family2);

        family2.setId(family1.getId());
        assertThat(family1).isEqualTo(family2);

        family2 = getFamilySample2();
        assertThat(family1).isNotEqualTo(family2);
    }

    @Test
    void membersTest() {
        Family family = getFamilyRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        family.addMembers(appUserBack);
        assertThat(family.getMembers()).containsOnly(appUserBack);
        assertThat(appUserBack.getFamily()).isEqualTo(family);

        family.removeMembers(appUserBack);
        assertThat(family.getMembers()).doesNotContain(appUserBack);
        assertThat(appUserBack.getFamily()).isNull();

        family.members(new HashSet<>(Set.of(appUserBack)));
        assertThat(family.getMembers()).containsOnly(appUserBack);
        assertThat(appUserBack.getFamily()).isEqualTo(family);

        family.setMembers(new HashSet<>());
        assertThat(family.getMembers()).doesNotContain(appUserBack);
        assertThat(appUserBack.getFamily()).isNull();
    }
}
