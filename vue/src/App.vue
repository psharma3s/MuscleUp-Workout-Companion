<template>
  <div id="capstone-app">
    <div v-if="$route.name !== 'login' && $route.name !== 'register'" class="background-image"></div>

    <div v-if="$route.name !== 'login' && $route.name !== 'register' && $route.name !== 'home'" class="header-container">
      <div class="header-bar">
        <router-link to="/" class="logo-link">
          <img src="@/assets/images/logo.jpg" alt="Logo" class="logo" />
        </router-link>

        <div v-if="$route.name !== 'profile'" class="nav-buttons">
          <router-link to="/workout-metrics" class="nav-button">View Workout Metrics</router-link>
          <router-link to="/gym-checkin" class="nav-button">History</router-link>
          <router-link v-if="isEmployee" to="/employee-checkout" class="nav-button">Employee Checkout</router-link>
          <router-link to="/startworkout" class="nav-button">Start Workout</router-link>
          <router-link to="/profile" class="nav-button">Profile</router-link>
          <router-link to="/logout" class="nav-button">Logout</router-link>
        </div>
      </div>

      
      <div class="page-title">
        <div v-if="$route.name === 'gym-checkin'">
          <h1>Gym Check-In History</h1>
        </div>
        <div v-if="$route.name === 'workout-metrics'">
          <h1>Workout and Equipment Usage</h1>
        </div>
        <div v-if="$route.name === 'employee-checkout'">
          <h1>Gym Members Check-In Status</h1>
        </div>
        <div v-if="$route.name === 'startworkout'">
          <h1>Start Workout</h1>
        </div>
        <div v-if="$route.name === 'equipmentuse'">
          <h1>Workout Equipment Usage Metrics</h1>
        </div>
      </div>
    </div>

    
    <router-view />

    
    <div v-if="$route.name !== 'login' && $route.name !== 'register' && $route.name !== 'home'" class="footer-bar">
      <div class="footer-content">
        <img src="@/assets/icons/footer.png" alt="Footer Image" class="footer-img" />
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "App",
  data() {
    return {
      features: [
        { name: 'View Workout Metrics', route: '/workout-metrics' },
        { name: 'History', route: '/gym-checkin' },
        { name: 'Employee Checkout', route: '/employee-checkout' },
        { name: 'Start Workout', route: '/startworkout' },
        { name: 'Logout', route: '/logout' }
      ]
    };
  },
  computed: {
    userRole() {
      return this.$store.state.user.role;
    }
  },
};
</script>

<style scoped>
#capstone-app {
  font-family: 'Arial', sans-serif;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  position: relative;
  width: 100%;
}

.background-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('@/assets/images/background-picture-homepage.jpg') no-repeat center center;
  background-size: cover;
  opacity: 0.7;
  z-index: -1;
}

/* Header Bar Styling */
.header-container {
  background-image: url('@/assets/icons/header.png');
  background-size: cover;
  background-position: center;
  padding: 10px 0; 
  z-index: 10;
}

.header-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 30px 20px; 
  position: relative;
  flex-direction: column;
  align-items: center;
}
.logo-link {
  position: fixed;
  left: 20px;
  top: 20px;
}
.logo {
  width: 150px; /* Adjusted size */
  height: auto;
  border-radius: 10%;
}

.nav-buttons {
  display: flex;
  gap: 15px;
  justify-content: center;
  width: 100%;
}

.nav-button {
  display: flex;
  font-weight: bold;
  color: rgb(0, 0, 0);
  text-decoration: none;
  padding: 8px 15px;
  border-radius: 5px;
  background-color: #f58f3be0;
  transition: background-color 0.3s;
}

.nav-button:hover {
  background-color: #72b9f3;
}
@font-face {
  font-family: 'MyCustomFont';
  src: url('src/assets/fonts/aaa.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}

.page-title {
  font-family: 'MyCustomFont';
  letter-spacing: 2px;
  opacity: 0.9;
  z-index: 20;
  display: flex;
  justify-content: center;
  font-size: 2rem; 
  font-weight: 0; 
  color: #130c01; 
  text-align: center;
  margin-top: 50px; 
  margin-bottom: 20px;
  flex-grow: 1;
  text-transform: capitalize;
  background-color: #f9f9fab7; 
  padding: 15px 30px; 
  border-radius: 10px; 
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); 
  text-shadow: 1px 1px 5px rgba(0, 0, 0, 0.3); 
  transition: all 0.3s ease; 
}



/* Footer Bar Styling */
.footer-bar {
  margin-bottom: 0;
  background-size: cover;
  background-position: center;
  padding: 10px 0;
  margin-top: auto;
  z-index: 10;
}

.footer-content {
  text-align: center;
}

.footer-img {
  width: 100%;
  height: auto;
  max-height: 100px;
}
</style>
