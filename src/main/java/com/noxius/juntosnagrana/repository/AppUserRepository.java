package com.noxius.juntosnagrana.repository;

import com.noxius.juntosnagrana.domain.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AppUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    /**
     * Find an AppUser by username.
     *
     * @param username the username to search for
     * @return the AppUser if found
     */
    Optional<AppUser> findByUsername(String username);
}
