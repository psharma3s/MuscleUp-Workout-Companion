<template>
  <div class="home">
    <!-- Header Bar -->
    <div class="header-bar">
      <div class="logo-container">
        <img src="@/assets/images/logo.jpg" alt="Logo" class="logo" />
      </div>
      <!-- Navigation Buttons -->
      <div class="nav-buttons">
        <router-link to="/" class="nav-button">🏠 Home</router-link>
        <router-link
          v-for="feature in features"
          :key="feature.name"
          :to="feature.route"
          class="feature-button"
        >
          {{ feature.name }}
        </router-link>
        
      </div>
    </div>

    <!-- Main Content -->
    <div class="home-header">
      <div class="profile-section">
        <div class="profile-picture">
          <img :src="profilePictureUrl" alt="Profile Picture" />
        </div>
        <div class="welcome-message">
          <h1>Welcome, <span class="highlight">{{ userName }}</span>!</h1>
          <p>Your journey to fitness starts here. Explore, progress, and conquer your goals!</p>
        </div>
      </div>
    </div>

    <!-- Start Workout Button -->
    <div class="start-workout-section">
      <router-link to="/startworkout" class="start-workout-button">
        Start Workout
      </router-link>
    </div>
  </div>
</template>

<script>
export default {
  computed: {
    userName() {
      return this.$store.state.user.name || "User";
    },
    profilePictureUrl() {
      const baseUrl = import.meta.env.VITE_BACKEND_URL; // Ensure the base URL is correct
  return this.$store.state.user.profilePicture
    ? `${baseUrl}${this.$store.state.user.profilePicture}`
    : "@/assets/icons/default-profile.png";

    },
  },
  data() {
    return {
      features: [
        {
          name: "View Workout Metrics",
          route: "/workout-metrics",
          icon: "@/assets/icons/workout-metrics.png",
        },
        {
          name: "Profile",
          route: "/profile",
          icon: "@/assets/icons/edit-profile.png",
        },
        {
          name: "Gym Check-In / Check-Out",
          route: "/gym-checkin",
          icon: "@/assets/icons/gym-checkin.png",
        },
        {
          name: "Logout",
          route: "/logout",
        }
      ],
    };
  },
};
</script>

<style scoped>

body {
  font-family: 'Arial', sans-serif;
  margin: 0;
  padding: 0;
}

.home {
  position: fixed;
  width: 100%;
  height: 100vh;
  overflow: hidden; /* Prevent scrollbars for the image */

  /* This ensures content stays above the background */
  z-index: 1;
}

.home::before {
  content: ""; /* Required for pseudo-element */
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('@/assets/images/background-picture-homepage.jpg') no-repeat center center;
  background-size: cover;
  opacity: 0.3; 
  z-index: -1; 
}



.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background-color: rgba(3, 3, 3, 0.9); /* Slight Transparency */
  color: white;
}
.logo-container {
  flex: 1;
}

.logo {
  width: 150px;
  height: auto;
}

.nav-button,
.feature-button {
  color: white;
  text-decoration: none;
  padding: 10px 20px;
  border-radius: 5px;
  background-color: #555;
  margin: 0 5px;
  transition: background-color 0.3s;
}

.nav-button:hover,
.feature-button:hover {
  background-color: #777;
}


.logout-button {
  background-color: #d9534f;
}

.logout-button:hover {
  background-color: #c9302c;
}


.profile-section {
  text-align: center;
  margin: 20px 0;
}

.profile-picture img {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 3px solid #ddd;
  object-fit: cover;
}

.welcome-message h1 {
  font-size: 2rem;
  color: #333;
}

.welcome-message .highlight {
  color: #ff6f61;
}


.start-workout-section {
  text-align: center;
  margin: 30px 0;
}

.start-workout-button {
  display: inline-block;
  background-color: #28a745;
  color: white;
  padding: 15px 30px;
  font-size: 1.2rem;
  border-radius: 5px;
  text-decoration: none;
  transition: background-color 0.3s;
}

.start-workout-button:hover {
  background-color: #218838;
}
</style>
