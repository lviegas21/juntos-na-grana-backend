package com.noxius.juntosnagrana.web.rest;

import static com.noxius.juntosnagrana.domain.DailyMissionAsserts.*;
import static com.noxius.juntosnagrana.web.rest.TestUtil.createUpdateProxyForBean;
import static com.noxius.juntosnagrana.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noxius.juntosnagrana.IntegrationTest;
import com.noxius.juntosnagrana.domain.DailyMission;
import com.noxius.juntosnagrana.domain.Family;
import com.noxius.juntosnagrana.domain.enumeration.DailyMissionType;
import com.noxius.juntosnagrana.domain.enumeration.GoalCategory;
import com.noxius.juntosnagrana.repository.DailyMissionRepository;
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
 * Integration tests for the {@link DailyMissionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DailyMissionResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_START_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_END_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final DailyMissionType DEFAULT_TYPE = DailyMissionType.SAVING;
    private static final DailyMissionType UPDATED_TYPE = DailyMissionType.RESTRICTION;

    private static final Double DEFAULT_TARGET_AMOUNT = 1D;
    private static final Double UPDATED_TARGET_AMOUNT = 2D;

    private static final GoalCategory DEFAULT_CATEGORY = GoalCategory.ADVENTURE;
    private static final GoalCategory UPDATED_CATEGORY = GoalCategory.SHIELD;

    private static final Integer DEFAULT_XP_REWARD = 1;
    private static final Integer UPDATED_XP_REWARD = 2;

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/daily-missions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DailyMissionRepository dailyMissionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDailyMissionMockMvc;

    private DailyMission dailyMission;

    private DailyMission insertedDailyMission;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DailyMission createEntity(EntityManager em) {
        DailyMission dailyMission = new DailyMission()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .type(DEFAULT_TYPE)
            .targetAmount(DEFAULT_TARGET_AMOUNT)
            .category(DEFAULT_CATEGORY)
            .xpReward(DEFAULT_XP_REWARD)
            .createdAt(DEFAULT_CREATED_AT);
        // Add required entity
        Family family;
        if (TestUtil.findAll(em, Family.class).isEmpty()) {
            family = FamilyResourceIT.createEntity();
            em.persist(family);
            em.flush();
        } else {
            family = TestUtil.findAll(em, Family.class).get(0);
        }
        dailyMission.setFamily(family);
        return dailyMission;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DailyMission createUpdatedEntity(EntityManager em) {
        DailyMission updatedDailyMission = new DailyMission()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .type(UPDATED_TYPE)
            .targetAmount(UPDATED_TARGET_AMOUNT)
            .category(UPDATED_CATEGORY)
            .xpReward(UPDATED_XP_REWARD)
            .createdAt(UPDATED_CREATED_AT);
        // Add required entity
        Family family;
        if (TestUtil.findAll(em, Family.class).isEmpty()) {
            family = FamilyResourceIT.createUpdatedEntity();
            em.persist(family);
            em.flush();
        } else {
            family = TestUtil.findAll(em, Family.class).get(0);
        }
        updatedDailyMission.setFamily(family);
        return updatedDailyMission;
    }

    @BeforeEach
    void initTest() {
        dailyMission = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedDailyMission != null) {
            dailyMissionRepository.delete(insertedDailyMission);
            insertedDailyMission = null;
        }
    }

    @Test
    @Transactional
    void createDailyMission() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DailyMission
        var returnedDailyMission = om.readValue(
            restDailyMissionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyMission)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DailyMission.class
        );

        // Validate the DailyMission in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertDailyMissionUpdatableFieldsEquals(returnedDailyMission, getPersistedDailyMission(returnedDailyMission));

        insertedDailyMission = returnedDailyMission;
    }

    @Test
    @Transactional
    void createDailyMissionWithExistingId() throws Exception {
        // Create the DailyMission with an existing ID
        dailyMission.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDailyMissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyMission)))
            .andExpect(status().isBadRequest());

        // Validate the DailyMission in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dailyMission.setTitle(null);

        // Create the DailyMission, which fails.

        restDailyMissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyMission)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dailyMission.setStartDate(null);

        // Create the DailyMission, which fails.

        restDailyMissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyMission)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dailyMission.setEndDate(null);

        // Create the DailyMission, which fails.

        restDailyMissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyMission)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dailyMission.setType(null);

        // Create the DailyMission, which fails.

        restDailyMissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyMission)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkXpRewardIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dailyMission.setXpReward(null);

        // Create the DailyMission, which fails.

        restDailyMissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyMission)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dailyMission.setCreatedAt(null);

        // Create the DailyMission, which fails.

        restDailyMissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyMission)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDailyMissions() throws Exception {
        // Initialize the database
        insertedDailyMission = dailyMissionRepository.saveAndFlush(dailyMission);

        // Get all the dailyMissionList
        restDailyMissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dailyMission.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(sameInstant(DEFAULT_START_DATE))))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(sameInstant(DEFAULT_END_DATE))))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].targetAmount").value(hasItem(DEFAULT_TARGET_AMOUNT)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].xpReward").value(hasItem(DEFAULT_XP_REWARD)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));
    }

    @Test
    @Transactional
    void getDailyMission() throws Exception {
        // Initialize the database
        insertedDailyMission = dailyMissionRepository.saveAndFlush(dailyMission);

        // Get the dailyMission
        restDailyMissionMockMvc
            .perform(get(ENTITY_API_URL_ID, dailyMission.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dailyMission.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.startDate").value(sameInstant(DEFAULT_START_DATE)))
            .andExpect(jsonPath("$.endDate").value(sameInstant(DEFAULT_END_DATE)))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.targetAmount").value(DEFAULT_TARGET_AMOUNT))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()))
            .andExpect(jsonPath("$.xpReward").value(DEFAULT_XP_REWARD))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)));
    }

    @Test
    @Transactional
    void getNonExistingDailyMission() throws Exception {
        // Get the dailyMission
        restDailyMissionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDailyMission() throws Exception {
        // Initialize the database
        insertedDailyMission = dailyMissionRepository.saveAndFlush(dailyMission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dailyMission
        DailyMission updatedDailyMission = dailyMissionRepository.findById(dailyMission.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDailyMission are not directly saved in db
        em.detach(updatedDailyMission);
        updatedDailyMission
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .type(UPDATED_TYPE)
            .targetAmount(UPDATED_TARGET_AMOUNT)
            .category(UPDATED_CATEGORY)
            .xpReward(UPDATED_XP_REWARD)
            .createdAt(UPDATED_CREATED_AT);

        restDailyMissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDailyMission.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedDailyMission))
            )
            .andExpect(status().isOk());

        // Validate the DailyMission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDailyMissionToMatchAllProperties(updatedDailyMission);
    }

    @Test
    @Transactional
    void putNonExistingDailyMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyMission.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDailyMissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dailyMission.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(dailyMission))
            )
            .andExpect(status().isBadRequest());

        // Validate the DailyMission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDailyMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyMission.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDailyMissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(dailyMission))
            )
            .andExpect(status().isBadRequest());

        // Validate the DailyMission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDailyMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyMission.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDailyMissionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyMission)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DailyMission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDailyMissionWithPatch() throws Exception {
        // Initialize the database
        insertedDailyMission = dailyMissionRepository.saveAndFlush(dailyMission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dailyMission using partial update
        DailyMission partialUpdatedDailyMission = new DailyMission();
        partialUpdatedDailyMission.setId(dailyMission.getId());

        partialUpdatedDailyMission.endDate(UPDATED_END_DATE).xpReward(UPDATED_XP_REWARD);

        restDailyMissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDailyMission.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDailyMission))
            )
            .andExpect(status().isOk());

        // Validate the DailyMission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDailyMissionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDailyMission, dailyMission),
            getPersistedDailyMission(dailyMission)
        );
    }

    @Test
    @Transactional
    void fullUpdateDailyMissionWithPatch() throws Exception {
        // Initialize the database
        insertedDailyMission = dailyMissionRepository.saveAndFlush(dailyMission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dailyMission using partial update
        DailyMission partialUpdatedDailyMission = new DailyMission();
        partialUpdatedDailyMission.setId(dailyMission.getId());

        partialUpdatedDailyMission
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .type(UPDATED_TYPE)
            .targetAmount(UPDATED_TARGET_AMOUNT)
            .category(UPDATED_CATEGORY)
            .xpReward(UPDATED_XP_REWARD)
            .createdAt(UPDATED_CREATED_AT);

        restDailyMissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDailyMission.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDailyMission))
            )
            .andExpect(status().isOk());

        // Validate the DailyMission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDailyMissionUpdatableFieldsEquals(partialUpdatedDailyMission, getPersistedDailyMission(partialUpdatedDailyMission));
    }

    @Test
    @Transactional
    void patchNonExistingDailyMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyMission.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDailyMissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dailyMission.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(dailyMission))
            )
            .andExpect(status().isBadRequest());

        // Validate the DailyMission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDailyMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyMission.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDailyMissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(dailyMission))
            )
            .andExpect(status().isBadRequest());

        // Validate the DailyMission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDailyMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyMission.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDailyMissionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(dailyMission)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DailyMission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDailyMission() throws Exception {
        // Initialize the database
        insertedDailyMission = dailyMissionRepository.saveAndFlush(dailyMission);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the dailyMission
        restDailyMissionMockMvc
            .perform(delete(ENTITY_API_URL_ID, dailyMission.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return dailyMissionRepository.count();
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

    protected DailyMission getPersistedDailyMission(DailyMission dailyMission) {
        return dailyMissionRepository.findById(dailyMission.getId()).orElseThrow();
    }

    protected void assertPersistedDailyMissionToMatchAllProperties(DailyMission expectedDailyMission) {
        assertDailyMissionAllPropertiesEquals(expectedDailyMission, getPersistedDailyMission(expectedDailyMission));
    }

    protected void assertPersistedDailyMissionToMatchUpdatableProperties(DailyMission expectedDailyMission) {
        assertDailyMissionAllUpdatablePropertiesEquals(expectedDailyMission, getPersistedDailyMission(expectedDailyMission));
    }
}
