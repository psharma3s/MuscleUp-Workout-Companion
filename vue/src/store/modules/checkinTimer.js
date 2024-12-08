let timerInterval = null;

export default {
  namespaced: true,
  state: {
    checkInStatus: false,
    checkInTime: null,
    liveSessionTime: "00:00:00",
  },
  mutations: {
    SET_CHECK_IN_STATUS(state, status) {
      state.checkInStatus = status;
    },
    SET_CHECK_IN_TIME(state, time) {
      state.checkInTime = time;
    },
    SET_LIVE_SESSION_TIME(state, time) {
      state.liveSessionTime = time;
    },
    RESET_TIMER(state) {
      state.liveSessionTime = "00:00:00";
      state.checkInTime = null;
      state.checkInStatus = false;
    },
  },
  actions: {
    startLiveTimer({ commit, state }) {
      // Stop any existing timer
      if (timerInterval) clearInterval(timerInterval);

      if (!state.checkInTime) return;

      timerInterval = setInterval(() => {
        const now = new Date();
        const elapsed = Math.floor((now - new Date(state.checkInTime)) / 1000);
        const hours = Math.floor(elapsed / 3600)
          .toString()
          .padStart(2, "0");
        const minutes = Math.floor((elapsed % 3600) / 60)
          .toString()
          .padStart(2, "0");
        const seconds = (elapsed % 60).toString().padStart(2, "0");
        commit("SET_LIVE_SESSION_TIME", `${hours}:${minutes}:${seconds}`);
      }, 1000);
    },
    stopLiveTimer() {
      if (timerInterval) {
        clearInterval(timerInterval);
        timerInterval = null;
      }
    },
  },
};
