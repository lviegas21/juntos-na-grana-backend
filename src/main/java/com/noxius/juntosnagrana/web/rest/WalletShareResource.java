package com.noxius.juntosnagrana.web.rest;

import com.noxius.juntosnagrana.domain.AppUser;
import com.noxius.juntosnagrana.domain.Wallet;
import com.noxius.juntosnagrana.domain.WalletShare;
import com.noxius.juntosnagrana.repository.AppUserRepository;
import com.noxius.juntosnagrana.repository.WalletRepository;
import com.noxius.juntosnagrana.repository.WalletShareRepository;
import com.noxius.juntosnagrana.security.SecurityUtils;
import com.noxius.juntosnagrana.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.noxius.juntosnagrana.domain.WalletShare}.
 */
@RestController
@RequestMapping("/api/wallet-shares")
@Transactional
public class WalletShareResource {

    private static final Logger LOG = LoggerFactory.getLogger(WalletShareResource.class);

    private static final String ENTITY_NAME = "walletShare";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WalletShareRepository walletShareRepository;
    private final WalletRepository walletRepository;
    private final AppUserRepository appUserRepository;

    public WalletShareResource(
        WalletShareRepository walletShareRepository,
        WalletRepository walletRepository,
        AppUserRepository appUserRepository
    ) {
        this.walletShareRepository = walletShareRepository;
        this.walletRepository = walletRepository;
        this.appUserRepository = appUserRepository;
    }

    /**
     * {@code POST  /wallet-shares} : Share a wallet with another user.
     *
     * @param walletId the ID of the wallet to share.
     * @param username the username of the user to share with.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new walletShare.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<WalletShare> shareWallet(
        @RequestParam Long walletId,
        @RequestParam String username
    ) throws URISyntaxException {
        LOG.debug("REST request to share Wallet {} with user {}", walletId, username);
        
        // Get current user
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (currentUserLogin == null) {
            throw new BadRequestAlertException("Current user not found", ENTITY_NAME, "usernotfound");
        }
        
        // Get the wallet
        Optional<Wallet> walletOpt = walletRepository.findById(walletId);
        if (walletOpt.isEmpty()) {
            throw new BadRequestAlertException("Wallet not found", ENTITY_NAME, "walletnotfound");
        }
        
        Wallet wallet = walletOpt.get();
        
        // Check if the current user is the owner of the wallet
        if (!wallet.getOwner().getUsername().equals(currentUserLogin)) {
            throw new BadRequestAlertException("You are not the owner of this wallet", ENTITY_NAME, "notowner");
        }
        
        // Get the user to share with
        Optional<AppUser> userToShareWithOpt = appUserRepository.findByUsername(username);
        if (userToShareWithOpt.isEmpty()) {
            throw new BadRequestAlertException("User to share with not found", ENTITY_NAME, "sharedusernotfound");
        }
        
        AppUser userToShareWith = userToShareWithOpt.get();
        
        // Check if the wallet is already shared with this user
        if (walletShareRepository.existsByWalletAndSharedWith(wallet, userToShareWith)) {
            throw new BadRequestAlertException("Wallet already shared with this user", ENTITY_NAME, "alreadyshared");
        }
        
        // Create the wallet share
        WalletShare walletShare = new WalletShare();
        walletShare.setWallet(wallet);
        walletShare.setSharedWith(userToShareWith);
        walletShare.setCreatedAt(ZonedDateTime.now());
        
        walletShare = walletShareRepository.save(walletShare);
        
        return ResponseEntity
            .created(new URI("/api/wallet-shares/" + walletShare.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, walletShare.getId().toString()))
            .body(walletShare);
    }

    /**
     * {@code GET  /wallet-shares} : get all wallet shares for the current user.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of walletShares in body.
     */
    @GetMapping("")
    public List<WalletShare> getAllWalletShares() {
        LOG.debug("REST request to get all WalletShares for current user");
        
        // Get current user
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (currentUserLogin == null) {
            throw new BadRequestAlertException("Current user not found", ENTITY_NAME, "usernotfound");
        }
        
        Optional<AppUser> currentUserOpt = appUserRepository.findByUsername(currentUserLogin);
        if (currentUserOpt.isEmpty()) {
            throw new BadRequestAlertException("Current app user not found", ENTITY_NAME, "appusernotfound");
        }
        
        AppUser currentUser = currentUserOpt.get();
        
        // Get all wallets shared with the current user
        return walletShareRepository.findBySharedWith(currentUser);
    }

    /**
     * {@code DELETE  /wallet-shares} : Remove a wallet share.
     *
     * @param walletId the ID of the wallet.
     * @param username the username of the user to remove sharing.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("")
    public ResponseEntity<Void> removeWalletShare(
        @RequestParam Long walletId,
        @RequestParam String username
    ) {
        LOG.debug("REST request to remove wallet share for wallet {} and user {}", walletId, username);
        
        // Get current user
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (currentUserLogin == null) {
            throw new BadRequestAlertException("Current user not found", ENTITY_NAME, "usernotfound");
        }
        
        // Get the wallet
        Optional<Wallet> walletOpt = walletRepository.findById(walletId);
        if (walletOpt.isEmpty()) {
            throw new BadRequestAlertException("Wallet not found", ENTITY_NAME, "walletnotfound");
        }
        
        Wallet wallet = walletOpt.get();
        
        // Check if the current user is the owner of the wallet
        if (!wallet.getOwner().getUsername().equals(currentUserLogin)) {
            throw new BadRequestAlertException("You are not the owner of this wallet", ENTITY_NAME, "notowner");
        }
        
        // Get the user to remove sharing
        Optional<AppUser> userToRemoveOpt = appUserRepository.findByUsername(username);
        if (userToRemoveOpt.isEmpty()) {
            throw new BadRequestAlertException("User not found", ENTITY_NAME, "usernotfound");
        }
        
        AppUser userToRemove = userToRemoveOpt.get();
        
        // Find and delete the wallet share
        List<WalletShare> walletShares = walletShareRepository.findByWallet(wallet);
        for (WalletShare share : walletShares) {
            if (share.getSharedWith().getUsername().equals(username)) {
                walletShareRepository.delete(share);
                return ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, share.getId().toString()))
                    .build();
            }
        }
        
        throw new BadRequestAlertException("Wallet is not shared with this user", ENTITY_NAME, "notshared");
    }
}
