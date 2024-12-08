<template>
  <div class="home">
    <!-- Header Bar -->
    <div class="header-bar">
      <div class="logo-container">
        <img src="@/assets/images/logo.jpg" alt="Logo" class="logo" />
      </div>

      <div class="check-in-out-section">
        <button @click="toggleCheckInOut" :class="[
          'check-in-out-button',
          checkInStatus ? 'check-out' : 'check-in',]">
          {{ checkInStatus ? "Check Out" : "Check In" }}
        </button>
        <div v-if="checkInStatus" class="current-session-time">
          Current Session Time: {{ liveSessionTime }}
        </div>
      </div>


      <!-- Navigation Buttons -->
      <div class="nav-buttons">
        <router-link to="/" class="nav-button">🏠 Home</router-link>
        <router-link v-for="feature in features" :key="feature.name" :to="feature.route" class="feature-button">
          {{ feature.name }}
        </router-link>
      </div>
    </div>

    <!-- Main Content -->
    <div class="home-header">
      <div class="profile-section">
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

    <!-- Replaced Calendar and Classes Code with the new component -->
    <CalendarSchedule :userName="$store.state.user.username" :isEmployee="isEmployee" :token="$store.state.token"
      :calendarEvents="calendarEvents" @update:calendarEvents="calendarEvents = $event" />

  </div>
</template>

<script>
import axios from 'axios';
import CalendarSchedule from '@/components/CalendarSchedule.vue';

export default {
  components: {
    CalendarSchedule,
  },
  computed: {
    userName() {
      return this.$store.state.user.username || 'User';
    },
    profilePictureUrl() {
      return this.$store.state.user.profilePictureUrl
        ? `/api${this.$store.state.user.profilePictureUrl}`
        : '@/assets/icons/default-profile.png';
    },
    isEmployee() {
      return this.$store.state.user.authorities?.some(auth => auth.name === 'ROLE_EMPLOYEE');
    },
    checkInStatus() {
      return this.$store.state.checkinTimer.checkInStatus;
    },
    liveSessionTime() {
      return this.$store.state.checkinTimer.liveSessionTime;
    },

  },
  data() {
    return {
      features: [
        { name: 'View Workout Metrics', route: '/workout-metrics' },
        { name: 'Profile', route: '/profile' },
        { name: 'History', route: '/gym-checkin' },
        { name: 'Logout', route: '/logout' },
      ],
      calendarEvents: [],
      checkInTime: null,
      timerInterval: null,
    };
  },
  async mounted() {
    // Fetch all classes from the backend
    const token = this.$store.state.token;
    try {
      const response = await axios.get('/classes', {
        headers: { Authorization: `Bearer ${token}` }
      });
      this.calendarEvents = response.data;
      this.fetchCheckInStatus();
    } catch (error) {
      console.error('Failed to fetch classes', error);
    }
  },
  methods: {
    async toggleCheckInOut() {
      if (this.checkInStatus) {
        await this.checkOut();
      } else {
        await this.checkIn();
      }
    },
    async fetchCheckInStatus() {
      try {
        const response = await axios.get("/gym-visit/check-in-status", {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        const isCheckedIn = response.data.checkedIn;
        this.$store.commit("checkinTimer/SET_CHECK_IN_STATUS", isCheckedIn);




        if (isCheckedIn) {
          await this.fetchCheckInTime();
        } else {
          this.$store.dispatch("checkinTimer/stopLiveTimer");
          this.$store.commit("checkinTimer/RESET_TIMER");

        }
      } catch (error) {
        console.error("Error fetching check-in status:", error.message);
      }
    },
    async fetchCheckInTime() {
      try {
        const response = await axios.get("/gym-visit/current-session-time", {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        this.checkInTime = new Date(response.data);

        const checkInTime = new Date(response.data);
        if (!isNaN(checkInTime.getTime())) {
          this.$store.commit("checkinTimer/SET_CHECK_IN_TIME", checkInTime);
          this.$store.dispatch("checkinTimer/startLiveTimer");

        } else {
          console.error("Invalid check-in time received:", response.data);
        }
      } catch (error) {
        console.error("Error fetching check-in time:", error.message);
        this.checkInTime = null;
      }
    },
    async checkIn() {
      try {
        await axios.post(
          "/gym-visit/check-in",
          {},
          {
            headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
          }
        );
        this.$store.commit("checkinTimer/SET_CHECK_IN_STATUS", true);
        await this.fetchCheckInTime();
      } catch (error) {
        console.error("Error during check-in:", error.message);
      }
    },
    async checkOut() {
      try {
        await axios.post(
          "/gym-visit/check-out",
          {},
          {
            headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
          }
        );
        this.$store.commit("checkinTimer/SET_CHECK_IN_STATUS", false);
        this.$store.dispatch("checkinTimer/stopLiveTimer");
        this.$store.commit("checkinTimer/RESET_TIMER");

      } catch (error) {
        console.error("Error during check-out:", error.message);
      }
    },
  },
  beforeDestroy() {
    this.$store.dispatch("checkinTimer/stopLiveTimer");
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
  overflow: hidden;
  z-index: 1;
}

.home::before {
  content: '';
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
  align-items: center;
  padding: 10px 20px;
  background-color: rgba(3, 3, 3, 0.9);
  color: white;
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

.check-in-out-section {
  display: flex;
  align-items: center;
  flex-grow: 1;
  margin-left: 20px;
  margin-right: auto;
}

.check-in-out-button {
  color: white;
  border-radius: 5px;
  font-size: 14px;
  text-decoration: none;
  padding: 10px 20px;
  margin-right: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
  border: none;
  margin-right: 20px;
  background-color: var(--check-in-color);
}

.check-in-out-button.check-in {
  --check-in-color: #4caf50;
}

.check-in-out-button.check-out {
  --check-in-color: #f44336;
}

.check-in-button:hover:enabled {
  background-color: #45a049;
}

.check-in:hover {
  background-color: #5cd65c;
}

.check-out:hover {
  background-color: #ff6666;
}

.check-in-out-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.current-session-time {
  font-size: 20px;
  color: #fff;
  margin-left: 10px;
  white-space: nowrap;

}
</style>
