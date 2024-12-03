<template>
  <div id="capstone-app">
    <div id="nav" v-if="$route.name !== 'login'">
      <router-link v-if="$route.name !== 'login'" v-bind:to="{ name: 'home' }">Home</router-link>&nbsp;|&nbsp;
      <router-link v-bind:to="{ name: 'logout' }" v-if="$store.state.token != ''">Logout</router-link>
    </div>
    <router-view />
  </div>
</template>
