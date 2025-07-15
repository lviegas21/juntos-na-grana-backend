package com.noxius.juntosnagrana.repository;

import com.noxius.juntosnagrana.domain.AppUser;
import com.noxius.juntosnagrana.domain.Wallet;
import com.noxius.juntosnagrana.domain.enumeration.WalletType;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Wallet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    /**
     * Find all wallets where the user is the owner.
     *
     * @param owner the owner of the wallet
     * @return the list of wallets
     */
    List<Wallet> findByOwner(AppUser owner);
    
    /**
     * Find all wallets of a specific type.
     *
     * @param type the wallet type
     * @return the list of wallets
     */
    List<Wallet> findByType(WalletType type);
}
