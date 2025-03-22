import { useEffect, useState } from 'react'
import {jwtDecode} from 'jwt-decode';
import { login as loginService} from '@/services/AuthService';
import { createContext } from 'react';

const AuthContext = createContext();

function AuthProvider({children}) {
  
  const [user,setUser] = useState(null);
  const [accessToken, setAccessToken] = useState('');
  const [loading, setLoading] = useState(true);

  const decodeAndSetUser = (token)=>{
    try {
      const decoded = jwtDecode(token);
      const currentTime = Math.floor(Date.now() / 1000);

      //  if token expired the logout
      if (decoded.exp && decoded.exp < currentTime) {
        return;
      }

      //  seting up user Context
      setUser({
        email: decoded.email,
        role: decoded.role,
        name: decoded.name,
        userId: decoded.sub,
        exp: decoded.exp,
        sub: decoded.sub,
      });
    } catch (error){
        console.log(error);
        setUser(null);
    }
  }

  // Initialize authentication on app load
   useEffect(()=>{
      const initializeAuth = ()=>{
        const storedAccessToken = localStorage.getItem('accessToken');
        if (storedAccessToken) {
          decodeAndSetUser(storedAccessToken);
        }
        setLoading(false);
      };
      initializeAuth();
   },[]);
 
  // Login function
  const login = async (credentials) => {
    try {
      const { data } = await loginService(credentials);
      const { accessToken, refreshToken } = data;

      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);

      setAccessToken(accessToken);
      decodeAndSetUser(accessToken);
      return data;
    } catch (error) {
      console.error('Login failed:', error);
      throw error;
    }
  };

  // Logout function
   const logout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    setUser(null);
  };

  // Context value
  const value = {
    user,
    accessToken,
    login,
    logout
  };

  if (loading) {
    return <div>Loading</div>
  };

  return (
    <AuthContext.Provider value ={value} >
      {children}
    </AuthContext.Provider>
  )
}

export default AuthProvider
export {AuthContext}
