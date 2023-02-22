<template>
  <div class="container big userhome">
    <div class="container mt-5" style="display:flex; width:60%">
      <div class="circle-expain1">
        {{ userName }}님의 현재 과목 전체 진행도 :
      </div>
      <div class="row" style="display:flex;flex-direction:column;margin-left:auto">
        <div class="col-md-3 col-sm-6 mt-4">
          <circle-progress :show-percent="true" :percent="subjectPercentage" :is-gradient="true" :gradient="{
            angle: 90,
            startColor: '#B01E68',
            stopColor: '#B01E68'
          }" style="width:13vw" />
        </div>
      </div>
    </div>
    <div class="container mt-5" style="display:flex;  width:60%">
      <div class="row" style="display:flex;flex-direction:column;margin-right;:auto">
        <div class="col-md-3 col-sm-6">
          <circle-progress style="width:13vw" :show-percent="true" :percent="roadmapPercentage" :is-gradient="true"
            :gradient="{
              angle: 90,
              startColor: '#B01E68',
              stopColor: '#B01E68'
            }
            " />
        </div>
      </div>
      <div class="circle-expain1">
        : 만큼 현재 과목에서 수강하셨군요!
      </div>
    </div>
    <div class="container">
      <router-link :to="{ name: 'RoadMap' }" v-if="!isRoadmapStarted" style="text-decoration: none;">
        <button class="btn a d-grid gap-2 col-2 mx-auto mt-4" style=" color:white;" v-if="!roadMapShowClicked">
          로드맵 시작하기!
        </button>
      </router-link>
      <div v-if="isRoadmapStarted">
        <button @click="routeByCurChapterPK" class="btn a d-grid gap-2 col-2 mx-auto mt-4">
          로드맵 이어하기!
        </button>
      </div>
    </div>

  </div>
</template>
<script>
import "vue3-circle-progress/dist/circle-progress.css";
import CircleProgress from "vue3-circle-progress";
import { userData } from '@/api'

export default {
  components: {
    CircleProgress,

  },

  name: "UserHome",
  data() {
    return {
      roadmapPercentage: 0,
      roadMapShowClicked: false,
      isSuccess: false,
      userId: null,
      userName: null,
      roadmapId: "-1",
      totalSubjectIdx: 0,
      curSubjectIdx: 0,
      chapterPercent: 0,
      curChapterPK: 0,
      subscribe: false,
      jwt: null,
      subjectPercentage: 0,
    };
  },
  mounted() {
    this.setProgressbar();
    this.getUserData();

  },
  computed: {
    isRoadmapStarted() {
      if (this.chapterPercent != 0) {
        return true;
      }
      return false;
    },
  },
  methods: {
    routeByCurChapterPK() {
      var cpk = this.curChapterPK;
      if (cpk === -1) {
        this.$router.push('/roadmap');
      } else {
        this.$router.push({ name: 'ChapterFrame', params: { chapterId: this.curChapterPK } })
      }
    },
    async getUserData() {
      const config = {
        headers: {
          jwt: this.$store.state.jwt
        }
      };
      await userData(config)
        .then((response) => {


          console.log("user data jwt : " + config);
          this.$store.commit('setAccountId', response.data.userId);
          this.userId = response.data.userId;
          this.userName = response.data.userName;
          this.roadmapId = response.data.roadmapId;
          if (this.roadmapId != "-1") {
            this.$store.commit('setIsRoadmap', true);
          }
          this.totalSubjectIdx = response.data.totalSubjectIdx;
          this.curSubjectIdx = response.data.curSubjectIdx;
          this.chapterPercent = response.data.chapterPercent;
          this.curChapterPK = response.data.curChapterPK;
          this.subscribe = response.data.subscribe;
          this.setProgressbar();
        })
        .catch(function (error) {
          console.log(error);
        });


    },
    setProgressbar() {
      if (this.chapterPercent == 0) {
        this.roadmapPercentage = 0;
        return;
      }
      this.roadmapPercentage =
        (this.chapterPercent)
        * 100;

      this.subjectPercentage = (this.curSubjectIdx / this.totalSubjectIdx) * 100;
      console.log("this subject percentage" + this.subjectPercentage);
    },

  },
};
</script>

<style>
.circle-expain1 {
  margin: auto;
  font-size: large;
}

.container.big.userhome {
  background-color: wheat;
  margin: auto;
  width: 70%;
  height: 70% !important;
  border-radius: 40px 40px;


}
</style>
