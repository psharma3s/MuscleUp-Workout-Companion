import { createRouter as createRouter, createWebHistory } from 'vue-router'
import { useStore } from 'vuex'


// Import components
import HomeView from '../views/HomeView.vue';
import LoginView from '../views/LoginView.vue';
import LogoutView from '../views/LogoutView.vue';
import RegisterView from '../views/RegisterView.vue';
import EditProfile from '../views/EditProfile.vue';
import WorkoutMetrics from '../views/WorkoutMetrics.vue';
import CheckInOutView from "../views/CheckInOutView.vue";
import StartExercise from "../views/StartExerciseView.vue";
import ProfileView from '../views/ProfileView.vue';
import EmployeeCheckoutView from '../views/EmployeeCheckoutView.vue';
import EmployeeEquipmentUsage from '../views/EmployeeEquipmentUsage.vue'
import PastClasses from '../views/PastClasses.vue'
/**
 * The Vue Router is used to "direct" the browser to render a specific view component
 * inside of App.vue depending on the URL.
 *
 * It also is used to detect whether or not a route requires the user to have first authenticated.
 * If the user has not yet authenticated (and needs to) they are redirected to /login
 * If they have (or don't need to) they're allowed to go about their way.
 */
const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
    meta: {
      requiresAuth: true
    }
  },
  {
    path: "/login",
    name: "login",
    component: LoginView,
    meta: {
      requiresAuth: false
    }
  },
  {
    path: "/logout",
    name: "logout",
    component: LogoutView,
    meta: {
      requiresAuth: false
    }
  },
  {
    path: "/register",
    name: "register",
    component: RegisterView,
    meta: {
      requiresAuth: false
    }
  },
  {
    path: "/edit-profile",
    name: "EditProfile",
    component: EditProfile,
    meta: {
      requiresAuth: true
    }
  },

  {
    path: '/workout-metrics',
    name: 'workout-metrics',
    component: () => import("@/views/WorkoutMetrics.vue"),
    meta: {
      requiresAuth: true,
    }
  },
  {
    path: "/gym-checkin",
    name: "gym-checkin",
    component: CheckInOutView,
  },
  {
    path: '/startworkout',
    name: 'StartWorkout',
    component: StartExercise,
    meta: {
      requiresAuth: true,
    }
  },
  {
    path: '/profile',
    name: 'profile',
    component: ProfileView,
    meta: { requiresAuth: true },
  },
  {
    path: '/employee-checkout',
  name: 'employee-checkout',
  component: EmployeeCheckoutView,
  meta: { requiresAuth: true, requiresRole: 'ROLE_EMPLOYEE' },
  },
  {
    path: '/equipmentuse',
    name: 'equipmentuse',
    component: EmployeeEquipmentUsage,
    meta: { requiresAuth: true, requiresRole: 'ROLE_EMPLOYEE' },
  },
  {
    path: '/pastclasses',
    name: 'Past Classes',
    component: PastClasses,
    meta: { requiresAuth: true },
  },
];

// Create the router
const router = createRouter({
  history: createWebHistory(),
  routes: routes
});

router.beforeEach((to) => {

  // Get the Vuex store
  const store = useStore();

  // Determine if the route requires Authentication
  const requiresAuth = to.matched.some(x => x.meta.requiresAuth);

  // If it does and they are not logged in, send the user to "/login"
  if (requiresAuth && store.state.token === '') {
    return { name: "login" };
  }
  // Otherwise, do nothing and they'll go to their next destination
});

export default router;
