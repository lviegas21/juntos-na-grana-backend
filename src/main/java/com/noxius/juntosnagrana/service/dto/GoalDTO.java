package com.noxius.juntosnagrana.service.dto;

import com.noxius.juntosnagrana.domain.Goal;
import com.noxius.juntosnagrana.domain.enumeration.GoalCategory;
import com.noxius.juntosnagrana.domain.enumeration.GoalPriority;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * DTO para receber dados de criação de metas sem exigir o campo user.
 */
public class GoalDTO {

    private Long id;

    @NotNull
    private String title;

    private String description;

    @NotNull
    private Double targetAmount;

    @NotNull
    private Double currentAmount;

    @NotNull
    private ZonedDateTime createdAt;

    private ZonedDateTime dueDate;

    @NotNull
    private GoalCategory category;

    @NotNull
    private GoalPriority priority;

    @NotNull
    private Boolean alertEnabled;

    @NotNull
    private Integer alertThreshold;

    private Long familyId;

    public GoalDTO() {
        // Construtor vazio necessário para deserialização
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(Double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public Double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(Double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(ZonedDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public GoalCategory getCategory() {
        return category;
    }

    public void setCategory(GoalCategory category) {
        this.category = category;
    }

    public GoalPriority getPriority() {
        return priority;
    }

    public void setPriority(GoalPriority priority) {
        this.priority = priority;
    }

    public Boolean getAlertEnabled() {
        return alertEnabled;
    }

    public void setAlertEnabled(Boolean alertEnabled) {
        this.alertEnabled = alertEnabled;
    }

    public Integer getAlertThreshold() {
        return alertThreshold;
    }

    public void setAlertThreshold(Integer alertThreshold) {
        this.alertThreshold = alertThreshold;
    }

    public Long getFamilyId() {
        return familyId;
    }

    public void setFamilyId(Long familyId) {
        this.familyId = familyId;
    }

    /**
     * Converte o DTO para a entidade Goal.
     * Obs: Não define o usuário, isso deve ser feito pelo serviço.
     */
    public Goal toEntity() {
        Goal goal = new Goal();
        goal.setId(this.id);
        goal.setTitle(this.title);
        goal.setDescription(this.description);
        goal.setTargetAmount(this.targetAmount);
        goal.setCurrentAmount(this.currentAmount);
        goal.setCreatedAt(this.createdAt);
        goal.setDueDate(this.dueDate);
        goal.setCategory(this.category);
        goal.setPriority(this.priority);
        goal.setAlertEnabled(this.alertEnabled);
        goal.setAlertThreshold(this.alertThreshold);
        return goal;
    }
}
