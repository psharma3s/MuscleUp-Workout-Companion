package com.techelevator.controller;

import com.techelevator.dao.WorkoutMetricsDao;
import com.techelevator.model.EquipmentUsageDto;
import com.techelevator.model.WorkoutMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/metrics")
public class WorkoutMetricsController {

    @Autowired
    private WorkoutMetricsDao workoutMetricsDao;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addMetric(@RequestBody WorkoutMetrics metric) {
        workoutMetricsDao.addMetric(metric);
    }

    @GetMapping("/{userId}")
    public List<WorkoutMetrics> getMetricsByUserId(@PathVariable int userId) {
        return workoutMetricsDao.getMetricsByUserId(userId);
    }

    @GetMapping
    public List<WorkoutMetrics> getAllMetrics() {
        return workoutMetricsDao.getAllMetrics();
    }

    @GetMapping("/{userId}/equipment")
    public List<WorkoutMetrics> getMetricsWithEquipment(@PathVariable int userId) {
        return workoutMetricsDao.getMetricsWithEquipmentByUserId(userId);
    }

    @GetMapping("/equipment-usage")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<EquipmentUsageDto>> getEquipmentUsage(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String year) {
        List<EquipmentUsageDto> usageMetrics = workoutMetricsDao.getEquipmentUsageForMonthOrYear(month, year);
        return ResponseEntity.ok(usageMetrics);
    }
    @GetMapping("/user/{userId}/totalWorkouts")
    public int getTotalWorkouts(@PathVariable int userId) {
        return workoutMetricsDao.getTotalWorkoutsByUserId(userId);  // Implement this method in your DAO
    }

}
