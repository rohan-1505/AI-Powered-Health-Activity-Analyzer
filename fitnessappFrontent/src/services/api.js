
import axios from "axios";

const API_URL = "http://localhost:8881/api";

const api = axios.create({
  baseURL: API_URL,
});

// Attach authentication token to every request
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// USER APIs
export const registerUser = (userData) =>
  api.post("/users/register", userData);

export const validateUser = (userId) =>
  api.get(`/users/${userId}/validate`);

export const getUserProfile = (userId) =>
  api.get(`/users/${userId}`);

// ACTIVITY APIs
export const getActivities = () =>
  api.get("/activities");

export const addActivity = (activity) =>
  api.post("/activities", activity);

export const getActivityById = (activityId) =>
  api.get(`/activities/${activityId}`);

// AI RECOMMENDATION APIs
export const getUserRecommendations = (userId) =>
  api.get(`/recommendation/user/${userId}`);

export const getActivityDetail = (activityId) =>
  api.get(`/recommendation/activity/${activityId}`);

export default api;
