<template>
    <div class="container adminhome">
        <table class="table table-hover" style="color : #B01E68;
                    border-color: #B01E68;">
            <thead>
                <tr>
                    <th scope="col">#</th>
                    <th scope="col">Email</th>
                    <th scope="col">작성하러 가기!</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(user, index) in noRoadmapUsers" :key="index">
                    <th scope="row">{{ index + 1 }}</th>
                    <td>{{ user }}</td>
                    <td>
                        <router-link :to="{name : 'RoadMapFactory', params : {userEmail : user}}" style="text-decoration: none; color:black;">
                            <button class="btn a">
                                유저 로드맵 작성하기
                            </button>
                        </router-link>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</template>
<script>
import { getNoRoadmapUserData } from '@/api';
export default {
    name: "AdminHome",
    data() {
        return {
            noRoadmapUsers: [],
        }
    },
    mounted() {
        this.getNoRoadMapUserDetail();
    },
    methods: {
        async getNoRoadMapUserDetail() {
            const config = {
                headers: {
                    jwt: this.$store.state.jwt
                }
            };
            await getNoRoadmapUserData(config)
                .then((response) => {
                    this.noRoadmapUsers = response.data.users;
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
    },
}
</script>

<style>
.container.adminhome {
    background-color: wheat;
    margin: auto !important;
    width: 70vw !important;
    height: 80% !important;
    overflow-y: scroll;
    border-radius: 40px 40px;
}

.btn.a {
    background-color: #B01E68 !important;
    color: white !important;
    margin-bottom: 5px;
}
</style>