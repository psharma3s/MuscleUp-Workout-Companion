package com.techelevator.dao;

import com.techelevator.model.EquipmentUsageDto;
import com.techelevator.model.WorkoutMetrics;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcWorkoutMetricsDao implements WorkoutMetricsDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcWorkoutMetricsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addMetric(WorkoutMetrics metric) {
        String sql = "INSERT INTO workout_metrics (user_id, equipment_used, weights, reps, date, distance, workout_type, workout_duration, exercise, sets) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, metric.getUserId(), metric.getEquipmentUsed(),
                metric.getWeights(), metric.getReps(), metric.getDate(), metric.getDistance(), metric.getWorkoutType(), metric.getWorkoutDuration(), metric.getExercise(), metric.getSets());
    }

    @Override
    public List<WorkoutMetrics> getMetricsByUserId(int userId) {
        List<WorkoutMetrics> metrics = new ArrayList<>();
        String sql = "SELECT * FROM workout_metrics WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while (results.next()) {
            metrics.add(mapRowToWorkoutMetrics(results));
        }
        return metrics;
    }

    @Override
    public List<WorkoutMetrics> getAllMetrics() {
        List<WorkoutMetrics> metrics = new ArrayList<>();
        String sql = "SELECT * FROM workout_metrics";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            metrics.add(mapRowToWorkoutMetrics(results));
        }
        return metrics;
    }

    private WorkoutMetrics mapRowToWorkoutMetrics(SqlRowSet rs) {
        WorkoutMetrics metric = new WorkoutMetrics();
        metric.setMetricId(rs.getInt("metric_id"));
        metric.setUserId(rs.getInt("user_id"));
        metric.setEquipmentUsed(rs.getString("equipment_used"));
        metric.setWeights(rs.getInt("weights"));
        metric.setReps(rs.getInt("reps"));
        metric.setDate(rs.getTimestamp("date").toLocalDateTime());
        metric.setDistance(rs.getString("distance"));
        metric.setWorkoutType(rs.getString("workout_type"));
        metric.setWorkoutDuration(rs.getInt("workout_duration"));
        metric.setExercise(rs.getString("exercise"));
        metric.setSets(rs.getInt("sets"));
        return metric;
    }

    @Override
    public List<WorkoutMetrics> getMetricsWithEquipmentByUserId(int userId) {
        List<WorkoutMetrics> metrics = new ArrayList<>();
        String sql = "SELECT * FROM workout_metrics WHERE user_id = ? AND equipment_used IS NOT NULL AND equipment_used != '' AND equipment_used != 'None'";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while (results.next()) {
            metrics.add(mapRowToWorkoutMetrics(results));
        }
        return metrics;
    }

    public List<EquipmentUsageDto> getEquipmentUsageForMonthOrYear(String month, String year) {
        String sql = "SELECT equipment_used, COUNT(*) as usage_count " +
                "FROM workout_metrics " +
                "WHERE 1=1 ";

        List<Object> params = new ArrayList<>();

        if (month != null && !month.isEmpty()) {
            sql += "AND TO_CHAR(date, 'YYYY-MM') = ? ";
            params.add(month);
        } else if (year != null && !year.isEmpty()) {
            sql += "AND TO_CHAR(date, 'YYYY') = ? ";
            params.add(year);
        }

        sql += "AND equipment_used IS NOT NULL " +
                "AND LOWER(equipment_used) != 'none' " +
                "AND LOWER(equipment_used) != '' " +
                "GROUP BY equipment_used " +
                "ORDER BY usage_count DESC";

        List<EquipmentUsageDto> usageMetrics = new ArrayList<>();
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, params.toArray());
        while (results.next()) {
            EquipmentUsageDto dto = new EquipmentUsageDto();
            dto.setEquipmentUsed(results.getString("equipment_used"));
            dto.setUsageCount(results.getInt("usage_count"));
            usageMetrics.add(dto);
        }
        return usageMetrics;
    }


    private String capitalize(String equipment) {
        if (equipment == null || equipment.isEmpty()) {
            return equipment;
        }
        return equipment.substring(0, 1).toUpperCase() + equipment.substring(1);
    }

    @Override
    public int getTotalWorkoutsByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM workout_metrics WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);

        if (results.next()) {
            return results.getInt(1);  // The first column in the result set will be the count
        }
        return 0;
    }
}
