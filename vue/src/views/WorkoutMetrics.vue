<template>
  <div class="metrics-container">
    <!-- Workout Metrics Box -->
    <div class="metrics-box">
      <div class="section-header">
        <h1>Workout Log</h1>
        <p>Track your performance over time.</p>
      </div>
      <div class="metrics-table">
        <table v-if="metrics && metrics.length">
          <thead>
            <tr>
              <th @click="sortTable('date')">Date</th>
              <th @click="sortTable('equipmentUsed')">Equipment Used</th>
              <th @click="sortTable('weights')">Weights (lbs)</th>
              <th @click="sortTable('reps')">Reps</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="metric in metrics" :key="metric.metricId" @click="selectWorkout(metric)" class="table-row">
              <td>{{ metric.date }}</td>
              <td>{{ metric.equipmentUsed }}</td>
              <td>{{ metric.weights }}</td>
              <td>{{ metric.reps }}</td>
            </tr>
          </tbody>
        </table>
        <p v-else>No workout metrics available.</p>
      </div>

      
    </div>
    <button v-if="!showEquipmentMetrics" @click="loadEquipmentMetrics" class="toggle-button">
        View Equipment Metrics
      </button>

    
    <div v-if="showEquipmentMetrics" class="metrics-box equipment-metrics">
      <div class="section-header">
        <h1>Equipment Log</h1>
        <p>View all metrics tracked with equipment usage.</p>
      </div>
      <button @click="toggleEquipmentMetrics" class="toggle-button">Close Equipment Metrics</button>
      
      <div class="metrics-table">
        <table v-if="equipmentMetrics && equipmentMetrics.length">
          <thead>
            <tr>
              <th @click="sortTable('date')">Date</th>
              <th @click="sortTable('equipmentUsed')">Equipment Used</th>
              <th @click="sortTable('weights')">Weights (lbs)</th>
              <th @click="sortTable('reps')">Reps</th>
              <th @click="sortTable('distance')">Distance</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="metric in equipmentMetrics" :key="metric.metricId" @click="selectWorkout(metric)" class="table-row">
              <td>{{ metric.date }}</td>
              <td>{{ metric.equipmentUsed }}</td>
              <td>{{ metric.weights }}</td>
              <td>{{ metric.reps }}</td>
              <td>{{ metric.distance }}</td>
            </tr>
          </tbody>
        </table>
        <p v-else>No equipment metrics available.</p>
      </div>
    </div>

    <!-- Workout Summary Section -->
    <div v-if="workoutSummary" class="workout-summary">
      <h2>Workout Summary</h2>
      <p v-if="workoutSummary.workoutType">Workout Type: {{ workoutSummary.workoutType }}</p>
      <p v-if="workoutSummary.strengthOption">Strength Option: {{ workoutSummary.strengthOption }}</p>
      <p v-if="workoutSummary.exercise">Exercise: {{ workoutSummary.exercise }}</p>
      <p v-if="workoutSummary.equipment">Equipment: {{ workoutSummary.equipment }}</p>
      <p v-if="workoutSummary.weight">Weight: {{ workoutSummary.weight }} lbs</p>
      <p v-if="workoutSummary.start">Start Time: {{ workoutSummary.start }}</p>
      <p v-if="workoutSummary.stop">Stop Time: {{ workoutSummary.stop }}</p>
      <p v-if="workoutSummary.workoutTime">Workout Duration: {{ workoutSummary.workoutTime }} seconds</p>
      <p v-if="workoutSummary.distance">Distance: {{ workoutSummary.distance }}</p>
      <p v-if="workoutSummary.reps">Strength Reps: {{ workoutSummary.reps }}</p>
      <p v-if="workoutSummary.cardioReps">Cardio Reps: {{ workoutSummary.cardioReps }}</p>
      <p v-if="workoutSummary.cardioSets">Cardio Sets: {{ workoutSummary.cardioSets }}</p>
      <p v-if="workoutSummary.strengthSets">Strength Sets: {{ workoutSummary.strengthSets }}</p>

      <button @click="closeWorkoutSummary" class="close-summary-button">Close Summary</button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      showEquipmentMetrics: false,
      sortKey: '', 
      sortAsc: true,  
      workoutSummary: null,
    };
  },
  computed: {
    metrics() {
      return this.$store.getters['workoutMetrics/metrics'] || [];
    },
    equipmentMetrics() {
      return this.$store.getters['workoutMetrics/equipmentMetrics'] || [];
    },
  },
  methods: {
    loadEquipmentMetrics() {
      const userId = this.$store.state.user?.id;
      if (userId) {
        this.$store.dispatch('workoutMetrics/fetchMetricsWithEquipment', userId).then(() => {
          this.showEquipmentMetrics = true;
        });
      }
    },
    toggleEquipmentMetrics() {
      this.showEquipmentMetrics = !this.showEquipmentMetrics;
    },
    
    sortTable(key) {
      if (this.sortKey === key) {
        this.sortAsc = !this.sortAsc;
      } else {
        this.sortKey = key;
        this.sortAsc = true;
      }
    },
    
    selectWorkout(metric) {
      this.workoutSummary = {
        workoutType: metric.workoutType,
        strengthOption: metric.strengthOption,
        exercise: metric.exercise,
        equipment: metric.equipmentUsed,
        weight: metric.weights,
        start: metric.start,
        stop: metric.stop,
        workoutTime: metric.workoutTime,
        distance: metric.distance,
        reps: metric.reps,
        cardioReps: metric.cardioReps,
        cardioSets: metric.cardioSets,
        strengthSets: metric.strengthSets,
      };
    },
    closeWorkoutSummary() {
      this.workoutSummary = null;
    },
  },
  created() {
    const userId = this.$store.state.user?.id;
    if (userId) {
      this.$store.dispatch('workoutMetrics/fetchMetricsByUserId', userId).catch((error) => {
        console.error('Failed to load metrics:', error);
      });
    }
  },
};
</script>

<style scoped>
.metrics-container {
  display: flex;
  justify-content: space-evenly;
  gap: 20px;
  padding: 20px;
  background-color: #f7f7f7ab;
}

.metrics-tables-container {
  display: flex;
  gap: 20px;
  justify-content: space-between;
  flex-wrap: wrap; 
}

.metrics-box {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
  width: 48%;
  max-height: 400px;
  overflow-y: auto;
}

section-header {
  text-align: center;
  margin-bottom: 15px;
}

h1 {
  font-size: 2rem;
  font-weight: bold;
  color: #333;
}

p {
  font-size: 1rem;
  color: #666;
}

table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}

th, td {
  padding: 10px;
  border: 1px solid #ddd;
  text-align: center;
}

th {
  background-color: #007bff;
  color: #fff;
  cursor: pointer;
}

th:hover {
  background-color: #0056b3;
}

button.toggle-button {
  padding: 1px 12px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
  height: 50px;
  width: 100vh;
}

button.toggle-button:hover {
  background-color: #218838;
}

button.close-summary-button {
  padding: 12px 20px;
  background-color: #dc3545;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

button.close-summary-button:hover {
  background-color: #c82333;
}

.workout-summary {
  background-color: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.workout-summary h2 {
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
}

.workout-summary p {
  color: #555;
}

tr:hover {
  background-color: #f1f1f1;
  cursor: pointer;
}

@media (max-width: 768px) {
  .metrics-tables-container {
    flex-direction: column;
    gap: 15px;
  }

  .metrics-box {
    width: 100%;
  }

  button.toggle-button, button.close-summary-button {
    width: 100%;
  }
}
</style>
