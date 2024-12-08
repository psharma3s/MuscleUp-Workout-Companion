<template>
  <div class="home">
    <!-- Header Bar -->
    <div class="header-bar">
      <div class="logo-container">
        <img src="@/assets/images/logo.jpg" alt="Logo" class="logo" />
      </div>
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
      <!-- Navigation Buttons -->
      <div class="nav-buttons">
        <router-link to="/" class="nav-button">🏠 Home</router-link>
        <router-link
          v-for="feature in features"
          :key="feature.name"
          :to="feature.route"
          class="feature-button"
        >
          {{ feature.name }}
        </router-link>
      </div>
    </div>
      

    <!-- Main Content -->
    <div class="home-header">
      <div class="profile-section">
        
        <div class="welcome-message">
          <h1>
            Welcome, <span class="highlight">{{ userName }}</span
            >!
          </h1>
          <p>
            Your journey to fitness starts here. Explore, progress, and conquer
            your goals!
          </p>
        </div>
      </div>
    </div>

    <!-- Start Workout Button -->
    <div class="start-workout-section">
      <router-link to="/startworkout" class="start-workout-button">
        Start Workout
      </router-link>
    </div>

    <!-- Calendar Section -->
    <div class="calendar-section">
      <h2>Upcoming Class Schedule</h2>
      <VCalendar :attributes="calendarDecorations" @dayclick="handleDayClick" />
      <button
        v-if="isEmployee"
        @click="openCreateClassPopup()"
        class="create-class-button"
      >
        Create Class
      </button>
    </div>

    <!-- Member's Registered Classes Section -->
    <div
      v-if="!isEmployee && myRegisteredClasses.length > 0"
      class="registered-classes-section"
    >
      <h2>My Registered Classes</h2>
      <ul class="scrollable-list">
        <li
          v-for="(cls, idx) in myRegisteredClasses"
          :key="idx"
          class="registered-class-item"
        >
          <h3>{{ cls.name }}</h3>
          <p><strong>Date:</strong> {{ cls.date }}</p>
          <p><strong>Time:</strong> {{ cls.time }}</p>
          <p><strong>Instructor:</strong> {{ cls.instructor }}</p>
          <p><strong>Duration:</strong> {{ cls.duration }}</p>
          <button @click="dropClassFromList(cls)">Drop</button>
        </li>
      </ul>
    </div>

    <!-- Employee Day Popup -->
    <div v-if="showEmployeeDayPopup" class="popup-overlay">
      <div class="popup">
        <h3>Actions for {{ currentDate }}</h3>
        <button
          @click="
            openCreateClassPopup(currentDate);
            showEmployeeDayPopup = false;
          "
        >
          Create Another Class
        </button>
        <button
          @click="
            showEmployeeViewClasses = true;
            showEmployeeDayPopup = false;
          "
        >
          View Class Info
        </button>
        <button
          @click="
            showEmployeeDeleteClasses = true;
            showEmployeeDayPopup = false;
          "
        >
          Delete Class(es)
        </button>
        <button @click="closeAllPopups">Close</button>
      </div>
    </div>

    <!-- Employee: View Classes Popup -->
    <div v-if="showEmployeeViewClasses" class="popup-overlay">
      <div class="popup">
        <h3>Classes on {{ currentDate }}</h3>
        <ul class="scrollable-list">
          <li v-for="(cls, idx) in classesForDate" :key="idx">
            <strong>{{ cls.name }}</strong> at {{ cls.time }}
            <button @click="viewClassInfo(cls, true)">View Info</button>
          </li>
        </ul>
        <button @click="showEmployeeViewClasses = false">Close</button>
      </div>
    </div>

    <!-- Employee: Delete Classes Popup -->
    <div v-if="showEmployeeDeleteClasses" class="popup-overlay">
      <div class="popup">
        <h3>Delete Classes on {{ currentDate }}</h3>
        <ul class="scrollable-list">
          <li v-for="(cls, idx) in classesForDate" :key="idx">
            <strong>{{ cls.name }}</strong> at {{ cls.time }}
            <button @click="deleteClass(cls)">Delete</button>
          </li>
        </ul>
        <button @click="showEmployeeDeleteClasses = false">Close</button>
      </div>
    </div>

    <!-- Employee: Class Info Popup -->
    <div v-if="showEmployeeClassInfoPopup" class="popup-overlay">
      <div class="popup">
        <h3>Class Info: {{ selectedClass.name }}</h3>
        <p><strong>Date:</strong> {{ selectedClass.date }}</p>
        <p><strong>Time:</strong> {{ selectedClass.time }}</p>
        <p><strong>Instructor:</strong> {{ selectedClass.instructor }}</p>
        <p><strong>Duration:</strong> {{ selectedClass.duration }}</p>
        <p><strong>Registered Members:</strong></p>
        <ul class="scrollable-list">
          <li
            v-for="(member, index) in selectedClass.registeredMembers"
            :key="index"
          >
            {{ member }}
          </li>
        </ul>
        <button @click="showEmployeeClassInfoPopup = false">Close</button>
      </div>
    </div>

    <!-- Member: Registration Popup -->
    <div v-if="showRegisterPopup" class="popup-overlay">
      <div class="popup">
        <h3>Available Classes on {{ currentDate }}</h3>
        <ul class="scrollable-list">
          <li v-for="(cls, idx) in classesForDate" :key="idx">
            <strong>{{ cls.name }}</strong> at {{ cls.time }}
            <button @click="registerForClass(cls)">Register</button>
            <button @click="viewClassInfo(cls, false)">View Info</button>
          </li>
        </ul>
        <button @click="showRegisterPopup = false">Close</button>
      </div>
    </div>

    <!-- Member: Drop Class Popup -->
    <div v-if="showDropClassPopup" class="popup-overlay">
      <div class="popup">
        <h3>My Classes on {{ currentDate }}</h3>
        <ul class="scrollable-list">
          <li v-for="(cls, idx) in registeredClassesForDate" :key="idx">
            <strong>{{ cls.name }}</strong> at {{ cls.time }}
            <button @click="viewClassInfo(cls, false)">View Class Info</button>
            <button @click="showOtherClassesPopup = true">
              Check Other Classes
            </button>
            <button @click="showWhichClassToDropPopup = true">
              Drop Class
            </button>
          </li>
        </ul>
        <button @click="showDropClassPopup = false">Close</button>
      </div>
    </div>

    <!-- Member: Other Classes Popup -->
    <div v-if="showOtherClassesPopup" class="popup-overlay">
      <div class="popup">
        <h3>Other Classes on {{ currentDate }}</h3>
        <ul class="scrollable-list">
          <li v-for="(cls, idx) in unregisteredClassesForDate" :key="idx">
            <strong>{{ cls.name }}</strong> at {{ cls.time }}
            <button @click="viewClassInfo(cls, false)">View Info</button>
            <button @click="registerForClass(cls)">Register</button>
          </li>
        </ul>
        <button @click="showOtherClassesPopup = false">Close</button>
      </div>
    </div>

    <!-- Member: Which Class to Drop Popup -->
    <div v-if="showWhichClassToDropPopup" class="popup-overlay">
      <div class="popup">
        <h3>Choose a Class to Drop</h3>
        <ul class="scrollable-list">
          <li v-for="(cls, idx) in registeredClassesForDate" :key="idx">
            <strong>{{ cls.name }}</strong> at {{ cls.time }}
            <button @click="dropClass(cls)">Drop</button>
          </li>
        </ul>
        <button @click="showWhichClassToDropPopup = false">Close</button>
      </div>
    </div>

    <!-- Class Info Popup (for members) -->
    <div v-if="showClassInfoPopup" class="popup-overlay">
      <div class="popup">
        <h3>Class Info: {{ selectedClass.name }}</h3>
        <p><strong>Date:</strong> {{ selectedClass.date }}</p>
        <p><strong>Time:</strong> {{ selectedClass.time }}</p>
        <p><strong>Instructor:</strong> {{ selectedClass.instructor }}</p>
        <p><strong>Duration:</strong> {{ selectedClass.duration }}</p>
        <p><strong>Registered Members:</strong></p>
        <ul class="scrollable-list">
          <li
            v-for="(member, index) in selectedClass.registeredMembers"
            :key="index"
          >
            {{ member }}
          </li>
        </ul>
        <button @click="showClassInfoPopup = false">Close</button>
      </div>
    </div>

    <!-- Create Class Popup -->
    <div v-if="showCreateClassForm" class="popup-overlay">
      <div class="popup">
        <h3>Create New Class on {{ createClassForDate || "selected date" }}</h3>
        <input v-model="newClass.name" placeholder="Class Name" />
        <input v-model="newClass.date" type="date" />
        <input v-model="newClass.time" type="time" />
        <input v-model="newClass.instructor" placeholder="Instructor Name" />
        <input
          v-model="newClass.duration"
          placeholder="Duration (e.g. 60 min)"
        />
        <button @click="createClassOnServer">Create</button>
        <button @click="showCreateClassForm = false">Close</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { Calendar as VCalendar } from "v-calendar";
