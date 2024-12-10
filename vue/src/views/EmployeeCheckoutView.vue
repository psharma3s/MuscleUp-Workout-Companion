<template>
  <div class="employee-checkout-view">
    <h1>Gym Users</h1>
    <table class="user-table">
      <thead>
        <tr>
          <th>Username</th>
          <th>User ID</th>
          <th>Last Check-In</th>
          <th>Last Check-Out</th>
          <th>Status</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in users" :key="user.userId">
          <td>{{ user.username }}</td>
          <td>{{ user.userId }}</td>
          <td>{{ user.lastCheckIn || "N/A" }}</td>
          <td>{{ user.lastCheckOut || "N/A" }}</td>
          <td>
            <span
              :class="{
                'checked-in': user.status === 'Checked In',
                'not-checked-in': user.status === 'Not Checked In',
              }"
            >
              <i
                v-if="user.status === 'Checked In'"
                class="fas fa-check-circle"
                aria-hidden="true"
              ></i>
              <i v-else class="fas fa-times-circle" aria-hidden="true"></i>
            </span>
          </td>
          <td>
            <button
              :class="user.status === 'Checked In' ? 'check-out' : 'check-in'"
              @click="user.status === 'Checked In' ? checkOutUser(user.userId) : checkInUser(user.userId)"
            >
              {{ user.status === 'Checked In' ? 'Check Out' : 'Check In' }}
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "EmployeeCheckoutView",
  data() {
    return {
      users: [],
    };
  },
  methods: {
    async fetchUsers() {
      try {
        const response = await axios.get("/gym-visit/checked-in-users", {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });

        const uniqueUsers = [];
        const seenUserIds = new Set();

        response.data.forEach((user) => {
          if (!seenUserIds.has(user.userId)) {
            uniqueUsers.push({
              username: user.username,
              userId: user.userId,
              lastCheckIn: user.lastCheckIn
                ? new Date(user.lastCheckIn).toLocaleString()
                : "N/A",
              lastCheckOut: user.lastCheckOut
                ? new Date(user.lastCheckOut).toLocaleString()
                : "N/A",
              status: user.checkedIn ? "Checked In" : "Not Checked In",
            });
            seenUserIds.add(user.userId);
          }
        });

        this.users = uniqueUsers;
      } catch (error) {
        console.error("Error fetching user data:", error.message);
        alert("Failed to load user data. Please try again.");
      }
    },

    async checkOutUser(userId) {
      try {
        await axios.post(`/gym-visit/${userId}/checkout`, null, {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        alert(`User ${userId} has been checked out.`);
        this.fetchUsers();
      } catch (error) {
        console.error(`Error checking out user ${userId}:`, error.message);
        alert("Failed to check out the user. Please try again.");
      }
    },

    async checkInUser(userId) {
      try {
        await axios.post(`/gym-visit/${userId}/checkin`, null, {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        alert(`User ${userId} has been checked in.`);
        this.fetchUsers();
      } catch (error) {
        console.error(`Error checking in user ${userId}:`, error.message);
        alert("Failed to check in the user. Please try again.");
      }
    },
    viewHistory(userId) {
      this.$router.push({ name: "UserHistory", params: { userId } });
    },
    viewMetrics(userId) {
      this.$router.push({ name: "UserMetrics", params: { userId } });
    },
  },
  mounted() {
    this.fetchUsers();
  },
};
</script>

<style scoped>
.employee-checkout-view {
  padding: 20px;
}

h1 {
  text-align: center;
  margin-bottom: 20px;
}

.user-table {
  background-color: rgba(255, 253, 208, 0.8);
  border-collapse: collapse;
  width: 100%;
  margin: 20px 0;
  text-align: left;
}

.user-table th,
.user-table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: center;
}

.user-table th {
  background-color: #f4f4f4;
}

.checked-in {
  color: green;
  font-size: 1.2rem;
}

.not-checked-in {
  color: red;
  font-size: 1.2rem;
}

button {
  padding: 5px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.check-in {
  background-color: #4caf50;
  color: white;
}
.check-in:hover {
  background-color: #45a049;
}

.check-out {
  background-color: #f44336;
  color: white;
}
.check-out:hover {
  background-color: #d32f2f;
}

.fas {
  margin-right: 5px;
}

.user-table tr:nth-child(even) {
  background-color: rgba(255, 253, 208, 0.5);
}

.user-table tr:hover {
  background-color: rgba(255, 253, 208, 0.6);
}
</style>