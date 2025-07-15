package com.noxius.juntosnagrana.web.rest;

import com.noxius.juntosnagrana.domain.AppUser;
import com.noxius.juntosnagrana.domain.Wallet;
import com.noxius.juntosnagrana.domain.WalletShare;
import com.noxius.juntosnagrana.domain.enumeration.WalletType;
import com.noxius.juntosnagrana.repository.AppUserRepository;
import com.noxius.juntosnagrana.repository.WalletRepository;
import com.noxius.juntosnagrana.repository.WalletShareRepository;
import com.noxius.juntosnagrana.security.SecurityUtils;
import com.noxius.juntosnagrana.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.noxius.juntosnagrana.domain.Wallet}.
 */
@RestController
@RequestMapping("/api/wallets")
@Transactional
public class WalletResource {

    private static final Logger LOG = LoggerFactory.getLogger(WalletResource.class);

    private static final String ENTITY_NAME = "wallet";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WalletRepository walletRepository;
    private final AppUserRepository appUserRepository;
    private final WalletShareRepository walletShareRepository;

    public WalletResource(WalletRepository walletRepository, AppUserRepository appUserRepository, WalletShareRepository walletShareRepository) {
        this.walletRepository = walletRepository;
        this.appUserRepository = appUserRepository;
        this.walletShareRepository = walletShareRepository;
    }

