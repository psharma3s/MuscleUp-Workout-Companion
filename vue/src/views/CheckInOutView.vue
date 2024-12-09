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

    <!-- Calendar Grid -->
    <h3>Check-In History</h3>
    <div class="calendar-grid">
      <div
        v-for="(day, index) in calendarDays"
        :key="index"
        class="calendar-cell"
        :class="{
          checkedIn: checkInDates.some(
            (date) => date.toDateString() === day.toDateString()
          ),
        }"
      >
        {{ day.getDate() }}
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
      checkInStatus: false, // Check-in state
      liveSessionTime: "00:00:00", // Timer display (live session time)
      averageTime: "Loading...", // Average time per visit
      totalTime: "Loading...", // Total time spent
      checkInDates: [], // Check-in dates for the calendar
      calendarDays: this.generateCalendarDays(), // Calendar days for the current month
    };
  },

  computed: {
    checkInStatus() {
      return this.$store.state.checkinTimer.checkInStatus;
    },
    liveSessionTime() {
      return this.$store.state.checkinTimer.liveSessionTime;
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
        this.checkInDates = response.data.map((date) => new Date(date));
      } catch (error) {
        console.error("Error fetching check-in dates:", error.message);
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

.time-summary {
  margin-top: 70px;
  margin-bottom: 70px;
}

.average-time,
.total-time {
  margin: 10px 0;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 5px;
  max-width: 300px;
  margin: 20px auto;
}

.calendar-cell {
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 12px;
  background-color: #f0f0f0;
  color: #333;
  cursor: default;
}

.calendar-cell.checkedIn {
  background-color: #4caf50;
  color: white;
}
</style>