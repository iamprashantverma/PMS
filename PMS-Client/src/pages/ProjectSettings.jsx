import React, { useState, useEffect } from 'react';
import { Outlet, Link, useParams } from 'react-router-dom';

function ProjectSettings({ projectId }) {
  const [project, setProject] = useState({});
  const { id } = useParams(); 

  useEffect(() => {
    setProject({
      title: 'Sample Project',
    });
  }, [projectId]);

  return (
    <div className="flex flex-col sm:flex-row min-h-screen bg-gray-50">
      {/* Left Sidebar */}
      <div className="w-full sm:w-1/4 bg-white border-r p-4 space-y-4 shadow-sm">
        <h2 className="text-xl font-semibold text-gray-700">{project.title}</h2>

        <nav className="flex flex-col space-y-2">
          <Link
            to={`/project/${projectId}/setting/details`}
            className="text-sm sm:text-base px-3 py-2 rounded-md hover:bg-blue-100"
          >
            Details
          </Link>
          <Link
            to={`/project/${projectId}/setting/notifications`}
            className="text-sm sm:text-base px-3 py-2 rounded-md hover:bg-blue-100"
          >
            Notifications
          </Link>
          <Link
            to={`/project/${projectId}/setting/teams`}
            className="text-sm sm:text-base px-3 py-2 rounded-md hover:bg-blue-100"
          >
            Teams
          </Link>
        </nav>
      </div>

      {/* Right Content */}
      <div className="w-full sm:w-3/4 p-4">
        <Outlet />
      </div>
    </div>
  );
}

export default ProjectSettings;
