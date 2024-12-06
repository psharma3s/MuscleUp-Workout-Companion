<template>
  <div class="metrics">
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
</template>

<script>
export default {
  computed: {
    metrics() {
       return this.$store.getters['workoutMetrics/metrics'] || [];
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
.metrics {
  padding: 20px;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th, td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}
th {
  background-color: #f4f4f4;
}
</style>