import "v-calendar/style.css";

export default {
  components: {
    VCalendar,
  },
  computed: {
    userName() {
      return this.$store.state.user.username || "User";
    },
    profilePictureUrl() {
      // If user.profilePictureUrl is a relative path, ensure it's served from the same origin or through /api
      return this.$store.state.user.profilePictureUrl
        ? `/api${this.$store.state.user.profilePictureUrl}`
        : "@/assets/icons/default-profile.png";
    },
    isEmployee() {
      return this.$store.state.user.authorities?.some(
        (auth) => auth.name === "ROLE_EMPLOYEE"
      );
    },
    classesForDate() {
      return this.calendarEvents.filter((evt) => evt.date === this.currentDate);
    },
    registeredClassesForDate() {
      return this.classesForDate.filter((cls) =>
        cls.registeredMembers?.includes(this.userName)
      );
    },
    myRegisteredClasses() {
      return this.calendarEvents.filter((cls) =>
        cls.registeredMembers?.includes(this.userName)
      );
    },
    unregisteredClassesForDate() {
      return this.classesForDate.filter(
        (cls) => !cls.registeredMembers?.includes(this.userName)
      );
    },
    calendarDecorations() {
      const dateMap = {};
      for (const evt of this.calendarEvents) {
        if (!dateMap[evt.date]) {
          dateMap[evt.date] = [];
        }
        dateMap[evt.date].push(evt);
      }

      const results = [];
      for (const dateKey in dateMap) {
        const classes = dateMap[dateKey];
        const userRegistered = classes.some((cls) =>
          cls.registeredMembers?.includes(this.userName)
        );
        const dotColor = userRegistered ? "red" : "blue";

        results.push({
          dates: dateKey + "T00:00:00",
          dot: {
            color: dotColor,
            background: "white",
            content: userRegistered ? "Registered" : "",
          },
        });
      }
      return results;
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
        { name: "View Workout Metrics", route: "/workout-metrics" },
        { name: "Profile", route: "/profile" },
        { name: "History", route: "/gym-checkin" },
        { name: "Logout", route: "/logout" },
      ],
      calendarEvents: [],
      currentDate: null,
      selectedClass: {},
      showRegisterPopup: false,
      showDropClassPopup: false,
      showCreateClassForm: false,
      showClassInfoPopup: false,
      showEmployeeDayPopup: false,
      showEmployeeViewClasses: false,
      showEmployeeDeleteClasses: false,
      showEmployeeClassInfoPopup: false,
      showOtherClassesPopup: false,
      showWhichClassToDropPopup: false,
      createClassForDate: null,
      newClass: {
        name: "",
        date: "",
        time: "",
        instructor: "",
        duration: "",
        caloriesBurned: "",
      },
      checkInTime: null,
      timerInterval: null,
    };
  },
  async mounted() {
    // Fetch all classes from the backend
    const token = this.$store.state.token;
    try {
      const response = await axios.get("/classes", {
        headers: { Authorization: `Bearer ${token}` },
      });
      this.calendarEvents = response.data;
    } catch (error) {
      console.error("Failed to fetch classes", error);
    }
  },
  methods: {
    async loadClasses() {
      const token = this.$store.state.token;
      try {
        const response = await axios.get("/classes", {
          headers: { Authorization: `Bearer ${token}` },
        });
        this.calendarEvents = response.data;
      } catch (error) {
        console.error("Failed to fetch classes", error);
      }
    },
    handleDayClick(day) {
      const selectedDate = day.id;
      this.currentDate = selectedDate;
      this.fetchClassesForDate(selectedDate);
    },
    async fetchClassesForDate(date) {
      console.log("User Name:", this.userName);
      const token = this.$store.state.token;
      try {
        const response = await axios.get(`/classes/date/${date}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        this.calendarEvents = this.calendarEvents
          .filter((evt) => evt.date !== date)
          .concat(response.data);

        const classesOnDate = this.classesForDate;
        console.log("User Name:", this.userName);
        classesOnDate.forEach((cls) => {
          console.log("Class:", cls.name);
          console.log("Registered Members:", cls.registeredMembers);
          cls.registeredMembers?.forEach((member) => {
            console.log(`Comparing "${member}" with "${this.userName}"`);
          });
        });
        if (
          classesOnDate.some((cls) =>
            cls.registeredMembers?.includes(this.userName)
          )
        ) {
          console.log("True.");
        }

        if (this.isEmployee) {
          if (classesOnDate.length > 0) {
            this.showEmployeeDayPopup = true;
          } else {
            this.openCreateClassPopup(date);
          }
        } else {
          if (classesOnDate.length > 0) {
            const isRegistered = classesOnDate.some((cls) =>
              cls.registeredMembers?.includes(this.userName)
            );
            if (isRegistered) {
              this.showDropClassPopup = true;
            } else {
              this.showRegisterPopup = true;
            }
          } else {
            console.log("No classes on this date for members.");
          }
        }
      } catch (error) {
        console.error("Failed to fetch classes by date", error);
      }
    },
    openCreateClassPopup(date = null) {
      if (date) {
        this.createClassForDate = date;
        this.newClass.date = date;
      } else {
        this.createClassForDate = null;
        this.newClass.date = "";
      }
      this.showCreateClassForm = true;
    },
    async registerForClass(cls) {
      const token = this.$store.state.token;
      try {
        await axios.post(
          `/classes/${cls.classId}/register`,
          {},
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );

        // Update local state to reflect that the user is now registered
        if (!cls.registeredMembers) {
          cls.registeredMembers = [];
        }
        if (!cls.registeredMembers.includes(this.userName)) {
          cls.registeredMembers.push(this.userName);
        }

        console.log("name:" + cls.registeredMembers);

        // Now that the user is registered, show the drop class popup immediately
        this.showRegisterPopup = false;
        this.showOtherClassesPopup = false;
        this.showDropClassPopup = true;
      } catch (error) {
        console.error("Failed to register for class", error);
      }
    },
    async createClassOnServer() {
      const token = this.$store.state.token;
      try {
        const response = await axios.post("/classes", this.newClass, {
          headers: { Authorization: `Bearer ${token}` },
        });
        const createdClass = response.data;
        this.calendarEvents.push(createdClass);
        this.showCreateClassForm = false;
        this.newClass = {
          name: "",
          date: "",
          time: "",
          instructor: "",
          duration: "",
          caloriesBurned: "",
        };
      } catch (error) {
        console.error("Failed to create class", error);
      }
    },
    async dropClass(cls) {
      const token = this.$store.state.token;
      try {
        await axios.post(
          `/classes/${cls.classId}/drop`,
          {},
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        if (cls.registeredMembers) {
          cls.registeredMembers = cls.registeredMembers.filter(
            (m) => m !== this.userName
          );
        }
        this.showDropClassPopup = false;
        this.showWhichClassToDropPopup = false;
      } catch (error) {
        console.error("Failed to drop class", error);
      }
    },
    async dropClassFromList(cls) {
      const token = this.$store.state.token;
      try {
        await axios.post(
          `/classes/${cls.classId}/drop`,
          {},
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        if (cls.registeredMembers) {
          cls.registeredMembers = cls.registeredMembers.filter(
            (m) => m !== this.userName
          );
        }
      } catch (error) {
        console.error("Failed to drop class from list", error);
      }
    },
    viewClassInfo(cls, employee = false) {
      this.selectedClass = cls;
      if (employee) {
        this.showEmployeeClassInfoPopup = true;
      } else {
        this.showClassInfoPopup = true;
      }
    },
    async deleteClass(cls) {
      const token = this.$store.state.token;
      try {
        await axios.delete(`/classes/${cls.classId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        const idx = this.calendarEvents.indexOf(cls);
        if (idx !== -1) {
          this.calendarEvents.splice(idx, 1);
        }
        if (this.classesForDate.length === 0) {
          this.showEmployeeDeleteClasses = false;
        }
      } catch (error) {
        console.error("Failed to delete class", error);
      }
    },
    closeAllPopups() {
      this.showEmployeeDayPopup = false;
      this.showEmployeeViewClasses = false;
      this.showEmployeeDeleteClasses = false;
      this.showRegisterPopup = false;
      this.showDropClassPopup = false;
      this.showClassInfoPopup = false;
      this.showEmployeeClassInfoPopup = false;
      this.showOtherClassesPopup = false;
      this.showWhichClassToDropPopup = false;
      this.showCreateClassForm = false;
    },

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

  mounted() {
    this.fetchCheckInStatus();
  },
  beforeDestroy() {
    this.$store.dispatch("checkinTimer/stopLiveTimer");
  },
};
</script>

<style scoped>
body {
  font-family: "Arial", sans-serif;
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
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url("@/assets/images/background-picture-homepage.jpg") no-repeat
    center center;
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

.logo-container {
  flex: 0 0 auto;
}

.logo {
  width: 150px;
  height: auto;
}

.check-in-out-section {
  display: flex;
  align-items: center;
  margin-left: 20px;
  margin-right: auto;
}

.check-in-out-button {
  color: white;
  text-decoration: none;
  padding: 10px 20px;
  border-radius: 5px;
  font-size: 14px;
  border: none;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-right: 20px;
  background-color: var(--check-in-color);
}

.check-in-out-button.check-in {
  --check-in-color: #4caf50;
}

.check-in-out-button.check-out {
  --check-in-color: #f44336;
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

.calendar-section {
  margin: 30px auto;
  max-width: 800px;
  text-align: center;
  position: relative;
  z-index: 1;
}

.create-class-button {
  margin-top: 10px;
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.registered-classes-section {
  margin: 20px auto;
  max-width: 800px;
  text-align: left;
  background: rgba(255, 255, 255, 0.9);
  padding: 20px;
  border-radius: 5px;
}

.registered-classes-section h2 {
  margin-top: 0;
}

.registered-class-item {
  border-bottom: 1px solid #ccc;
  padding: 10px 0;
}

.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.popup {
  background: white;
  padding: 10px;
  text-align: center;
  max-width: 400px;
}

/* Add a max-height and enable scrolling for long lists */
.scrollable-list {
  max-height: 200px;
  /* Adjust as needed */
  overflow-y: auto;
}

.popup button {
  margin: 10px 5px;
  padding: 10px;
}

.vc-container {
  z-index: 0;
  /* Ensure calendar elements are behind the popup */
}
</style>
