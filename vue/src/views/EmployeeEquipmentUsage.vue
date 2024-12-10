<template>
    <div class="equipment-metrics">
        <h1>Workout Equipment Usage Metrics</h1>

        <div class="filter-section">
            <label for="month">Select Month:</label>
            <input type="month" v-model="selectedMonth" @change="fetchEquipmentMetrics" />
        </div>
    </div>

        <div class="equipment-metrics_container" v-if="metrics.length">
                <div class="analysis">
                    <h3>Most Used Machine: {{ mostUsedMachine }}</h3>
                    <h3>Least Used Machine: {{ leastUsedMachine }}</h3>
                </div>        
            <h2>Equipment Usage</h2>
            <table>
                <thead>
                    <tr>
                        <th>Equipment</th>
                        <th>Usage Count</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="metric in metrics" :key="metric.equipmentUsed">
                        <td>{{ metric.equipmentUsed }}</td>
                        <td>{{ metric.usageCount }}</td>
                    </tr>
                </tbody>
            </table>
        </div>
        <p v-else class="equipment-metrics">No data available for the selected period.</p>
</template>

<script>
import axios from 'axios';

export default {
    data() {
        return {
            selectedMonth: '',
            selectedYear: '',
            metrics: [],
        };
    },
    computed: {
        mostUsedMachine() {
            return this.metrics.length ? this.metrics[0].equipmentUsed : 'N/A';
        },
        leastUsedMachine() {
            return this.metrics.length ? this.metrics[this.metrics.length - 1].equipmentUsed : 'N/A';
        },
    },
    methods: {
        fetchEquipmentMetrics() {
            const params = {};
            if (this.selectedMonth) {
                params.month = this.selectedMonth;
            } else if (this.selectedYear) {
                params.year = this.selectedYear;
            }

            axios
                .get(`/api/metrics/equipment-usage`, { params })
                .then(response => {
                    this.metrics = response.data;
                })
                .catch(error => {
                    console.error('Error fetching equipment metrics:', error);
                });
        },
    },
};
</script>

<style scoped>
.equipment-metrics {
    padding: 20px;
    max-width: 800px;
    margin: auto;
  border: 2px solid #ccc;
  border-radius: 8px;
  background-color: #f9f9f9;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.equipment-metrics_container {
    padding: 20px;
    max-width: 800px;
    margin: auto;
  border: 2px solid #ccc;
  border-radius: 8px;
  background-color: #f9f9f9;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
height: 770px;  /* Fixed height to make the section scrollable */
}

.filter-section {
    margin-bottom: 20px;
    display: flex;
    gap: 10px;
    align-items: center;
}

label {
    font-weight: bold;
}

input {
    padding: 5px;
    font-size: 1rem;
}

table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 20px;
}

th,
td {
    border: 1px solid #ddd;
    padding: 8px;
    text-align: left;
}

th {
    background-color: #f4f4f4;
}

.analysis {
    margin-top: 20px;
    font-weight: bold;
}

</style>
