<template>
  <div class="check-in-out-view">
    <h1>Gym Check-In History</h1>

    <!-- Current Check-In Status -->
    <div class="status">
      <h3>Status: {{ checkInStatus ? "Checked In" : "Not Checked In" }}</h3>
      <h3 v-if="checkInStatus">Current Session Time: {{ liveSessionTime }}</h3>
    </div>

    <!-- Average and Total Time -->
    <div class="time-summary">
      <div class="average-time">
        <h3>Average Time Per Visit: {{ averageTime }}</h3>
      </div>
      <div class="total-time">
        <h3>Total Time Spent in the Gym: {{ totalTime }}</h3>
      </div>
    </div>

    <!-- Calendar Navigation and Display -->
    <div class="calendar-section">
      <h3>Check-In Calendar</h3>
      <div class="calendar-header">
        <button @click="prevMonth">Previous</button>
        <span class="month-label">{{ currentMonthName }} {{ currentYear }}</span>
        <button @click="nextMonth">Next</button>
      </div>

      <!-- Days of the Week Labels -->
      <div class="calendar-grid">
        <div v-for="(day, index) in weekDays" :key="'label-' + index" class="calendar-label">
          {{ day }}
        </div>

        <!-- Calendar Days -->
        <div
          v-for="(day, index) in paddedDaysInMonth"
          :key="'day-' + index"
          :class="['calendar-cell', { checkedIn: day && isCheckedIn(day) }]"
        >
          {{ day ? day.getDate() : '' }}
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
      liveSessionTime: "00:00:00",
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

  // Labels for the days of the week
  weekDays() {
    return ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
  },

  // Days in the current month with padding for alignment
  paddedDaysInMonth() {
    const firstDayOfMonth = new Date(this.currentYear, this.currentMonth, 1);
    const lastDayOfMonth = new Date(this.currentYear, this.currentMonth + 1, 0);

    const days = [];

    // Add nulls for padding before the first day of the month
    for (let i = 0; i < firstDayOfMonth.getDay(); i++) {
      days.push(null);
    }

    // Add the actual days of the month
    for (let i = 1; i <= lastDayOfMonth.getDate(); i++) {
      days.push(new Date(this.currentYear, this.currentMonth, i));
    }

    return days;
  },

  // Display name for the current month
  currentMonthName() {
    return new Date(this.currentYear, this.currentMonth).toLocaleString(
      "default",
      {
        month: "long",
      }
    );
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
          console.error("Invalid check-in time received:", response.data);
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
        ); // Convert to ISO strings for uniformity
      } catch (error) {
        console.error("Error fetching check-in dates:", error.message);
      }
    },
    isCheckedIn(day) {
      if (!day) return false; // Skip null padding days
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
  },
};
</script>

<style scoped>
.check-in-out-view {
  text-align: center;
  margin: 20px;
}

.status {
  margin: 20px 0;
}

.status h3 {
  margin: 5px 0;
  font-size: 18px;
  color: #333;
}

.time-summary {
  margin-top: 50px;
  margin-bottom: 50px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.average-time,
.total-time {
  margin: 10px 0;
  font-size: 16px;
  font-weight: bold;
  color: #555;
}

.calendar-section {
  margin-top: 30px;
}

.calendar-header {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
}

.calendar-header button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}

.calendar-header button:hover {
  background-color: #0056b3;
}

.calendar-header .month-label {
  margin: 0 15px;
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 5px;
  max-width: 350px;
  margin: 0 auto;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background-color: #fafafa;
}

.calendar-cell {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 14px;
  background-color: #f8f9fa;
  color: #555;
}

.calendar-cell.checkedIn {
  background-color: #28a745;
  color: white;
  font-weight: bold;
}

.calendar-cell:hover {
  transform: scale(1.1);
  border-color: #007bff;
}

.navigation {
  margin-top: 10px;
  display: flex;
  justify-content: center;
}

.navigation button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  margin: 0 5px;
}

.calendar-label {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  color: #555;
  background-color: #f0f0f0;
  border: 1px solid #ccc;
  border-radius: 6px;
}

.navigation button:hover {
  background-color: #0056b3;
}
</style>