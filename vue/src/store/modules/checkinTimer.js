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
      localStorage.setItem("checkInStatus", JSON.stringify(status));
    },
    SET_CHECK_IN_TIME(state, time) {
      state.checkInTime = time;
      localStorage.setItem("checkInTime", time);
    },
    SET_LIVE_SESSION_TIME(state, time) {
      state.liveSessionTime = time; // No need to persist this
    },
    RESET_TIMER(state) {
      state.liveSessionTime = "00:00:00";
      state.checkInTime = null;
      state.checkInStatus = false;
      localStorage.removeItem("checkInStatus");
      localStorage.removeItem("checkInTime");
    },
  },
  actions: {
    async initializeCheckInStatus({ commit, dispatch }) {
      try {
        const response = await axios.get("/gym-visit/check-in-status");
        const status = response.data.checkedIn;

        commit("SET_CHECK_IN_STATUS", status);
        if (status) {
          // Fetch the last check-in time
          const checkInResponse = await axios.get("/gym-visit/current-session-time");
          const checkInTime = checkInResponse.data; // Expecting ISO timestamp

          commit("SET_CHECK_IN_TIME", checkInTime);
          dispatch("startLiveTimer");
        }
      } catch (error) {
        console.error("Error initializing check-in status:", error);
      }
    },
    startLiveTimer({ commit, state }) {
      if (timerInterval) clearInterval(timerInterval);

      // Ensure checkInTime is set
      if (!state.checkInTime) return;

      // Parse checkInTime from localStorage (if reloading) and calculate initial elapsed time
      const checkInTime = new Date(state.checkInTime);
      const now = new Date();
      const elapsed = Math.floor((now - checkInTime) / 1000);

      // Calculate and set the initial session time
      const hours = Math.floor(elapsed / 3600)
        .toString()
        .padStart(2, "0");
      const minutes = Math.floor((elapsed % 3600) / 60)
        .toString()
        .padStart(2, "0");
      const seconds = (elapsed % 60).toString().padStart(2, "0");
      commit("SET_LIVE_SESSION_TIME", `${hours}:${minutes}:${seconds}`);

      // Start the timer for continuous updates
      timerInterval = setInterval(() => {
        const now = new Date();
        const elapsed = Math.floor((now - checkInTime) / 1000);
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
  state: {
    checkInStatus: JSON.parse(localStorage.getItem("checkInStatus")) || false,
    checkInTime: localStorage.getItem("checkInTime") || null,
    liveSessionTime: "00:00:00",
  },
};
