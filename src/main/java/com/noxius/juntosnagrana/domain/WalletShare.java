package com.noxius.juntosnagrana.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A WalletShare represents a sharing relationship between a wallet and a user.
 */
@Entity
@Table(name = "wallet_share")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class WalletShare implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "owner" }, allowSetters = true)
    private Wallet wallet;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "family" }, allowSetters = true)
    private AppUser sharedWith;

    public Long getId() {
        return this.id;
    }

    public WalletShare id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public WalletShare createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Wallet getWallet() {
        return this.wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public WalletShare wallet(Wallet wallet) {
        this.setWallet(wallet);
        return this;
    }

    public AppUser getSharedWith() {
        return this.sharedWith;
    }

    public void setSharedWith(AppUser appUser) {
        this.sharedWith = appUser;
    }

    public WalletShare sharedWith(AppUser appUser) {
        this.setSharedWith(appUser);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WalletShare)) {
            return false;
        }
        return getId() != null && getId().equals(((WalletShare) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "WalletShare{" +
            "id=" + getId() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", wallet=" + getWallet().getId() +
            ", sharedWith=" + getSharedWith().getUsername() +
            "}";
    }
}
