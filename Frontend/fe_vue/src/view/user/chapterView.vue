<template>
  <div class="container chapterview">
    <div class="title sticky">
      {{ courseName }}
    </div>
    <div class="d-flex justify-content-center mt-4" style="height: 80%;width: 70%;margin: auto;overflow-y: scroll;">
      <div class="card-deck">
        <div v-for="chapter in chapters" :key="chapter">
          <router-link :to="{ name: 'ChapterFrame', params: { chapterId: chapter.chapterId } }"
            style="text-decoration: none; color: black;">
            <div class="card text-center mb-4">
              <div class="card-body chapterbody">
                <h5 class="card-title">
                  <div>
                    <div class="form-check">
                      <input class="form-check-input" type="checkbox" :checked="chapter.finish" value=""
                        id="flexCheckDefault">
                      {{ chapter.chapterName }}
                    </div>
                  </div>
                </h5>
                <p class="card-text">{{ chapter.description }}</p>
              </div>
            </div>
          </router-link>

        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getCourseData } from "@/api";


export default {
  name: "ChapterView",
  data() {
    return {
      curCourseId: "",
      chapters: [],
      curChapterId: "",
      isChecked: false,
      courseName: ""
      /* chapters
         "chapterName" : "자료형", 
         "chapterId" : 10011, 
         "chapterUrl" : "http://toa/lec/10011", 
         "thumbnailUrl":"url", 
         "description" : "자료형의 종류는 무엇이 있을까용?",
         "finish" : true*/
    };
  },
  computed: {
    courseId() {
      return this.$route.params.courseId;
    },
  },
  mounted() {
    this.getchapters();
  },
  methods: {
    async getchapters() {
      this.curCourseId = this.$route.params.courseId;

      const config = {
        headers: {
          jwt: this.$store.state.jwt
        }
      };
      await getCourseData(this.curCourseId, config)
        .then((response) => {
          this.chapters = response.data.chapters;
          this.courseName = response.data.chapters[0].courseName;
          this.curChapterId = response.data.curChapterId;
        })
        .catch(function (error) {
          console.log(error);
        });
    },
  },
};
</script>
<style>
.container.chapterview {
  background-color: wheat;
  margin: auto;
  width: 70vw;
  height: 80% !important;
  border-radius: 40px 40px;


}

@supports (position: sticky) or (position: -webkit-sticky) {
  .sticky {
    position: sticky;
    top: 4px;
    background: wheat;

  }
}

.card-body.chapterbody {
  margin: auto;
}

.title {
  text-align: center;
  font-size: 23px;
  margin-top: 10px;
  position: sticky;
  background: wheat;
  border-radius: 40px 40px;
  color: #B01E68;

}

.title:after {
  content: "";
  display: block;
  margin-top: 10px !important;
  border-bottom: 1.5px solid #B01E68;
  width: 70%;
  margin: auto;
}
</style>
