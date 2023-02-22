import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import "bootstrap/dist/css/bootstrap.min.css"; // [bootstrap]
import "bootstrap"; // [bootstrap]
import "bootstrap-icons/font/bootstrap-icons.css";
import store from "./store";
import LoadScript from "vue-plugin-load-script";
import VueIframe from "vue-iframes";

createApp(App)
  .use(store)
  .use(router)
  .use(VueIframe)
  .use(LoadScript)
  .mount("#app");
