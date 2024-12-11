<template>
    <div class="past-classes">
        <h2>My Past Classes</h2>
        <table v-if="pastClasses.length > 0">
            <thead>
                <tr>
                    <th>Class Name</th>
                    <th>Class Date</th>
                    <th>Instructor</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(cls, index) in pastClasses" :key="index">
                    <td>{{ cls.class_name }}</td>
                    <td>{{ formatDate(cls.class_date) }}</td>
                    <td>{{ cls.instructor }}</td>
                </tr>
            </tbody>
        </table>
        <p v-else>No past classes found.</p>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    name: 'PastClasses',
    data() {
        return {
            pastClasses: [],
        };
    },
    methods: {
        async fetchPastClasses() {
            try {
                const response = await axios.get('/classes/past-classes', {
                    headers: { Authorization: `Bearer ${this.$store.state.token}` },
                });
                this.pastClasses = response.data;
            } catch (error) {
                console.error('Failed to fetch past classes:', error);
            }
        },
        formatDate(date) {
            const adjustedDate = new Date(date);
            adjustedDate.setDate(adjustedDate.getDate() + 1);
            return adjustedDate.toLocaleDateString();
        },
    },
    mounted() {
        this.fetchPastClasses();
    },
};
</script>

<style scoped>
.past-classes {
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

h2 {
    margin-bottom: 20px;
    color: #333;
}

table {
    width: 100%;
    border-collapse: collapse;
    background-color: #fff;
    border-radius: 5px;
    overflow: hidden;
    box-shadow: 0px 2px 8px rgba(0, 0, 0, 0.1);
}

thead {
    background-color: #007bff;
    color: white;
}

th,
td {
    padding: 12px;
    border-bottom: 1px solid #ddd;
    text-align: left;
}

tr:nth-child(even) {
    background-color: #f2f2f2;
}

tr:hover {
    background-color: #f1f1f1;
}

p {
    color: #555;
    font-size: 1rem;
}
</style>
