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

      
      <div class="nav-buttons">
                  <router-link v-if="isEmployee" to="/equipmentuse" class="nav-button">
            Equipment Usage
            </router-link>
        <router-link
          v-for="feature in features"
          :key="feature.name"
          :to="feature.route"
          class="nav-button"
        >
          {{ feature.name }}
        </router-link>
      </div>

      <!-- Check-in Button (right aligned) -->
      <div class="check-in-out-section">
        <button
          @click="toggleCheckInOut"
          :class="[
            'check-in-out-button',
            checkInStatus ? 'check-out' : 'check-in',
          ]"
        >
          {{ checkInStatus ? "Check Out" : "Check In" }}
        </button>
        <div v-if="checkInStatus" class="current-session-time">
          Current Session Time: {{ liveSessionTime }}
        </div>
      </div>
    </div>

    
    <div class="container">
      
      <div
        class="welcome-box w-full md:w-1/3 p-6 bg-white shadow-lg rounded-lg"
      >
        <div class="welcome-message">
          <h1>
            <span class="highlight">{{ greeting }}</span>
          </h1>

          <p class="text-lg mb-4">
            Your journey to fitness starts here. Explore, progress, and conquer
            your goals!
          </p>
          
          <p class="text-sm text-gray-600">
            You can view your classes, your workout history and metrics, and so much more with this ultimate fitness companion. Thank you for using MuscleUp.
            Start your workout here!
          </p>
          
          <router-link to="/startworkout" class="start-workout-btn">
            Start Workout
          </router-link>
          <img src="src/assets/images/motivation.gif"/>
        </div>
      </div>

      
      <div
        class="calendar-box w-full md:w-2/3 p-6 bg-white shadow-lg rounded-lg"
      >
        
        <CalendarSchedule
          :userName="$store.state.user.username"
          :isEmployee="isEmployee"
          :token="$store.state.token"
          :calendarEvents="calendarEvents"
          @update:calendarEvents="calendarEvents = $event"
        />
      </div>
    </div>

    
    <div class="footer-bar">
      <div class="footer-content">
        <img
          src="@/assets/icons/footer.png"
          alt="Footer Image"
          class="footer-img"
        />
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import CalendarSchedule from "@/components/CalendarSchedule.vue";

