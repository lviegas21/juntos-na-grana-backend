package com.noxius.juntosnagrana.repository;

import com.noxius.juntosnagrana.domain.Goal;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Goal entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUserUsername(String username);
}
