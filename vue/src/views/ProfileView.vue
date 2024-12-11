<template>
  <div class="profile-view">
    
    <div class="sidebar">
      <div class="profile-picture-container">
        <img :src="user.profilePictureUrl || ('src/assets/icons/default-profile.png')" 
             alt="Profile Picture" class="profile-image" />
        <h2>{{ user.username }}</h2>
        <router-link to="/edit-profile" class="edit-profile-button">Edit Profile</router-link>
      </div>

      
      <ul class="nav-links">
        <li><router-link to="/" class="nav-link">Home</router-link></li>
        <li><router-link to="/workout-metrics" class="nav-link">Workout Metrics</router-link></li>
        <li><router-link to="/gym-checkin" class="nav-link">History</router-link></li>
        <li><router-link to="/logout" class="nav-link">Logout</router-link></li>
      </ul>
    </div>

    
    <div class="main-content">
      <div class="profile-details">
       
        <h3>Profile Information</h3>
        <div class="profile-info">
          <p><strong>Weight:</strong> {{ user.weight }} lbs</p>
          <p><strong>Height:</strong> {{ user.height }} cm</p>
          <p><strong>Workout Goals:</strong> {{ user.workoutGoals }}</p>
        </div>

        
        <div class="fitness-stats">
          <div class="stats-card">
            <h4>Total Workouts</h4>
            <p>{{ totalWorkouts }}</p>
          </div>

          <div class="stats-card">
            <h4>Classes Attended</h4>
            <p>{{ totalClassesAttended }}</p>
          </div>
        </div>
      </div>

      <div class="milestone-goals">
        <h3>Milestones</h3>

        <div class="goal-container">
          <input 
            type="text" 
            v-model="newGoal.name" 
            placeholder="Enter your milestone goal" 
            class="goal-input"
          />
          <button @click="addGoal" class="add-goal-btn">
            <i class="fas fa-plus"></i> Add Goal
          </button>
        </div>

        
        <div v-if="goals.length > 0">
          <div class="goal-list">
            <div v-for="(goal, index) in goals" :key="index" class="goal-card">
              <div class="goal-info">
                <p class="goal-description">
                  <strong>{{ goal.name }}</strong>
                </p>
                <span v-if="goal.completed" class="completed-message">Completed!</span>
              </div>
              <div class="goal-actions">
                <button 
                  v-if="!goal.completed" 
                  @click="markAsCompleted(index)" 
                  class="complete-btn">
                  Complete
                </button>
                <button 
                  @click="deleteGoal(index)" 
                  class="delete-btn">
                  <i class="fas fa-trash"></i> Delete
                </button>
              </div>
            </div>
          </div>
        </div>
        <div v-else>
          <p>No milestones set yet.</p>
        </div>
      </div>

      
      <div v-if="showCongrats" class="congrats-container">
        <img src="@/assets/images/congrats.gif" alt="Congratulations" class="congrats-gif" />
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      newGoal: {
        name: "",
      },
      goals: JSON.parse(localStorage.getItem('goals')) || [], // Load goals from localStorage
    showCongrats: false,
    };
  },
  methods: {
  addGoal() {
    if (!this.newGoal.name) return;

    
    this.goals.push({
      name: this.newGoal.name,
      completed: false,
    });

    
    localStorage.setItem("goals", JSON.stringify(this.goals));

    
    this.newGoal.name = "";
  },

  markAsCompleted(index) {
    
    this.goals[index].completed = true;

    
    this.showCongratulations();

    
    localStorage.setItem("goals", JSON.stringify(this.goals));
  },

  deleteGoal(index) {
    
    this.goals.splice(index, 1);

    
    localStorage.setItem("goals", JSON.stringify(this.goals));
  },

  showCongratulations() {
    this.showCongrats = true;

    
    setTimeout(() => {
      this.showCongrats = false;
    }, 2000);
  }
},


  computed: {
    user() {
      return this.$store.state.user;
    },
    metrics() {
      return this.$store.getters['workoutMetrics/metrics']; 
    },
    totalWorkouts() {
      return this.metrics.length;
    },
    totalClassesAttended() {
      return this.user.classesAttended || 10;
    }
  },
  mounted() {
    const userId = this.user.id;
    if (userId) {
      this.$store.dispatch('workoutMetrics/fetchMetricsByUserId', userId).catch((error) => {
        console.error('Failed to load metrics:', error);
      });
    }
    
  },
};
</script>

<style scoped>

.congrats-container {
  position: absolute;
  top: 80px; 
  left: 80%;
  transform: translateX(-50%);
  z-index: 9999;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%; 
  text-align: center;
}

.congrats-gif {
  width: 200px;
  height: auto;
  animation: fadeInOut 3s ease-out;
}

@keyframes fadeInOut {
  0% {
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0;
  }
}



.profile-view {
  display: flex;
  flex-direction: row;
  font-family: 'Arial', sans-serif;
  background-color: #fafafabb;
  padding: 20px;
  margin: 0;
  background-image: url('/src/assets/images/profile.avif');
  background-size: cover;
  background-position: center;
}

.sidebar {
  width: 240px;
  background-color: #007bff;
  color: white;
  padding: 30px;
  border-radius: 10px;
  position: sticky;
  top: 20px;
  flex-shrink: 0;
  box-sizing: border-box;
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
  padding: 6px 15px;
  background-color: #fff;
  color: #007bff;
  border-radius: 5px;
  text-decoration: none;
  font-size: 0.9rem;
}

.nav-links {
  list-style-type: none;
  padding: 0;
}

.nav-links li {
  margin: 15px 0;
}

.nav-links li a {
  color: white;
  text-decoration: none;
  font-size: 1.2rem;
  display: block;
  padding: 10px;
  border-radius: 5px;
  transition: background-color 0.3s ease;
}

.nav-links li a:hover {
  background-color: #0056b3;
}

.main-content {
  flex-grow: 1;
  padding: 20px;
  margin-left: 20px;
  overflow-y: auto;
  display: flex;
  gap: 20px;
}

.profile-details {
  background-color: rgba(243, 236, 236, 0.589);
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  flex: 1;
}

.profile-info p {
  font-size: 1.2rem;
  margin-bottom: 10px;
}

.fitness-stats {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 30px;
}

.stats-card {
  background-color: #007bff;
  padding: 15px;
  border-radius: 8px;
  width: 100%;
  text-align: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.stats-card h4 {
  margin-bottom: 10px;
  font-size: 1.2rem;
  font-weight: bold;
}

.milestone-goals {
  padding: 30px;
  background-color: #f0f8ff;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 40%;
  max-height: 400px; 
  overflow-y: auto; 
}

.milestone-goals h3 {
  font-size: 1.4rem;
  color: #007bff;
  font-weight: bold;
  margin-bottom: 20px;
}

.goal-container {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.goal-input {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  width: 75%;
  font-size: 14px;
}

.add-goal-btn {
  padding: 10px 15px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
}

.add-goal-btn:hover {
  background-color: #0056b3;
}

.goal-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.goal-card {
  display: flex;
  justify-content: space-between;
  background-color: #ffffff;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.goal-info {
  display: flex;
  flex-direction: column;
}

.goal-description {
  font-weight: bold;
}

.completed-message {
  color: green;
  font-weight: bold;
}

.goal-actions {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.complete-btn,
.delete-btn {
  padding: 8px 12px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-bottom: 5px;
}

.complete-btn:hover {
  background-color: #218838;
}

.delete-btn {
  background-color: #dc3545;
}

.delete-btn:hover {
  background-color: #c82333;
}

button i {
  margin-right: 5px;
}
</style>
