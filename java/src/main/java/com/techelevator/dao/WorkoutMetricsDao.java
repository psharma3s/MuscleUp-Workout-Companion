package com.techelevator.dao;

import com.techelevator.model.WorkoutMetrics;

import java.util.List;

public interface WorkoutMetricsDao {


    void addMetric(WorkoutMetrics metric);


    List<WorkoutMetrics> getMetricsByUserId(int userId);

    List<WorkoutMetrics> getAllMetrics();
}
