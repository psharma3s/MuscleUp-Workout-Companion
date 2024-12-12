<template>
    <div class="equipment-metrics">
    

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
import { useStore } from 'vuex';

export default {
    setup() {
        const store = useStore();

        if (!store.state.user.authorities?.some(auth => auth.name === 'ROLE_EMPLOYEE')) {
            window.location.href = '/';
        }
    },
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
    display: flex;
    flex-direction: row;
    padding: 30px;
    max-width: 900px;
    margin: 30px auto;
    border: none;
    border-radius: 12px;
    background: linear-gradient(135deg, #f0f0f0 0%, #e0e0e0 100%);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease-in-out;
}

/* Hover effect for the main container */
.equipment-metrics:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

/* Container for the filter section and table */
.equipment-metrics_container {

    padding: 20px;
    max-width: 800px;
    margin: 0 auto;
    border-radius: 12px;
    background-color: #ffffff;
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
    overflow-y: auto;
    height: 700px;
}

/* Filter section styling */
.filter-section {
    margin-bottom: 30px;
    display: flex;
    gap: 20px;
    align-items: center;
    justify-content: space-between;
}

label {
    font-weight: 600;
    font-size: 1.1rem;
    color: #333;
}

input, select {
    padding: 10px;
    font-size: 1rem;
    border-radius: 8px;
    border: 2px solid #ccc;
    transition: border-color 0.3s ease-in-out;
}

/* Input and select field hover effect */
input:hover, select:hover {
    border-color: #3c8dfe;
}

/* Table styling */
table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 20px;
}

th, td {
    padding: 12px;
    text-align: left;
    border: 1px solid #ddd;
}

th {
    background-color: #f1f1f1;
    font-size: 1.1rem;
    color: #333;
}

/* Table row hover effect */
tr:hover {
    background-color: #f9f9f9;
    cursor: pointer;
}

/* Analysis section styling */
.analysis {
    margin-top: 30px;
    font-weight: bold;
    font-size: 1.2rem;
    color: #333;
    background-color: #d1f7e5;
    padding: 10px;
    border-radius: 8px;
    border: 1px solid #b8e3c7;
}

/* Button styling */
button {
    padding: 12px 20px;
    background-color: #28a745;
    color: white;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    transition: background-color 0.3s ease-in-out;
    font-size: 1rem;
}

/* Button hover effect */
button:hover {
    background-color: #218838;
}

/* Responsive design for smaller screens */
@media (max-width: 768px) {
    .filter-section {
        flex-direction: column;
    }

    input, select {
        width: 100%;
        margin-bottom: 10px;
    }

    .equipment-metrics, .equipment-metrics_container {
        max-width: 100%;
        padding: 15px;
    }

    table {
        font-size: 0.9rem;
    }
}


</style>
