<template>
  <div id="capstone-app">
   
    <div v-if="$route.name !== 'login' && $route.name !== 'register'" class="background-image"></div>

    <!-- Header Bar: Logo and Navigation -->
    <div v-if="$route.name !== 'login' && $route.name !== 'register' && $route.name !== 'home'" class="header-container">
      <div class="header-bar">
        <router-link to="/" class="logo-link">
          <img src="@/assets/images/logo.jpg" alt="Logo" class="logo" />
        </router-link>
        <!-- Navigation Links Visible Only on Home Page -->
        <div v-if="$route.name === 'home'" class="nav-buttons">
          <router-link to="/" class="nav-button">Home</router-link>
          <router-link v-for="feature in features" :key="feature.name" :to="feature.route" class="nav-button">
            {{ feature.name }}
          </router-link>
        </div>
      </div>
    </div>

    <!-- Router View for all pages -->
    <router-view />

    <!-- Footer Bar -->
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
        { name: 'Profile', route: '/profile' },
        { name: 'History', route: '/gym-checkin' },
        { name: 'Logout', route: '/logout' }
      ]
    };
  },
};
</script>

<style scoped>
/* General Layout Styling for the page */
#capstone-app {
  font-family: 'Arial', sans-serif;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  position: fixed;
  width: 100%;
}

/* Background Image Styling (For Every Page Except Login and Register) */
.background-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('@/assets/images/background-picture-homepage.jpg') no-repeat center center;
  background-size: cover;
  opacity: 0.7; /* Adjust opacity of the background */
  z-index: -1; /* Ensure it's behind all content */
}

/* Header Bar Styling */
.header-container {
  background-image: url('@/assets/icons/header.png');
  background-size: cover;
  background-position: center;
  padding: 20px 0;
  z-index: 10; /* Ensure header is above background */
}

.header-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 20px;
}

.logo {
  width: 180px;
  height: auto;
}

.nav-buttons {
  display: flex;
  gap: 15px;
}

.nav-button {
  color: white;
  text-decoration: none;
  padding: 10px 20px;
  border-radius: 5px;
  background-color: #5dd9ff;
  transition: background-color 0.3s;
}

.nav-button:hover {
  background-color: #72b9f3;
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
