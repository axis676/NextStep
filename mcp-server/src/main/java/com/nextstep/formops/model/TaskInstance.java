package com.nextstep.formops.model;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class TaskInstance {
    private final String taskId;
    private final String requester;
    private final String goal;
    private final String processCode;
    private final String dashboardUrl;
    private final OffsetDateTime createdAt;
    private final Map<String, Object> context;
    private final Map<String, TaskStepStatus> stepsByFormCode = new LinkedHashMap<>();
    private String currentStep;
    private String status;
    private String nextAction;

    public TaskInstance(String taskId, String requester, String goal, String processCode, String dashboardUrl, Map<String, Object> context) {
        this.taskId = taskId;
        this.requester = requester;
        this.goal = goal;
        this.processCode = processCode;
        this.dashboardUrl = dashboardUrl;
        this.context = new LinkedHashMap<>(context == null ? Map.of() : context);
        this.createdAt = OffsetDateTime.now();
        this.status = "CREATED";
    }

    public String getTaskId() {
        return taskId;
    }

    public String getRequester() {
        return requester;
    }

    public String getGoal() {
        return goal;
    }

    public String getProcessCode() {
        return processCode;
    }

    public String getDashboardUrl() {
        return dashboardUrl;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public Map<String, TaskStepStatus> getStepsByFormCode() {
        return stepsByFormCode;
    }

    public String getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNextAction() {
        return nextAction;
    }

    public void setNextAction(String nextAction) {
        this.nextAction = nextAction;
    }
}
