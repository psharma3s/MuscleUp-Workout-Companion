<template>
  <div id="register" class="text-center">
    <form v-on:submit.prevent="register" enctype="multipart/form-data">
      <h1>Create Account</h1>
      <div role="alert" v-if="registrationErrors">
        {{ registrationErrorMsg }}
      </div>
      <div class="form-input-group">
        <label for="username">Username</label>
        <input type="text" id="username" v-model="user.username" required autofocus @input="clearErrors" />
      </div>
      <div class="form-input-group">
        <label for="password">Password</label>
        <input type="password" id="password" v-model="user.password" required @input="clearErrors" />
      </div>
      <div class="form-input-group">
        <label for="confirmPassword">Confirm Password</label>
        <input type="password" id="confirmPassword" v-model="user.confirmPassword" required @input="clearErrors" />
      </div>
      <div class="form-input-group">
        <label for="name">Name</label>
        <input type="text" id="name" v-model="user.name" required @input="clearErrors" />
      </div>
      <div class="form-input-group">
        <label for="email">Email Address</label>
        <input type="email" id="email" v-model="user.email" required @input="clearErrors" />
      </div>
      <div class="form-input-group">
        <label for="workoutGoals">Workout Goals</label>
        <textarea id="workout_goals" v-model="user.workoutGoals" rows="3" @input="clearErrors"></textarea>
      </div>
      <div class="form-input-group">
        <label for="profilePicture">Profile Picture</label>
        <input type="file" id="profilePicture" @change="onFileSelected" />
      </div>
      <button type="submit">Create Account</button>
      <p>
        <router-link v-bind:to="{ name: 'login' }">Already have an account? Log in.</router-link>
      </p>
    </form>
  </div>
</template>

<script>
import authService from '../services/AuthService';

export default {
  data() {
    return {
      user: {
        username: '',
        password: '',
        confirmPassword: '',
        role: 'ROLE_USER', // Backend expects "ROLE_" prefix
        name: '',
        email: '',
        workoutGoals: '',
        profilePicture: null, // File input
      },
      registrationErrors: false,
      registrationErrorMsg: 'There were problems registering this user.',
    };
  },
  methods: {
  onFileSelected(event) {
    const file = event.target.files[0];
    if (file) {
      authService.uploadProfilePicture(file)
        .then(response => {
          this.user.profilePictureUrl = response.data.filePath; // Save the file path from the response
        })
        .catch(error => {
          console.error("Failed to upload profile picture:", error);
        });
    }
  },
  register() {
    if (this.user.password !== this.user.confirmPassword) {
      this.registrationErrors = true;
      this.registrationErrorMsg = 'Password and Confirm Password do not match.';
      return;
    }

    authService.register(this.user)
      .then(() => {
        this.$router.push({ path: '/login', query: { registration: 'success' } });
      })
      .catch(error => {
        console.error("Registration failed:", error);
        this.registrationErrors = true;
        this.registrationErrorMsg = 'Registration failed. Please try again.';
      });
  },
},

    clearErrors() {
      this.registrationErrors = false;
      this.registrationErrorMsg = 'There were problems registering this user.';
    },
  };
</script>


<style scoped>
.form-input-group {
  margin-bottom: 1rem;
}
label {
  margin-right: 0.5rem;
}
textarea {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}
</style>
