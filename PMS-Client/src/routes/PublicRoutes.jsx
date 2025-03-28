import { Routes, Route } from 'react-router-dom';
import Login from '@/pages/Login'; 
import Signup from '@/pages/Signup';

import { Home } from 'lucide-react';

function PublicRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<Login/>} />
      <Route path='/signup' element ={<Signup/>} />
      <Route path='/' element={<div></div>}/>
    </Routes>
  );
}
export default PublicRoutes;
