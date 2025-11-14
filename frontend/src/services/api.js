import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'https://claimsupdate.onrender.com/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for error handling
api.interceptors.request.use(
  (config) => config,
  (error) => {
    console.error('Request error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const errorMessage = error.response?.data?.message || 
                        error.response?.data || 
                        error.message || 
                        'An unexpected error occurred';
    console.error('API Error:', errorMessage);
    return Promise.reject(new Error(errorMessage));
  }
);

export const claimAPI = {
  submitClaim: async (claimData) => {
    if (!claimData) {
      throw new Error('Claim data is required');
    }
    
    try {
      const response = await api.post('/claims/submit', claimData);
      return response.data;
    } catch (error) {
      // Error is already handled by interceptor
      throw error;
    }
  }
};

export default api;