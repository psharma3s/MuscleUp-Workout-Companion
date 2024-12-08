<template>
  <div class="workout-container">
    <div class="workout-box">
      <h1>Start Workout</h1>
      <!-- Dropdown for workout type -->
      <label for="workoutType">Choose Workout Type:</label>
      <select v-model="workoutType" id="workoutType" :disabled="isWorkoutActive">
        <option value="" disabled>Select Workout Type</option>
        <option value="cardio">Cardio</option>
        <option value="strength">Strength</option>
      </select>

      <!--Cardio Equipment-->
      <div v-if="workoutType === 'cardio'">
        <!-- Cardio Equipment Input -->
        <label for="equipment">Equipment (if any):</label>
        <input type="text" id="equipment" v-model="equipment" placeholder="For example: bike, treadmill, skateboard..."
          :disabled="isWorkoutActive" />
      </div>

      <!-- Cardio Exercise -->
      <div v-if="workoutType === 'cardio'">
        <label for="exercise">Cardio exercise (if any):</label>
        <input type="text" id="exercise" v-model="exercise"
          placeholder="For example: walking, running, swimming, sprinting" :disabled="isWorkoutActive" />
      </div>

      <!-- Conditional dropdown for strength exercises or equipment -->
      <div v-if="workoutType === 'strength'">
        <label for="strengthOption">Choose Strength Option:</label>
        <select v-model="strengthOption" id="strengthOption" :disabled="isWorkoutActive">
          <option value="" disabled>Select Strength Option</option>
          <option value="selectedWorkoutExercise">Compound Exercise</option>
          <option value="selectedWorkoutEquipment">Workout Equipment</option>
        </select>

        <!-- Strength Exercise Dropdown -->
        <div v-if="strengthOption === 'selectedWorkoutExercise'">
          <label for="selectedWorkoutExercise">Choose Muscle Group to Work On:</label>
          <select v-model="selectedWorkoutExercise" id="exercise" :disabled="isWorkoutActive">
            <option value="" disabled>Select Muscle Group</option>
            <option value="chest">Chest</option>
            <option value="back">Back</option>
            <option value="biceps">Biceps</option>
            <option value="triceps">Triceps</option>
            <option value="legs">Legs</option>
            <option value="shoulder">Shoulder</option>
            <option value="abs">Abs</option>
          </select>

          <!-- Exercises for selected muscle group -->
          <div v-if="selectedWorkoutExercise">
            <label for="exerciseOption">Choose Exercise:</label>
            <select v-model="exercise" id="exerciseOption" :disabled="isWorkoutActive">
              <option value="" disabled>Select Exercise</option>
              <option v-for="workoutExercises in workoutExercises[selectedWorkoutExercise]" :key="workoutExercises"
                :value="workoutExercises">{{ workoutExercises }}</option>
              <option value="other">Other</option>
            </select>
          </div>

          <!-- Custom Exercise Input -->
          <div v-if="exercise === 'other'">
            <label for="customExercise">Enter Custom Exercise:</label>
            <input type="text" id="customExercise" v-model="customExercise" placeholder="Enter your exercise"
              :disabled="isWorkoutActive" />
          </div>
        </div>

        <!-- Strength Equipment Dropdown -->
        <div v-if="strengthOption === 'selectedWorkoutEquipment'">
          <label for="selectedWorkoutEquipment">Choose Muscle Group to Work On:</label>
          <select v-model="selectedWorkoutEquipment" id="equipment" :disabled="isWorkoutActive">
            <option value="" disabled>Select Muscle Group</option>
            <option value="chest">Chest</option>
            <option value="back">Back</option>
            <option value="biceps">Biceps</option>
            <option value="triceps">Triceps</option>
            <option value="legs">Legs</option>
            <option value="shoulder">Shoulder</option>
            <option value="abs">Abs</option>
            <option value="other">Other</option>
          </select>

          <!-- Exercises for selected muscle group -->
          <div v-if="selectedWorkoutEquipment">
            <label for="exerciseOption">Choose Exercise:</label>
            <select v-model="equipment" id="exerciseOption" :disabled="isWorkoutActive">
              <option value="" disabled>Select Exercise</option>
              <option v-for="workoutEquipment in workoutEquipment[selectedWorkoutEquipment]" :key="workoutEquipment"
                :value="workoutEquipment">{{ workoutEquipment }}</option>
              <option value="other">Other</option>
            </select>
          </div>

          <!-- Custom Equipment Input -->
          <div v-if="equipment === 'other'">
            <label for="customEquipment">Enter Custom Equipment:</label>
            <input type="text" id="customEquipment" v-model="customEquipment" placeholder="Enter your equipment"
              :disabled="isWorkoutActive" />
          </div>

          <!-- Input for weight if equipment is selected or "Other" -->
          <div v-if="equipment && (equipment !== 'other' || customEquipment)">
            <label for="weight">Input Weight (lbs):</label>
            <input type="number" id="weight" v-model="weight" placeholder="Enter weight" :disabled="isWorkoutActive" />
          </div>
        </div>
      </div>

      <!-- Start/Stop button -->
      <button @click="toggleWorkout">
        {{ isWorkoutActive ? 'Stop Workout' : 'Start Workout' }}
      </button>

      <!-- Warning message if workout is active -->
      <p v-if="isWorkoutActive" class="warning-message">
        You are locked into the current exercise. Stop the workout to choose another.
      </p>

      <!-- Dynamic workout tracking -->
      <div v-if="isWorkoutActive">
        <div v-if="workoutType === 'strength'">
          <label for="strengthReps">Enter reps:</label>
          <input type="number" id="strengthReps" v-model="reps" placeholder="Enter number of reps" />
          <label for="strengthSet"> and/or enter sets:</label>
          <input type="number" id="strengthSets" v-model="strengthSets" placeholder="Enter number of sets" />
        </div>
        <div v-else-if="workoutType === 'cardio'">
          <p>Track distance and input here:</p>
          <label for="distance">Distance:</label>
          <input type="text" id="distance" v-model="distance" placeholder="Enter distance moved" />
          <label for="cardioReps"> and/or enter reps:</label>
          <input type="number" id="cardioReps" v-model="cardioReps" placeholder="Enter number of reps" />
          <label for="cardioSets"> and/or enter sets:</label>
          <input type="number" id="cardioSets" v-model="cardioSets" placeholder="Enter number of sets" />
        </div>
      </div>

      <!-- Display workout summary -->
      <div v-if="workoutSummary">
        <h2>Workout Summary</h2>
        <p v-if="workoutSummary.workoutType">Workout Type: {{ workoutSummary.workoutType }}</p>
        <p v-if="workoutSummary.strengthOption">
          Strength Option: {{ workoutSummary.strengthOption }}
        </p>
        <p v-if="workoutSummary.exercise">Exercise: {{ workoutSummary.exercise }}</p>
        <p v-if="workoutSummary.equipment">Equipment: {{ workoutSummary.equipment }}</p>
        <p v-if="workoutSummary.weight">Weight: {{ workoutSummary.weight }} lbs</p>
        <p v-if="workoutSummary.start">Start Time: {{ workoutSummary.start }}</p>
        <p v-if="workoutSummary.stop">Stop Time: {{ workoutSummary.stop }}</p>
        <p v-if="workoutSummary.workoutTime">
          Workout Duration: {{ workoutSummary.workoutTime }} seconds
        </p>
        <p v-if="workoutSummary.distance">Distance: {{ workoutSummary.distance }}</p>
        <p v-if="workoutSummary.reps">Strength Reps: {{ workoutSummary.reps }}</p>
        <p v-if="workoutSummary.cardioReps">Cardio Reps: {{ workoutSummary.cardioReps }}</p>
        <p v-if="workoutSummary.cardioSets">Cardio Sets: {{ workoutSummary.cardioSets }}</p>
        <p v-if="workoutSummary.strengthSets">Strength Sets: {{ workoutSummary.strengthSets }}</p>
      </div>
    </div>
  </div>
  <div class="assistance-box">
    <h2>Equipment Assistance</h2>
    <!-- Dropdown for muscle group -->
    <label for="muscleGroup">Choose Muscle Group:</label>
    <select v-model="selectedMuscleGroup" id="muscleGroup">
      <option value="" disabled>Select Muscle Group</option>
      <option value="chest">Chest</option>
      <option value="back">Back</option>
      <option value="biceps">Biceps</option>
      <option value="triceps">Triceps</option>
      <option value="legs">Legs</option>
      <option value="shoulder">Shoulder</option>
      <option value="abs">Abs</option>
    </select>

    <!-- Exercises for selected muscle group -->
    <div v-if="selectedMuscleGroup">
      <label for="exerciseOption">Choose Exercise:</label>
      <select v-model="selectedExercise" id="exerciseOption">
        <option value="" disabled>Select Exercise</option>
        <option v-for="exercise in exercises[selectedMuscleGroup]" :key="exercise" :value="exercise">{{ exercise }}
        </option>
      </select>
    </div>

    <!-- Display exercise animation -->
    <div v-if="selectedExercise">
      <h3>Exercise Animation:</h3>
      <img :src="getExerciseAnimation(selectedExercise)" alt="Exercise Animation" />
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      workoutType: '', // cardio or strength
      strengthOption: '', // "exercise" or "equipment"
      workoutMuscleGroup: '',
      workoutEquipmentGroup: '',
      exercise: '', // selected strength exercise
      customExercise: '', // user-defined exercise
      equipment: '', // selected workout equipment
      customEquipment: '', // user-defined equipment
      weight: null, // weight in lbs
      reps: null, // strength reps
      cardioReps: null, // cardio reps
      distance: null, // distance ran
      start: null, // workout start time
      stop: null, // workout stop time
      isWorkoutActive: false, // flag for workout state
      workoutSummary: null, // final workout payload
      cardioSets: null,
      strengthSets: null,

      selectedWorkoutExercise: '',
      selectedWorkoutEquipment: '',
      selectedMuscleGroup: '',
      selectedExercise: '',
      workoutExercises: {
        chest: ['Push-Ups', 'Chest Dips', 'Plank to Push-Up'],
        back: ['Superman', 'Prone Y Raise', 'Bird-Dogs'],
        biceps: ['Reverse Table Pull-Ups', 'Wall Push-ups', 'Isometric Squeeze'],
        triceps: ['Tricep Dips', 'Diamond Push-ups', 'Plank to Elbows'],
        legs: ['Squats', 'Lunges', 'Wall Sit'],
        shoulder: ['Arm Circles', 'Wall Walks', 'Y Raises'],
        abs: ['Sit-Ups', 'Bicycle Crunches', 'Plank']
      },
      workoutEquipment: {
        chest: ['Barbell Bench Press', 'Dumbbell Fly', 'Push-Ups', 'Cable Crossover', 'Machine Fly', 'Dumbbell Bench Press'],
        back: ['Barbell Deadlift', 'Pull-Up', 'Barbell Bent-Over Row', 'Back Hyperextension', 'Dumbbell One Arm Row'],
        biceps: ['Barbell Curl', 'Cable Bicep Curl', 'Dumbbell Alternating Bicep Curl', 'Machine Bicep Curl Reverse Grip', 'Dumbbell Seated Bicep Curl'],
        triceps: ['Dip', 'Cable One Arm Tricep Pushdown', 'Cable Tricep Pulldown', 'Dumbbell Tricep Extension', 'Dumbbell One Arm Tricep Kickback'],
        legs: ['Barbell Squat', 'Machine Calf Raises', 'Machine Leg Press', 'Machine Leg Curl', 'Machine Seated Leg Curl'],
        shoulder: ['Barbell Military Press', 'Cable Shoulder Press', 'Dumbbell Seated Shoulder Press', 'Machine Shoulder Press', 'Smith Machine Shoulder Press'],
        abs: ['Bird Dog', 'Leg Raise', 'Plank', 'Machine Ab Crunch', 'Cable Kneeling Crunch'],
      },
      exercises: {
        chest: ['Barbell Bench Press', 'Dumbbell Fly', 'Push-Ups', 'Cable Crossover', 'Machine Fly', 'Dumbbell Bench Press'],
        back: ['Barbell Deadlift', 'Pull-Up', 'Barbell Bent-Over Row', 'Back Hyperextension', 'Dumbbell One Arm Row'],
        biceps: ['Barbell Curl', 'Cable Bicep Curl', 'Dumbbell Alternating Bicep Curl', 'Machine Bicep Curl Reverse Grip', 'Dumbbell Seated Bicep Curl'],
        triceps: ['Dip', 'Cable One Arm Tricep Pushdown', 'Cable Tricep Pulldown', 'Dumbbell Tricep Extension', 'Dumbbell One Arm Tricep Kickback'],
        legs: ['Barbell Squat', 'Machine Calf Raises', 'Machine Leg Press', 'Machine Leg Curl', 'Machine Seated Leg Curl'],
        shoulder: ['Barbell Military Press', 'Cable Shoulder Press', 'Dumbbell Seated Shoulder Press', 'Machine Shoulder Press', 'Smith Machine Shoulder Press'],
        abs: ['Bird Dog', 'Leg Raise', 'Plank', 'Machine Ab Crunch', 'Cable Kneeling Crunch'],
      },
    };
  },
  methods: {
    toggleWorkout() {
      if (!this.isWorkoutActive) {
        // Start workout
        this.start = new Date();
        this.isWorkoutActive = true;
      } else {
        // Stop workout
        this.stop = new Date();
        const workoutTime = Math.round((this.stop - this.start) / 1000); // Workout duration in seconds

        // Prepare workout summary
        this.workoutSummary = {
          workoutType: this.workoutType,
          exercise: this.exercise === 'other' ? this.customExercise || null : this.exercise || null,
          equipment: this.equipment === 'other' ? this.customEquipment || null : this.equipment || null,
          weight: this.weight || null,
          start: this.start.toLocaleString(),
          stop: this.stop.toLocaleString(),
          workoutTime,
          reps: this.workoutType === 'strength' ? this.reps : null,
          cardioReps: this.workoutType === 'cardio' ? this.cardioReps : null,
          distance: this.workoutType === 'cardio' ? this.distance : null,
          cardioSets: this.workoutType === 'cardio' ? this.cardioSets : null,
          strengthSets: this.workoutType === 'strength' ? this.strengthSets : null
        };

        let equipmentUsed;
        let exerciseCompleted;

        if (this.workoutType === 'strength') {
          if (this.exercise === 'other') {
            exerciseCompleted = this.customExercise || 'Unnamed compound exercise'; // Handle empty custom exercise
          } else if (this.exercise) {
            exerciseCompleted = this.exercise; // Use selected exercise
          } else {
            exerciseCompleted = 'Non-compound exercise'
          }
          if (this.equipment === 'other') {
            equipmentUsed = this.customEquipment || 'None'; // Handle empty custom equipment
          } else if (this.equipment) {
            equipmentUsed = this.equipment; // Use selected equipment
          } else {
            equipmentUsed = 'None'; // Fallback if no input provided
          }
        } else if (this.workoutType === 'cardio') {
          equipmentUsed = this.equipment || 'None'; // Non-strength workouts have no equipment
          exerciseCompleted = this.exercise || 'Unnamed cardio exercise';

        } else {
          equipmentUsed = 'None'; // Non-strength workouts have no equipment
          exerciseCompleted = 'Non-compound exercise';
        }

        const metric = {
          userId: this.$store.state.user.id, // Use actual logged-in user ID
          equipmentUsed: equipmentUsed,
          weights: this.weight || 0,
          reps: this.reps || this.cardioReps || 0,
          date: this.start.toISOString(),
          distance: this.distance || 0,
          workoutType: this.workoutType || "None",
          workoutDuration: workoutTime,
          exercise: exerciseCompleted,
          sets: this.cardioSets || this.strengthSets || 0,
        };

        console.log("Metric being sent:", metric);
        this.$store.dispatch('workoutMetrics/addMetric', metric);

        // Reset state
        this.resetWorkout();
      }
    },
    resetWorkout() {
      this.isWorkoutActive = false;
      this.reps = null;
      this.cardioReps = null;
      this.distance = null;
      this.weight = null;
      this.start = null;
      this.stop = null;
      this.equipment = null;
      this.exercise = null;
      this.cardioSets = null;
      this.strengthSets = null;
    },
    getExerciseAnimation(exercise) {
      return `/animations/muscle-group/${this.selectedMuscleGroup.toLowerCase()}/${exercise.toLowerCase().replace(' ', '-')}.gif`;
    },
  },
  computed: {
    metrics() {
      return this.$store.getters['workoutMetrics/metrics'];
    },
  },
  created() {
    const userId = this.$store.state.user.id; // Replace with actual user ID
    this.$store.dispatch('workoutMetrics/fetchMetricsByUserId', userId);
  },
};
</script>

<style scoped>
.workout-container {
  display: flex;
  flex-direction: row;
  /* Arrange items horizontally */
  align-items: flex-start;
  justify-content: space-between;
  /* Space between the two boxes */
  padding: 15px;
}

.workout-box,
.assistance-box {
  width: 30%;
  /* Ensure the two boxes are next to each other */
  padding: 20px;
  border: 2px solid #ccc;
  border-radius: 8px;
  background-color: #f9f9f9;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

h1,
h2 {
  font-size: 24px;
  margin-bottom: 20px;
  text-align: center;
}

label {
  display: block;
  margin-top: 10px;
  font-weight: bold;
}

select,
input {
  width: calc(100% - 16px);
  /* Adjusted width to account for padding */
  padding: 8px;
  margin-top: 5px;
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  width: calc(100% - 16px);
  /* Adjusted width to account for padding */
  padding: 10px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
}

button:hover {
  background-color: #218838;
}

.warning-message {
  color: red;
  margin-top: 10px;
}

.assistance-box img {
  width: calc(100% - 16px);
  /* Adjusted width to account for padding */
}
</style>