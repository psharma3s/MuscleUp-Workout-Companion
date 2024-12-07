<template>
  <div class="register">
    <div id="register-form-container">
      
      <form class="register-form" v-on:submit.prevent="register">
        <h1 class="form-title">Create Account</h1>
        <div v-if="registrationErrors" class="error-message">
          {{ registrationErrorMsg }}
        </div>
        <div class="form-input-group">
          <label for="username">Username</label>
          <input
            type="text"
            id="username"
            v-model="user.username"
            required
            autofocus
            @input="clearErrors"
          />
        </div>
        <div class="form-input-group">
          <label for="password">Password</label>
          <input
            type="password"
            id="password"
            v-model="user.password"
            required
            @input="clearErrors"
          />
        </div>
        <div class="form-input-group">
          <label for="confirmPassword">Confirm Password</label>
          <input
            type="password"
            id="confirmPassword"
            v-model="user.confirmPassword"
            required
            @input="clearErrors"
          />
        </div>
        <div class="form-input-group">
          <label for="name">Name</label>
          <input
            type="text"
            id="name"
            v-model="user.name"
            required
            @input="clearErrors"
          />
        </div>
        <div class="form-input-group">
          <label for="email">Email Address</label>
          <input
            type="email"
            id="email"
            v-model="user.email"
            required
            @input="clearErrors"
          />
        </div>
        <div class="form-input-group">
          <label for="workoutGoals">Workout Goals</label>
          <select id="workoutGoals" v-model="user.workoutGoals" required>
            <option disabled value="">Select your workout goal</option>
            <option value="Bulk">Bulk</option>
            <option value="Lose Weight">Lose Weight</option>
            <option value="Shred">Shred</option>
            <option value="General Fitness">General Fitness</option>
            <option value="Other">Other</option>
          </select>
          <p class="info-message">
            Don't worry, you can always change this later in your profile!
          </p>
        </div>
        <div class="form-input-group">
          <label for="role">Role</label>
          <select id="role" v-model="user.role" required>
            <option value="ROLE_USER">Member</option>
            <option value="ROLE_EMPLOYEE">Employee</option>
          </select>
        </div>
        <button type="submit" class="submit-button">Create Account</button>
        <p class="login-link">
          Already have an account?
          <router-link v-bind:to="{ name: 'login' }">Log in here</router-link>.
        </p>
      </form>
    </div>
  </div>
</template>

<script>
import authService from "../services/AuthService";

export default {
  data() {
    return {
      user: {
        username: "",
        password: "",
        confirmPassword: "",
        role: "ROLE_USER", // Default to "Member"
        name: "",
        email: "",
        workoutGoals: "",
      },
      registrationErrors: false,
      registrationErrorMsg: "There were problems registering this user.",
    };
  },
  methods: {
    register() {
      if (this.user.password !== this.user.confirmPassword) {
        this.registrationErrors = true;
        this.registrationErrorMsg =
          "Password and Confirm Password do not match.";
        return;
      }

      authService
        .register(this.user)
        .then(() => {
          this.$router.push({ path: "/login", query: { registration: "success" } });
        })
        .catch((error) => {
          console.error("Registration failed:", error);
          this.registrationErrors = true;
          this.registrationErrorMsg = "Registration failed. Please try again.";
        });
    },
    clearErrors() {
      this.registrationErrors = false;
      this.registrationErrorMsg = "There were problems registering this user.";
    },
  },
};
</script>

<style scoped>
body {
  font-family: 'Arial', sans-serif;
  margin: 0;
  padding: 0;
}

.register {
  display: flex; /* Enables flexbox for centering */
  justify-content: center; /* Horizontally centers the content */
  align-items: center; /* Vertically centers the content */
  position: fixed;
  width: 100%;
  height: 100vh;
  overflow: hidden; /* Prevent scrollbars for the image */
  z-index: 1; /* Ensures content stays above the background */
}

.register::before {
  content: ""; 
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('@/assets/images/background-picture.jpg') no-repeat
    center center;
  background-size: cover;
  z-index: -1;
}

.register-form {
  background: rgba(255, 255, 255, 0.719);
  padding: 2.5rem;
  border-radius: 10px;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
  text-align: center;
}

.form-title {
  font-size: 1.8rem;
  color: #333;
  margin-bottom: 1rem;

}

.form-input-group {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  font-weight: bold;
  margin-bottom: 0.5rem;
  color: #555;
}

input,
textarea,
select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 1rem;
}

input:focus,
textarea:focus,
select:focus {
  border-color: #6c63ff;
  outline: none;
  box-shadow: 0 0 5px rgba(108, 99, 255, 0.5);
}

.info-message {
  font-size: 0.9rem;
  color: #292727;
  margin-top: 0.5rem;
  font-weight: bold;
}

.submit-button {
  width: 100%;
  padding: 0.75rem;
  background-color: #6c63ff;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 1.2rem;
  cursor: pointer;
  transition: background-color 0.3s;
}

.submit-button:hover {
  background-color: #514acb;
}

.login-link {
  text-align: center;
  margin-top: 1rem;
  font-size: 0.9rem;
}

.login-link a {
  color: #6c63ff;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}

.error-message {
  background-color: #ffe6e6;
  color: #cc0000;
  padding: 0.5rem;
  margin-bottom: 1rem;
  border-radius: 5px;
}
</style>
