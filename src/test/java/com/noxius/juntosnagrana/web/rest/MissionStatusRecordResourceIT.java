package com.noxius.juntosnagrana.web.rest;

import static com.noxius.juntosnagrana.domain.MissionStatusRecordAsserts.*;
import static com.noxius.juntosnagrana.web.rest.TestUtil.createUpdateProxyForBean;
import static com.noxius.juntosnagrana.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noxius.juntosnagrana.IntegrationTest;
import com.noxius.juntosnagrana.domain.DailyMission;
import com.noxius.juntosnagrana.domain.MissionStatusRecord;
import com.noxius.juntosnagrana.domain.enumeration.MissionStatusType;
import com.noxius.juntosnagrana.repository.MissionStatusRecordRepository;
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
 * Integration tests for the {@link MissionStatusRecordResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MissionStatusRecordResourceIT {

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final MissionStatusType DEFAULT_STATUS_TYPE = MissionStatusType.PENDING;
    private static final MissionStatusType UPDATED_STATUS_TYPE = MissionStatusType.COMPLETED;

    private static final String ENTITY_API_URL = "/api/mission-status-records";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MissionStatusRecordRepository missionStatusRecordRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMissionStatusRecordMockMvc;

    private MissionStatusRecord missionStatusRecord;

    private MissionStatusRecord insertedMissionStatusRecord;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MissionStatusRecord createEntity(EntityManager em) {
        MissionStatusRecord missionStatusRecord = new MissionStatusRecord().date(DEFAULT_DATE).statusType(DEFAULT_STATUS_TYPE);
        // Add required entity
        DailyMission dailyMission;
        if (TestUtil.findAll(em, DailyMission.class).isEmpty()) {
            dailyMission = DailyMissionResourceIT.createEntity(em);
            em.persist(dailyMission);
            em.flush();
        } else {
            dailyMission = TestUtil.findAll(em, DailyMission.class).get(0);
        }
        missionStatusRecord.setMission(dailyMission);
        return missionStatusRecord;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MissionStatusRecord createUpdatedEntity(EntityManager em) {
        MissionStatusRecord updatedMissionStatusRecord = new MissionStatusRecord().date(UPDATED_DATE).statusType(UPDATED_STATUS_TYPE);
        // Add required entity
        DailyMission dailyMission;
        if (TestUtil.findAll(em, DailyMission.class).isEmpty()) {
            dailyMission = DailyMissionResourceIT.createUpdatedEntity(em);
            em.persist(dailyMission);
            em.flush();
        } else {
            dailyMission = TestUtil.findAll(em, DailyMission.class).get(0);
        }
        updatedMissionStatusRecord.setMission(dailyMission);
        return updatedMissionStatusRecord;
    }

    @BeforeEach
    void initTest() {
        missionStatusRecord = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedMissionStatusRecord != null) {
            missionStatusRecordRepository.delete(insertedMissionStatusRecord);
            insertedMissionStatusRecord = null;
        }
    }

    @Test
    @Transactional
    void createMissionStatusRecord() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MissionStatusRecord
        var returnedMissionStatusRecord = om.readValue(
            restMissionStatusRecordMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(missionStatusRecord)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MissionStatusRecord.class
        );

        // Validate the MissionStatusRecord in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertMissionStatusRecordUpdatableFieldsEquals(
            returnedMissionStatusRecord,
            getPersistedMissionStatusRecord(returnedMissionStatusRecord)
        );

        insertedMissionStatusRecord = returnedMissionStatusRecord;
    }

    @Test
    @Transactional
    void createMissionStatusRecordWithExistingId() throws Exception {
        // Create the MissionStatusRecord with an existing ID
        missionStatusRecord.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMissionStatusRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(missionStatusRecord)))
            .andExpect(status().isBadRequest());

        // Validate the MissionStatusRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        missionStatusRecord.setDate(null);

        // Create the MissionStatusRecord, which fails.

        restMissionStatusRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(missionStatusRecord)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        missionStatusRecord.setStatusType(null);

        // Create the MissionStatusRecord, which fails.

        restMissionStatusRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(missionStatusRecord)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMissionStatusRecords() throws Exception {
        // Initialize the database
        insertedMissionStatusRecord = missionStatusRecordRepository.saveAndFlush(missionStatusRecord);

        // Get all the missionStatusRecordList
        restMissionStatusRecordMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(missionStatusRecord.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].statusType").value(hasItem(DEFAULT_STATUS_TYPE.toString())));
    }

    @Test
    @Transactional
    void getMissionStatusRecord() throws Exception {
        // Initialize the database
        insertedMissionStatusRecord = missionStatusRecordRepository.saveAndFlush(missionStatusRecord);

        // Get the missionStatusRecord
        restMissionStatusRecordMockMvc
            .perform(get(ENTITY_API_URL_ID, missionStatusRecord.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(missionStatusRecord.getId().intValue()))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)))
            .andExpect(jsonPath("$.statusType").value(DEFAULT_STATUS_TYPE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingMissionStatusRecord() throws Exception {
        // Get the missionStatusRecord
        restMissionStatusRecordMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMissionStatusRecord() throws Exception {
        // Initialize the database
        insertedMissionStatusRecord = missionStatusRecordRepository.saveAndFlush(missionStatusRecord);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the missionStatusRecord
        MissionStatusRecord updatedMissionStatusRecord = missionStatusRecordRepository.findById(missionStatusRecord.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMissionStatusRecord are not directly saved in db
        em.detach(updatedMissionStatusRecord);
        updatedMissionStatusRecord.date(UPDATED_DATE).statusType(UPDATED_STATUS_TYPE);

        restMissionStatusRecordMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMissionStatusRecord.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedMissionStatusRecord))
            )
            .andExpect(status().isOk());

        // Validate the MissionStatusRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMissionStatusRecordToMatchAllProperties(updatedMissionStatusRecord);
    }

    @Test
    @Transactional
    void putNonExistingMissionStatusRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        missionStatusRecord.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMissionStatusRecordMockMvc
            .perform(
                put(ENTITY_API_URL_ID, missionStatusRecord.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(missionStatusRecord))
            )
            .andExpect(status().isBadRequest());

        // Validate the MissionStatusRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMissionStatusRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        missionStatusRecord.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMissionStatusRecordMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(missionStatusRecord))
            )
            .andExpect(status().isBadRequest());

        // Validate the MissionStatusRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMissionStatusRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        missionStatusRecord.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMissionStatusRecordMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(missionStatusRecord)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MissionStatusRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMissionStatusRecordWithPatch() throws Exception {
        // Initialize the database
        insertedMissionStatusRecord = missionStatusRecordRepository.saveAndFlush(missionStatusRecord);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the missionStatusRecord using partial update
        MissionStatusRecord partialUpdatedMissionStatusRecord = new MissionStatusRecord();
        partialUpdatedMissionStatusRecord.setId(missionStatusRecord.getId());

        restMissionStatusRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMissionStatusRecord.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMissionStatusRecord))
            )
            .andExpect(status().isOk());

        // Validate the MissionStatusRecord in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMissionStatusRecordUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMissionStatusRecord, missionStatusRecord),
            getPersistedMissionStatusRecord(missionStatusRecord)
        );
    }

    @Test
    @Transactional
    void fullUpdateMissionStatusRecordWithPatch() throws Exception {
        // Initialize the database
        insertedMissionStatusRecord = missionStatusRecordRepository.saveAndFlush(missionStatusRecord);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the missionStatusRecord using partial update
        MissionStatusRecord partialUpdatedMissionStatusRecord = new MissionStatusRecord();
        partialUpdatedMissionStatusRecord.setId(missionStatusRecord.getId());

        partialUpdatedMissionStatusRecord.date(UPDATED_DATE).statusType(UPDATED_STATUS_TYPE);

        restMissionStatusRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMissionStatusRecord.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMissionStatusRecord))
            )
            .andExpect(status().isOk());

        // Validate the MissionStatusRecord in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMissionStatusRecordUpdatableFieldsEquals(
            partialUpdatedMissionStatusRecord,
            getPersistedMissionStatusRecord(partialUpdatedMissionStatusRecord)
        );
    }

    @Test
    @Transactional
    void patchNonExistingMissionStatusRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        missionStatusRecord.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMissionStatusRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, missionStatusRecord.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(missionStatusRecord))
            )
            .andExpect(status().isBadRequest());

        // Validate the MissionStatusRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMissionStatusRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        missionStatusRecord.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMissionStatusRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(missionStatusRecord))
            )
            .andExpect(status().isBadRequest());

        // Validate the MissionStatusRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMissionStatusRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        missionStatusRecord.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMissionStatusRecordMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(missionStatusRecord)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MissionStatusRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMissionStatusRecord() throws Exception {
        // Initialize the database
        insertedMissionStatusRecord = missionStatusRecordRepository.saveAndFlush(missionStatusRecord);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the missionStatusRecord
        restMissionStatusRecordMockMvc
            .perform(delete(ENTITY_API_URL_ID, missionStatusRecord.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return missionStatusRecordRepository.count();
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

    protected MissionStatusRecord getPersistedMissionStatusRecord(MissionStatusRecord missionStatusRecord) {
        return missionStatusRecordRepository.findById(missionStatusRecord.getId()).orElseThrow();
    }

    protected void assertPersistedMissionStatusRecordToMatchAllProperties(MissionStatusRecord expectedMissionStatusRecord) {
        assertMissionStatusRecordAllPropertiesEquals(
            expectedMissionStatusRecord,
            getPersistedMissionStatusRecord(expectedMissionStatusRecord)
        );
    }

    protected void assertPersistedMissionStatusRecordToMatchUpdatableProperties(MissionStatusRecord expectedMissionStatusRecord) {
        assertMissionStatusRecordAllUpdatablePropertiesEquals(
            expectedMissionStatusRecord,
            getPersistedMissionStatusRecord(expectedMissionStatusRecord)
        );
    }
}
