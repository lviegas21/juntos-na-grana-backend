package com.noxius.juntosnagrana.web.rest;

import static com.noxius.juntosnagrana.domain.FamilyAsserts.*;
import static com.noxius.juntosnagrana.web.rest.TestUtil.createUpdateProxyForBean;
import static com.noxius.juntosnagrana.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noxius.juntosnagrana.IntegrationTest;
import com.noxius.juntosnagrana.domain.Family;
import com.noxius.juntosnagrana.repository.FamilyRepository;
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
 * Integration tests for the {@link FamilyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FamilyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/families";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFamilyMockMvc;

    private Family family;

    private Family insertedFamily;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Family createEntity() {
        return new Family().name(DEFAULT_NAME).createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Family createUpdatedEntity() {
        return new Family().name(UPDATED_NAME).createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        family = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFamily != null) {
            familyRepository.delete(insertedFamily);
            insertedFamily = null;
        }
    }

    @Test
    @Transactional
    void createFamily() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Family
        var returnedFamily = om.readValue(
            restFamilyMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(family)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Family.class
        );

        // Validate the Family in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertFamilyUpdatableFieldsEquals(returnedFamily, getPersistedFamily(returnedFamily));

        insertedFamily = returnedFamily;
    }

    @Test
    @Transactional
    void createFamilyWithExistingId() throws Exception {
        // Create the Family with an existing ID
        family.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFamilyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(family)))
            .andExpect(status().isBadRequest());

        // Validate the Family in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        family.setName(null);

        // Create the Family, which fails.

        restFamilyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(family)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        family.setCreatedAt(null);

        // Create the Family, which fails.

        restFamilyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(family)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFamilies() throws Exception {
        // Initialize the database
        insertedFamily = familyRepository.saveAndFlush(family);

        // Get all the familyList
        restFamilyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(family.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));
    }

    @Test
    @Transactional
    void getFamily() throws Exception {
        // Initialize the database
        insertedFamily = familyRepository.saveAndFlush(family);

        // Get the family
        restFamilyMockMvc
            .perform(get(ENTITY_API_URL_ID, family.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(family.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)));
    }

    @Test
    @Transactional
    void getNonExistingFamily() throws Exception {
        // Get the family
        restFamilyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFamily() throws Exception {
        // Initialize the database
        insertedFamily = familyRepository.saveAndFlush(family);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the family
        Family updatedFamily = familyRepository.findById(family.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFamily are not directly saved in db
        em.detach(updatedFamily);
        updatedFamily.name(UPDATED_NAME).createdAt(UPDATED_CREATED_AT);

        restFamilyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFamily.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedFamily))
            )
            .andExpect(status().isOk());

        // Validate the Family in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFamilyToMatchAllProperties(updatedFamily);
    }

    @Test
    @Transactional
    void putNonExistingFamily() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        family.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFamilyMockMvc
            .perform(put(ENTITY_API_URL_ID, family.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(family)))
            .andExpect(status().isBadRequest());

        // Validate the Family in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFamily() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        family.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFamilyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(family))
            )
            .andExpect(status().isBadRequest());

        // Validate the Family in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFamily() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        family.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFamilyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(family)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Family in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFamilyWithPatch() throws Exception {
        // Initialize the database
        insertedFamily = familyRepository.saveAndFlush(family);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the family using partial update
        Family partialUpdatedFamily = new Family();
        partialUpdatedFamily.setId(family.getId());

        partialUpdatedFamily.createdAt(UPDATED_CREATED_AT);

        restFamilyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFamily.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFamily))
            )
            .andExpect(status().isOk());

        // Validate the Family in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFamilyUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedFamily, family), getPersistedFamily(family));
    }

    @Test
    @Transactional
    void fullUpdateFamilyWithPatch() throws Exception {
        // Initialize the database
        insertedFamily = familyRepository.saveAndFlush(family);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the family using partial update
        Family partialUpdatedFamily = new Family();
        partialUpdatedFamily.setId(family.getId());

        partialUpdatedFamily.name(UPDATED_NAME).createdAt(UPDATED_CREATED_AT);

        restFamilyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFamily.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFamily))
            )
            .andExpect(status().isOk());

        // Validate the Family in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFamilyUpdatableFieldsEquals(partialUpdatedFamily, getPersistedFamily(partialUpdatedFamily));
    }

    @Test
    @Transactional
    void patchNonExistingFamily() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        family.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFamilyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, family.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(family))
            )
            .andExpect(status().isBadRequest());

        // Validate the Family in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFamily() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        family.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFamilyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(family))
            )
            .andExpect(status().isBadRequest());

        // Validate the Family in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFamily() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        family.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFamilyMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(family)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Family in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFamily() throws Exception {
        // Initialize the database
        insertedFamily = familyRepository.saveAndFlush(family);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the family
        restFamilyMockMvc
            .perform(delete(ENTITY_API_URL_ID, family.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return familyRepository.count();
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

    protected Family getPersistedFamily(Family family) {
        return familyRepository.findById(family.getId()).orElseThrow();
    }

    protected void assertPersistedFamilyToMatchAllProperties(Family expectedFamily) {
        assertFamilyAllPropertiesEquals(expectedFamily, getPersistedFamily(expectedFamily));
    }

    protected void assertPersistedFamilyToMatchUpdatableProperties(Family expectedFamily) {
        assertFamilyAllUpdatablePropertiesEquals(expectedFamily, getPersistedFamily(expectedFamily));
    }
}
