<template>
  <div class="container courseview">
    <div class="row row-cols-1 row-cols-md-3 g-4" style="overflow-y: scroll;width: 70vw;margin: auto;">
      <div class="col" v-for="course in subjectDetail" :key="course">
        <router-link :to="{ name: 'ChapterView', params: { courseId: course.id } }"
          style="text-decoration: none; color:black;">
          <div class="card">
            <img :src="course.thumbnailUrl" class="card-img-top" alt="...">
            <div class="card-body" style="background-color:white !important">
              <h5 class="card-title">{{ course.courseName }}</h5>
              <p class="card-text">{{ course.description }}</p>
            </div>
          </div>
        </router-link>
      </div>
    </div>
  </div>
</template>
<script>
import { getSubjectDetail } from "@/api";

export default {
  data() {
    return {
      subid: "",
      subjectDetail: null,
    };
  },
  mounted() {
    this.getCourseData();
  },
  methods: {
    async getCourseData() {
      this.subid = this.$store.state.curSubjectId;

      const config = {
        headers: {
          jwt: this.$store.state.jwt
        }
      };
      await getSubjectDetail(this.subid, config)
        .then((response) => {
          this.subjectDetail = response.data.courses;
        })
        .catch(function (error) {
          console.log(error);
        });
    },
  },
};
</script>
<style>
.card {
  cursor: pointer;
  transition: all 0.3s ease-in-out;

}

.card:hover {
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.3);
}

.container.courseview {
  background-color: wheat;
  margin: auto;
  width: 70vw;
  height: 85% !important;
  border-radius: 40px 40px;


}
</style>