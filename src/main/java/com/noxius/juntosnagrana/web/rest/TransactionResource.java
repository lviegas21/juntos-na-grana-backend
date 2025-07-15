package com.noxius.juntosnagrana.web.rest;

import com.noxius.juntosnagrana.domain.AppUser;
import com.noxius.juntosnagrana.domain.Transaction;
import com.noxius.juntosnagrana.domain.Wallet;
import com.noxius.juntosnagrana.domain.enumeration.TransactionType;
import com.noxius.juntosnagrana.repository.AppUserRepository;
import com.noxius.juntosnagrana.repository.TransactionRepository;
import com.noxius.juntosnagrana.repository.WalletRepository;
import com.noxius.juntosnagrana.repository.WalletShareRepository;
import com.noxius.juntosnagrana.security.SecurityUtils;
import com.noxius.juntosnagrana.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.noxius.juntosnagrana.domain.Transaction}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TransactionResource {

    private final Logger log = LoggerFactory.getLogger(TransactionResource.class);

    private static final String ENTITY_NAME = "transaction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final AppUserRepository appUserRepository;
    private final WalletShareRepository walletShareRepository;

    public TransactionResource(
        TransactionRepository transactionRepository,
        WalletRepository walletRepository,
        AppUserRepository appUserRepository,
        WalletShareRepository walletShareRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.appUserRepository = appUserRepository;
        this.walletShareRepository = walletShareRepository;
    }

    /**
     * {@code POST  /transactions} : Create a new transaction.
     *
     * @param transaction the transaction to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transaction, or with status {@code 400 (Bad Request)} if the transaction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/transactions")
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody Transaction transaction) throws URISyntaxException {
        log.debug("REST request to save Transaction : {}", transaction);
        if (transaction.getId() != null) {
            throw new BadRequestAlertException("A new transaction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        
        // Verificar se a carteira existe
        if (transaction.getWallet() == null || transaction.getWallet().getId() == null) {
            throw new BadRequestAlertException("Invalid wallet", ENTITY_NAME, "walletinvalid");
        }
        
        Optional<Wallet> walletOpt = walletRepository.findById(transaction.getWallet().getId());
        if (walletOpt.isEmpty()) {
            throw new BadRequestAlertException("Wallet not found", ENTITY_NAME, "walletnotfound");
        }
        
        Wallet wallet = walletOpt.get();
        
        // Verificar se o usuário atual tem acesso à carteira
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (currentUserLogin == null) {
            throw new BadRequestAlertException("Current user login not found", ENTITY_NAME, "usernotfound");
        }
        
        Optional<AppUser> currentAppUser = appUserRepository.findByUsername(currentUserLogin);
        if (currentAppUser.isEmpty()) {
            throw new BadRequestAlertException("Current app user not found", ENTITY_NAME, "appusernotfound");
        }
        
        AppUser currentUser = currentAppUser.get();
        
        // Verificar se o usuário é o proprietário ou tem acesso compartilhado
        boolean isOwner = wallet.getOwner().getId().equals(currentUser.getId());
        boolean hasSharedAccess = false;
        
        if (!isOwner) {
            hasSharedAccess = walletShareRepository.existsByWalletAndSharedWith(wallet, currentUser);
            
            if (!hasSharedAccess) {
                throw new BadRequestAlertException("You don't have permission to add transactions to this wallet", ENTITY_NAME, "nopermission");
            }
        }
        
        // Definir a data da transação se não for fornecida
        if (transaction.getTransactionDate() == null) {
            transaction.setTransactionDate(ZonedDateTime.now());
        }
        
        // Atualizar o saldo da carteira
        double currentBalance = wallet.getBalance();
        if (transaction.getType() == TransactionType.INCOME) {
            wallet.setBalance(currentBalance + transaction.getAmount());
        } else if (transaction.getType() == TransactionType.EXPENSE) {
            wallet.setBalance(currentBalance - transaction.getAmount());
        }
        
        // Salvar a carteira atualizada
        walletRepository.save(wallet);
        
        // Salvar a transação
        Transaction result = transactionRepository.save(transaction);
        return ResponseEntity
            .created(new URI("/api/transactions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /transactions/:id} : Updates an existing transaction.
     *
     * @param id the id of the transaction to save.
     * @param transaction the transaction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transaction,
     * or with status {@code 400 (Bad Request)} if the transaction is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transaction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/transactions/{id}")
    public ResponseEntity<Transaction> updateTransaction(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Transaction transaction
    ) throws URISyntaxException {
        log.debug("REST request to update Transaction : {}, {}", id, transaction);
        if (transaction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transaction.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        
        // Obter a transação existente
        Optional<Transaction> existingTransactionOpt = transactionRepository.findById(id);
        if (existingTransactionOpt.isEmpty()) {
            throw new BadRequestAlertException("Transaction not found", ENTITY_NAME, "transactionnotfound");
        }
        
        Transaction existingTransaction = existingTransactionOpt.get();
        Wallet wallet = existingTransaction.getWallet();
        
        // Verificar se o usuário atual tem acesso à carteira
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (currentUserLogin == null) {
            throw new BadRequestAlertException("Current user login not found", ENTITY_NAME, "usernotfound");
        }
        
        Optional<AppUser> currentAppUser = appUserRepository.findByUsername(currentUserLogin);
        if (currentAppUser.isEmpty()) {
            throw new BadRequestAlertException("Current app user not found", ENTITY_NAME, "appusernotfound");
        }
        
        AppUser currentUser = currentAppUser.get();
        
        // Verificar se o usuário é o proprietário ou tem acesso compartilhado
        boolean isOwner = wallet.getOwner().getId().equals(currentUser.getId());
        boolean hasSharedAccess = false;
        
        if (!isOwner) {
            hasSharedAccess = walletShareRepository.existsByWalletAndSharedWith(wallet, currentUser);
            
            if (!hasSharedAccess) {
                throw new BadRequestAlertException("You don't have permission to update transactions in this wallet", ENTITY_NAME, "nopermission");
            }
        }
        
        // Reverter o efeito da transação anterior no saldo da carteira
        double currentBalance = wallet.getBalance();
        if (existingTransaction.getType() == TransactionType.INCOME) {
            currentBalance -= existingTransaction.getAmount();
        } else if (existingTransaction.getType() == TransactionType.EXPENSE) {
            currentBalance += existingTransaction.getAmount();
        }
        
        // Aplicar o efeito da nova transação no saldo da carteira
        if (transaction.getType() == TransactionType.INCOME) {
            currentBalance += transaction.getAmount();
        } else if (transaction.getType() == TransactionType.EXPENSE) {
            currentBalance -= transaction.getAmount();
        }
        
        // Atualizar o saldo da carteira
        wallet.setBalance(currentBalance);
        walletRepository.save(wallet);
        
        // Garantir que a carteira não seja alterada
        transaction.setWallet(existingTransaction.getWallet());
        
        Transaction result = transactionRepository.save(transaction);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transaction.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /transactions} : get all the transactions.
     *
     * @param walletId the wallet ID to filter by
     * @param type the transaction type to filter by
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transactions in body.
     */
    @GetMapping("/transactions")
    public List<Transaction> getAllTransactions(
        @RequestParam(required = true) Long walletId,
        @RequestParam(required = false) TransactionType type,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate,
        @RequestParam(required = false) String category
    ) {
        log.debug("REST request to get all Transactions for wallet: {}", walletId);
        
        // Verificar se a carteira existe
        Optional<Wallet> walletOpt = walletRepository.findById(walletId);
        if (walletOpt.isEmpty()) {
            throw new BadRequestAlertException("Wallet not found", ENTITY_NAME, "walletnotfound");
        }
        
        Wallet wallet = walletOpt.get();
        
        // Verificar se o usuário atual tem acesso à carteira
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (currentUserLogin == null) {
            throw new BadRequestAlertException("Current user login not found", ENTITY_NAME, "usernotfound");
        }
        
        Optional<AppUser> currentAppUser = appUserRepository.findByUsername(currentUserLogin);
        if (currentAppUser.isEmpty()) {
            throw new BadRequestAlertException("Current app user not found", ENTITY_NAME, "appusernotfound");
        }
        
        AppUser currentUser = currentAppUser.get();
        
        // Verificar se o usuário é o proprietário ou tem acesso compartilhado
        boolean isOwner = wallet.getOwner().getId().equals(currentUser.getId());
        boolean hasSharedAccess = false;
        
        if (!isOwner) {
            hasSharedAccess = walletShareRepository.existsByWalletAndSharedWith(wallet, currentUser);
            
            if (!hasSharedAccess) {
                throw new BadRequestAlertException("You don't have permission to view transactions in this wallet", ENTITY_NAME, "nopermission");
            }
        }
        
        // Filtrar transações com base nos parâmetros fornecidos
        if (category != null && !category.isEmpty()) {
            return transactionRepository.findByWalletAndCategoryOrderByTransactionDateDesc(wallet, category);
        } else if (type != null && startDate != null && endDate != null) {
            return transactionRepository.findByWalletAndTypeAndTransactionDateBetweenOrderByTransactionDateDesc(
                wallet, type, startDate, endDate
            );
        } else if (startDate != null && endDate != null) {
            return transactionRepository.findByWalletAndTransactionDateBetweenOrderByTransactionDateDesc(
                wallet, startDate, endDate
            );
        } else if (type != null) {
            return transactionRepository.findByWalletAndTypeOrderByTransactionDateDesc(wallet, type);
        } else {
            return transactionRepository.findByWalletOrderByTransactionDateDesc(wallet);
        }
    }

    /**
     * {@code GET  /transactions/:id} : get the "id" transaction.
     *
     * @param id the id of the transaction to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transaction, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/transactions/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long id) {
        log.debug("REST request to get Transaction : {}", id);
        
        Optional<Transaction> transactionOpt = transactionRepository.findById(id);
        
        if (transactionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Transaction transaction = transactionOpt.get();
        Wallet wallet = transaction.getWallet();
        
        // Verificar se o usuário atual tem acesso à carteira
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (currentUserLogin == null) {
            throw new BadRequestAlertException("Current user login not found", ENTITY_NAME, "usernotfound");
        }
        
        Optional<AppUser> currentAppUser = appUserRepository.findByUsername(currentUserLogin);
        if (currentAppUser.isEmpty()) {
            throw new BadRequestAlertException("Current app user not found", ENTITY_NAME, "appusernotfound");
        }
        
        AppUser currentUser = currentAppUser.get();
        
        // Verificar se o usuário é o proprietário ou tem acesso compartilhado
        boolean isOwner = wallet.getOwner().getId().equals(currentUser.getId());
        boolean hasSharedAccess = false;
        
        if (!isOwner) {
            hasSharedAccess = walletShareRepository.existsByWalletAndSharedWith(wallet, currentUser);
            
            if (!hasSharedAccess) {
                throw new BadRequestAlertException("You don't have permission to view this transaction", ENTITY_NAME, "nopermission");
            }
        }
        
        return ResponseUtil.wrapOrNotFound(transactionOpt);
    }

    /**
     * {@code DELETE  /transactions/:id} : delete the "id" transaction.
     *
     * @param id the id of the transaction to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        log.debug("REST request to delete Transaction : {}", id);
        
        // Obter a transação
        Optional<Transaction> transactionOpt = transactionRepository.findById(id);
        if (transactionOpt.isEmpty()) {
            throw new BadRequestAlertException("Transaction not found", ENTITY_NAME, "transactionnotfound");
        }
        
        Transaction transaction = transactionOpt.get();
        Wallet wallet = transaction.getWallet();
        
        // Verificar se o usuário atual tem acesso à carteira
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (currentUserLogin == null) {
            throw new BadRequestAlertException("Current user login not found", ENTITY_NAME, "usernotfound");
        }
        
        Optional<AppUser> currentAppUser = appUserRepository.findByUsername(currentUserLogin);
        if (currentAppUser.isEmpty()) {
            throw new BadRequestAlertException("Current app user not found", ENTITY_NAME, "appusernotfound");
        }
        
        AppUser currentUser = currentAppUser.get();
        
        // Verificar se o usuário é o proprietário ou tem acesso compartilhado
        boolean isOwner = wallet.getOwner().getId().equals(currentUser.getId());
        boolean hasSharedAccess = false;
        
        if (!isOwner) {
            hasSharedAccess = walletShareRepository.existsByWalletAndSharedWith(wallet, currentUser);
            
            if (!hasSharedAccess) {
                throw new BadRequestAlertException("You don't have permission to delete transactions in this wallet", ENTITY_NAME, "nopermission");
            }
        }
        
        // Reverter o efeito da transação no saldo da carteira
        double currentBalance = wallet.getBalance();
        if (transaction.getType() == TransactionType.INCOME) {
            currentBalance -= transaction.getAmount();
        } else if (transaction.getType() == TransactionType.EXPENSE) {
            currentBalance += transaction.getAmount();
        }
        
        // Atualizar o saldo da carteira
        wallet.setBalance(currentBalance);
        walletRepository.save(wallet);
        
        // Excluir a transação
        transactionRepository.deleteById(id);
        
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
