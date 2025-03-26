import React from 'react';
import { Routes, Route } from 'react-router-dom';
import PrivateRoutes from './PrivateRoutes';
import ProjectSettings from '@/pages/ProjectSettings';
import ProfileSettings from '@/pages/ProfileSettings';
import AllProject from '@/components/Project/AllProject';
import DetailSettings from '@/components/Project/DetailSettings';
import TeamsSettings from '@/components/Project/TeamsSettings';

function UserRoutes() {
  return (
    <Routes>
      {/* Wrap all user routes with PrivateRoutes */}
      <Route element={<PrivateRoutes />}>
        <Route path="/profile" element={<ProfileSettings />} />

        <Route path="/projects" element={<AllProject />} />

        {/* Nested project settings route */}
        <Route path="/projects/settings/:projectId"  element={<ProjectSettings />}>
          <Route index element={<DetailSettings />} />
          <Route path='teams' element= {<TeamsSettings/>} />
      </Route>

      </Route>
    </Routes>
  );
}

export default UserRoutes;
