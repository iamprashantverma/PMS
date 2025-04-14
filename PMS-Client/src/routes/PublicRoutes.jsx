import { Routes, Route } from 'react-router-dom';
import Login from '@/pages/Login'; 
import Signup from '@/pages/Signup';
import Home from '@/pages/Home';
import ForgetPassword from '@/pages/ForgetPassword';

function PublicRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<Login/>} />
      <Route path='/signup' element ={<Signup/>} />
      <Route path='/' element={<Home/>}/>
      <Route path='forgetPassword' element= {<ForgetPassword/>} />
    </Routes>
  );
}
export default PublicRoutes;
