package com.techelevator.model;

import java.time.LocalDateTime;

public class WorkoutMetrics {
    private int metricId;
    private int userId;
    private String equipmentUsed;
    private int weights;
    private int reps;
    private LocalDateTime date;
    private String distance;
    private String workoutType;
    private int workoutDuration;
    private String exercise;
    private int sets;

    // Getters and Setters
    public int getMetricId() {
        return metricId;
    }

    public void setMetricId(int metricId) {
        this.metricId = metricId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEquipmentUsed() {
        return equipmentUsed;
    }

    public void setEquipmentUsed(String equipmentUsed) {
        this.equipmentUsed = equipmentUsed;
    }

    public int getWeights() {
        return weights;
    }

    public void setWeights(int weights) {
        this.weights = weights;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(String workoutType) {
        this.workoutType = workoutType;
    }

    public int getWorkoutDuration() {
        return workoutDuration;
    }

    public void setWorkoutDuration(int workoutDuration) {
        this.workoutDuration = workoutDuration;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }
}
