package com.techelevator.controller;

import com.techelevator.dao.WorkoutMetricsDao;
import com.techelevator.model.WorkoutMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
}
