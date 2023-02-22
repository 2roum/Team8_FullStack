<template>
    <div class="d-flex justify-content-center align-items-center" style="height: 80vh;overflow-y: scroll; ">
        <div class="card text-center" style="width: 500px;background-color: white !important;">
            <div class="card-header text-white" style="background-color:#B01E68 !important">{{ subjectName }} 시험지</div>
            <div class="card-body">
                <p class="card-title">{{ name }}</p>
                <hr>
                <!-- <ul class="list-group list-group-flush">
                    <li class="list-group-item">{{ description }}</li>
                </ul> -->
            </div>
            <form @submit.prevent="onSubmit">
                <div v-for="(question, index) in questions" :key="index">
                    <div class="card-body">
                        <h5 class="card-title">{{ question.title }}!</h5>
                        <p class="card-text">{{ question.content }}</p>
                        <div class="form-check">
                            <template v-for="(choice, choiceIndex) in question.choices" :key="choice">
                                <div class="form-check">
                                    <input class="form-check-input" type="radio" :value="choiceIndex" v-model="myAns[index]"
                                        :id="`q${index}-choice${choiceIndex}`">
                                    <label class="form-check-label" :for="`q${index}-choice${choiceIndex}`">{{
                                        choice
                                    }}</label>
                                </div>
                            </template>
                        </div>
                    </div>
                </div>
                <div class="card-footer">
                    <router-link :to="{ name: 'RoadMap' }">
                        <button type="submit" @click="onSubmit" class="btn a mt-2">제출하기</button>
                    </router-link>
                </div>
            </form>
        </div>
    </div>
</template>
  

<script>
import { getExamDetailData, sendExamResult } from '@/api';
export default {
    name: 'McqExamView',
    data() {
        return {
            subjectName: "",
            name: "",
            description: "",
            questions: [],
            ans: [],
            myAns: [],
            isPassed: false,
            mcqExamId: "",
        }
    },
    mounted() {
        console.log("mcq examview exam id : " + this.$route.params.mcqExamId);
        this.mcqExamId = this.$route.params.mcqExamId;
        this.getExamDetail();
    },
    methods: {
        setTestResult() {
            var correct = 0;
            for (var i = 0; i < this.ans.length; i++) {
                if (this.ans[i] === this.myAns[i]) {
                    correct++;
                }
            }
            var result = (correct / this.ans.length) * 100;
            console.log("시험 결과  : " + result)
            if (result >= this.$store.state.passingScore) {
                this.isPassed = true;
            } else this.isPassed = false;
        },
        async onSubmit() {
            console.log("test pending....")

            this.setTestResult();
            console.log("test pending....")
            const params = {
                examId: this.mcqExamId,
                result: this.isPassed
            };
            const config = {
                headers: {
                    jwt: this.$store.state.jwt
                }
            };
            await sendExamResult(config, params)
                .then((response) => {
                    if (response.data.success) {
                        console.log("테스트 제출 성공")
                        console.log("result: this.isPassed : " + this.isPassed);
                        // 현재는 테스트 결과에 상관 없이 roadmap 으로 반환한다. 라우터 링크.
                        // this.$router.push('/roadmap');
                    } else {
                        console.log("테스트 실패")
                    }
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
        async getExamDetail() {
            const params = this.mcqExamId;
            console.log(" const params = this.mcqExamId : " + this.mcqExamId)
            const config = {
                headers: {
                    jwt: this.$store.state.jwt
                }
            };
            await getExamDetailData(config, params)
                .then((response) => {
                    if (response.data.success) {
                        this.subjectName = response.data.examDetail.subjectName;
                        this.name = response.data.examDetail.name;
                        this.description = response.data.examDetail.description;
                        this.questions = response.data.examDetail.questions;
                        this.ans = response.data.examDetail.ans;
                        console.log("questions : " + this.questions);
                    } else {
                        console.log("데이터를 불러오는데 실패하였습니다!")
                    }
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
    }

}
</script>

<style></style>