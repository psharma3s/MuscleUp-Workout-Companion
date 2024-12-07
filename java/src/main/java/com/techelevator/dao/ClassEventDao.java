package com.techelevator.dao;

import com.techelevator.model.ClassEvent;
import java.util.List;

public interface ClassEventDao {

    List<ClassEvent> getAllClasses();

    List<ClassEvent> getClassesByDate(String date); // date in YYYY-MM-DD

    ClassEvent getClassById(int classId);

    ClassEvent createClass(ClassEvent classEvent);

    void deleteClass(int classId);

    void registerUserForClass(int userId, int classId);

    void dropUserFromClass(int userId, int classId);

    List<String> getRegisteredMembersForClass(int classId);
}
