<template>
  <div class="profile-view">
    <!-- Sidebar Navigation -->
    <div class="sidebar">
      <div class="profile-picture-container">
        <img :src="user.profilePictureUrl || ('src/assets/icons/default-profile.png')" 
             alt="Profile Picture" class="profile-image" />
        <h2>{{ user.username }}</h2>
        <router-link to="/edit-profile" class="edit-profile-button">Edit Profile</router-link>
      </div>

      <!-- Navigation Links -->
      <ul class="nav-links">
        <li><router-link to="/home">Home</router-link></li>
        <li><router-link to="/workout-metrics">Workout Metrics</router-link></li>
        <li><router-link to="/gym-checkin">History</router-link></li>
        <li><router-link to="/blog">Blog</router-link></li>
        <li><router-link to="/logout">Logout</router-link></li>
      </ul>
    </div>

    <!-- Main Profile Content -->
    <div class="main-content">
      <!-- Profile Info -->
      <div class="profile-details">
        <h3>Profile Information</h3>
        <div class="profile-info">
          <p><strong>Weight:</strong> {{ user.weight }} kg</p>
          <p><strong>Height:</strong> {{ user.height }} cm</p>
          <p><strong>Workout Goals:</strong> {{ user.workoutGoals }}</p>
        </div>

        <!-- Fitness Stats -->
        <div class="fitness-stats">
          <div class="stats-card">
            <h4>Calories Burned</h4>
            <div class="progress-bar">
              <div class="progress" :style="{ width: user.caloriesBurnedProgress + '%' }"></div>
            </div>
            <p>{{ user.caloriesBurned }} kcal</p>
          </div>

          <div class="stats-card">
            <h4>Total Workouts</h4>
            <p>{{ user.totalWorkouts }}</p>
          </div>

          <div class="stats-card">
            <h4>Classes Attended</h4>
            <p>{{ user.classesAttended }}</p>
          </div>
        </div>

        <!-- Workout Progress Graph -->
        <div class="progress-graph">
          <h3>Workout Progress</h3>
          <div class="graph-container">
            <div class="graph">
              <!-- Placeholder for graph -->
              <canvas id="progressGraph"></canvas>
            </div>
          </div>
        </div>

        <!-- Recent Activities -->
        <div class="recent-activities">
          <h3>Recent Activities</h3>
          <div class="activity-list">
            <div class="activity-card" v-for="activity in user.recentActivities" :key="activity.id">
              <h4>{{ activity.name }}</h4>
              <p>{{ activity.details }}</p>
              <p><strong>Date:</strong> {{ activity.date }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
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
  position: static;
  display: flex;
  flex-direction: row;
  max-height: 80vh; 
  font-family: 'Arial', sans-serif;
  background-color: #fafafabb;
  padding: 10px;
  margin: 0; 
}


.sidebar {
  width: 220px;
  background-color: #007bff;
  color: white;
  padding: 20px;
  border-radius: 10px;
  position: sticky;
  top: 20px;
  flex-shrink: 0; 
  height: calc(100vh - 40px); 
  box-sizing: border-box;
  max-height: 70vh;
}

.profile-picture-container {
  text-align: center;
  margin-bottom: 20px;
}

.profile-image {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 10px;
}

.edit-profile-button {
  display: inline-block;
  padding: 6px 15px;
  background-color: #fff;
  color: #007bff;
  border-radius: 5px;
  text-decoration: none;
  margin-top: 10px;
  font-size: 0.9rem;
}

.nav-links {
  list-style-type: none;
  padding: 0;
}

.nav-links li {
  margin: 8px 0;
}

.nav-links li a {
  color: white;
  text-decoration: none;
  font-size: 1rem;
}


.main-content {
  flex-grow: 1; 
  width: calc(100% - 240px); 
  padding: 20px;
  margin: 0;
  overflow-y: auto; 
  display: flex;
  flex-direction: column;
  gap: 20px; 
}

.profile-details {
  background-color: rgba(255, 255, 255, 0.589);
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.profile-info {
  margin-bottom: 15px;
}

.profile-info h3 {
  font-size: 1.2rem;
  margin-bottom: 10px;
}

.fitness-stats {
  display: flex;
  justify-content: space-between;
}

.stats-card {
  background-color: #007bff;
  padding: 12px;
  border-radius: 8px;
  width: 30%;
  text-align: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 10px;
}

.stats-card h4 {
  margin-bottom: 10px;
}

.progress-bar {
  height: 8px;
  background-color: #ddd;
  border-radius: 5px;
}

.progress {
  height: 100%;
  background-color: #00f73a;
  border-radius: 5px;
}

.progress-graph {
  margin-top: 20px;
}

.recent-activities {
  margin-top: 30px;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.activity-card {
  background-color: white;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* Responsive Design */
@media (max-width: 768px) {
  .profile-view {
    flex-direction: column;
    align-items: center;
  }

  .main-content {
    width: 100%;
  }

  .fitness-stats {
    flex-direction: column;
    align-items: center;
  }

  .stats-card {
    width: 100%;
    margin-bottom: 20px;
  }
}
</style>