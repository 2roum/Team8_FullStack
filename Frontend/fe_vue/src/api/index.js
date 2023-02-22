import axios from "axios";
import store from "../store";

const axiosService = axios.create({
  baseURL: "https://backend.devroad.site/",
  headers: {
    "Content-Type": "application/json",
    jwt: store.state.jwt,
  },
});
function putFinishChapter(config, param) {
  axiosService.headers = config;
  console.log("configconfigconfig : " + config);
  return axiosService.put("api/chapter/" + param);
}
//AI Review Fin GET /api/exam/ai/{:submissionId}
function getAiReviewFin(config, param) {
  return axiosService.get("api/exam/ai/" + param, config);
}
//GET /api/exam/peer/{:examId} Peer Detail
function getPeerDetail(config, param) {
  return axiosService.get("api/exam/peer/" + param, config);
}
function adminUser(config) {
  return axiosService.get("api/admin/users", config);
}
function signinUser(userData) {
  return axiosService.post("api/user/signin", userData);
}
function signupUser(userData) {
  return axiosService.post("api/user/signup", userData);
}
function userData(config) {
  return axiosService.get("api/user", config);
}
function getRoadmap(config) {
  return axiosService.get("api/roadmap", config);
}
function getSubjectDetail(param, config) {
  return axiosService.get("api/subject" + "/" + param, config);
}
function getCourseData(param, config) {
  return axiosService.get("api/course" + "/" + param, config);
}
function getAllSubjectData(config) {
  return axiosService.get("api/subject", config);
}
function postAiCodeReviewModal(config, params) {
  return axiosService.post("api/exam/ai/" + params, config);
}
function postRoadmapToUserByEmail(config, data) {
  return axiosService.post("api/roadmap", data, config);
}
function getNoRoadmapUserData(config) {
  return axiosService.get("api/user/noRoadmap", config);
}
function getExamDetailData(config, param) {
  return axiosService.get("api/exam/" + param, config);
}
function postAssignMentData(config, param) {
  return axiosService.post("api/exam/assignment/", param, config);
}
function sendExamResult(config, param) {
  return axiosService.put("api/exam/result", param, config);
}
function getChapterDetailData(config, param) {
  return axiosService.get("api/chapter/" + param, config);
}
function deleteRoadmapData(config, param) {
  return axiosService.delete("api/roadmap/" + param, config);
}
export {
  adminUser,
  signinUser,
  signupUser,
  userData,
  getRoadmap,
  getSubjectDetail,
  getCourseData,
  getAllSubjectData,
  postRoadmapToUserByEmail,
  getNoRoadmapUserData,
  getExamDetailData,
  postAssignMentData,
  sendExamResult,
  getChapterDetailData,
  putFinishChapter,
  getPeerDetail,
  postAiCodeReviewModal,
  getAiReviewFin,
  deleteRoadmapData,
};
