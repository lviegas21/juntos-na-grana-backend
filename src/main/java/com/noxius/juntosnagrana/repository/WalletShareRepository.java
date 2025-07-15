package com.noxius.juntosnagrana.repository;

import com.noxius.juntosnagrana.domain.AppUser;
import com.noxius.juntosnagrana.domain.Wallet;
import com.noxius.juntosnagrana.domain.WalletShare;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WalletShare entity.
 */
@Repository
public interface WalletShareRepository extends JpaRepository<WalletShare, Long> {
    /**
     * Find all wallet shares for a specific user.
     *
     * @param sharedWith the user who has access to shared wallets
     * @return the list of wallet shares
     */
    List<WalletShare> findBySharedWith(AppUser sharedWith);
    
    /**
     * Find all wallet shares for a specific wallet.
     *
     * @param wallet the wallet being shared
     * @return the list of wallet shares
     */
    List<WalletShare> findByWallet(Wallet wallet);
    
    /**
     * Check if a wallet is shared with a specific user.
     *
     * @param wallet the wallet to check
     * @param sharedWith the user to check
     * @return true if the wallet is shared with the user
     */
    boolean existsByWalletAndSharedWith(Wallet wallet, AppUser sharedWith);
}
