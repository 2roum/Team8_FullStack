<template>
    <div class="d-flex justify-content-center align-items-center" style="height: 80vh;">
        <div class="card text-center" style="width: 500px; background-color: white !important;">
            <div class="card-header text-white" style="background-color: #B01E68 !important;">시험지</div>

            <div class="card-body">
                <p class="card-title">
                    제출란
                </p>
                <form>
                    <div class="form-group">
                        <textarea class="form-control" id="inputAnswer" v-model="submitForm.description" rows="10"
                            style="resize:none; font-size: 1.2rem;"></textarea>
                    </div>
                </form>
            </div>
            <div class="card-footer d-flex justify-content-between">
                <input type="text" class="form-control" id="inputAnswer" v-model="submitForm.url"
                    placeholder="Enter your gitrepo here" style="width: 70%">
                <router-link :to="{ name: 'RoadMap' }">
                    <button type="submit" class="btn a" @click="postAssignMent">제출하기</button>
                </router-link>
            </div>
        </div>
    </div>
</template>
<script>
import { postAssignMentData } from '@/api'
export default {

    name: "FrqExamSubmitView",
    data() {
        return {
            subjectName: "",
            frqExamId: "",
            submitForm: {
                url: "",
                description: "// 프로젝트에 대한 간단한 설명\n"
                    + "// 각각의 파일이 무슨일을 하는지 적어주세요.\n"
                    + "// 해당 시험 진행하면서 어려웠던 점이나 고민했던 부분을 적어주세요.\n"
                    + "// 해당 과정을 들으면서 본인이 무엇을 배웠고 얼마나 성장했는지 적어주세요\n",
                frqExamId: ""
            }
        }
    },
    mounted() {
        this.frqExamId = this.$route.params.frqExamIdForSubmit;
    },
    methods: {
        async postAssignMent() {
            console.log("this.frqExamId" + this.frqExamId);
            const config = {
                headers: {
                    jwt: this.$store.state.jwt
                }
            };
            const params = {
                url: this.submitForm.url,
                description: this.submitForm.description,
                examId: this.frqExamId
            };
            await postAssignMentData(config, params)
                .then((response) => {
                    if (response.data.success) {
                        console.log("테스트 제출 완료!")
                    }

                })
                .catch(function (error) {
                    console.log(error);
                });
        }
    },
}
</script>

<style>
.card-frq {
    background-color: wheat !important;
    border-width: 0 !important;
    color: wheat !important;
    background: none !important;
}
</style>