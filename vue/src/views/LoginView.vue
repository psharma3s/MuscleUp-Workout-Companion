<template>

  <div id="login-page" class="login-page-container">
      
      <img src="@/assets/images/logo.jpg" alt="MuscleUp Logo" class="login-logo" />
    <div id="login" class="login-container">
      <form class="login-form" v-on:submit.prevent="login">
        
        <h1 class="login-title">Welcome To Your Favorite Workout Companion</h1>
        
        <p class="login-subtitle">Please sign in to your account</p>
        <div class="alert alert-danger" v-if="invalidCredentials">
          Invalid username or password. Please try again!
        </div>
        <div class="alert alert-success" v-if="this.$route.query.registration">
          Registration successful! Please sign in to continue.
        </div>
  
        <div class="form-input-group">
          <label for="username" class="form-label">Username</label>
          <input
            type="text"
            id="username"
            class="form-input"
            placeholder="Enter your username"
            v-model="user.username"
            required
            autofocus
          />
        </div>
        <div class="form-input-group">
          <label for="password" class="form-label">Password</label>
          <input
            type="password"
            id="password"
            class="form-input"
            placeholder="Enter your password"
            v-model="user.password"
            required
          />
        </div>
  
        <button type="submit" class="btn btn-primary">Sign In</button>
        <p class="register-link">
          Don't have an account? 
          <router-link class="link" v-bind:to="{ name: 'register' }">Sign up here</router-link>.
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
        },
        invalidCredentials: false,
      };
    },
    methods: {
      login() {
        authService
          .login(this.user)
          .then((response) => {
            if (response.status == 200) {
              this.$store.commit("SET_AUTH_TOKEN", response.data.token);
              this.$store.commit("SET_USER", response.data.user);
              this.$router.push("/");
            }
          })
          .catch((error) => {
            const response = error.response;
            if (response.status === 401) {
              this.invalidCredentials = true;
            }
          });
      },
    },
  };
  </script>
  
  <style scoped>
  
  .login-container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100vh;
    width: 100vw;
    background: url('@/assets/images/background-picture.jpg') no-repeat center center fixed;
    background-size: cover;
    backdrop-filter: blur(5px); 
    position: fixed;
  }
  
  
  .login-logo {
    position: absolute;
    top: 5%; 
    left: 50%; 
    transform: translateX(-50%); 
    max-width: 250px; 
    height: auto;
    z-index: 10; 
  }
  
  
  
  .login-form {
    background-color: rgba(221, 223, 214, 0.788);
    padding: 2rem;
    border-radius: 8px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.87);
    width: 100%;
    max-width: 400px;
    text-align: center;
  }
  
  
  .login-title {
    font-size: 2rem;
    color: #4e0115;
    margin-bottom: 0.5rem;
  }
  
  .login-subtitle {
    font-size: 1.5rem;
    color: #130b0b;
    margin-bottom: 1.5rem;
  }
  
  
  .alert {
    padding: 1rem;
    border-radius: 4px;
    margin-bottom: 1rem;
    text-align: left;
  }
  
  .alert-danger {
    background-color: #f8d7da;
    color: #842029;
    border: 1px solid #f5c2c7;
  }
  
  .alert-success {
    background-color: #d1e7dd;
    color: #0f5132;
    border: 1px solid #badbcc;
  }
  
  
  .form-input-group {
    margin-bottom: 1.5rem;
    text-align: left;
  }
  
  .form-label {
    display: block;
    font-weight: bold;
    margin-bottom: 0.5rem;
    color: #555;
  }
  
  .form-input {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 1rem;
  }
  
  .form-input:focus {
    outline: none;
    border-color: #83a4d4;
    box-shadow: 0 0 5px rgba(131, 164, 212, 0.5);
  }
  
  
  .btn {
    display: inline-block;
    padding: 0.75rem 1.5rem;
    font-size: 1rem;
    color: white;
    background-color: #c53f0a;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s ease;
  }
  
  .btn-primary:hover {
    background-color: #6f94c1;
  }
  
  
  .register-link {
    margin-top: 1rem;
    font-size: 0.9rem;
    color: #333;
  }
  
  .link {
    color: #c53f0a;
    text-decoration: none;
    font-weight: bold;
  }
  
  .link:hover {
    text-decoration: underline;
  }
  </style>
  