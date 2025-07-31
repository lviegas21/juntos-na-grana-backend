package com.noxius.juntosnagrana.web.rest;

import com.noxius.juntosnagrana.domain.Goal;
import com.noxius.juntosnagrana.domain.Family;
import com.noxius.juntosnagrana.repository.GoalRepository;
import com.noxius.juntosnagrana.repository.FamilyRepository;
import com.noxius.juntosnagrana.repository.AppUserRepository;
import com.noxius.juntosnagrana.security.SecurityUtils;
import com.noxius.juntosnagrana.service.dto.GoalDTO;
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
 * REST controller for managing {@link com.noxius.juntosnagrana.domain.Goal}.
 */
@RestController
@RequestMapping("/api/goals")
@Transactional
public class GoalResource {

    private static final Logger LOG = LoggerFactory.getLogger(GoalResource.class);

    private static final String ENTITY_NAME = "goal";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GoalRepository goalRepository;
    private final AppUserRepository appUserRepository;
    private final FamilyRepository familyRepository;

    public GoalResource(GoalRepository goalRepository, AppUserRepository appUserRepository, FamilyRepository familyRepository) {
        this.goalRepository = goalRepository;
        this.appUserRepository = appUserRepository;
        this.familyRepository = familyRepository;
    }

    /**
     * {@code POST  /goals} : Create a new goal.
     *
     * @param goalDTO o DTO da meta a ser criada.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new goal, or with status {@code 400 (Bad Request)} if the goal has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Goal> createGoal(@Valid @RequestBody GoalDTO goalDTO) throws URISyntaxException {
        LOG.debug("REST request to save Goal : {}", goalDTO);
        if (goalDTO.getId() != null) {
            throw new BadRequestAlertException("A new goal cannot already have an ID", ENTITY_NAME, "idexists");
        }
        
        // Obter o usuário atual
        String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> 
            new BadRequestAlertException("Usuário não autenticado", ENTITY_NAME, "usernotfound"));
        
        // Converter DTO para entidade
        Goal goal = goalDTO.toEntity();
        
        // Definir o usuário atual como proprietário da meta
        appUserRepository.findByUsername(userLogin).ifPresent(goal::setUser);
        
        // Associar família se o ID foi fornecido
        if (goalDTO.getFamilyId() != null) {
            familyRepository.findById(goalDTO.getFamilyId()).ifPresent(goal::setFamily);
        }
        
        goal = goalRepository.save(goal);
        return ResponseEntity.created(new URI("/api/goals/" + goal.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, goal.getId().toString()))
            .body(goal);
    }

    /**
     * {@code PUT  /goals/:id} : Updates an existing goal.
     *
     * @param id the id of the goal to save.
     * @param goalDTO the goal DTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated goal,
     * or with status {@code 400 (Bad Request)} if the goal is not valid,
     * or with status {@code 500 (Internal Server Error)} if the goal couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Goal> updateGoal(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody GoalDTO goalDTO)
        throws URISyntaxException {
        LOG.debug("REST request to update Goal : {}, {}", id, goalDTO);
        if (goalDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, goalDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!goalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        
        // Buscar a meta existente para manter o usuário associado
        Goal existingGoal = goalRepository.findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
        
        // Converter DTO para entidade
        Goal goal = goalDTO.toEntity();
        
        // Manter o usuário original
        goal.setUser(existingGoal.getUser());
        
        // Associar família se o ID foi fornecido, caso contrário manter a família original
        if (goalDTO.getFamilyId() != null) {
            familyRepository.findById(goalDTO.getFamilyId()).ifPresent(goal::setFamily);
        } else if (existingGoal.getFamily() != null) {
            goal.setFamily(existingGoal.getFamily());
        }
        
        goal = goalRepository.save(goal);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, goal.getId().toString()))
            .body(goal);
    }

    /**
     * {@code PATCH  /goals/:id} : Partial updates given fields of an existing goal, field will ignore if it is null
     *
     * @param id the id of the goal to save.
     * @param goalDTO the goal DTO with fields to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated goal,
     * or with status {@code 400 (Bad Request)} if the goal is not valid,
     * or with status {@code 404 (Not Found)} if the goal is not found,
     * or with status {@code 500 (Internal Server Error)} if the goal couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Goal> partialUpdateGoal(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody GoalDTO goalDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Goal partially : {}, {}", id, goalDTO);
        if (goalDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, goalDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!goalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Goal> result = goalRepository
            .findById(goalDTO.getId())
            .map(existingGoal -> {
                if (goalDTO.getTitle() != null) {
                    existingGoal.setTitle(goalDTO.getTitle());
                }
                if (goalDTO.getDescription() != null) {
                    existingGoal.setDescription(goalDTO.getDescription());
                }
                if (goalDTO.getTargetAmount() != null) {
                    existingGoal.setTargetAmount(goalDTO.getTargetAmount());
                }
                if (goalDTO.getCurrentAmount() != null) {
                    existingGoal.setCurrentAmount(goalDTO.getCurrentAmount());
                }
                if (goalDTO.getCreatedAt() != null) {
                    existingGoal.setCreatedAt(goalDTO.getCreatedAt());
                }
                if (goalDTO.getDueDate() != null) {
                    existingGoal.setDueDate(goalDTO.getDueDate());
                }
                if (goalDTO.getCategory() != null) {
                    existingGoal.setCategory(goalDTO.getCategory());
                }
                if (goalDTO.getPriority() != null) {
                    existingGoal.setPriority(goalDTO.getPriority());
                }
                if (goalDTO.getAlertEnabled() != null) {
                    existingGoal.setAlertEnabled(goalDTO.getAlertEnabled());
                }
                if (goalDTO.getAlertThreshold() != null) {
                    existingGoal.setAlertThreshold(goalDTO.getAlertThreshold());
                }
                
                // Atualizar família se o ID foi fornecido
                if (goalDTO.getFamilyId() != null) {
                    familyRepository.findById(goalDTO.getFamilyId()).ifPresent(existingGoal::setFamily);
                }

                return existingGoal;
            })
            .map(goalRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, goalDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /goals} : get all the goals.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of goals in body.
     */
    @GetMapping("")
    public List<Goal> getAllGoals() {
        LOG.debug("REST request to get all Goals for current user");
        String username = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> 
            new BadRequestAlertException("Usuário não autenticado", ENTITY_NAME, "usernotfound"));
        return goalRepository.findByUserUsername(username);
    }

    /**
     * {@code GET  /goals/:id} : get the "id" goal.
     *
     * @param id the id of the goal to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the goal, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Goal> getGoal(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Goal : {}", id);
        Optional<Goal> goal = goalRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(goal);
    }

    /**
     * {@code DELETE  /goals/:id} : delete the "id" goal.
     *
     * @param id the id of the goal to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Goal : {}", id);
        goalRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
