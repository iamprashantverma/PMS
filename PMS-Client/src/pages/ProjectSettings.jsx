import React, { useState, useEffect } from 'react';
import { Outlet, Link, useParams } from 'react-router-dom';
import { useQuery } from '@apollo/client';
import { FIND_PROJECT_BY_ID } from '@/graphql/Queries/project-service';

function ProjectSettings() {
  const [project, setProject] = useState({});
  const { projectId } = useParams();

  const { data, loading, error } = useQuery(FIND_PROJECT_BY_ID, {
    variables: { projectId },
    skip: !projectId, 
  });
  
  useEffect(() => {
    if (data?.getProject) {
      setProject(data.getProject);
    }
  }, [data])

  return (
    <div className="flex flex-col sm:flex-row h-[90vh] bg-gray-50 overflow-hidden">
      
      {/* Sidebar */}
      <div className="relative w-full sm:w-1/4 sm:h-full bg-white shadow-sm p-4 overflow-y-visible sm:overflow-y-auto sm:block">

        {/* Fake vertical line starting lower */}
        <div className="hidden sm:block absolute top-10 right-0 h-[calc(100%-2.5rem)] w-px bg-gray-200" />

        <h2 className="text-xl font-semibold text-gray-700 sticky top-0 bg-white pb-2 sm:static">
          {project.title}
        </h2>

        <nav className="flex flex-row sm:flex-col flex-wrap gap-2 sm:space-y-2 pt-4">
          <Link
         to={`/projects/settings/${projectId}`}
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
            to={`teams`}
            className="text-sm sm:text-base px-3 py-2 rounded-md hover:bg-blue-100"
          >
            Teams
          </Link>
        </nav>
      </div>

      {/* Content */}
      <div className="w-full sm:w-3/4 h-full overflow-y-auto p-4">
        <Outlet />
      </div>
    </div>
  );
}

export default ProjectSettings;