    /**
     * {@code POST  /wallets} : Create a new wallet.
     *
     * @param wallet the wallet to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new wallet, or with status {@code 400 (Bad Request)} if the wallet has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Wallet> createWallet(@Valid @RequestBody Wallet wallet) throws URISyntaxException {
        LOG.debug("REST request to save Wallet : {}", wallet);
        if (wallet.getId() != null) {
            throw new BadRequestAlertException("A new wallet cannot already have an ID", ENTITY_NAME, "idexists");
        }
        
        // Set the current user as the owner if not specified
        if (wallet.getOwner() == null) {
            String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
            if (currentUserLogin == null) {
                throw new BadRequestAlertException("Current user login not found", ENTITY_NAME, "usernotfound");
            }
            
            Optional<AppUser> currentAppUser = appUserRepository.findByUsername(currentUserLogin);
            if (currentAppUser.isEmpty()) {
                throw new BadRequestAlertException("Current app user not found", ENTITY_NAME, "appusernotfound");
            }
            
            wallet.setOwner(currentAppUser.get());
        }
        
        // Set creation date if not specified
        if (wallet.getCreatedAt() == null) {
            wallet.setCreatedAt(ZonedDateTime.now());
        }
        
        wallet = walletRepository.save(wallet);
        return ResponseEntity.created(new URI("/api/wallets/" + wallet.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, wallet.getId().toString()))
            .body(wallet);
    }

    /**
     * {@code PUT  /wallets/:id} : Updates an existing wallet.
     *
     * @param id the id of the wallet to save.
     * @param wallet the wallet to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated wallet,
     * or with status {@code 400 (Bad Request)} if the wallet is not valid,
     * or with status {@code 500 (Internal Server Error)} if the wallet couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Wallet> updateWallet(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Wallet wallet
    ) throws URISyntaxException {
        LOG.debug("REST request to update Wallet : {}, {}", id, wallet);
        if (wallet.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, wallet.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!walletRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        
        // Verificar se o usuário atual é o proprietário ou tem acesso compartilhado
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (currentUserLogin == null) {
            throw new BadRequestAlertException("Current user login not found", ENTITY_NAME, "usernotfound");
        }
        
        Optional<AppUser> currentAppUser = appUserRepository.findByUsername(currentUserLogin);
        if (currentAppUser.isEmpty()) {
            throw new BadRequestAlertException("Current app user not found", ENTITY_NAME, "appusernotfound");
        }
        
        // Obter a carteira existente do banco de dados
        Optional<Wallet> existingWalletOpt = walletRepository.findById(id);
        if (existingWalletOpt.isEmpty()) {
            throw new BadRequestAlertException("Wallet not found", ENTITY_NAME, "walletnotfound");
        }
        
        Wallet existingWallet = existingWalletOpt.get();
        AppUser currentUser = currentAppUser.get();
        
        // Verificar se o usuário é o proprietário
        boolean isOwner = existingWallet.getOwner().getId().equals(currentUser.getId());
        
        // Verificar se o usuário tem acesso compartilhado
        boolean hasSharedAccess = false;
        if (!isOwner) {
            hasSharedAccess = walletShareRepository.existsByWalletAndSharedWith(existingWallet, currentUser);
            
            if (!hasSharedAccess) {
                throw new BadRequestAlertException("You don't have permission to update this wallet", ENTITY_NAME, "nopermission");
            }
        }
        
        // Manter o proprietário original
        wallet.setOwner(existingWallet.getOwner());
        
        // Manter a data de criação original
        wallet.setCreatedAt(existingWallet.getCreatedAt());
        
        wallet = walletRepository.save(wallet);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, wallet.getId().toString()))
            .body(wallet);
    }

    /**
     * {@code PATCH  /wallets/:id} : Partial updates given fields of an existing wallet, field will ignore if it is null
     *
     * @param id the id of the wallet to save.
     * @param wallet the wallet to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated wallet,
     * or with status {@code 400 (Bad Request)} if the wallet is not valid,
     * or with status {@code 404 (Not Found)} if the wallet is not found,
     * or with status {@code 500 (Internal Server Error)} if the wallet couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Wallet> partialUpdateWallet(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Wallet wallet
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Wallet partially : {}, {}", id, wallet);
        if (wallet.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, wallet.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!walletRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Wallet> result = walletRepository
            .findById(wallet.getId())
            .map(existingWallet -> {
                if (wallet.getName() != null) {
                    existingWallet.setName(wallet.getName());
                }
                if (wallet.getBalance() != null) {
                    existingWallet.setBalance(wallet.getBalance());
                }
                if (wallet.getType() != null) {
                    existingWallet.setType(wallet.getType());
                }
                if (wallet.getIcon() != null) {
                    existingWallet.setIcon(wallet.getIcon());
                }
                if (wallet.getColor() != null) {
                    existingWallet.setColor(wallet.getColor());
                }
                if (wallet.getDescription() != null) {
                    existingWallet.setDescription(wallet.getDescription());
                }
                if (wallet.getCreatedAt() != null) {
                    existingWallet.setCreatedAt(wallet.getCreatedAt());
                }

                return existingWallet;
            })
            .map(walletRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, wallet.getId().toString())
        );
    }

    /**
     * {@code GET  /wallets} : get all the wallets owned by the current user or shared with them.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of wallets in body.
     */
    @GetMapping("")
    public List<Wallet> getAllWallets() {
        LOG.debug("REST request to get all Wallets for current user or shared with them");
        
        // Get the current user login
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (currentUserLogin == null) {
            LOG.warn("No authenticated user found, returning empty wallet list");
            return Collections.emptyList();
        }
        
        // Find the AppUser entity for the current user
        Optional<AppUser> currentAppUser = appUserRepository.findByUsername(currentUserLogin);
        if (currentAppUser.isEmpty()) {
            LOG.warn("No AppUser found for login: {}, returning empty wallet list", currentUserLogin);
            return Collections.emptyList();
        }
        
        AppUser user = currentAppUser.get();
        
        // Get wallets owned by the user
        List<Wallet> ownedWallets = walletRepository.findByOwner(user);
        
        // Get wallets shared with the user
        List<WalletShare> sharedWallets = walletShareRepository.findBySharedWith(user);
        List<Wallet> sharedWalletsList = sharedWallets.stream()
            .map(WalletShare::getWallet)
            .collect(Collectors.toList());
        
        // Combine both lists
        List<Wallet> result = new ArrayList<>(ownedWallets);
        result.addAll(sharedWalletsList);
        
        return result;
    }

    /**
     * {@code GET  /wallets/:id} : get the "id" wallet.
     *
     * @param id the id of the wallet to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the wallet, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Wallet> getWallet(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Wallet : {}", id);
        Optional<Wallet> wallet = walletRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(wallet);
    }

    /**
     * {@code DELETE  /wallets/:id} : delete the "id" wallet.
     *
     * @param id the id of the wallet to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWallet(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Wallet : {}", id);
        walletRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
