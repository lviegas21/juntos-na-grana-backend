package com.noxius.juntosnagrana.domain;

import static com.noxius.juntosnagrana.domain.AppUserTestSamples.*;
import static com.noxius.juntosnagrana.domain.WalletTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.noxius.juntosnagrana.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WalletTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Wallet.class);
        Wallet wallet1 = getWalletSample1();
        Wallet wallet2 = new Wallet();
        assertThat(wallet1).isNotEqualTo(wallet2);

        wallet2.setId(wallet1.getId());
        assertThat(wallet1).isEqualTo(wallet2);

        wallet2 = getWalletSample2();
        assertThat(wallet1).isNotEqualTo(wallet2);
    }

    @Test
    void ownerTest() {
        Wallet wallet = getWalletRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        wallet.setOwner(appUserBack);
        assertThat(wallet.getOwner()).isEqualTo(appUserBack);

        wallet.owner(null);
        assertThat(wallet.getOwner()).isNull();
    }
}
