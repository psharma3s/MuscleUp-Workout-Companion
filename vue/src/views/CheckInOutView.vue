<template>
  <div class="check-in-out-view">
  

    <!-- Main Content Container -->
    <div class="container">
      <!-- Left Section (Status and Average Time) -->
      <div class="left-section">
        <!-- Current Check-In Status -->
        <div class="status-box">
          <h3>Status: {{ checkInStatus ? "Checked In" : "Not Checked In" }}</h3>
          <h3 v-if="checkInStatus">Current Session Time: {{ liveSessionTime }}</h3>
        </div>

        <div class="time-summary-box">
          <div class="time-summary">
            <h3>Average Time Per Visit: {{ averageTime }}</h3>
          </div>
          <div class="time-summary">
            <h3>Total Time Spent in the Gym: {{ totalTime }}</h3>
          </div>
        </div>
      </div>

      <!-- Right Section (Calendar) -->
      <div class="right-section">
        <div class="calendar-section-box">
          <h3>Check-In Calendar</h3>
          <div class="calendar-header">
            <button @click="prevMonth">Previous</button>
            <span class="month-label">{{ currentMonthName }} {{ currentYear }}</span>
            <button @click="nextMonth">Next</button>
          </div>

          <div class="calendar-grid">
            <div v-for="(day, index) in weekDays" :key="'label-' + index" class="calendar-label">
              {{ day }}
            </div>

            <div v-for="(day, index) in paddedDaysInMonth" :key="'day-' + index" :class="['calendar-cell', { checkedIn: day && isCheckedIn(day) }]">
              {{ day ? day.getDate() : "" }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "CheckInOutView",
  data() {
    return {
      checkInStatus: false,
      averageTime: "Loading...",
      totalTime: "Loading...",
      checkInDates: [],
      currentMonth: new Date().getMonth(),
      currentYear: new Date().getFullYear(),
    };
  },

  computed: {
    checkInStatus() {
      return this.$store.state.checkinTimer.checkInStatus;
    },
    liveSessionTime() {
      return this.$store.state.checkinTimer.liveSessionTime;
    },

    weekDays() {
      return ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
    },

    paddedDaysInMonth() {
      const firstDayOfMonth = new Date(this.currentYear, this.currentMonth, 1);
      const lastDayOfMonth = new Date(this.currentYear, this.currentMonth + 1, 0);

      const days = [];

      for (let i = 0; i < firstDayOfMonth.getDay(); i++) {
        days.push(null);
      }

      for (let i = 1; i <= lastDayOfMonth.getDate(); i++) {
        days.push(new Date(this.currentYear, this.currentMonth, i));
      }

      return days;
    },

    currentMonthName() {
      return new Date(this.currentYear, this.currentMonth).toLocaleString("default", {
        month: "long",
      });
    },
  },

  methods: {
    async fetchCheckInStatus() {
      try {
        const response = await axios.get("/gym-visit/check-in-status", {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        this.checkInStatus = response.data.checkedIn;

        if (this.checkInStatus) {
          this.fetchCheckInTime();
        } else {
          this.liveSessionTime = "00:00:00";
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
          this.updateLiveSessionTime(checkInTime);
        } else {
          console.warn("Invalid check-in time received:", response.data);
          this.liveSessionTime = "Not Checked In";
        }
      } catch (error) {
        console.error("Error fetching check-in time:", error.message);
      }
    },
    updateLiveSessionTime(checkInTime) {
      const now = new Date();
      const elapsed = Math.floor((now - checkInTime) / 1000);
      const hours = Math.floor(elapsed / 3600)
        .toString()
        .padStart(2, "0");
      const minutes = Math.floor((elapsed % 3600) / 60)
        .toString()
        .padStart(2, "0");
      const seconds = (elapsed % 60).toString().padStart(2, "0");
      this.liveSessionTime = `${hours}:${minutes}:${seconds}`;
    },
    async fetchAverageTime() {
      try {
        const response = await axios.get("/gym-visit/average-time", {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        this.averageTime = response.data;
      } catch (error) {
        console.error("Error fetching average time:", error.message);
        this.averageTime = "Error loading average time";
      }
    },
    async fetchTotalTime() {
      try {
        const response = await axios.get("/gym-visit/total-time", {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        this.totalTime = response.data;
      } catch (error) {
        console.error("Error fetching total time:", error.message);
        this.totalTime = "Error loading total time";
      }
    },
    async fetchCheckInDates() {
      try {
        const response = await axios.get("/gym-visit/check-in-dates", {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        this.checkInDates = response.data.map(
          (date) => new Date(date).toISOString().split("T")[0]
        );
      } catch (error) {
        console.error("Error fetching check-in dates:", error.message);
      }
    },
    isCheckedIn(day) {
      if (!day) return false;
      const formattedDate = day.toISOString().split("T")[0];
      return this.checkInDates.includes(formattedDate);
    },
    prevMonth() {
      this.currentMonth--;
      if (this.currentMonth < 0) {
        this.currentMonth = 11;
        this.currentYear--;
      }
    },
    nextMonth() {
      this.currentMonth++;
      if (this.currentMonth > 11) {
        this.currentMonth = 0;
        this.currentYear++;
      }
    },
    generateCalendarDays() {
      const days = [];
      const today = new Date();
      const firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
      const lastDay = new Date(today.getFullYear(), today.getMonth() + 1, 0);

      for (let day = firstDay; day <= lastDay; day.setDate(day.getDate() + 1)) {
        days.push(new Date(day));
      }
      return days;
    },

    async fetchUserHistory(userId) {
      try {
        const response = await axios.get(`/gym-visit/history/${userId}`, {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        this.history = response.data;
      } catch (error) {
        console.error(
          `Error fetching history for user ${userId}:`,
          error.message
        );
        alert("Failed to load user history.");
      }
    },
  },
  mounted() {
    this.fetchCheckInStatus();
    this.fetchCheckInTime();
    this.fetchAverageTime();
    this.fetchTotalTime();
    this.fetchCheckInDates();
    this.$store.dispatch("checkinTimer/initializeCheckInStatus").then(() => {
      if (this.$store.state.checkinTimer.checkInStatus) {
        this.$store.dispatch("checkinTimer/startLiveTimer");
      }
    });
    const userId = this.$route.params.userId;
    if (userId) {
      console.log(`Loading history for user ${userId}`);
      this.fetchUserHistory(userId);
    } else {
      console.error("No userId provided in the route.");
    }
  },
};
</script>

<style scoped>
.check-in-out-view {
  margin: 20px;
}

.container {
  display: flex;
  gap: 20px;
  justify-content: space-between;
}

.page-title {
  display: flex;
  justify-content: center;
  font-size: 2rem;
  color: #0576ee;
  text-align: center;
  margin-bottom: 20px;
}

.content-box {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.left-section,
.right-section {
  flex: 1;
  min-width: 320px;
  max-width: 48%;
}

.status-box,
.time-summary-box,
.calendar-section-box {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.status-box h3,
.time-summary-box h3,
.calendar-section-box h3 {
  color: #333;
  font-size: 1.2rem;
  margin-bottom: 10px;
}

.time-summary {
  display: flex;
  flex-direction: column;
  gap: 10px;
  text-align: center;
}

.calendar-header {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.calendar-header button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 5px;
  cursor: pointer;
}

.calendar-header button:hover {
  background-color: #0056b3;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 10px;
  padding: 10px;
  border-radius: 8px;
  background-color: #fafafa;
  border: 1px solid #ddd;
}

.calendar-cell {
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f8f9fa;
}

.calendar-cell.checkedIn {
  background-color: #28a745;
  color: white;
  font-weight: bold;
}

.calendar-cell:hover {
  background-color: #d1ecf1;
  cursor: pointer;
}
</style>
