<template>
  <div class="calendar-section">
    <!-- Flex container for calendar and registered classes -->
    <div class="flex-container">
      <!-- Calendar Section -->
      <div class="calendar-box">
        <h2>Upcoming Class Schedule</h2>
        <VCalendar v-model="selectedDate" :expanded="true" :events="calendarEvents" :attributes="calendarDecorations"
          @dayclick="handleDayClick">
          <template v-slot:day="props">
            <div class="custom-day" @mouseover="handleMouseOver(props)" @mouseleave="handleMouseLeave"
              :style="{ height: '120px', textAlign: 'center', display: 'flex', flexDirection: 'column' }">
              <div>{{ props.day.date.day }}</div>
              <div v-if="showEventLabel && currentDate === props.day.date" class="event-label">
                <div v-for="event in props.day.events" :key="event.id">
                  {{ event.name }}
                </div>
              </div>
            </div>
          </template>
        </VCalendar>
      </div>

      <!-- Member's Registered Classes Section -->
      <div v-if="myRegisteredClasses.length > 0" class="registered-classes-section">
        <h2>My Registered Classes</h2>
        <ul class="scrollable-list">
          <li v-for="(cls, idx) in myRegisteredClasses" :key="idx" class="registered-class-item">
            <h3>{{ cls.name }}</h3>
            <p><strong>Date:</strong> {{ cls.date }}</p>
            <p><strong>Time:</strong> {{ cls.time }}</p>
            <p><strong>Instructor:</strong> {{ cls.instructor }}</p>
            <p><strong>Duration:</strong> {{ cls.duration }}</p>
            <button @click="dropClassFromList(cls)">Drop</button>
          </li>
        </ul>
      </div>
    </div>
    <button v-if="isEmployee" @click="openCreateClassPopup()" class="create-class-button">
      Create Class
    </button>


    <!-- Employee Day Popup -->
    <div v-if="showEmployeeDayPopup" class="popup-overlay">
      <div class="popup">
        <h3>Actions for {{ currentDate }}</h3>
        <button @click="openCreateClassPopup(currentDate); showEmployeeDayPopup = false;">Create Another Class</button>
        <button @click="showEmployeeViewClasses = true; showEmployeeDayPopup = false">View Class Info</button>
        <button @click="showEmployeeDeleteClasses = true; showEmployeeDayPopup = false">Delete Class(es)</button>
        <button @click="showRegisterPopup = true; showEmployeeDayPopup = false">Register for Class</button>
        <button @click="showDropClassPopup = true; showEmployeeDayPopup = false">Drop Class</button>
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
            <button @click="registerForClass(cls)" v-if="!isEmployee">Register</button>
            <button @click="dropClass(cls)" v-if="!isEmployee">Drop</button>
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
          <li v-for="(member, index) in selectedClass.registeredMembers" :key="index">{{ member }}</li>
        </ul>
        <button @click="showEmployeeClassInfoPopup = false">Close</button>
      </div>
    </div>

    <!-- Member: Registration Popup -->
    <div v-if="showRegisterPopup" class="popup-overlay">
      <div class="popup">
        <h3>Available Classes on {{ currentDate }}</h3>
        <ul class="scrollable-list">
          <li v-for="(cls, idx) in unregisteredClassesForDate" :key="idx">
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
            <button @click="showOtherClassesPopup = true">Check Other Classes</button>
            <button @click="showWhichClassToDropPopup = true">Drop Class</button>
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
          <li v-for="(member, index) in selectedClass.registeredMembers" :key="index">{{ member }}</li>
        </ul>
        <button @click="showClassInfoPopup = false">Close</button>
      </div>
    </div>

    <!-- Create Class Popup -->
    <div v-if="showCreateClassForm" class="popup-overlay">
      <div class="popup">
        <h3>Create New Class on {{ createClassForDate || 'selected date' }}</h3>
        <input v-model="newClass.name" placeholder="Class Name" />
        <input v-model="newClass.date" type="date" />
        <input v-model="newClass.time" type="time" />
        <input v-model="newClass.instructor" placeholder="Instructor Name" />
        <input v-model="newClass.duration" placeholder="Duration (e.g. 60 min)" />
        <button @click="createClassOnServer">Create</button>
        <button @click="showCreateClassForm = false">Close</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { Calendar as VCalendar } from 'v-calendar';
import 'v-calendar/style.css';

