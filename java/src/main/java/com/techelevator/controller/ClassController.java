package com.techelevator.controller;

import com.techelevator.dao.ClassEventDao;
import com.techelevator.dao.UserDao;
import com.techelevator.model.ClassEvent;
import com.techelevator.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;
import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/classes")
public class ClassController {

    private final ClassEventDao classEventDao;
    private final UserDao userDao;

    public ClassController(ClassEventDao classEventDao, UserDao userDao) {
        this.classEventDao = classEventDao;
        this.userDao = userDao;
    }

    @GetMapping
    @CrossOrigin
    public List<ClassEvent> getAllClasses() {
        return classEventDao.getAllClasses();
    }

    @CrossOrigin
    @GetMapping("/date/{date}")
    public List<ClassEvent> getClassesByDate(@PathVariable String date) {
        return classEventDao.getClassesByDate(date);
    }

    @GetMapping("/{classId}")
    @CrossOrigin
    public ClassEvent getClassById(@PathVariable int classId) {
        return classEventDao.getClassById(classId);
    }
    @GetMapping("/user/{userId}/classesAttended")
    public int getClassesAttended(@PathVariable int userId) {
        return classEventDao.getClassesAttendedByUserId(userId);
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    @CrossOrigin
    public ResponseEntity<ClassEvent> createClass(@RequestBody ClassEvent classEvent) {
        ClassEvent created = classEventDao.createClass(classEvent);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @DeleteMapping("/{classId}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @CrossOrigin
    public ResponseEntity<Void> deleteClass(@PathVariable int classId) {
        classEventDao.deleteClass(classId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{classId}/register")
    @CrossOrigin
    public ResponseEntity<String> registerUserForClass(@PathVariable int classId, Principal principal) {
        User user = userDao.getUserByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        classEventDao.registerUserForClass(user.getId(), classId);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/{classId}/drop")
    @CrossOrigin
    public ResponseEntity<String> dropUserFromClass(@PathVariable int classId, Principal principal) {
        User user = userDao.getUserByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        classEventDao.dropUserFromClass(user.getId(), classId);
        return ResponseEntity.ok("User dropped from class");
    }

}
