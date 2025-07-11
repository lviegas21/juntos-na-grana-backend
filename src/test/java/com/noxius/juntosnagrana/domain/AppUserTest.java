package com.noxius.juntosnagrana.domain;

import static com.noxius.juntosnagrana.domain.AppUserTestSamples.*;
import static com.noxius.juntosnagrana.domain.FamilyTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.noxius.juntosnagrana.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AppUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppUser.class);
        AppUser appUser1 = getAppUserSample1();
        AppUser appUser2 = new AppUser();
        assertThat(appUser1).isNotEqualTo(appUser2);

        appUser2.setId(appUser1.getId());
        assertThat(appUser1).isEqualTo(appUser2);

        appUser2 = getAppUserSample2();
        assertThat(appUser1).isNotEqualTo(appUser2);
    }

    @Test
    void familyTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        Family familyBack = getFamilyRandomSampleGenerator();

        appUser.setFamily(familyBack);
        assertThat(appUser.getFamily()).isEqualTo(familyBack);

        appUser.family(null);
        assertThat(appUser.getFamily()).isNull();
    }
}
