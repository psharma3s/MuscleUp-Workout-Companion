package com.techelevator.dao;

import com.techelevator.exception.DaoException;
import com.techelevator.model.ClassEvent;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcClassEventDao implements ClassEventDao {

    private final JdbcTemplate jdbcTemplate;
    private final UserDao userDao;

    public JdbcClassEventDao(JdbcTemplate jdbcTemplate, UserDao userDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDao = userDao;
    }

    @Override
    public List<ClassEvent> getAllClasses() {
        String sql = "SELECT class_id, name, class_date, class_time, instructor, duration, calories_burned FROM classes";
        List<ClassEvent> classEvents = new ArrayList<>();
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                classEvents.add(mapRowToClassEvent(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to database", e);
        }

        // Populate registered members
        for (ClassEvent ce : classEvents) {
            ce.setRegisteredMembers(getRegisteredMembersForClass(ce.getClassId()));
        }
        return classEvents;
    }

    @Override
    public List<ClassEvent> getClassesByDate(String date) {
        String sql = "SELECT class_id, name, class_date, class_time, instructor, duration, calories_burned FROM classes WHERE class_date = ?";

        LocalDate localDate = LocalDate.parse(date);
        Date sqlDate = Date.valueOf(localDate);

        List<ClassEvent> classEvents = new ArrayList<>();
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, sqlDate);
            while (results.next()) {
                ClassEvent ce = mapRowToClassEvent(results);
                ce.setRegisteredMembers(getRegisteredMembersForClass(ce.getClassId()));
                classEvents.add(ce);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to database", e);
        }
        return classEvents;
    }

    @Override
    public ClassEvent getClassById(int classId) {
        String sql = "SELECT class_id, name, class_date, class_time, instructor, duration, calories_burned FROM classes WHERE class_id = ?";
        ClassEvent ce = null;
        try {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, classId);
            if (rs.next()) {
                ce = mapRowToClassEvent(rs);
                ce.setRegisteredMembers(getRegisteredMembersForClass(classId));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to database", e);
        }
        return ce;
    }

    @Override
    public ClassEvent createClass(ClassEvent classEvent) {
        String sql = "INSERT INTO classes (name, class_date, class_time, instructor, duration, calories_burned) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING class_id";

        LocalDate localDate = LocalDate.parse(classEvent.getDate());
        Date sqlDate = Date.valueOf(localDate);

        LocalTime localTime = LocalTime.parse(classEvent.getTime());
        Time sqlTime = Time.valueOf(localTime);

        try {
            Integer newId = jdbcTemplate.queryForObject(sql, Integer.class,
                    classEvent.getName(),
                    sqlDate,
                    sqlTime,
                    classEvent.getInstructor(),
                    classEvent.getDuration(),
                    classEvent.getCaloriesBurned()
            );
            classEvent.setClassId(newId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to database", e);
        }
        return classEvent;
    }

    @Override
    public void deleteClass(int classId) {
        String deleteRegistrationsSql = "DELETE FROM class_registrations WHERE class_id = ?";
        String deleteClassSql = "DELETE FROM classes WHERE class_id = ?";
        try {
            jdbcTemplate.update(deleteRegistrationsSql, classId);
            jdbcTemplate.update(deleteClassSql, classId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to database", e);
        }
    }

    @Override
    public void registerUserForClass(int userId, int classId) {
        String sql = "INSERT INTO class_registrations (class_id, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try {
            jdbcTemplate.update(sql, classId, userId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to database", e);
        }
    }

    @Override
    public void dropUserFromClass(int userId, int classId) {
        String sql = "DELETE FROM class_registrations WHERE class_id = ? AND user_id = ?";
        try {
            jdbcTemplate.update(sql, classId, userId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to database", e);
        }
    }

    @Override
    public List<String> getRegisteredMembersForClass(int classId) {
        String sql = "SELECT u.username FROM class_registrations cr " +
                "JOIN users u ON u.user_id = cr.user_id " +
                "WHERE cr.class_id = ?";
        List<String> members = new ArrayList<>();
        try {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, classId);
            while (rs.next()) {
                members.add(rs.getString("username"));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to database", e);
        }
        return members;
    }
    public int getClassesAttendedByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM class_event WHERE user_id = ?";  // Adjust the query based on your schema
        return jdbcTemplate.queryForObject(sql, Integer.class, userId);  // Returns the number of classes attended
    }

    private ClassEvent mapRowToClassEvent(SqlRowSet rs) {
        ClassEvent ce = new ClassEvent();
        ce.setClassId(rs.getInt("class_id"));
        ce.setName(rs.getString("name"));

        // Convert date back to String
        Date classDate = rs.getDate("class_date");
        ce.setDate(classDate.toLocalDate().toString());

        // Convert time back to String "HH:MM"
        Time classTime = rs.getTime("class_time");
        LocalTime lt = classTime.toLocalTime();
        ce.setTime(lt.toString());

        ce.setInstructor(rs.getString("instructor"));
        ce.setDuration(rs.getString("duration"));
        ce.setCaloriesBurned(rs.getString("calories_burned"));
        return ce;
    }
}
