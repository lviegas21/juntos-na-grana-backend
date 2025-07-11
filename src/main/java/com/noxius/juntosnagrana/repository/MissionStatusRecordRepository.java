package com.noxius.juntosnagrana.repository;

import com.noxius.juntosnagrana.domain.MissionStatusRecord;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MissionStatusRecord entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MissionStatusRecordRepository extends JpaRepository<MissionStatusRecord, Long> {}
