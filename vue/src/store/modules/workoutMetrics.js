import axios from 'axios';

export default {
  namespaced: true, // Enables namespacing for the module
  state: {
    metrics: [], // Store workout metrics data here
  },
  mutations: {
    SET_METRICS(state, metrics) {
      state.metrics = metrics;
    },
    ADD_METRIC(state, metric) {
      state.metrics.push(metric);
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
  },
  getters: {
    metrics(state) {
      return state.metrics;
    },
  },
};
