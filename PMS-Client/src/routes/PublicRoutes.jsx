import { Routes, Route } from 'react-router-dom';
import Login from '@/pages/Login'; 
import Signup from '@/pages/Signup';
import NavBar from '@/components/NavBar';
import ProfileSettings from '@/pages/ProfileSettings';

function PublicRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<Login/>} />
      <Route path='/signup' element ={<Signup/>} />
      <Route path='/profile' element={<ProfileSettings/>} />
    </Routes>
  );
}

export default PublicRoutes;
