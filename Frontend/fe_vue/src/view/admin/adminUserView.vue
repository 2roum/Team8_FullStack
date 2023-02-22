<template>
    <div class="container adminuserview">
        <table class="table table-hover" style="color : #B01E68;border-color: #B01E68;">
            <thead>
                <tr>
                    <th scope="col">#</th>
                    <th scope="col">이름</th>
                    <th scope="col">Email</th>
                    <th scope="col">로드맵 삭제</th>
                    <!-- <th scope="col">코드 관리</th> -->

                </tr>
            </thead>
            <tbody>
                <tr v-for="(user, index) in allUserData" :key="index">
                    <th scope="row">{{ index + 1 }}</th>
                    <td>{{ user.userName }}</td>
                    <td>{{ user.email }}</td>
                    <td>
                        <!-- <router-link :to="'/roadmapFactory/' + user" style="text-decoration: none; color:black;"> -->
                        <button class="btn a" v-if="!isRoadmapExists(user)" disabled>
                            로드맵 없음
                        </button>
                        <button v-else class="btn a" @click="deleteRoadmap(user.id)">
                            로드맵 삭제
                        </button>
                        <!-- </router-link> -->
                    </td>
                    <td>
                        <!-- <router-link :to="'/roadmapFactory/' + user" style="text-decoration: none; color:black;"> -->
                        <!-- <button class="btn btn-dark">
                            코드 관리
                        </button> -->
                        <!-- </router-link> -->
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script>
import { adminUser, deleteRoadmapData } from '@/api'
export default {
    name: "AdminUserView",
    data() {
        return {
            allUserData: [],
            success: false,
        };
    },
    mounted() {
        this.getAllUserDataForAdmin();
    },
    methods: {
        isRoadmapExists(user) {
            if (user.roadmapId == "0") {
                return false
            } else return true;
        },
        async getAllUserDataForAdmin() {
            const config = {
                headers: {
                    jwt: this.$store.state.jwt
                }
            };

            await adminUser(config)
                .then((response) => {
                    this.success = response.data.success;
                    this.allUserData = response.data.userDetailList;
                })
                .catch(function (error) {
                    console.log(error);
                });

        },
        async deleteRoadmap(accountId) {
            const config = {
                headers: {
                    jwt: this.$store.state.jwt
                }
            };
            await deleteRoadmapData(config, accountId)
                .then((response) => {
                    this.success = response.data.success;
                    this.$router.push('/adminuserview');
                })
                .catch(function (error) {
                    console.log(error);
                });

        }

    }


}
</script>

<style>
.container.adminuserview {
    background-color: wheat;
    margin: auto !important;
    width: 70vw !important;
    height: 80% !important;
    overflow-y: scroll;
    border-radius: 40px 40px;
}
</style>