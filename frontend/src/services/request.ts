import axios from "axios";

const request = axios.create({
    baseURL: import.meta.env.VITE_API_BASE ?? "http://localhost:8080/api",
    headers: {
        "Content-Type": "application/json",
    },
    withCredentials: false,
});

request.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

request.interceptors.response.use(
    (response) => response,
    (error) => {
        const message =
            error.response?.data?.message ?? "请求失败，请稍后重试";
        return Promise.reject(new Error(message));
    }
);

export default request;
