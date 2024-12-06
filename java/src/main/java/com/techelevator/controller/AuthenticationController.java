package com.techelevator.controller;

import javax.validation.Valid;

import com.techelevator.exception.DaoException;
import com.techelevator.model.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.techelevator.dao.UserDao;
import com.techelevator.security.jwt.JWTFilter;
import com.techelevator.security.jwt.TokenProvider;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class AuthenticationController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserDao userDao;

    public AuthenticationController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, UserDao userDao) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDao = userDao;
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginDto loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, false);

        User user;
        try {
            user = userDao.getUserByUsername(loginDto.getUsername());
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password is incorrect.");
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        return new ResponseEntity<>(new LoginResponseDto(jwt, user), httpHeaders, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public void register(@Valid @RequestBody RegisterUserDto newUser) {
        try {
            if (userDao.getUserByUsername(newUser.getUsername()) != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists.");
            }

            // Create user in the database
            userDao.createUser(newUser);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User registration failed.", e);
        }
    }

    @PostMapping(path = "/upload-profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CrossOrigin
    public ResponseEntity<Map<String, String>> uploadProfilePicture(@RequestParam("file") MultipartFile file, @RequestParam("userId") int userId) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

        try {
            String userPicturesDir = System.getenv("USERPROFILE") + "\\Pictures";
            File directory = new File(userPicturesDir);

            // Create the directory if it does not exist
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    throw new IOException("Failed to create directory: " + userPicturesDir);
                }
            }

            String fileName = userId + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File destination = new File(userPicturesDir + fileName);

            file.transferTo(destination);

            String filePath = "/uploads/" + fileName;
            userDao.updateProfilePicture(userId, filePath);

            return ResponseEntity.ok(Map.of("filePath", filePath));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload profile picture.", e);
        }
    }

    @PutMapping("/profile")
    @CrossOrigin
    public ResponseEntity<String> updateProfile(@RequestBody User user, Principal principal) {
        try {

            String username = principal.getName();

            User existingUser = userDao.getUserByUsername(username);

            if (existingUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setWorkoutGoals(user.getWorkoutGoals());
            existingUser.setProfilePictureUrl(user.getProfilePictureUrl());


            userDao.updateUserProfile(
                    existingUser.getId(),
                    existingUser.getName(),
                    existingUser.getEmail(),
                    existingUser.getWorkoutGoals(),
                    existingUser.getProfilePictureUrl()
            );
            System.out.println("Payload User: " + existingUser.getName() + ", " + existingUser.getEmail() + ", " + existingUser.getId());

            return ResponseEntity.ok("Profile updated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update profile.");
        }
    }


}
