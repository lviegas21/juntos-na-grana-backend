package com.noxius.juntosnagrana.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.noxius.juntosnagrana.domain.enumeration.GoalCategory;
import com.noxius.juntosnagrana.domain.enumeration.GoalPriority;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Goal.
 */
@Entity
@Table(name = "goal")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Goal implements Serializable {

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
    @Column(name = "target_amount", nullable = false)
    private Double targetAmount;

    @NotNull
    @Column(name = "current_amount", nullable = false)
    private Double currentAmount;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "due_date")
    private ZonedDateTime dueDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private GoalCategory category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private GoalPriority priority;

    @NotNull
    @Column(name = "alert_enabled", nullable = false)
    private Boolean alertEnabled;

    @NotNull
    @Column(name = "alert_threshold", nullable = false)
    private Integer alertThreshold;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "members" }, allowSetters = true)
    private Family family;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Goal id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Goal title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Goal description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getTargetAmount() {
        return this.targetAmount;
    }

    public Goal targetAmount(Double targetAmount) {
        this.setTargetAmount(targetAmount);
        return this;
    }

    public void setTargetAmount(Double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public Double getCurrentAmount() {
        return this.currentAmount;
    }

    public Goal currentAmount(Double currentAmount) {
        this.setCurrentAmount(currentAmount);
        return this;
    }

    public void setCurrentAmount(Double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public Goal createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getDueDate() {
        return this.dueDate;
    }

    public Goal dueDate(ZonedDateTime dueDate) {
        this.setDueDate(dueDate);
        return this;
    }

    public void setDueDate(ZonedDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public GoalCategory getCategory() {
        return this.category;
    }

    public Goal category(GoalCategory category) {
        this.setCategory(category);
        return this;
    }

    public void setCategory(GoalCategory category) {
        this.category = category;
    }

    public GoalPriority getPriority() {
        return this.priority;
    }

    public Goal priority(GoalPriority priority) {
        this.setPriority(priority);
        return this;
    }

    public void setPriority(GoalPriority priority) {
        this.priority = priority;
    }

    public Boolean getAlertEnabled() {
        return this.alertEnabled;
    }

    public Goal alertEnabled(Boolean alertEnabled) {
        this.setAlertEnabled(alertEnabled);
        return this;
    }

    public void setAlertEnabled(Boolean alertEnabled) {
        this.alertEnabled = alertEnabled;
    }

    public Integer getAlertThreshold() {
        return this.alertThreshold;
    }

    public Goal alertThreshold(Integer alertThreshold) {
        this.setAlertThreshold(alertThreshold);
        return this;
    }

    public void setAlertThreshold(Integer alertThreshold) {
        this.alertThreshold = alertThreshold;
    }

    public Family getFamily() {
        return this.family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public Goal family(Family family) {
        this.setFamily(family);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Goal)) {
            return false;
        }
        return getId() != null && getId().equals(((Goal) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Goal{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", targetAmount=" + getTargetAmount() +
            ", currentAmount=" + getCurrentAmount() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            ", category='" + getCategory() + "'" +
            ", priority='" + getPriority() + "'" +
            ", alertEnabled='" + getAlertEnabled() + "'" +
            ", alertThreshold=" + getAlertThreshold() +
            "}";
    }
}
