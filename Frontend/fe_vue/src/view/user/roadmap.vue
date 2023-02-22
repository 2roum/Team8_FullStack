<template>
  <div class="container big">
    <roadmapGuideVue class="roadmapGuide"></roadmapGuideVue>
    <h1 class="d-flex justify-content-center mt-4" style="color: #B01E68;">MY RoadMap</h1>
    <div class="d-flex justify-content-center mt-4" style="height: 80vh; overflow-y: scroll;">
      <div class="card-deck " style="width: 80vh;">
        <transition-group name="subject-cards" tag="div" class="card-deck">
          <transition :key="subject.subjectId" v-for="(subject, index) in subjects" :name="'subject-card-' + index"
            :css="false">
            <div class="subjectCards">
              <div class="card text-center mb-4">
                <div class="card-body">
                  <button class="btn a d-grid gap-2 col-6 mx-auto"
                    @click="[$router.push({ name: 'CourseView', params: { subjectId: subject.subjectId } }), setCurrentSubjectId(subject.subjectId)]"
                    style="margin-right:auto;">{{ subject.name }}</button>
                  <div class="col">
                    <button class="btn gap-2 col-2" :class="getButtonClass(subject.mcqState)" type="button"
                      @click="switchRouterByState(subject.mcqState, subject.mcqExamId, 'MCQ')"
                      style="margin-right: 50px;">
                      <span>객관식</span>
                      <span class="bi bi-file-text"></span>
                    </button>
                    <button class="btn gap-2 col-2" :class="getButtonClass(subject.frqState)" type="button"
                      style="margin-left: 50px;" @click="switchRouterByState(subject.frqState, subject.frqExamId, 'FRQ')">
                      <span>주관식</span>
                      <span class="bi bi-file-text"></span>
                    </button>
                  </div>

                </div>
              </div>
            </div>
          </transition>
        </transition-group>
      </div>
    </div>
    <reviewSelectModal @card-selected="onCardSelected" @close="closeModal" v-if="showReviewSelectModal">
    </reviewSelectModal>
    <reviewAiModal @card-selected="onCardSelected" @close="showReviewAiModal = false" v-if="showReviewAiModal">
    </reviewAiModal>
    <reviewPeerModal @card-selected="onCardSelected" @close="showReviewPeerModal = false" v-if="showReviewPeerModal">
    </reviewPeerModal>
  </div>
</template>


<script>
import { getRoadmap } from '@/api'
import reviewSelectModal from '@/components/reviewSelectModal.vue'
import reviewAiModal from '@/components/reviewAiModal.vue'
import reviewPeerModal from '@/components/reviewPeerModal.vue';
import roadmapGuideVue from '@/components/roadmapGuide.vue';
export default {
  name: "RoadMap",
  components: {
    reviewSelectModal,
    reviewAiModal,
    roadmapGuideVue,
    reviewPeerModal
  },
  data() {
    return {
      isCardOn: false,
      success: false,
      subDataSuccess: false,

      isSuccess: false,
      subjects: [],

      showReviewSelectModal: false,
      showReviewAiModal: false,
      showReviewPeerModal: false,
      frqExamIdForModal: "",
    };
  },
  mounted() {
    this.getSubData();
  },
  methods:
  {
    openReviewModal() {
      this.showReviewSelectModal = true
    },
    closeModal() {
      this.showReviewSelectModal = false
    },
    onCardSelected(cardClass) {
      this.selectedReviewType = cardClass;
      console.log("cardClass : " + cardClass);
      if (cardClass === 'gobackToSelectFromPeer') {
        this.showReviewPeerModal = false;
        this.showReviewSelectModal = true;

      } else if (cardClass === 'gobackToSelectFromAi') {
        this.showReviewAiModal = false;
        this.showReviewSelectModal = true;
      }
      else {
        this.showReviewSelectModal = false
        if (cardClass === 'ai') {
          this.showReviewAiModal = true
        }
        if (cardClass === 'peer') {
          this.showReviewPeerModal = true
        }
      }


    },

    async getSubData() {
      const config = {
        headers: {
          jwt: this.$store.state.jwt,
        }
      }
      var vm = this;
      await getRoadmap(config)
        .then((response) => {
          console.log("Getroadmap!!data : " + response.data.success)
          if (!response.data.success) {
            this.$router.push('/');
          }
          vm.isSuccess = response.data.success;
          vm.subjects = response.data.subjects;
        })
        .catch(function (error) {
          console.log(error);
        });
    },

    setCurrentSubjectId(subId) {
      // var courseStr = String(course);
      // const subjectID = courseStr.split(',')[1];
      console.log("subjec id setting : " + subId);
      return this.$store.commit("setCurSubjectId", subId);
    },
    getButtonClass(state) {
      switch (state) {
        case 'NONE':
          return 'btn btn-light btn-sm';
        case 'PURCHASED':
          return 'btn btn-info btn-sm';
        case 'SUBMITTED':
          return 'btn btn-warning btn-sm';
        case 'PASSED':
          return 'btn btn-success btn-sm';
        case 'FAILED':
          return 'btn btn-danger btn-sm';
        default:
          return 'btn btn-light btn-sm';
      }
    },
    switchRouterByState(state, examId, isMcqOrFrq) {
      if (state === 'NONE') {
        this.$router.push({ name: "ExamPurchaseView", params: { examId: examId } });
      }
      if (state === 'PASSED') {
        if (isMcqOrFrq == 'FRQ') {
          this.$store.commit('setCurSubjectExamId', examId);
          this.openReviewModal();
        }
      }
      if (state === 'PURCHASED') {
        if (isMcqOrFrq === 'MCQ') {
          console.log("purchased button  exam id log : " + examId)
          this.$router.push({ name: 'McqExamView', params: { mcqExamId: examId } });
        } else {
          console.log("purchased button  exam id log : " + examId)
          this.$router.push({ name: 'FrqExamView', params: { frqExamId: examId } });
        }
      }

    }
  }
};
</script>

<style>
.card {
  background-color: none !important;
  border-width: 0 !important;
  color: none !important;
  background: none !important;


}

.card:hover {
  transform: none !important;
}

.card button:hover {
  transform: translateY(-2px);
}


.card-body {
  background-color: none !important;
  transform: none !important;

  /* border-block-color: FF1DC !important; */
}

.subject-cards-enter-active,
.subject-cards-leave-active {
  transition-duration: 1000ms;
}

.container.big {
  background-color: wheat;
  margin: auto;
  width: 70vw;
  margin: auto;
  height: 80% !important;
  border-radius: 40px 40px;

}

.roadmapBody {
  background-repeat: repeat-y;
  background-size: 40vh;
  background-position: center;
  background-image: url("@/assets/road_svg.svg");
}

.subjectCards {
  opacity: 0;
  transform: translateX(-100px);
  animation: fadeInAndMove 1.5s ease-out forwards;
}

.subjectCards:nth-child(1) {
  animation-delay: 0.2s;
}

.subjectCards:nth-child(2) {
  animation-delay: 0.6s;
}

.subjectCards:nth-child(3) {
  animation-delay: 1.0s;
}

.subjectCards:nth-child(4) {
  animation-delay: 1.4s;
}

.subjectCards:nth-child(5) {
  animation-delay: 1.8s;
}

.subjectCards:nth-child(6) {
  animation-delay: 2.2s;
}

.btn.a {
  background-color: #B01E68 !important;
  color: white !important;
  margin-bottom: 5px;
}

@keyframes fadeInAndMove {
  0% {
    opacity: 0;
    transform: translateX(-50px);
  }

  100% {
    opacity: 1;
    transform: translateX(0);
  }
}
</style>
