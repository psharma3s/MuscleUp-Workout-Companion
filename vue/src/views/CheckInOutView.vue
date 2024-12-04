<template>
    <div class="check-in-out-view">
      <h1>Gym Check-In & Check-Out</h1>
      <div class="status">
        <h3>Status: {{ checkInStatus ? "Checked In" : "Not Checked In" }}</h3>
      </div>
      <div class="actions">
        <button @click="checkIn" :disabled="checkInStatus">Check In</button>
        <button @click="checkOut" :disabled="!checkInStatus">Check Out</button>
      </div>
      <div class="total-time">
        <h3>Total Time Spent in the Gym: {{ totalTime }}</h3>
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
        totalTime: "Loading...",
      };
    },
    methods: {
      async fetchCheckInStatus() {
        try {
          const response = await axios.get("/gym-visit/check-in-status", {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          });
          this.checkInStatus = response.data.checkedIn;
        } catch (error) {
          console.error(
            "Error fetching check-in status:",
            error.response?.data || error.message
          );
        }
      },
      async fetchTotalTime() {
        try {
          const response = await axios.get("/gym-visit/total-time", {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          });
          this.totalTime = response.data;
        } catch (error) {
          console.error(
            "Error fetching total time:",
            error.response?.data || error.message
          );
          this.totalTime = "Error loading total time";
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
          this.fetchCheckInStatus();
        } catch (error) {
          console.error(
            "Error during check-in:",
            error.response?.data || error.message
          );
        }
      },
  
      async checkOut() {
        try {
          console.log("Sending check-out request...");
          await axios.post(
            "/gym-visit/check-out",
            {},
            {
              headers: {
                Authorization: `Bearer ${localStorage.getItem("token")}`,
              },
            }
          );
          console.log("Check-out request successful!");
  
          this.checkInStatus = false;
  
          await this.fetchTotalTime();
        } catch (error) {
          console.error(
            "Error during check-out:",
            error.response?.data || error.message
          );
        }
      },
    },
    mounted() {
      this.fetchCheckInStatus();
      this.fetchTotalTime();
    },
  };
  </script>
    
    <style scoped>
  .check-in-out-view {
    text-align: center;
    margin: 20px;
  }
  
  .actions {
    margin: 20px 0;
  }
  
  button {
    margin: 5px;
    padding: 10px 20px;
    font-size: 16px;
    cursor: pointer;
  }
  
  button:disabled {
    cursor: not-allowed;
    background-color: #ccc;
  }
  </style>