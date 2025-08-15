import React, { useState, useEffect } from 'react';
import { Outlet, Link, useParams } from 'react-router-dom';
import { useQuery } from '@apollo/client';
import { useApolloClients } from '@/graphql/Clients/ApolloClientContext';
import { FIND_PROJECT_BY_ID } from '@/graphql/Queries/project-service';
import { useAuth } from '@/context/AuthContext';

function ProjectSettings() {
  const {user} = useAuth();
  const { projectId } = useParams();
  const { projectClient } = useApolloClients();
  const [project, setProject] = useState(null);

  const {
    data,
    loading,
    error,
    refetch
  } = useQuery(FIND_PROJECT_BY_ID, {
    client: projectClient,
    variables: { projectId },
    skip: !projectId,
    fetchPolicy:"network-only",
  });

  useEffect(() => {
    if (data?.getProject) {
      setProject(data.getProject);
    }
  }, [data]);

  if (loading) {
    return (
      <div className="flex items-center justify-center h-screen text-gray-600">
        Loading project settings...
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-screen text-red-600">
        Error loading project: {error.message}
      </div>
    );
  }


  return (
    <div className="flex flex-col sm:flex-row h-[90vh] bg-gray-50 overflow-hidden">
      {/* Sidebar */}
      <div className="relative w-full sm:w-1/4 bg-white shadow-sm p-4 sm:overflow-y-auto">
        {/* Vertical divider line */}
        <div className="hidden sm:block absolute top-10 right-0 h-[calc(100%-2.5rem)] w-px bg-gray-200" />

        <h2 className="text-xl font-semibold text-gray-700 sticky top-0 bg-white pb-2 z-10">
          {project?.title || 'Project'}
        </h2>

        <nav className="flex flex-row sm:flex-col flex-wrap gap-2 sm:space-y-2 pt-4">
          { project && user.userId === project.projectCreator && <Link
            to={`/projects/settings/${projectId}`}
            className="text-sm sm:text-base px-3 py-2 rounded-md hover:bg-blue-100"
          >
            Details
          </Link>}
         { project && user.userId === project.projectCreator &&  <Link
            to={`notification`}
            className="text-sm sm:text-base px-3 py-2 rounded-md hover:bg-blue-100"
          >
            Notifications
          </Link>}
          <Link
            to={`teams`}
            className="text-sm sm:text-base px-3 py-2 rounded-md hover:bg-blue-100"
          >
            Teams
          </Link>
        </nav>
      </div>

      {/* Content Area */}
      <div className="w-full sm:w-3/4 h-full overflow-y-auto p-4">
        <Outlet context={{ refetchProject: refetch }} />
      </div>
    </div>
  );
}

export default ProjectSettings;
