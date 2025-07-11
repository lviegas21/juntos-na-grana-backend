package com.noxius.juntosnagrana.web.rest;

import com.noxius.juntosnagrana.domain.DailyMission;
import com.noxius.juntosnagrana.repository.DailyMissionRepository;
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
 * REST controller for managing {@link com.noxius.juntosnagrana.domain.DailyMission}.
 */
@RestController
@RequestMapping("/api/daily-missions")
@Transactional
public class DailyMissionResource {

    private static final Logger LOG = LoggerFactory.getLogger(DailyMissionResource.class);

    private static final String ENTITY_NAME = "dailyMission";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DailyMissionRepository dailyMissionRepository;

    public DailyMissionResource(DailyMissionRepository dailyMissionRepository) {
        this.dailyMissionRepository = dailyMissionRepository;
    }

    /**
     * {@code POST  /daily-missions} : Create a new dailyMission.
     *
     * @param dailyMission the dailyMission to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dailyMission, or with status {@code 400 (Bad Request)} if the dailyMission has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DailyMission> createDailyMission(@Valid @RequestBody DailyMission dailyMission) throws URISyntaxException {
        LOG.debug("REST request to save DailyMission : {}", dailyMission);
        if (dailyMission.getId() != null) {
            throw new BadRequestAlertException("A new dailyMission cannot already have an ID", ENTITY_NAME, "idexists");
        }
        dailyMission = dailyMissionRepository.save(dailyMission);
        return ResponseEntity.created(new URI("/api/daily-missions/" + dailyMission.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, dailyMission.getId().toString()))
            .body(dailyMission);
    }

    /**
     * {@code PUT  /daily-missions/:id} : Updates an existing dailyMission.
     *
     * @param id the id of the dailyMission to save.
     * @param dailyMission the dailyMission to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dailyMission,
     * or with status {@code 400 (Bad Request)} if the dailyMission is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dailyMission couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DailyMission> updateDailyMission(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DailyMission dailyMission
    ) throws URISyntaxException {
        LOG.debug("REST request to update DailyMission : {}, {}", id, dailyMission);
        if (dailyMission.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dailyMission.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dailyMissionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        dailyMission = dailyMissionRepository.save(dailyMission);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dailyMission.getId().toString()))
            .body(dailyMission);
    }

    /**
     * {@code PATCH  /daily-missions/:id} : Partial updates given fields of an existing dailyMission, field will ignore if it is null
     *
     * @param id the id of the dailyMission to save.
     * @param dailyMission the dailyMission to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dailyMission,
     * or with status {@code 400 (Bad Request)} if the dailyMission is not valid,
     * or with status {@code 404 (Not Found)} if the dailyMission is not found,
     * or with status {@code 500 (Internal Server Error)} if the dailyMission couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DailyMission> partialUpdateDailyMission(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DailyMission dailyMission
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DailyMission partially : {}, {}", id, dailyMission);
        if (dailyMission.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dailyMission.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dailyMissionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DailyMission> result = dailyMissionRepository
            .findById(dailyMission.getId())
            .map(existingDailyMission -> {
                if (dailyMission.getTitle() != null) {
                    existingDailyMission.setTitle(dailyMission.getTitle());
                }
                if (dailyMission.getDescription() != null) {
                    existingDailyMission.setDescription(dailyMission.getDescription());
                }
                if (dailyMission.getStartDate() != null) {
                    existingDailyMission.setStartDate(dailyMission.getStartDate());
                }
                if (dailyMission.getEndDate() != null) {
                    existingDailyMission.setEndDate(dailyMission.getEndDate());
                }
                if (dailyMission.getType() != null) {
                    existingDailyMission.setType(dailyMission.getType());
                }
                if (dailyMission.getTargetAmount() != null) {
                    existingDailyMission.setTargetAmount(dailyMission.getTargetAmount());
                }
                if (dailyMission.getCategory() != null) {
                    existingDailyMission.setCategory(dailyMission.getCategory());
                }
                if (dailyMission.getXpReward() != null) {
                    existingDailyMission.setXpReward(dailyMission.getXpReward());
                }
                if (dailyMission.getCreatedAt() != null) {
                    existingDailyMission.setCreatedAt(dailyMission.getCreatedAt());
                }

                return existingDailyMission;
            })
            .map(dailyMissionRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dailyMission.getId().toString())
        );
    }

    /**
     * {@code GET  /daily-missions} : get all the dailyMissions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dailyMissions in body.
     */
    @GetMapping("")
    public List<DailyMission> getAllDailyMissions() {
        LOG.debug("REST request to get all DailyMissions");
        return dailyMissionRepository.findAll();
    }

    /**
     * {@code GET  /daily-missions/:id} : get the "id" dailyMission.
     *
     * @param id the id of the dailyMission to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dailyMission, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DailyMission> getDailyMission(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DailyMission : {}", id);
        Optional<DailyMission> dailyMission = dailyMissionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(dailyMission);
    }

    /**
     * {@code DELETE  /daily-missions/:id} : delete the "id" dailyMission.
     *
     * @param id the id of the dailyMission to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDailyMission(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DailyMission : {}", id);
        dailyMissionRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
