package com.noxius.juntosnagrana.repository;

import com.noxius.juntosnagrana.domain.Transaction;
import com.noxius.juntosnagrana.domain.Wallet;
import com.noxius.juntosnagrana.domain.enumeration.TransactionType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for the Transaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    /**
     * Find all transactions by wallet.
     *
     * @param wallet the wallet
     * @return the list of transactions
     */
    List<Transaction> findByWalletOrderByTransactionDateDesc(Wallet wallet);
    
    /**
     * Find all transactions by wallet and type.
     *
     * @param wallet the wallet
     * @param type the transaction type
     * @return the list of transactions
     */
    List<Transaction> findByWalletAndTypeOrderByTransactionDateDesc(Wallet wallet, TransactionType type);
    
    /**
     * Find all transactions by wallet and date range.
     *
     * @param wallet the wallet
     * @param startDate the start date
     * @param endDate the end date
     * @return the list of transactions
     */
    List<Transaction> findByWalletAndTransactionDateBetweenOrderByTransactionDateDesc(
        Wallet wallet, 
        ZonedDateTime startDate, 
        ZonedDateTime endDate
    );
    
    /**
     * Find all transactions by wallet, type and date range.
     *
     * @param wallet the wallet
     * @param type the transaction type
     * @param startDate the start date
     * @param endDate the end date
     * @return the list of transactions
     */
    List<Transaction> findByWalletAndTypeAndTransactionDateBetweenOrderByTransactionDateDesc(
        Wallet wallet, 
        TransactionType type, 
        ZonedDateTime startDate, 
        ZonedDateTime endDate
    );
    
    /**
     * Find all transactions by wallet and category.
     *
     * @param wallet the wallet
     * @param category the category
     * @return the list of transactions
     */
    List<Transaction> findByWalletAndCategoryOrderByTransactionDateDesc(Wallet wallet, String category);
}
