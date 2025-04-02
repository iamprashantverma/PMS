import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api/v1/activity'

// Create an axios instance with the base URL
const apiClient = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});


export const getCalendarEvents = async(projectId,token)=>{
    try {
        const { data } = await apiClient.get(`/calendar/project/${projectId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        return data;
      } catch ({ response }) {
        throw response?.data?.error;
    }
}