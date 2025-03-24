import axios from 'axios';

const BASE_URL = 'http://localhost:9010/users';

const apiClient = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const getUserDetails = async (userId, token) => {
  try {
    const { data } = await apiClient.get(`/details?userId=${userId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return data;
  } catch ({ response }) {
    throw response?.data?.error;
  }
};

export const updateUserDetails = async (formData, token) => {
  try {
    const { data } = await apiClient.put("/update", formData, {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'multipart/form-data',
      },
    });
    return data;
  } catch ({ response }) {
    throw response?.data?.error;
  }
};


