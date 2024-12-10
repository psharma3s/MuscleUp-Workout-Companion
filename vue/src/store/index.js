import { createStore as _createStore } from 'vuex';
import axios from 'axios';
import workoutMetrics from './modules/workoutMetrics';
import checkinTimer from './modules/checkinTimer';

export function createStore(currentToken, currentUser) {
  let store = _createStore({
    modules: {
      workoutMetrics,
      checkinTimer,
    },
    state: {
      token: currentToken || '',
      user: currentUser || {
        username: "",
        name: "",
        email: "",
        workoutGoals: "",
        profilePictureUrl: "", 
        weight: "",
        height: "",
      }
    },
    mutations: {
      SET_AUTH_TOKEN(state, token) {
        state.token = token;
        localStorage.setItem('token', token);
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      },
      // Mutation to update user profile
      SET_USER(state, userData) {
        state.user = userData;
        localStorage.setItem('user', JSON.stringify(userData));
      },
      LOGOUT(state) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        state.token = '';
        state.user = {};
        axios.defaults.headers.common = {};
      }
    },
    actions: {
      // Optionally, you can use actions if needed
    },
  });

  return store;
}
