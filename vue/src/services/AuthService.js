import axios from 'axios';

const http = axios.create({
  baseURL: import.meta.env.VITE_REMOTE_API,
});

export default {
  login(user) {
    return http.post('/login', user);
  },

  register(user) {
    return http.post('/register', user);
  },

  uploadProfilePicture(file, userId) {
    if (!userId || userId === undefined || userId === null) {
      console.error("Invalid userId:", userId);
      return; // Prevent the request if userId is not valid
    }
  
    const formData = new FormData();
    formData.append("file", file);
    formData.append("userId", userId);
  
    return axios.post("/upload-profile-picture", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  }
  
};
