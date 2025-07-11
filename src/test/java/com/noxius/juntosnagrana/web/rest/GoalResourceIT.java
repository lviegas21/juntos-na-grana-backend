package com.noxius.juntosnagrana.web.rest;

import static com.noxius.juntosnagrana.domain.GoalAsserts.*;
import static com.noxius.juntosnagrana.web.rest.TestUtil.createUpdateProxyForBean;
import static com.noxius.juntosnagrana.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noxius.juntosnagrana.IntegrationTest;
import com.noxius.juntosnagrana.domain.Family;
import com.noxius.juntosnagrana.domain.Goal;
import com.noxius.juntosnagrana.domain.enumeration.GoalCategory;
import com.noxius.juntosnagrana.domain.enumeration.GoalPriority;
import com.noxius.juntosnagrana.repository.GoalRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link GoalResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GoalResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Double DEFAULT_TARGET_AMOUNT = 1D;
    private static final Double UPDATED_TARGET_AMOUNT = 2D;

    private static final Double DEFAULT_CURRENT_AMOUNT = 1D;
    private static final Double UPDATED_CURRENT_AMOUNT = 2D;

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_DUE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DUE_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final GoalCategory DEFAULT_CATEGORY = GoalCategory.ADVENTURE;
    private static final GoalCategory UPDATED_CATEGORY = GoalCategory.SHIELD;

    private static final GoalPriority DEFAULT_PRIORITY = GoalPriority.LOW;
    private static final GoalPriority UPDATED_PRIORITY = GoalPriority.MEDIUM;

    private static final Boolean DEFAULT_ALERT_ENABLED = false;
    private static final Boolean UPDATED_ALERT_ENABLED = true;

    private static final Integer DEFAULT_ALERT_THRESHOLD = 1;
    private static final Integer UPDATED_ALERT_THRESHOLD = 2;

    private static final String ENTITY_API_URL = "/api/goals";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGoalMockMvc;

    private Goal goal;

    private Goal insertedGoal;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Goal createEntity(EntityManager em) {
        Goal goal = new Goal()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .targetAmount(DEFAULT_TARGET_AMOUNT)
            .currentAmount(DEFAULT_CURRENT_AMOUNT)
            .createdAt(DEFAULT_CREATED_AT)
            .dueDate(DEFAULT_DUE_DATE)
            .category(DEFAULT_CATEGORY)
            .priority(DEFAULT_PRIORITY)
            .alertEnabled(DEFAULT_ALERT_ENABLED)
            .alertThreshold(DEFAULT_ALERT_THRESHOLD);
        // Add required entity
        Family family;
        if (TestUtil.findAll(em, Family.class).isEmpty()) {
            family = FamilyResourceIT.createEntity();
            em.persist(family);
            em.flush();
        } else {
            family = TestUtil.findAll(em, Family.class).get(0);
        }
        goal.setFamily(family);
        return goal;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Goal createUpdatedEntity(EntityManager em) {
        Goal updatedGoal = new Goal()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .targetAmount(UPDATED_TARGET_AMOUNT)
            .currentAmount(UPDATED_CURRENT_AMOUNT)
            .createdAt(UPDATED_CREATED_AT)
            .dueDate(UPDATED_DUE_DATE)
            .category(UPDATED_CATEGORY)
            .priority(UPDATED_PRIORITY)
            .alertEnabled(UPDATED_ALERT_ENABLED)
            .alertThreshold(UPDATED_ALERT_THRESHOLD);
        // Add required entity
        Family family;
        if (TestUtil.findAll(em, Family.class).isEmpty()) {
            family = FamilyResourceIT.createUpdatedEntity();
            em.persist(family);
            em.flush();
        } else {
            family = TestUtil.findAll(em, Family.class).get(0);
        }
        updatedGoal.setFamily(family);
        return updatedGoal;
    }

    @BeforeEach
    void initTest() {
        goal = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedGoal != null) {
            goalRepository.delete(insertedGoal);
            insertedGoal = null;
        }
    }

    @Test
    @Transactional
    void createGoal() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Goal
        var returnedGoal = om.readValue(
            restGoalMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(goal)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Goal.class
        );

        // Validate the Goal in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertGoalUpdatableFieldsEquals(returnedGoal, getPersistedGoal(returnedGoal));

        insertedGoal = returnedGoal;
    }

    @Test
    @Transactional
    void createGoalWithExistingId() throws Exception {
        // Create the Goal with an existing ID
        goal.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGoalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(goal)))
            .andExpect(status().isBadRequest());

        // Validate the Goal in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        goal.setTitle(null);

        // Create the Goal, which fails.

        restGoalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(goal)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTargetAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        goal.setTargetAmount(null);

        // Create the Goal, which fails.

        restGoalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(goal)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCurrentAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        goal.setCurrentAmount(null);

        // Create the Goal, which fails.

        restGoalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(goal)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        goal.setCreatedAt(null);

        // Create the Goal, which fails.

        restGoalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(goal)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCategoryIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        goal.setCategory(null);

        // Create the Goal, which fails.

        restGoalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(goal)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriorityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        goal.setPriority(null);

        // Create the Goal, which fails.

        restGoalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(goal)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAlertEnabledIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        goal.setAlertEnabled(null);

        // Create the Goal, which fails.

        restGoalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(goal)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAlertThresholdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        goal.setAlertThreshold(null);

        // Create the Goal, which fails.

        restGoalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(goal)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllGoals() throws Exception {
        // Initialize the database
        insertedGoal = goalRepository.saveAndFlush(goal);

        // Get all the goalList
        restGoalMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(goal.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].targetAmount").value(hasItem(DEFAULT_TARGET_AMOUNT)))
            .andExpect(jsonPath("$.[*].currentAmount").value(hasItem(DEFAULT_CURRENT_AMOUNT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(sameInstant(DEFAULT_DUE_DATE))))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.toString())))
            .andExpect(jsonPath("$.[*].alertEnabled").value(hasItem(DEFAULT_ALERT_ENABLED)))
            .andExpect(jsonPath("$.[*].alertThreshold").value(hasItem(DEFAULT_ALERT_THRESHOLD)));
    }

    @Test
    @Transactional
    void getGoal() throws Exception {
        // Initialize the database
        insertedGoal = goalRepository.saveAndFlush(goal);

        // Get the goal
        restGoalMockMvc
            .perform(get(ENTITY_API_URL_ID, goal.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(goal.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.targetAmount").value(DEFAULT_TARGET_AMOUNT))
            .andExpect(jsonPath("$.currentAmount").value(DEFAULT_CURRENT_AMOUNT))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.dueDate").value(sameInstant(DEFAULT_DUE_DATE)))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()))
            .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY.toString()))
            .andExpect(jsonPath("$.alertEnabled").value(DEFAULT_ALERT_ENABLED))
            .andExpect(jsonPath("$.alertThreshold").value(DEFAULT_ALERT_THRESHOLD));
    }

    @Test
    @Transactional
    void getNonExistingGoal() throws Exception {
        // Get the goal
        restGoalMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingGoal() throws Exception {
        // Initialize the database
        insertedGoal = goalRepository.saveAndFlush(goal);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the goal
        Goal updatedGoal = goalRepository.findById(goal.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedGoal are not directly saved in db
        em.detach(updatedGoal);
        updatedGoal
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .targetAmount(UPDATED_TARGET_AMOUNT)
            .currentAmount(UPDATED_CURRENT_AMOUNT)
            .createdAt(UPDATED_CREATED_AT)
            .dueDate(UPDATED_DUE_DATE)
            .category(UPDATED_CATEGORY)
            .priority(UPDATED_PRIORITY)
            .alertEnabled(UPDATED_ALERT_ENABLED)
            .alertThreshold(UPDATED_ALERT_THRESHOLD);

        restGoalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedGoal.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedGoal))
            )
            .andExpect(status().isOk());

        // Validate the Goal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedGoalToMatchAllProperties(updatedGoal);
    }

    @Test
    @Transactional
    void putNonExistingGoal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        goal.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGoalMockMvc
            .perform(put(ENTITY_API_URL_ID, goal.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(goal)))
            .andExpect(status().isBadRequest());

        // Validate the Goal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGoal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        goal.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGoalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(goal))
            )
            .andExpect(status().isBadRequest());

        // Validate the Goal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGoal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        goal.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGoalMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(goal)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Goal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGoalWithPatch() throws Exception {
        // Initialize the database
        insertedGoal = goalRepository.saveAndFlush(goal);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the goal using partial update
        Goal partialUpdatedGoal = new Goal();
        partialUpdatedGoal.setId(goal.getId());

        partialUpdatedGoal.targetAmount(UPDATED_TARGET_AMOUNT).priority(UPDATED_PRIORITY).alertEnabled(UPDATED_ALERT_ENABLED);

        restGoalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGoal.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGoal))
            )
            .andExpect(status().isOk());

        // Validate the Goal in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGoalUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedGoal, goal), getPersistedGoal(goal));
    }

    @Test
    @Transactional
    void fullUpdateGoalWithPatch() throws Exception {
        // Initialize the database
        insertedGoal = goalRepository.saveAndFlush(goal);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the goal using partial update
        Goal partialUpdatedGoal = new Goal();
        partialUpdatedGoal.setId(goal.getId());

        partialUpdatedGoal
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .targetAmount(UPDATED_TARGET_AMOUNT)
            .currentAmount(UPDATED_CURRENT_AMOUNT)
            .createdAt(UPDATED_CREATED_AT)
            .dueDate(UPDATED_DUE_DATE)
            .category(UPDATED_CATEGORY)
            .priority(UPDATED_PRIORITY)
            .alertEnabled(UPDATED_ALERT_ENABLED)
            .alertThreshold(UPDATED_ALERT_THRESHOLD);

        restGoalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGoal.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGoal))
            )
            .andExpect(status().isOk());

        // Validate the Goal in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGoalUpdatableFieldsEquals(partialUpdatedGoal, getPersistedGoal(partialUpdatedGoal));
    }

    @Test
    @Transactional
    void patchNonExistingGoal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        goal.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGoalMockMvc
            .perform(patch(ENTITY_API_URL_ID, goal.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(goal)))
            .andExpect(status().isBadRequest());

        // Validate the Goal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGoal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        goal.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGoalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(goal))
            )
            .andExpect(status().isBadRequest());

        // Validate the Goal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGoal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        goal.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGoalMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(goal)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Goal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGoal() throws Exception {
        // Initialize the database
        insertedGoal = goalRepository.saveAndFlush(goal);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the goal
        restGoalMockMvc
            .perform(delete(ENTITY_API_URL_ID, goal.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return goalRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Goal getPersistedGoal(Goal goal) {
        return goalRepository.findById(goal.getId()).orElseThrow();
    }

    protected void assertPersistedGoalToMatchAllProperties(Goal expectedGoal) {
        assertGoalAllPropertiesEquals(expectedGoal, getPersistedGoal(expectedGoal));
    }

    protected void assertPersistedGoalToMatchUpdatableProperties(Goal expectedGoal) {
        assertGoalAllUpdatablePropertiesEquals(expectedGoal, getPersistedGoal(expectedGoal));
    }
}
