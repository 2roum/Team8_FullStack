<template>
    <div class="container chapterFrame">
        <div class="container wrapper">
            <div class="myframe d-flex justify-content-center">
                <iframe :src="chapterUrl" title="YouTube video player" frameborder="0"
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                    width="560" height="315" allowfullscreen
                    style="width:100vh; height: 50vh; margin-top: 40px;!important" />
            </div>
            <div class="d-flex justify-content-center mt-4">
                <button class="btn a" @click="finishChapter" onmouseover="this.innerHTML='다음 강의로';"
                    onmouseout="this.innerHTML='수강완료';" style="margin-right:10px !important">수강완료</button>
                <button class="btn a"
                    @click="$router.push({ name: 'ChapterView', params: { courseId: courseId } })">뒤로가기</button>
            </div>
        </div>
        <lastChapterAlertModalVue v-if="showlastChapterAlertModalVue" @close="closeModal"></lastChapterAlertModalVue>
    </div>
</template>
<script>
import { getChapterDetailData, putFinishChapter } from '@/api';
import lastChapterAlertModalVue from '@/components/lastChapterAlertModal.vue';
export default {
    name: 'ChapterFrame',
    components: {
        lastChapterAlertModalVue
    },
    data: () => ({
        myIframe: null,
        baseUrl: "https://youtube.com/embed/",
        chapterId: "",
        chapterName: "",
        chapterUrl: "",
        thumbnailUrl: "",
        description: "",
        finish: true,
        nextChapterId: "",
        courseId: "",
        showlastChapterAlertModalVue: false,

    }),
    mounted() {
        this.getChapterDetail(this.$route.params.chapterId);
    },
    methods: {
        openModal() {
            this.showlastChapterAlertModalVue = true
        },
        closeModal() {
            this.showlastChapterAlertModalVue = false
            this.redirectWhenModalClosed();
        },
        redirectWhenModalClosed() {
            this.$router.push('/roadmap');
        },
        async finishChapter() {
            const config = {
                headers: {
                    jwt: this.$store.state.jwt
                },
            };
            await putFinishChapter(config, this.chapterId)
                .then((response) => {
                    if (response.data.success) {
                        this.nextChapterId = response.data.nextChapterId;
                        if (this.nextChapterId == "-1") {
                            console.log("마지막강의입니다!")
                            this.openModal();
                        } else {
                            this.getChapterDetail(this.nextChapterId);
                        }

                    }
                })
                .catch(function (error) {
                    console.log(error);
                });

        },
        refreshAll() {
            this.$router.go(0);
        }
        ,
        onLoad(frame) {
            this.myIframe = frame.contentWindow
        },
        async getChapterDetail(chapterId) {
            this.chapterId = chapterId;

            const config = {
                headers: {
                    jwt: this.$store.state.jwt
                }
            };
            await getChapterDetailData(config, this.chapterId)
                .then((response) => {
                    if (response.data.success) {
                        this.chapterName = response.data.chapterDetail.chapterName;
                        this.chapterId = response.data.chapterDetail.chapterId;
                        this.chapterUrl = this.baseUrl + response.data.chapterDetail.chapterUrl.split('?v=')[1];
                        console.log("this.chapterurl" + this.chapterUrl);
                        this.thumbnailUrl = response.data.chapterDetail.thumbnailUrl;
                        this.description = response.data.chapterDetail.description;
                        this.finish = response.data.chapterDetail.finish;
                        this.courseId = response.data.chapterDetail.courseId;


                    }
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

    }
}
</script>

<style>
.container.chapterFrame {
    background-color: wheat;
    margin: auto;
    width: 70vw;
    height: 80% !important;
    border-radius: 40px 40px;


}

.container.wrapper {
    background-color: wheat;
    width: 70vw;
    height: 80% !important;
    border-radius: 40px 40px;


}
</style>