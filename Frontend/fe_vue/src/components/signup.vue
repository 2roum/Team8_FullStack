<template>
  <div class="container signup">
    <div class="row">
      <div class="offset-3 col-6 mt-4">
        <form @submit.prevent="submitForm" class="col-12">
          <div class="mb-4 ps-5 pe-5">
            <label for="InputEmail" class="form-label">Email address</label>
            <input type="email" class="form-control" id="InputEmail" v-model="formData.email"
              aria-describedby="emailHelp">
          </div>
          <div class="mb-4 ps-5 pe-5">
            <label for="InputPassword1" class="form-label">Password</label>
            <input type="password" class="form-control" v-model="formData.password" id="InputPassword1">
          </div>
          <div class="mb-4 ps-5 pe-5">
            <label for="InputName" class="form-label">Name</label>
            <input type="text" class="form-control" v-model="formData.name" id="InputEmail1" aria-describedby="emailHelp">
          </div>
          <div class="mb-4 ps-5 pe-5">
            <label for="InputPhoneNumber" class="form-label">PhoneNumber</label>
            <input type="text" class="form-control" v-model="formData.phoneNumber" id="InputPhoneNumber">
          </div>

          <button type="submit" variant="primary" class="btn btn-dark d-grid gap-2 mx-auto">Submit</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { signupUser } from "@/api";

export default {
  name: "SignUp",
  data() {
    return {
      formData: {
        email: "",
        password: "",
        name: "",
        phoneNumber: ""
      }

    };
  },

  methods: {
    async submitForm() {
      const userData = {
        email: this.formData.email,
        password: this.formData.password,
        name: this.formData.name,
        phone: this.formData.phoneNumber
      };
      await signupUser(userData).then(response => {
        if (response.data.success) {
          this.$router.push({ name: "Home" });
        } else {
          console.log("signup failed : " + response.data.success);
        }
      });
    }
  }
};
</script>
<style>
.container.signup {
  width: auto;
  margin: auto;
}
</style>