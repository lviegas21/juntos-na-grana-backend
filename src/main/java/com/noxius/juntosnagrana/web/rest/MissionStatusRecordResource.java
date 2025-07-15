package com.noxius.juntosnagrana.web.rest;

import com.noxius.juntosnagrana.domain.MissionStatusRecord;
import com.noxius.juntosnagrana.repository.MissionStatusRecordRepository;
import com.noxius.juntosnagrana.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
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
 * REST controller for managing {@link com.noxius.juntosnagrana.domain.MissionStatusRecord}.
 */
@RestController
@RequestMapping("/api/mission-status-records")
@Transactional
public class MissionStatusRecordResource {

    private static final Logger LOG = LoggerFactory.getLogger(MissionStatusRecordResource.class);

    private static final String ENTITY_NAME = "missionStatusRecord";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MissionStatusRecordRepository missionStatusRecordRepository;

    public MissionStatusRecordResource(MissionStatusRecordRepository missionStatusRecordRepository) {
        this.missionStatusRecordRepository = missionStatusRecordRepository;
    }

    /**
     * {@code POST  /mission-status-records} : Create a new missionStatusRecord.
     *
     * @param missionStatusRecord the missionStatusRecord to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new missionStatusRecord, or with status {@code 400 (Bad Request)} if the missionStatusRecord has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MissionStatusRecord> createMissionStatusRecord(@Valid @RequestBody MissionStatusRecord missionStatusRecord)
        throws URISyntaxException {
        LOG.debug("REST request to save MissionStatusRecord : {}", missionStatusRecord);
        if (missionStatusRecord.getId() != null) {
            throw new BadRequestAlertException("A new missionStatusRecord cannot already have an ID", ENTITY_NAME, "idexists");
        }
        missionStatusRecord = missionStatusRecordRepository.save(missionStatusRecord);
        return ResponseEntity.created(new URI("/api/mission-status-records/" + missionStatusRecord.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, missionStatusRecord.getId().toString()))
            .body(missionStatusRecord);
    }

    /**
     * {@code PUT  /mission-status-records/:id} : Updates an existing missionStatusRecord.
     *
     * @param id the id of the missionStatusRecord to save.
     * @param missionStatusRecord the missionStatusRecord to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated missionStatusRecord,
     * or with status {@code 400 (Bad Request)} if the missionStatusRecord is not valid,
     * or with status {@code 500 (Internal Server Error)} if the missionStatusRecord couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MissionStatusRecord> updateMissionStatusRecord(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MissionStatusRecord missionStatusRecord
    ) throws URISyntaxException {
        LOG.debug("REST request to update MissionStatusRecord : {}, {}", id, missionStatusRecord);
        if (missionStatusRecord.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, missionStatusRecord.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!missionStatusRecordRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        missionStatusRecord = missionStatusRecordRepository.save(missionStatusRecord);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, missionStatusRecord.getId().toString()))
            .body(missionStatusRecord);
    }

    /**
     * {@code PATCH  /mission-status-records/:id} : Partial updates given fields of an existing missionStatusRecord, field will ignore if it is null
     *
     * @param id the id of the missionStatusRecord to save.
     * @param missionStatusRecord the missionStatusRecord to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated missionStatusRecord,
     * or with status {@code 400 (Bad Request)} if the missionStatusRecord is not valid,
     * or with status {@code 404 (Not Found)} if the missionStatusRecord is not found,
     * or with status {@code 500 (Internal Server Error)} if the missionStatusRecord couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MissionStatusRecord> partialUpdateMissionStatusRecord(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MissionStatusRecord missionStatusRecord
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MissionStatusRecord partially : {}, {}", id, missionStatusRecord);
        if (missionStatusRecord.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, missionStatusRecord.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!missionStatusRecordRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MissionStatusRecord> result = missionStatusRecordRepository
            .findById(missionStatusRecord.getId())
            .map(existingMissionStatusRecord -> {
                if (missionStatusRecord.getDate() != null) {
                    existingMissionStatusRecord.setDate(missionStatusRecord.getDate());
                }
                if (missionStatusRecord.getStatusType() != null) {
                    existingMissionStatusRecord.setStatusType(missionStatusRecord.getStatusType());
                }

                return existingMissionStatusRecord;
            })
            .map(missionStatusRecordRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, missionStatusRecord.getId().toString())
        );
    }

    /**
     * {@code GET  /mission-status-records} : get all the missionStatusRecords.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of missionStatusRecords in body.
     */
    @GetMapping("")
    public List<MissionStatusRecord> getAllMissionStatusRecords() {
        LOG.debug("REST request to get all MissionStatusRecords");
        return missionStatusRecordRepository.findAll();
    }

    /**
     * {@code GET  /mission-status-records/:id} : get the "id" missionStatusRecord.
     *
     * @param id the id of the missionStatusRecord to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the missionStatusRecord, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MissionStatusRecord> getMissionStatusRecord(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MissionStatusRecord : {}", id);
        Optional<MissionStatusRecord> missionStatusRecord = missionStatusRecordRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(missionStatusRecord);
    }

    /**
     * {@code DELETE  /mission-status-records/:id} : delete the "id" missionStatusRecord.
     *
     * @param id the id of the missionStatusRecord to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMissionStatusRecord(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MissionStatusRecord : {}", id);
        missionStatusRecordRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
