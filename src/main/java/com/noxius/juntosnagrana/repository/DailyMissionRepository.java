package com.noxius.juntosnagrana.repository;

import com.noxius.juntosnagrana.domain.DailyMission;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DailyMission entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DailyMissionRepository extends JpaRepository<DailyMission, Long> {}