export default {
  name: 'CalendarSchedule',
  components: {
    VCalendar,
  },
  props: {
    userName: {
      type: String,
      required: true,
    },
    isEmployee: {
      type: Boolean,
      required: true,
    },
    token: {
      type: String,
      required: true,
    },
    // calendarEvents is passed down from HomeView
    calendarEvents: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      showEventLabel: false,
      currentDate: null,
      selectedClass: {},
      selectedDate: null,
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
        name: '',
        date: '',
        time: '',
        instructor: '',
        duration: '',
        caloriesBurned: ''
      },
      // We'll use a local copy of calendarEvents and sync with parent if needed
    };
  },
  computed: {
    classesForDate() {
      return this.calendarEvents.filter(evt => evt.date === this.currentDate);
    },
    registeredClassesForDate() {
      return this.classesForDate.filter(cls => cls.registeredMembers?.includes(this.userName));
    },
    myRegisteredClasses() {
      return this.calendarEvents.filter(cls => cls.registeredMembers?.includes(this.userName));
    },
    unregisteredClassesForDate() {
      return this.classesForDate.filter(cls => !cls.registeredMembers?.includes(this.userName));
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
        const userRegistered = classes.some(cls => cls.registeredMembers?.includes(this.userName));
        const dotColor = userRegistered ? 'red' : 'blue';

        results.push({
          dates: dateKey + 'T00:00:00',
          dot: {
            color: dotColor,
            background: 'white',
            content: userRegistered ? 'Registered' : '',
          },
        });
      }
      return results;
    }
  },
  methods: {
    async loadClasses() {
      // If you need to refetch classes from the server:
      try {
        const response = await axios.get('/classes', {
          headers: { Authorization: `Bearer ${this.token}` }
        });
        // If you want to update parent's calendarEvents, emit an event:
        this.$emit('update:calendarEvents', response.data);
      } catch (error) {
        console.error('Failed to fetch classes', error);
      }
    },
    async fetchClassesForDate(date) {
      try {
        const response = await axios.get(`/classes/date/${date}`, {
          headers: { Authorization: `Bearer ${this.token}` }
        });
        // Merge the fetched classes for date into calendarEvents
        // Since calendarEvents is a prop, we might need to emit an event to update it
        const updatedEvents = this.calendarEvents.filter(evt => evt.date !== date).concat(response.data);
        this.$emit('update:calendarEvents', updatedEvents);

        const classesOnDate = this.classesForDate;
        if (this.isEmployee) {
          if (classesOnDate.length > 0) {
            this.showEmployeeDayPopup = true;
          } else {
            this.openCreateClassPopup(date);
          }
        } else {
          if (classesOnDate.length > 0) {
            const isRegistered = classesOnDate.some(cls => cls.registeredMembers?.includes(this.userName));
            if (isRegistered) {
              this.showDropClassPopup = true;
            } else {
              this.showRegisterPopup = true;
            }
          } else {
            console.log('No classes on this date for members.');
          }
        }
      } catch (error) {
        console.error('Failed to fetch classes by date', error);
      }
    },
    handleDayClick(day) {
      const selectedDate = day.id;
      this.currentDate = selectedDate;
      this.fetchClassesForDate(selectedDate);
    },
    handleMouseOver(props) {
      // Display the event label when hovering over a day
      this.showEventLabel = true;
      this.currentDate = props.day.date;
    },

    handleMouseLeave() {
      // Hide the event label when mouse leaves the day cell
      this.showEventLabel = false;
    },
    openCreateClassPopup(date = null) {
      if (date) {
        this.createClassForDate = date;
        this.newClass.date = date;
      } else {
        this.createClassForDate = null;
        this.newClass.date = '';
      }
      this.showCreateClassForm = true;
    },
    async registerForClass(cls) {
      try {
        await axios.post(`/classes/${cls.classId}/register`, {}, {
          headers: { Authorization: `Bearer ${this.token}` }
        });

        // Update locally since we have direct access to calendarEvents via props
        if (!cls.registeredMembers) {
          cls.registeredMembers = [];
        }
        if (!cls.registeredMembers.includes(this.userName)) {
          cls.registeredMembers.push(this.userName);
        }

        this.showRegisterPopup = false;
        this.showOtherClassesPopup = false;
        this.showDropClassPopup = true;
      } catch (error) {
        console.error('Failed to register for class', error);
      }
    },
    async createClassOnServer() {
      try {
        const response = await axios.post('/classes', this.newClass, {
          headers: { Authorization: `Bearer ${this.token}` }
        });
        const createdClass = response.data;
        const updatedEvents = [...this.calendarEvents, createdClass];
        this.$emit('update:calendarEvents', updatedEvents);
        this.showCreateClassForm = false;
        this.newClass = { name: '', date: '', time: '', instructor: '', duration: '', caloriesBurned: '' };
      } catch (error) {
        console.error('Failed to create class', error);
      }
    },
    async dropClass(cls) {
      try {
        await axios.post(`/classes/${cls.classId}/drop`, {}, {
          headers: { Authorization: `Bearer ${this.token}` }
        });
        if (cls.registeredMembers) {
          cls.registeredMembers = cls.registeredMembers.filter(m => m !== this.userName);
        }
        this.showDropClassPopup = false;
        this.showWhichClassToDropPopup = false;
      } catch (error) {
        console.error('Failed to drop class', error);
      }
    },
    async dropClassFromList(cls) {
      try {
        await axios.post(`/classes/${cls.classId}/drop`, {}, {
          headers: { Authorization: `Bearer ${this.token}` }
        });
        if (cls.registeredMembers) {
          cls.registeredMembers = cls.registeredMembers.filter(m => m !== this.userName);
        }
      } catch (error) {
        console.error('Failed to drop class from list', error);
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
      try {
        await axios.delete(`/classes/${cls.classId}`, {
          headers: { Authorization: `Bearer ${this.token}` }
        });
        const idx = this.calendarEvents.indexOf(cls);
        if (idx !== -1) {
          this.calendarEvents.splice(idx, 1);
          this.$emit('update:calendarEvents', this.calendarEvents);
        }
        if (this.classesForDate.length === 0) {
          this.showEmployeeDeleteClasses = false;
        }
      } catch (error) {
        console.error('Failed to delete class', error);
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
    // Additional methods for popup actions
    createAnotherClass() {
      this.showEmployeeDayPopup = false;
      this.openCreateClassPopup(this.currentDate);
    },
    viewClassesOnDate() {
      this.showEmployeeDayPopup = false;
      this.showEmployeeViewClasses = true;
    },
    deleteClassesOnDate() {
      this.showEmployeeDayPopup = false;
      this.showEmployeeDeleteClasses = true;
    }
  }
};
</script>

<style scoped>
/* Calendar Section */
html,
body {
  overflow: hidden;
  /* Disable scrolling on the whole page */
  height: 100%;
  /* Ensure body takes the full height of the viewport */
}

.calendar-section {
  margin: 40px auto;
  width: 100%;
  /* Take full width available */
  text-align: center;
  position: relative;
  z-index: 1;
  box-sizing: border-box;
}

/* Flex container for Calendar and Registered Classes */
.flex-container {
  display: flex;
  justify-content: space-between;
  /* Add space between the calendar and registered classes */
  gap: 20px;
  /* Add space between boxes */
  padding: 20px;
  flex-wrap: nowrap;
  /* Prevent wrapping */
}

/* Calendar Box */
.calendar-box {
  width: 70%;
  /* Calendar takes 70% of the width */
  background-color: #5674a1 !important;
  padding: 10px;
  border-radius: 12px;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
}

/* Registered Classes Section */
.registered-classes-section {
  width: 30%;
  /* Registered classes take 30% of the width */
  background: rgba(255, 255, 255, 0.9);
  padding: 20px;
  border-radius: 5px;
  box-shadow: 0px 6px 15px rgba(0, 0, 0, 0.2);
  margin-top: 20px;
  overflow-y: auto;
  height: 350px;
  /* Fixed height to make the section scrollable */
}

.registered-classes-section table {
  width: 100%;
  border-collapse: collapse;
}

.registered-classes-section th,
.registered-classes-section td {
  padding: 10px;
  border: 1px solid #ddd;
  text-align: left;
}

.registered-classes-section th {
  background-color: #f2f2f2;
}

/* Hover effect for table rows */
.registered-classes-section tbody tr:hover {
  background-color: #f5f5f5;
}

/* Event styling */
.vc-event {
  background-color: #28a745 !important;
  color: white;
  padding: 6px 10px;
  border-radius: 5px;
  font-size: 1.1rem;
  text-align: center;
  transition: background-color 0.3s ease;
}

/* Hover effect for events */
.vc-event:hover {
  background-color: #218838 !important;
}

/* Pop-up buttons */
.create-class-button {
  margin-top: 20px;
  padding: 12px 25px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 50px;
  cursor: pointer;
  font-size: 1.1rem;
  box-shadow: 0px 6px 12px rgba(0, 0, 0, 0.2);
  transition: background-color 0.3s ease, box-shadow 0.3s ease;
}

.create-class-button:hover {
  background-color: #0056b3;
  box-shadow: 0px 6px 15px rgba(0, 0, 0, 0.3);
}

/* Pop-up styling */
.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  /* Ensure the popup is above other elements */
}

.popup {
  background: #fff;
  padding: 20px;
  max-width: 500px;
  width: 90%;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0px 6px 15px rgba(0, 0, 0, 0.2);
  animation: popupFadeIn 0.5s ease-in-out;
}

.popup button {
  margin: 15px 10px;
  padding: 12px 25px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.popup button:hover {
  background-color: #0056b3;
}

/* Pop-up Fade-in Animation */
@keyframes popupFadeIn {
  0% {
    opacity: 0;
    transform: translateY(-50px);
  }

  100% {
    opacity: 1;
    transform: translateY(0);
  }
}</style>
