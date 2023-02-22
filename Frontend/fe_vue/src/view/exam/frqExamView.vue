
<template>
    <div class="d-flex justify-content-center align-items-center" style="height: 80vh; overflow-y: scroll;">
        <div class="card text-center" style="width: 500px; background-color: white !important;">
            <div class="card-header text-white" style="background-color: #B01E68 !important;">시험지</div>
            <div class="card-body">
                <p class="card-title">{{ examTitle }}</p>
                <p class="card-text"> {{ examExplain }}</p>

            </div>
            <div class="card-footer">
                <a v-bind:href="examUrl" style="color:#B01E68;margin-right: 10px;">템플릿 코드를 다운로드하는 곳</a>
                <button class="btn a mt-2" @click="$router.push({
                    name: 'FrqExamSubmitView', params: {
                        frqExamIdForSubmit: frqExamId
                    }
                })">제출하러가기</button>
            </div>
        </div>
    </div>
</template>
<script>
import { getExamDetailData } from "@/api"
export default {
    name: "FrqExamView",
    data() {
        return {
            examUrl: "",
            examTitle: "",
            examExplain: "",
            frqExamId: "",
        }
    },
    mounted() {
        this.frqExamId = this.$route.params.frqExamId
        this.getExamDetail()
    },
    methods: {
        async getExamDetail() {
            const params = this.frqExamId
            const config = {
                headers: {
                    jwt: this.$store.state.jwt,
                },
            }
            await getExamDetailData(config, params)
                .then((response) => {
                    if (response.data.success) {
                        this.examUrl = response.data.examDetail.url
                        this.examTitle = response.data.examDetail.name
                        this.examExplain = response.data.examDetail.description
                    } else {
                        console.log("데이터를 불러오는데 실패하였습니다!")
                    }
                })
                .catch(function (error) {
                    console.log(error)
                })
        },
    },
}
</script>
<style>
.card-frqexam {
    background-color: wheat !important;
    border-width: 0 !important;
    color: black !important;
    background: wheat !important;
    width: 50%;
}

.card-header-frqexam {
    background-color: lightgray !important;
}

.container.frqexam {
    background-color: none;
    margin: auto;
    width: 60vw;
    margin: auto;
    height: 80% !important;
    border-radius: 40px 40px;
}
</style>