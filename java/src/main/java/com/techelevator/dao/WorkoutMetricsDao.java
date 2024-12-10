package com.techelevator.dao;

import com.techelevator.model.EquipmentUsageDto;
import com.techelevator.model.WorkoutMetrics;

import java.util.List;

public interface WorkoutMetricsDao {


    void addMetric(WorkoutMetrics metric);
    int getTotalWorkoutsByUserId(int userId);


    List<WorkoutMetrics> getMetricsByUserId(int userId);

    List<WorkoutMetrics> getAllMetrics();

    List<WorkoutMetrics> getMetricsWithEquipmentByUserId(int userId);
    List<EquipmentUsageDto> getEquipmentUsageForMonthOrYear(String month, String year);
}
