<template>
  <div class="profile-edit">
    <h1>Edit Profile</h1>
    <form @submit.prevent="updateProfile">
      <!-- Name Field -->
      <div class="form-group">
        <label for="name">Name:</label>
        <input id="name" v-model="profile.name" required />
      </div>

      <!-- Email Field -->
      <div class="form-group">
        <label for="email">Email:</label>
        <input id="email" v-model="profile.email" required />
      </div>

      <!-- Workout Goals Field -->
      <div class="form-group">
        <label for="workoutGoals">Workout Goals:</label>
        <textarea id="workoutGoals" v-model="profile.workoutGoals"></textarea>
      </div>

      <!-- Profile Picture Upload Section -->
      <div class="form-group">
        <label for="profilePicture">Profile Picture:</label>
        <input type="file" @change="onFileSelected" />

        <!-- Profile Picture Preview -->
        <div v-if="profile.profilePictureUrl" class="profile-picture-preview">
          <h3>Preview:</h3>
          <img :src="profile.profilePictureUrl" alt="Profile Picture" width="150" height="150" />
        </div>
      </div>

      <!-- Submit Button -->
      <button type="submit" class="btn-save">Save Changes</button>
    </form>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      profile: {
        name: "",
        email: "",
        workoutGoals: "",
        profilePictureUrl: "", // URL for profile picture
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
  const file = event.target.files[0];
  if (file) {
    const formData = new FormData();
    formData.append("image", file);  // Correct field name for the file

    const apiKey = import.meta.env.VITE_API_KEY;  // Make sure API key is loaded correctly

    // Make the POST request to ImgBB API
    // After successfully uploading the image to ImgBB
axios.post(`https://api.imgbb.com/1/upload?key=${apiKey}`, formData, {
  headers: {
    'Content-Type': 'multipart/form-data',
  }
})
.then((response) => {
  if (response.data && response.data.data && response.data.data.url) {
    // Set the profile picture URL in the profile object
    this.profile.profilePictureUrl = response.data.data.url;
    
    // Update Vuex store with the new user data
    this.$store.commit('SET_USER', { 
      ...this.$store.state.user, 
      profilePictureUrl: this.profile.profilePictureUrl 
    });
    
    console.log("Uploaded Image URL:", this.profile.profilePictureUrl); // Log URL for debugging
  } else {
    console.error("Error: Image URL not found in response.");
  }
})
.catch((error) => {
  console.error("Failed to upload profile picture:", error);
});

  }
},


    updateProfile() {
      axios.put("http://localhost:9000/profile", this.profile, {
        headers: {
          "Authorization": `Bearer ${localStorage.getItem("token")}`,  // Include the token in the header
        }
      })
      .then(() => {
        alert("Profile updated successfully!");
      })
      .catch((error) => {
        console.error("Failed to update profile:", error.response?.data || error.message);
      });
    }
  }
};
</script>

<style scoped>
/* Style the form layout */
.profile-edit {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

/* Heading style */
h1 {
  text-align: center;
  color: #333;
}

/* Form field container */
.form-group {
  margin-bottom: 20px;
}

/* Label style */
label {
  font-size: 16px;
  font-weight: 600;
  display: block;
  margin-bottom: 8px;
}

/* Input and textarea style */
input, textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

/* Textarea specific style */
textarea {
  resize: vertical;
  height: 100px;
}

/* Profile picture preview style */
.profile-picture-preview {
  margin-top: 15px;
  text-align: center;
}

.profile-picture-preview img {
  border-radius: 50%;
  object-fit: cover;
}

/* Save button style */
button {
  display: block;
  width: 100%;
  padding: 12px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}

.btn-save:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
