<template>
  <div>
    <h1>Edit Profile</h1>
    <form @submit.prevent="updateProfile">
      <label for="name">Name:</label>
      <input id="name" v-model="profile.name" required />
      
      <label for="email">Email:</label>
      <input id="email" v-model="profile.email" required />
      
      <label for="workoutGoals">Workout Goals:</label>
      <textarea id="workoutGoals" v-model="profile.workoutGoals"></textarea>
      
      <label for="profilePicture">Profile Picture:</label>
      <input type="file" @change="onFileSelected" />
      
      <button type="submit">Save Changes</button>
    </form>
  </div>
</template>

<script>
import axios from "axios";
import authService from '../services/AuthService';

const http = axios.create({
  baseURL: import.meta.env.VITE_REMOTE_API,
});

// Add a request interceptor to always use the latest token
http.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token"); // Get the latest token
    if (token) {
      config.headers.Authorization = `Bearer ${token}`; // Add token to headers
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default {
  data() {
    return {
      profile: {
        name: "",
        email: "",
        workoutGoals: "",
        profilePictureUrl: "", // Stores the uploaded picture's URL or path
      },
    };
  },
  mounted() {
    const user = this.$store.state.user;
    if (user) {
      this.profile.name = user.name || ""; 
      this.profile.email = user.email || ""; 
      this.profile.workoutGoals = user.workoutGoals || "";
      this.profile.profilePictureUrl = user.profilePictureUrl || "";
    }
  },
  methods: {
    onFileSelected(event) {
      const userId = this.$store.state.user.id;
      const file = event.target.files[0];
      if (file) {
         authService.uploadProfilePicture(file, userId)
        .then(response => {
          this.profile.profilePictureUrl = response.data.filePath; // Save the file path from the response
        })
        .catch(error => {
          console.error("Failed to upload profile picture:", error);
        });
      }
    },
    updateProfile() {
      http
        .put("/profile", this.profile)
        .then(() => {
          alert("Profile updated successfully!");
        })
        .catch((error) => {
          console.error("Failed to update profile:", error.response?.data || error.message);
        });
    },
  },
};
</script>