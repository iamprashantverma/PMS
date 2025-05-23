import React from 'react';
import Notification from '@/components/Project/Notification';
import { Routes, Route } from 'react-router-dom';
import PrivateRoutes from './PrivateRoutes';
import ProjectSettings from '@/pages/ProjectSettings';
import ProfileSettings from '@/pages/ProfileSettings';
import AllProject from '@/components/Project/AllProject';
import DetailSettings from '@/components/Project/DetailSettings';
import TeamsSettings from '@/components/Project/TeamsSettings';
import CreateProjectForm from '@/components/Project/CreateProjectForm';
import Home from '@/components/Common/Home';
import TaskCalendar from '@/components/Common/TaskCalendar';
import Board from '@/components/Common/Board';
import AllIssues from '@/components/Common/AllIssues';
import TaskDetails from '@/components/Task/TaskDetails';
import Timeline from '@/components/Task/Timeline';
import BugDetails from '@/components/Task/BugDetails';

import AllWorks from '@/components/Work/AllWorks';
import UserSetting from '@/pages/UserSetting';
import Dashboard from '@/pages/Dashboard';

function UserRoutes() {
  return (
    <Routes>
      <Route element={<PrivateRoutes />}>
        <Route path='/dashboard' element={<Dashboard/>}/>
        <Route path="/your-work" element={<AllWorks />}>
        <Route path="Task/:taskId" element={<TaskDetails />} />
      </Route>
        <Route path="/profile" element={<UserSetting/>} />
        <Route path="/project/:projectId" element={<Home />}>
          <Route path="calendar" element={<TaskCalendar />} />
          <Route path="board" element={<Board />} />
          <Route path="issues" element={<AllIssues />} />
          <Route path='timeline' element ={<Timeline/>}/>
          <Route path="task/:taskId" element={<TaskDetails />} />
          <Route path='bug/:bugId' element={<BugDetails/>} />
        </Route>

        <Route path="/projects" element={<AllProject />} />
        <Route path="/create" element={<CreateProjectForm />} />

        <Route path="/projects/settings/:projectId" element={<ProjectSettings />}>
          <Route index element={<DetailSettings />} />
          <Route path="teams" element={<TeamsSettings />} />
          <Route path="notification" element={<Notification />} />
        </Route>
      </Route>
    </Routes>
  );
}

export default UserRoutes;
