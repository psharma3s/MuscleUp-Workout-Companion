<template>
  <div class="profile-view">
    <h1>My Profile</h1>
    
    <!-- Profile Picture Section -->
    <div class="profile-picture">
      <!-- If the user has a profile picture, show it. If not, show the default image -->
      <img :src="user.profilePictureUrl || 'src/assets/icons/default-profile.png'" 
           alt="Profile Picture" 
           width="150" 
           height="150" 
           class="profile-image" />
    </div>
    
    <!-- Profile Details -->
    <div class="profile-details">
      <p><strong>Username:</strong> {{ user.username }}</p>
      <p><strong>Name:</strong> {{ user.name }}</p>
      <p><strong>Email:</strong> {{ user.email }}</p>
      <p><strong>Workout Goals:</strong> {{ user.workoutGoals }}</p>
    </div>

    <!-- Edit Profile Link -->
    <router-link to="/edit-profile" class="edit-profile-button">Edit Profile</router-link>
  </div>
</template>

<script>
export default {
  computed: {
    user() {
      return this.$store.state.user; // Assuming user data is stored in Vuex store
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

.profile-picture {
  margin-bottom: 20px;
}

.profile-image {
  border-radius: 50%;
  object-fit: cover;
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