export default {
  components: {
    CalendarSchedule,
  },
  computed: {
    greeting() {
      const hours = new Date().getHours();
      if (hours < 12) return `Good morning, ${this.userName}!`;
      else if (hours < 18) return `Good afternoon, ${this.userName}!`;
      else return `Good evening, ${this.userName}!`;
    },
    userName() {
      return this.$store.state.user.username || "User";
    },
    profilePictureUrl() {
      return this.$store.state.user.profilePictureUrl
        ? `/api${this.$store.state.user.profilePictureUrl}`
        : "@/assets/icons/default-profile.png";
    },
    isEmployee() {
      return this.$store.state.user.authorities?.some(
        (auth) => auth.name === "ROLE_EMPLOYEE"
      );
    },
    checkInStatus() {
      return this.$store.state.checkinTimer.checkInStatus;
    },
    liveSessionTime() {
      return this.$store.state.checkinTimer.liveSessionTime;
    },
  },
  data() {
    const features = [
      { name: "View Workout Metrics", route: "/workout-metrics" },
      { name: "Profile", route: "/profile" },
      { name: "History", route: "/gym-checkin" },
    ];

   
    if (
      this.$store.state.user.authorities?.some(
        (auth) => auth.name === "ROLE_EMPLOYEE"
      )
    ) {
      features.push({ name: "Employee Checkout", route: "/employee-checkout" });
    }

    
    features.push({ name: "Logout", route: "/logout" });

    return {
      features,
      calendarEvents: [],
    };
  },
  async mounted() {
    const token = this.$store.state.token;
    try {
      const response = await axios.get("/classes", {
        headers: { Authorization: `Bearer ${token}` },
      });
      this.calendarEvents = response.data;
      this.fetchCheckInStatus();
    } catch (error) {
      console.error("Failed to fetch classes", error);
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
        const checkInTime = new Date(response.data);
        if (!isNaN(checkInTime.getTime())) {
          this.$store.commit("checkinTimer/SET_CHECK_IN_TIME", checkInTime);
          this.$store.dispatch("checkinTimer/startLiveTimer");
        } else {
          console.error("Invalid check-in time received:", response.data);
        }
      } catch (error) {
        console.error("Error fetching check-in time:", error.message);
      }
    },
    async checkIn() {
      try {
        await axios.post(
          "/gym-visit/check-in",
          {},
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
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
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
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
};
</script>
<style scoped>
body {
  font-family: "Arial", sans-serif;
  margin: 0;
  padding: 0;
}


.header-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 20px;
  background-image: url("@/assets/icons/header.png");
  background-size: cover;
  background-position: center;
}

.logo-container {
  flex: 0 0 auto;
}

.logo {
  width: 120px;
  height: auto;
  border-radius: 10%;
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
  
  font-weight: bold;
  color: rgb(1, 8, 19);
  text-decoration: none;
  padding: 10px 20px;
  border-radius: 5px;
  background-color: #f58f3be0;
  margin: 10 5px;
  transition: background-color 0.3s;
}

.nav-button:hover,
.feature-button:hover {
  background-color: #8ea2c9;
}

/* Check-in Button Section */
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
  padding: 10px;
}


.welcome-box {
  background-color: #f3f2f2e7;
  border: 1px solid #e5e5e5;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 20px;
  border-radius: 8px;
  width: 100%;  
  box-sizing: border-box;
  background-image: url('/src/assets/images/welcome.png');
  background-size: cover;
  background-position: center;
  
  
}
.welcome-box img {
  width: 80%;
  height: auto;
}
.welcome-message h1 {
  color: #333;
}


@font-face {
  font-family: 'MyCustomFont';
  src: url('src/assets/fonts/aaa.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}

.welcome-message .highlight {
  font-family: 'MyCustomFont', sans-serif;
  color: #360346;
  font-size: 3rem;
}

.welcome-message p {
  font-size: 1.2rem;
}
.start-workout-btn {
  display: inline-block;
  background-color: #007bff;
  color: white;
  padding: 10px 20px;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  text-decoration: none;
  transition: background-color 0.3s ease;
}

.start-workout-btn:hover {
  background-color: #0056b3;
}


.calendar-box {
  background-color: #f9f9f9be;
  border: 1px solid #ebe4e4cc;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 20px;
  border-radius: 8px;
  margin-top: 1px;
  width: 100%;  
  box-sizing: border-box;
  background-image:  url('/src/assets/images/calendar1.png'); 
  background-position: center;
  background-size: cover;
}

/* Flex Layout for Main Sections (Welcome Box and Calendar) */
.container {
  display: flex; /* Ensure horizontal layout */
  justify-content: space-between; /* Space between the two boxes */
  gap: 20px; /* Space between boxes */
  padding: 20px;
  flex-wrap: wrap; /* Allow wrapping if needed */
}

.container .welcome-box {
  flex: 1 1 30%; /* Welcome box takes 30% of available space */
}

.container .calendar-box {
  flex: 1 1 65%; /* Calendar box takes 65% of available space */
}

/* Footer Bar */
.footer-bar {
  margin-top: 100px;
  background-image: url("@/assets/icons/footer.png");
  background-size: cover;
  background-position: center;
  padding: 10px 0;
}

.footer-content {
  text-align: center;
}

.footer-img {
  width: 100%;
  height: auto;
  max-height: 100px;
}

/* Responsiveness (Mobile) */
@media (max-width: 768px) {
  .container {
    flex-direction: column; /* Stacks vertically on mobile */
  }

  .container .welcome-box,
  .container .calendar-box {
    flex: 1 1 100%; /* Make both take full width on mobile */
  }
}
</style>
