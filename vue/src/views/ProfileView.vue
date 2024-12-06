<template>
    <div class="profile-view">
      <h1>My Profile</h1>
      <div class="profile-details">
        <p><strong>Username:</strong> {{ user.username }}</p>
        <p><strong>Name:</strong> {{ user.name }}</p>
        <p><strong>Email:</strong> {{ user.email }}</p>
        <p><strong>Workout Goals:</strong> {{ user.workoutGoals }}</p>
      </div>
      <router-link to="/edit-profile" class="edit-profile-button">Edit Profile</router-link>
    </div>
  </template>
  
  <script>
  export default {
    computed: {
      user() {
        return this.$store.state.user;
      },
    },
  };
  </script>
  
  <style scoped>
  .profile-view {
    text-align: center;
    margin: 20px;
  }
  
  .profile-details {
    text-align: left;
    margin: 0 auto;
    max-width: 400px;
    border: 1px solid #ddd;
    padding: 20px;
    border-radius: 5px;
    background-color: #f9f9f9;
  }
  
  .edit-profile-button {
    display: inline-block;
    margin-top: 20px;
    padding: 10px 20px;
    background-color: #007bff;
    color: white;
    text-decoration: none;
    border-radius: 5px;
  }
  
  .edit-profile-button:hover {
    background-color: #0056b3;
  }
  </style>
  