<template>
    <div class="container" style="display:contents;">
        <div class="message">
            <div class="slide-up">Make your own Career</div>
            <div class="buttons">
                <button class="button" @click="showLoginForm" v-show="!showLogin && !$store.state.isLogin">Login</button>
                <button class="button" @click="$router.push('/signup')" v-show="!showLogin && !$store.state.isLogin">Sign
                    up</button>
                <button class="button" v-show="$store.state.isLogin" @click="$router.push('/roadmap')">To Roadmap</button>
            </div>
        </div>
        <transition name="fade">
            <div v-if="showLogin" class="login-form-container">
                <Login @closeLoginForm="closeLoginForm" />
            </div>
        </transition>
    </div>
</template>
  
<script>
import Login from "@/components/login.vue";

export default {
    name: "Home",
    data() {
        return {
            showLogin: false,
        };
    },
    components: {
        Login,
    },
    methods: {
        showLoginForm() {
            this.showLogin = true;
        },
        closeLoginForm() {
            this.showLogin = false;
        },
    },
};
</script>
  
<style>
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.5s;
}

.fade-enter,
.fade-leave-to {
    opacity: 0;
}

.login-form-container {
    justify-content: center;
    position: relative;
    margin-top: 25%;
    height: 40%;
    animation: fadeIn 1s ease forwards;
}

.background {
    height: 100vh;
    overflow: hidden;
    margin: 0;
    background: linear-gradient(to bottom, #FFE15D, #ff7f50, #ff6b6b);
}

.message {
    position: absolute;
    top: 60%;
    height: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    color: #fff;
    font-size: 36px;
    font-weight: bold;
    text-align: center;
    opacity: 0;
    animation: fadeIn 1s ease forwards, slideUp 0.5s ease forwards 1.5s;
}

.buttons {
    display: flex;
    justify-content: center;
    opacity: 0;
    animation: fadeIn 1s ease forwards 2s;
}

.button {
    display: block;
    margin: 20px;
    padding: 10px 20px;
    font-size: 24px;
    font-weight: bold;
    border: none;
    border-radius: 5px;
    color: #fff;
    background-color: #DC3535;
}

@keyframes slideUp {
    from {
        transform: translate(-50%, -50%) translateY(0);
        /* start from current position */
    }

    to {
        transform: translate(-50%, -50%) translateY(-50px);
        /* move up by 50px */
    }
}

@keyframes fadeIn {
    from {
        opacity: 0;
    }

    to {
        opacity: 1;
    }
}

.homeTitle {
    padding-top: 150px;
    text-align: center;
}
</style>
  