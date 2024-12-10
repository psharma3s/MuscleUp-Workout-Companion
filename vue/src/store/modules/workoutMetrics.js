import axios from 'axios';

export default {
  namespaced: true, 
  state: {
    metrics: [], 
    equipmentMetrics: [],
  },
  mutations: {
    SET_METRICS(state, metrics) {
      state.metrics = metrics;
    },
    ADD_METRIC(state, metric) {
      state.metrics.push(metric);
    },
    SET_EQUIPMENT_METRICS(state, metrics) {
      console.log('Committing Equipment Metrics:', metrics);
      state.equipmentMetrics = metrics;
    },
  },
  actions: {
    async fetchMetricsByUserId({ commit }, userId) {
      try {
        const response = await axios.get(`/api/metrics/${userId}`);
        commit('SET_METRICS', response.data);
      } catch (error) {
        console.error('Error fetching user metrics:', error);
        throw error;
      }
    },
    async fetchAllMetrics({ commit }) {
      try {
        const response = await axios.get('/api/metrics');
        commit('SET_METRICS', response.data);
      } catch (error) {
        console.error('Error fetching all metrics:', error);
        throw error;
      }
    },
    async addMetric({ commit }, metric) {
      return axios
        .post('/api/metrics', metric)
        .then(() => {
          commit('ADD_METRIC', metric);
        })
        .catch((error) => {
          console.error('Error adding metric:', error);
        });
    },
    async fetchMetricsWithEquipment({ commit }, userId) {
      try {
        const response = await axios.get(`/api/metrics/${userId}/equipment`);
        console.log('API Response for Equipment Metrics:', response.data);
        commit('SET_EQUIPMENT_METRICS', response.data);
      } catch (error) {
        console.error('Error fetching equipment metrics:', error);
      }
    },
  },
  getters: {
    metrics(state) {
      return state.metrics;
    },
    equipmentMetrics: (state) => state.equipmentMetrics,
  },
};
