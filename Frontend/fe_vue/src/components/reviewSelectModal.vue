<template>
    <transition name="modal" appear>
        <div class="modal-mask">
            <div class="modal-wrapper">
                <div class="modal-container">
                    <div class="modal-header mt-1">
                        테스트를 통과하셨군요! 이후 과정을 어떻게 진행하시겠어요?
                    </div>
                    <div class="modal-body">
                        <div class="row row-cols-1 row-cols-md-2 g-4 mt-1" style="height:100%">
                            <div class="col">
                                <div class="card reviewcard" @click="$emit('card-selected', card.ai.class)">
                                    <div class="card-header">{{ card.ai.title }}</div>
                                    <div class="card-body">{{ card.ai.description }}</div>
                                </div>
                            </div>
                            <div class="col">
                                <div class="card reviewcard" @click="$emit('card-selected', card.peer.class)"
                                    style="height:80%">
                                    <div class="card-header">{{ card.peer.title }}</div>
                                    <div class="card-body">{{ card.peer.description }}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <footer class="modal-footer">
                        <slot name="footer">
                            <button class="btn a" @click="$emit('close')">Close</button>
                        </slot>
                    </footer>
                </div>

            </div>
        </div>
    </transition>
</template>
<script>
export default {
    name: 'ReviewSelectModal',
    components: {

    },
    data() {
        return {

            card: {
                ai: {
                    id: 1,
                    title: 'AI 와 함께하는 리뷰',
                    description: '최신 AI 와 함께 틀린 부분을 논의해 보아요!',
                    class: 'ai'
                },
                peer: {
                    id: 2,
                    title: 'Peer 리뷰',
                    description: '같은 과목을 들은 학생들과 서로 리뷰해 보아요!',
                    class: 'peer'
                }

            },


        };
    },
    methods: {

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
    background-color: white !important;
    border-radius: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
    transition: all 0.3s ease;

}

.card.reviewcard {
    height: 80%;
    background-color: snow !important;
    ;
}

.modal-header {
    margin-top: 0;
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
</style>