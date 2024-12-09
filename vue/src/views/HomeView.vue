<template>
  <div class="home">
    <!-- Header Bar with Background Image -->
    <div class="header-bar">
      <!-- Logo Section (left aligned) -->
      <div class="logo-container">
        <router-link to="/">
          <img src="@/assets/images/logo.jpg" alt="Logo" class="logo" />
        </router-link>
      </div>

      <!-- Navigation Buttons (center and right aligned) -->
      <div class="nav-buttons">
        <router-link to="/" class="nav-button">Home</router-link>
        <router-link v-for="feature in features" :key="feature.name" :to="feature.route" class="nav-button">
          {{ feature.name }}
        </router-link>
      </div>

      <!-- Check-in Button (right aligned) -->
      <div class="check-in-out-section">
        <button @click="toggleCheckInOut" :class="[ 'check-in-out-button', checkInStatus ? 'check-out' : 'check-in', ]">
          {{ checkInStatus ? "Check Out" : "Check In" }}
        </button>
        <div v-if="checkInStatus" class="current-session-time">
          Current Session Time: {{ liveSessionTime }}
        </div>
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
    
    <!-- Footer Bar with Background Image -->
    <div class="footer-bar">
      <div class="footer-content">
        <img src="@/assets/icons/footer.png" alt="Footer Image" class="footer-img" />
      </div>
    </div>
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
    };
  },
  async mounted() {
    const token = this.$store.state.token;
    try {
      const response = await axios.get('/classes', {
        headers: { Authorization: `Bearer ${token}` },
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
        const response = await axios.get('/gym-visit/check-in-status', {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
        });
        const isCheckedIn = response.data.checkedIn;
        this.$store.commit('checkinTimer/SET_CHECK_IN_STATUS', isCheckedIn);

        if (isCheckedIn) {
          await this.fetchCheckInTime();
        } else {
          this.$store.dispatch('checkinTimer/stopLiveTimer');
          this.$store.commit('checkinTimer/RESET_TIMER');
        }
      } catch (error) {
        console.error('Error fetching check-in status:', error.message);
      }
    },
    async fetchCheckInTime() {
      try {
        const response = await axios.get('/gym-visit/current-session-time', {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
        });
        const checkInTime = new Date(response.data);
        if (!isNaN(checkInTime.getTime())) {
          this.$store.commit('checkinTimer/SET_CHECK_IN_TIME', checkInTime);
          this.$store.dispatch('checkinTimer/startLiveTimer');
        } else {
          console.error('Invalid check-in time received:', response.data);
        }
      } catch (error) {
        console.error('Error fetching check-in time:', error.message);
      }
    },
    async checkIn() {
      try {
        await axios.post('/gym-visit/check-in', {}, {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
        });
        this.$store.commit('checkinTimer/SET_CHECK_IN_STATUS', true);
        await this.fetchCheckInTime();
      } catch (error) {
        console.error('Error during check-in:', error.message);
      }
    },
    async checkOut() {
      try {
        await axios.post('/gym-visit/check-out', {}, {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
        });
        this.$store.commit('checkinTimer/SET_CHECK_IN_STATUS', false);
        this.$store.dispatch('checkinTimer/stopLiveTimer');
        this.$store.commit('checkinTimer/RESET_TIMER');
      } catch (error) {
        console.error('Error during check-out:', error.message);
      }
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

.header-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 20px;
  background-image: url('@/assets/icons/header.png');
  background-size: cover;
  background-position: center;
}

.logo-container {
  flex: 0 0 auto;
}

.logo {
  width: 120px;
  height: auto;
}

.nav-buttons {
  display: flex;
  gap: 15px;
  justify-content: center;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  
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

.check-in-out-section {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.check-in-out-button {
  color: white;
  border-radius: 5px;
  font-size: 14px;
  text-decoration: none;
  padding: 10px 20px;
  margin-left: 20px;
  cursor: pointer;
  transition: background-color 0.3s;
  border: none;
  background-color: var(--check-in-color);
  position: fixed;
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
  color: #2e2a2a;
  margin-right: 130px;
  white-space: nowrap;
  font-weight: bold;
}

.home-header {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 10px;
  text-align: center;
}

.welcome-message {
  max-width: 800px;
  margin-top: 0;
  text-align: center;
  font-size: 1.8rem;
}

.welcome-message .highlight {
  color: #ff6f61;
  font-weight: bold;
  font-size: 4rem;
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

/* Footer Bar Styling */
.footer-bar {
  margin-bottom: 0;
  background-image: url('@/assets/icons/footer.png');
  background-size: cover;
  background-position: center;
  padding: 10px 0;
  margin-top: auto;
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
