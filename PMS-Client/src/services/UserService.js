import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api/v1/users';

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


export const updateCommentMentions = async (userId, value, token) => {

  try {
    const data = await apiClient.patch(`/notifications/comment-mentions?userId=${userId}&value=${value}`,{},
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    return data;
  } catch ({response}) {
    throw response?.data?.error;
  }
};

export const updateTaskUpdates = async (userId, value, token) => {
  try {
    const { data } = await apiClient.patch(
      `/notifications/task-updates?userId=${userId}&value=${value}`,
      {},
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    return data;
  } catch ({ response }) {
    throw response?.data?.error;
  }
};

export const updateSubTaskUpdates = async (userId, value, token) => {
  try {
    const { data } = await apiClient.patch(
      `/notifications/sub-task-updates?userId=${userId}&value=${value}`,
      {},
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    return data;
  } catch ({ response }) {
    throw response?.data?.error;
  }
};

export const updateBugUpdates = async (userId, value, token) => {
  try {
    const { data } = await apiClient.patch(
      `/notifications/bug-updates?userId=${userId}&value=${value}`,
      {},
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    return data;
  } catch ({ response }) {
    throw response?.data?.error;
  }
};

export const updateEmailUpdates = async (userId, value, token) => {
  try {
    const { data } = await apiClient.patch(
      `/notifications/email-updates?userId=${userId}&value=${value}`,
      {},
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    return data;
  } catch ({ response }) {
    throw response?.data?.error;
  }
};

export const sendOtp = async (formData) => {
  try {
    const { data } = await apiClient.patch("/auth/otp",formData);
    return data;
  } catch ({ response }) {
    throw response?.data?.error;
  }
};
export const userLogOut = async () => {
  try {
    const { data } = await apiClient.post("/auth/logout");
    
    return data;
  } catch ({ response }) {
    console.log(response);
    throw response?.data?.error;
  }
};

export const verifyOtp = async (formData) => {
  try {
    const { data } = await apiClient.patch("/auth/verify",formData);
    return data;
  } catch ({ response }) {
    
    throw response?.data?.error;
  }
};


