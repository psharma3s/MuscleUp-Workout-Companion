import axios from 'axios';

export default {
  state: {
    metrics: [], // Store workout metrics data here
  },
  mutations: {
    SET_METRICS(state, metrics) {
      state.metrics = metrics;
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
  },
  getters: {
    metrics(state) {
      return state.metrics;
    },
  },
};
