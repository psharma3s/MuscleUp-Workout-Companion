<template>
  <div class="home">
   <div class="home-header">
    <div class="profile-section">
     <div class="profile-picture">
      <img
       :src="profilePictureUrl"
       alt="Profile Picture"
      />
     </div>
     <div class="welcome-message">
      <h1>Welcome, {{userName }}!</h1>
      <p>Your journey to fitness starts here. Check out your progress or explore new features!</p>
     </div>
    </div>
   </div>
   <div class="home-dashboard">
    <div class="dashboard-card" v-for="feature in features" :key="feature.name">
     <router-link :to="feature.route">
      <div class="card-icon">
       <img :src="feature.icon" :alt="feature.name" />
      </div>
      <div class="card-title">
       <h2>{{feature.name }}</h2>
      </div>
     </router-link>
    </div>
   </div>
  </div>
 </template>
 <script>
 export default {
  computed: {
   userName() {
    return this.$store.state.user.name || "User"; // Replace with actual user name from store
   },
   profilePictureUrl() {
    return (
     this.$store.state.user.profilePicture || "@/assets/icons/default-profile.png" // Replace with actual default picture
    );
   },
  },
  data() {
   return {
    features: [
     {
      name: "View Workout Metrics",
      route: "/workout-metrics",
      icon: "@/assets/icons/workout-metrics.png",
     },
     {
      name: "Edit Profile",
      route: "/edit-profile",
      icon: "@/assets/icons/edit-profile.png",
     },
     {
      name: "Gym Check-In / Check-Out",
      route: "/gym-checkin",
      icon: "@/assets/icons/gym-checkin.png",
     },
     {
      name: "Start Workout (temporary button)",
      route: "/startworkout",
      icon: "@/assets/icons/gym-checkin.png",
     }
    ],
   };
  },
 };
 </script>
 <style scoped>
 .home {
  text-align: center;
  margin: 20px;
 }
 .home-header {
  background-color: #F4F4F4;
  padding: 20px;
  border-radius: 10px;
  margin-bottom: 30px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2);
 }
 .profile-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
 }
 .profile-picture img {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  border: 2px solid #ddd;
  object-fit: cover;
 }
 .welcome-message h1 {
  color: #333;
  margin: 0;
 }
 .welcome-message p {
  color: #666;
  font-size: 1.2rem;
 }
 .home-dashboard {
  display: flex;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
 }
 .dashboard-card {
  width: 200px;
  background: #FFFFFF;
  border-radius: 10px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2);
  text-align: center;
  transition: transform 0.2s;
 }
 .dashboard-card:hover {
  transform: scale(1.05);
 }
 .card-icon img {
  width: 80px;
  height: 80px;
  margin: 20px auto;
 }
 .card-title {
  padding: 10px;
  background-color: #F8F8F8;
  border-top: 1px solid #ddd;
 }
 .card-title h2 {
  font-size: 1.2rem;
  color: #333;
 }
 </style>
