<template>
  <div class="metrics-container">
    <div :class="['metrics', { fullWidth: !showEquipmentMetrics }]">
      <h1>Workout Metrics</h1>
      <table v-if="metrics && metrics.length">
        <thead>
          <tr>
            <th>Date</th>
            <th>Equipment Used</th>
            <th>Weights (lbs)</th>
            <th>Reps</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="metric in metrics" :key="metric.metricId">
            <td>{{ metric.date }}</td>
            <td>{{ metric.equipmentUsed }}</td>
            <td>{{ metric.weights }}</td>
            <td>{{ metric.reps }}</td>
          </tr>
        </tbody>
      </table>
      <p v-else>No workout metrics available.</p>
    </div>

    <div v-if="showEquipmentMetrics" class="equipment-metrics">
      <h1>Equipment Log</h1>
      <button @click="toggleEquipmentMetrics">Close Equipment Metrics</button>
      <table v-if="equipmentMetrics && equipmentMetrics.length">
        <thead>
          <tr>
            <th>Date</th>
            <th>Equipment Used</th>
            <th>Weights (lbs)</th>
            <th>Reps</th>
            <th>Distance</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="metric in equipmentMetrics" :key="metric.metricId">
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
    <!-- Show Equipment Metrics Button -->
    <button v-if="!showEquipmentMetrics" @click="loadEquipmentMetrics">View Equipment Metrics</button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      showEquipmentMetrics: false, // Toggle state for equipment metrics table
    };
  },
  computed: {
    metrics() {
      return this.$store.getters['workoutMetrics/metrics'] || [];
    },
    equipmentMetrics() {
      console.log('Equipment Metrics:', this.$store.getters['workoutMetrics/equipmentMetrics']);
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
  gap: 20px;
}

.metrics {
  width: 50%;
  padding: 20px;
}

.metrics.fullWidth {
  width: 100%;
}

.equipment-metrics {
  width: 50%;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

th {
  background-color: #f4f4f4;
}</style>