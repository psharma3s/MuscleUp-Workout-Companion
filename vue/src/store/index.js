import { createStore as _createStore } from 'vuex';
import axios from 'axios';
import workoutMetrics from './modules/workoutMetrics';
import checkinTimer from './modules/checkinTimer';

export function createStore(currentToken, currentUser) {
  // Load the user and weight/height from localStorage if they exist
  const savedUser = JSON.parse(localStorage.getItem('user')) || {};
  const savedWeight = localStorage.getItem('userWeight');
  const savedHeight = localStorage.getItem('userHeight');
  
  // If weight or height is not available in localStorage, use savedUser defaults
  savedUser.weight = savedWeight || savedUser.weight;
  savedUser.height = savedHeight || savedUser.height;

  let store = _createStore({
    modules: {
      workoutMetrics,
      checkinTimer,
    },
    state: {
      token: currentToken || '',
      user: savedUser || {
        username: "",
        name: "",
        email: "",
        workoutGoals: "",
        profilePictureUrl: "",
        weight: "",
        height: "",
      },
      goals: JSON.parse(localStorage.getItem('goals')) || [],
    },
    mutations: {
      SET_AUTH_TOKEN(state, token) {
        state.token = token;
        localStorage.setItem('token', token);
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      },

      SET_USER(state, userData) {
        state.user = userData;
        // Save user data to localStorage
        localStorage.setItem('user', JSON.stringify(userData));
        localStorage.setItem('userWeight', userData.weight); // Save weight to localStorage
        localStorage.setItem('userHeight', userData.height); // Save height to localStorage
      },

      ADD_GOAL(state, goal) {
        state.goals.push(goal);
        localStorage.setItem('goals', JSON.stringify(state.goals));  // Persist goals in localStorage
      },

      DELETE_GOAL(state, index) {
        state.goals.splice(index, 1);
        localStorage.setItem('goals', JSON.stringify(state.goals));  // Update localStorage after deletion
      },

      LOGOUT(state) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        localStorage.removeItem('userWeight');
        localStorage.removeItem('userHeight');
        state.token = '';
        state.user = {};
        axios.defaults.headers.common = {};
      }
    },
    actions: {

    },
  });

  return store;
}
