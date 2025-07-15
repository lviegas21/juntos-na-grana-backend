package com.noxius.juntosnagrana.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.noxius.juntosnagrana.domain.enumeration.DailyMissionType;
import com.noxius.juntosnagrana.domain.enumeration.GoalCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A DailyMission.
 */
@Entity
@Table(name = "daily_mission")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DailyMission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private ZonedDateTime startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private ZonedDateTime endDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private DailyMissionType type;

    @Column(name = "target_amount")
    private Double targetAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private GoalCategory category;

    @NotNull
    @Column(name = "xp_reward", nullable = false)
    private Integer xpReward;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "mission")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "mission" }, allowSetters = true)
    private Set<MissionStatusRecord> statusRecords = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "members" }, allowSetters = true)
    private Family family;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DailyMission id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public DailyMission title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public DailyMission description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getStartDate() {
        return this.startDate;
    }

    public DailyMission startDate(ZonedDateTime startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return this.endDate;
    }

    public DailyMission endDate(ZonedDateTime endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public DailyMissionType getType() {
        return this.type;
    }

    public DailyMission type(DailyMissionType type) {
        this.setType(type);
        return this;
    }

    public void setType(DailyMissionType type) {
        this.type = type;
    }

    public Double getTargetAmount() {
        return this.targetAmount;
    }

    public DailyMission targetAmount(Double targetAmount) {
        this.setTargetAmount(targetAmount);
        return this;
    }

    public void setTargetAmount(Double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public GoalCategory getCategory() {
        return this.category;
    }

    public DailyMission category(GoalCategory category) {
        this.setCategory(category);
        return this;
    }

    public void setCategory(GoalCategory category) {
        this.category = category;
    }

    public Integer getXpReward() {
        return this.xpReward;
    }

    public DailyMission xpReward(Integer xpReward) {
        this.setXpReward(xpReward);
        return this;
    }

    public void setXpReward(Integer xpReward) {
        this.xpReward = xpReward;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public DailyMission createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<MissionStatusRecord> getStatusRecords() {
        return this.statusRecords;
    }

    public void setStatusRecords(Set<MissionStatusRecord> missionStatusRecords) {
        if (this.statusRecords != null) {
            this.statusRecords.forEach(i -> i.setMission(null));
        }
        if (missionStatusRecords != null) {
            missionStatusRecords.forEach(i -> i.setMission(this));
        }
        this.statusRecords = missionStatusRecords;
    }

    public DailyMission statusRecords(Set<MissionStatusRecord> missionStatusRecords) {
        this.setStatusRecords(missionStatusRecords);
        return this;
    }

    public DailyMission addStatusRecords(MissionStatusRecord missionStatusRecord) {
        this.statusRecords.add(missionStatusRecord);
        missionStatusRecord.setMission(this);
        return this;
    }

    public DailyMission removeStatusRecords(MissionStatusRecord missionStatusRecord) {
        this.statusRecords.remove(missionStatusRecord);
        missionStatusRecord.setMission(null);
        return this;
    }

    public Family getFamily() {
        return this.family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public DailyMission family(Family family) {
        this.setFamily(family);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DailyMission)) {
            return false;
        }
        return getId() != null && getId().equals(((DailyMission) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DailyMission{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", type='" + getType() + "'" +
            ", targetAmount=" + getTargetAmount() +
            ", category='" + getCategory() + "'" +
            ", xpReward=" + getXpReward() +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
