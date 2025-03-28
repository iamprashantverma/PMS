import React from 'react';
import Notification from '@/components/Project/Notification';
import { Routes, Route } from 'react-router-dom';
import PrivateRoutes from './PrivateRoutes';
import ProjectSettings from '@/pages/ProjectSettings';
import ProfileSettings from '@/pages/ProfileSettings';
import AllProject from '@/components/Project/AllProject';
import DetailSettings from '@/components/Project/DetailSettings';
import TeamsSettings from '@/components/Project/TeamsSettings';
import CreateProjectForm from '@/components/Project/CreateProjectForn';
import Home from '@/components/Common/Home';

function UserRoutes() {
  return (
    <Routes>
      {/* Wrap all user routes with PrivateRoutes */}
      <Route element={<PrivateRoutes />}>
        <Route path="/profile" element={<ProfileSettings />} />
        <Route path='/'element={<Home/>} />
        <Route path="/projects" element={<AllProject />} />
        <Route path='/create'  element={<CreateProjectForm/>} />

        {/* Nested project settings route */}
        <Route path="/projects/settings/:projectId"  element={<ProjectSettings />}>
          <Route index element={<DetailSettings />} />
          <Route path='teams' element= {<TeamsSettings/>} />
          <Route path='notification' element={<Notification/>} />
      </Route>

      </Route>
    </Routes>
  );
}

export default UserRoutes;
