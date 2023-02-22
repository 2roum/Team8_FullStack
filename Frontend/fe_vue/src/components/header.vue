<template>
  <nav class="navbar sticky-top">
    <button class="btn" style="position:absolute;left:90%;" @click="demoOnOff">
      {{ DemoMode }}
      <demo-btn v-if="DemoOnOff"></demo-btn>
    </button>
    <div class="menu" v-if="!isAdmin">
      <div class="container pullUp">
        <router-link to="/" class="mainLogo no-pull-up" style="color : #B01E68">DevRoad</router-link>
        <b v-if="!isLogin">
          <router-link :to="{ name: 'LogIn' }" style="color : #B01E68;">LogIn</router-link>
        </b>
        <b v-if="!isLogin">
          <router-link :to="{ name: 'SignUp' }" style="color : #B01E68;">Sign Up
          </router-link>
        </b>
        <b v-if="isLogin">
          <router-link :to="{ name: 'Home' }" @click="logout">
            <i class="bi bi-person-slash" style="color : #B01E68;font-size: 26px;"></i>
          </router-link>
        </b>
        <b v-if="isLogin">
          <router-link :to="{ name: 'RoadMap' }">
            <i class="bi bi-signpost-split-fill" style="color : #B01E68;font-size: 26px;"></i>
          </router-link>
        </b>
        <b v-if="isLogin">
          <router-link :to="{ name: 'UserHome' }">
            <i class="bi bi-person-circle" style="color : #B01E68;font-size: 26px;"></i>
          </router-link>
        </b>
      </div>
    </div>
    <div class="menu" v-if="isAdmin">
      <div class="container pullUp">

        <router-link to="/" @click="$router.go(0)" class="mainLogo no-pull-up"
          style="color : #B01E68">DevRoad</router-link>

        <b v-if="isLogin">
          <router-link :to="{ name: 'Home' }" @click="logout">
            <i class="bi bi-person-slash" style="color : #B01E68;font-size: 26px;"></i>
          </router-link>
        </b>
        <b v-if="isLogin">
          <router-link :to="{ name: 'AdminHome' }">
            <i class="bi bi-signpost-split" style="color : #B01E68;font-size: 26px;"></i>
          </router-link>

        </b>
        <b v-if="isLogin">
          <router-link :to="{ name: 'AdminUserView' }">
            <i class="bi bi-people-fill" style="color : #B01E68;font-size: 26px;"></i>
          </router-link>
        </b>
      </div>

    </div>

  </nav>
</template>

<script>
import demoBtn from './demoBtn.vue';
export default {
  name: "Header",
  components: {
    demoBtn
  },
  data() {
    return {
      DemoOnOff: true,
      DemoMode: "DemoModeOff"
    }
  },
  computed: {

    isAdmin() {
      return this.$store.state.isAdmin;
    },
    isLogin() {
      return this.$store.state.isLogin;
    },
  },
  methods: {
    logout() {
      this.$store.commit('logout');
      window.localStorage.clear();
    },
    demoOnOff() {
      this.DemoOnOff = !this.DemoOnOff;
      if (this.DemoOnOff == true) {
        this.DemoMode = "DemoMode Off"
      } else {
        this.DemoMode = "DemoMode On"
      }
    }
  },
};
</script>

<style>
.menu {
  border-top: 40px;
  width: 70vw;
  margin: 20px auto;
}

.menu:after {
  content: "";
  display: block;
  border-bottom: 2px solid #B01E68;
}

.mainLogo {
  color: black;
  text-align: left;
  font-size: 30px;
  text-decoration: none;
}

.mainLogo:before {
  display: none;
}

.menu b {
  color: black;
  padding: 20px;
  text-decoration: none;
}

/* .div.container {
  font-family: Raleway;
  margin: 0 auto;
  padding: 10em 3em;
  text-align: center;
} */

div.container a {
  color: #FFF;
  text-decoration: none;
  font: Raleway;
  margin: 0px 10px;
  padding: 10px 10px;
  position: relative;
  z-index: 0;
  cursor: pointer;
}

div.pullUp a:before {
  position: absolute;
  width: 100%;
  height: 2px;
  left: 0px;
  bottom: 0px;
  content: '';
  background: #B01E68;
  opacity: 0.3;
  transition: all 0.3s;
}

div.pullUp a:hover:before {
  height: 100%;
}
</style>
