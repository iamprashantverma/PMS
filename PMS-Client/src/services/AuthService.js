import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api/v1/users/auth'

// Create an axios instance with the base URL
const apiClient = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Login function
export const login = async (formData) => {
  try {
    const {data} = await apiClient.post('/login',formData);
      return data;
    } catch ({response}) {
      throw response?.data?.error;
  }
}

// signup function
export const signup = async(formData)=>{
    try {
        const {data}= await apiClient.post('/signup',formData);
        return data
    } catch ({response}) {
      throw response?.data?.error;
  }
}
