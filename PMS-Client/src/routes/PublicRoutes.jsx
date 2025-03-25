import { Routes, Route } from 'react-router-dom';
import Login from '@/pages/Login'; 
import Signup from '@/pages/Signup';
import NavBar from '@/components/NavBar';
import ProfileSettings from '@/pages/ProfileSettings';
import ProjectDropDown from '../components/Project/ProjectDropDown';
import AllProject from '@/components/Project/AllProject';
import ProjectSettings from '@/pages/ProjectSettings';

function PublicRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<Login/>} />
      <Route path='/signup' element ={<Signup/>} />
      <Route path='/profile' element={<ProfileSettings/>} />

      <Route path='/notification' element ={<ProjectDropDown/>} />
      <Route path='/allproject' element ={<AllProject/>} />

      <Route path="/project/:projectId" element={<ProjectSettings/>}>
        <Route path="details" element={<DetailSettings />} />
        {/* <Route path="notifications" element={<NotificationSettings />} /> */}
        {/* <Route path="teams" element={<TeamSettings />} /> */}
      </Route>

    </Routes>
  );
}

export default PublicRoutes;
