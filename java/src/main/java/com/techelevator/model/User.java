package com.techelevator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class User {

   private int id;
   private String username;
   @JsonIgnore
   private String password;
   @JsonIgnore
   private boolean activated;
   private Set<Authority> authorities = new HashSet<>();
   private String name;
   private String email;
   private String workoutGoals;
   private String profilePictureUrl; // URL or file path for the profile picture

   public User() { }

   public User(int id, String username, String password, String authorities) {
      this.id = id;
      this.username = username;
      this.password = password;
      if (authorities != null) this.setAuthorities(authorities);
      this.activated = true;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public boolean isActivated() {
      return activated;
   }

   public void setActivated(boolean activated) {
      this.activated = activated;
   }

   public Set<Authority> getAuthorities() {
      return authorities;
   }

   public void setAuthorities(Set<Authority> authorities) {
      this.authorities = authorities;
   }

   public void setAuthorities(String authorities) {
      String[] roles = authorities.split(",");
      for (String role : roles) {
         String authority = role.contains("ROLE_") ? role : "ROLE_" + role;
         this.authorities.add(new Authority(authority));
      }
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getWorkoutGoals() {
      return workoutGoals;
   }

   public void setWorkoutGoals(String workoutGoals) {
      this.workoutGoals = workoutGoals;
   }

   public String getProfilePictureUrl() {
      return profilePictureUrl;
   }

   public void setProfilePictureUrl(String profilePictureUrl) {
      this.profilePictureUrl = profilePictureUrl;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      User user = (User) o;
      return id == user.id &&
              activated == user.activated &&
              Objects.equals(username, user.username) &&
              Objects.equals(password, user.password) &&
              Objects.equals(authorities, user.authorities) &&
              Objects.equals(name, user.name) &&
              Objects.equals(email, user.email) &&
              Objects.equals(workoutGoals, user.workoutGoals) &&
              Objects.equals(profilePictureUrl, user.profilePictureUrl);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, username, password, activated, authorities, name, email, workoutGoals, profilePictureUrl);
   }

   @Override
   public String toString() {
      return "User{" +
              "id=" + id +
              ", username='" + username + '\'' +
              ", activated=" + activated +
              ", authorities=" + authorities +
              ", name='" + name + '\'' +
              ", email='" + email + '\'' +
              ", workoutGoals='" + workoutGoals + '\'' +
              ", profilePictureUrl='" + profilePictureUrl + '\'' +
              '}';
   }
}
