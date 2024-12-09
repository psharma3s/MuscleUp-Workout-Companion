<script>
import { useStore } from 'vuex';

export default {
  name: 'EmployeeCheckoutView',
  setup() {
    const store = useStore();

    // Redirect if the user is not an employee
    if (!store.state.user.authorities?.some(auth => auth.name === 'ROLE_EMPLOYEE')) {
      window.location.href = '/'; // Redirect to homepage
    }
  },
};
</script>