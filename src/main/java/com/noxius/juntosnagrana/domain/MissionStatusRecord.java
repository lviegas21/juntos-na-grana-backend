package com.noxius.juntosnagrana.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.noxius.juntosnagrana.domain.enumeration.MissionStatusType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A MissionStatusRecord.
 */
@Entity
@Table(name = "mission_status_record")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MissionStatusRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    private ZonedDateTime date;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_type", nullable = false)
    private MissionStatusType statusType;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "statusRecords", "family" }, allowSetters = true)
    private DailyMission mission;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MissionStatusRecord id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDate() {
        return this.date;
    }

    public MissionStatusRecord date(ZonedDateTime date) {
        this.setDate(date);
        return this;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public MissionStatusType getStatusType() {
        return this.statusType;
    }

    public MissionStatusRecord statusType(MissionStatusType statusType) {
        this.setStatusType(statusType);
        return this;
    }

    public void setStatusType(MissionStatusType statusType) {
        this.statusType = statusType;
    }

    public DailyMission getMission() {
        return this.mission;
    }

    public void setMission(DailyMission dailyMission) {
        this.mission = dailyMission;
    }

    public MissionStatusRecord mission(DailyMission dailyMission) {
        this.setMission(dailyMission);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MissionStatusRecord)) {
            return false;
        }
        return getId() != null && getId().equals(((MissionStatusRecord) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MissionStatusRecord{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", statusType='" + getStatusType() + "'" +
            "}";
    }
}
