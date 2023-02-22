<template>
  <div class="background">
    <Header></Header>
    <router-view> </router-view>
  </div>
</template>

<script>
import Header from "./components/header.vue";
export default {
  name: 'App',
  components: {
    Header
  },
  mounted() {
    this.logout();
  },
  methods: {
    logout() {
      this.$store.commit('logout');
      window.localStorage.clear();
    },
  }
}
</script>

<style lang="scss" scoped>
.background {
  height: 100vh;
  overflow: hidden;
  margin: 0;
  background: linear-gradient(to bottom, #FFE15D, #ff7f50);

}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
