<template>
    <div class="metrics">
        <h1>All Workout Metrics</h1>
        <ul>
            <li v-for="metric in metrics" :key="metric.metricId">
                <p>User ID: {{ metric.userId }}</p>
                <p>Date: {{ metric.date }}</p>
                <p>Equipment: {{ metric.equipmentUsed }}</p>
                <p>Weights: {{ metric.weights }} lbs</p>
                <p>Reps: {{ metric.reps }}</p>
                <p>Time Spent: {{ metric.timeSpent }}</p>
            </li>
        </ul>
    </div>
</template>

<script>
export default {
    computed: {
        metrics() {
            return this.$store.state.metrics;
        }
    },
    created() {
        this.$store.dispatch("fetchAllMetrics");
    }
};
</script>

<style>
/* Add styles */
</style>
