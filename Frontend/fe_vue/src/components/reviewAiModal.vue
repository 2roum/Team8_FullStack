<template>
    <transition name="modal" appear>
        <div class="modal fade show" style="display: block;">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" style="color : #B01E68">AI와 함께하는 리뷰입니다</h5>
                    </div>
                    <div class="modal-body">
                        <div v-if="isPending" style="height:inherit">
                            <div class="container reviewPendingContainer">
                                <div class="clock"></div>
                                <div class="reviewMessage" style="text-align:center;margin-top: 15%; ">리뷰를 작성 중입니다..</div>
                            </div>

                        </div>
                        <div v-else>
                            <p>리뷰가 작성되었습니다! GitHub Issue를 확인해 보세요!</p>
                            <div @click="gotoCodeUrl(issueUrl)" class="text-success mb-0 mt-2"
                                style="text-decoration:underline">
                                이슈 보러가기
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <div>
                            <button class="btn a mr-1" style="margin:10px"
                                @click="$emit('card-selected', 'gobackToSelectFromAi')">뒤로가기</button>
                            <button type="button" class="btn a" @click="$emit('close')">Close</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </transition>
</template>
<script>
import { postAiCodeReviewModal, getAiReviewFin } from '@/api'
export default {
    name: "reviewAiModal",
    data() {
        return {
            isPending: true,
            pendingDot: '.',
            issueUrl: "",
        }
    },
    mounted() {
        this.postAiCodeReview();
    },
    methods: {
        gotoCodeUrl(url) {
            window.open(url, "_blank")
        },
        async postAiCodeReview() {
            // const params = {
            //     "submissionId": 149
            // }
            const config = {
                headers: {
                    jwt: this.$store.state.jwt
                }
            };
            await postAiCodeReviewModal(config, 136)
                .then((response) => {
                    if (response.data.success) {
                        const intervalId = setInterval(() => {
                            if (this.pendingDot.length >= 4) {
                                this.pendingDot = ".";
                            } else {
                                this.pendingDot += ".";
                            }
                            getAiReviewFin(config, 136)
                                .then((response) => {
                                    if (response.data.success === true) {
                                        this.issueUrl = response.data.issueUrl;
                                        clearInterval(intervalId);
                                        this.isPending = false;
                                        return
                                    }
                                })
                                .catch((error) => {
                                    console.log(error);
                                });
                        }, 1500);
                        console.log("이슈 깃헙 성공 ");
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

<style>
.modal-mask {
    position: fixed;
    z-index: 9998;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: table;
    transition: opacity 0.3s ease;

}

.modal-wrapper {
    display: table-cell;
    vertical-align: middle;

}

.modal-container {
    width: 800px;
    height: 400px;
    margin: 0px auto;
    padding: 20px 30px;
    background-color: #fff;
    border-radius: 2px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
    transition: all 0.3s ease;

}

.modal-header h3 {
    margin-top: 0;
    color: #43b983;

}

.modal-body {
    height: 280px;

}

.modal-enter {
    opacity: 0;

}

.modal-leave-active {
    opacity: 0;
}

.modal-enter .modal-container,
.modal-leave-active .modal-container {
    -webkit-transform: scale(1.1);
    transform: scale(1.1);
}

.modal-body,
.modal {
    color: #666 !important;
}

.clock {
    border-radius: 60px;
    border: 3px solid #B01E68;
    height: 80px;
    width: 80px;
    position: relative;

    top: 38%;
    top: -webkit-calc(50% - 43px);
    top: calc(50% - 43px);
    left: 35%;
    left: -webkit-calc(50% - 43px);
    left: calc(50% - 43px);
}

.clock:after {
    content: "";
    position: absolute;
    background-color: #B01E68 !important;
    top: 2px;
    left: 48%;
    height: 38px;
    width: 4px;
    border-radius: 5px;
    -webkit-transform-origin: 50% 97%;
    transform-origin: 50% 97%;
    -webkit-animation: grdAiguille 2s linear infinite;
    animation: grdAiguille 2s linear infinite;
}

@-webkit-keyframes grdAiguille {
    0% {
        -webkit-transform: rotate(0deg);
    }

    100% {
        -webkit-transform: rotate(360deg);
    }
}

@keyframes grdAiguille {
    0% {
        transform: rotate(0deg);
    }

    100% {
        transform: rotate(360deg);
    }
}

.clock:before {
    content: "";
    position: absolute;
    background-color: #B01E68;
    top: 6px;
    left: 48%;
    height: 35px;
    width: 4px;
    border-radius: 5px;
    -webkit-transform-origin: 50% 94%;
    transform-origin: 50% 94%;
    -webkit-animation: ptAiguille 12s linear infinite;
    animation: ptAiguille 12s linear infinite;
}

@-webkit-keyframes ptAiguille {
    0% {
        -webkit-transform: rotate(0deg);
    }

    100% {
        -webkit-transform: rotate(360deg);
    }
}

@keyframes ptAiguille {
    0% {
        transform: rotate(0deg);
    }

    100% {
        transform: rotate(360deg);
    }
}

@import url(https://fonts.googleapis.com/css?family=Lato:300);


.box {
    display: inline-block;
    height: 200px;
    width: 33.3%;
    position: relative;
    /*margin:0 -4px -5px -2px;*/
    transition: all .2s ease;
}

.container.reviewPendingContainer {
    height: 70%;
}
</style>