import React from 'react';
import { useQuery } from '@apollo/client';
import { FIND_ALL_PROJECT_BY_USER } from '../../graphql/Queries/project-service';
import { useAuth } from '@/context/AuthContext';
import { useAppContext } from '@/context/AppContext';
import { useApolloClients } from '@/graphql/Clients/ApolloClientContext';
import { Link } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';

function ProjectDropDown() {
  const navigate = useNavigate();
  const { projectClient, taskClient } = useApolloClients();
  const { user } = useAuth();
  const userId = user?.id;
  const { dropDown, open, setOpen } = useAppContext();
  const page = 0;
  const { data, loading, error } = useQuery(FIND_ALL_PROJECT_BY_USER, {
    client: projectClient,
    variables: { 
      userId, 
      pageNo: page ?? 0 
    },
    skip: !userId || page === undefined, 
    fetchPolicy: "network-only"
  });

  if (dropDown !== 'project' || !open) return null;
  
  const containerClasses = `
  absolute z-50 bg-white border border-gray-200 rounded-lg shadow-lg 
  top-[110%] left-[10%] 
  sm:left-[20%] sm:top-[90%] 
  md:left-[25%] 
  lg:left-[21%] 
  w-[75vw] sm:w-[55vw] md:w-[45vw] lg:w-[18rem]
`;


  if (loading)
    return (
      <div className={containerClasses + ' p-3'}>
        <p className="text-sm font-medium text-gray-600">Loading...</p>
      </div>
    );

  if (error)
    return (
      <div className={containerClasses + ' p-3'}>
        <p className="text-sm font-medium text-red-600">{error.message}</p>
      </div>
    );

  const projects = data?.findAllProject || [];

  return (
    <div className={containerClasses}> 
      {/* Scrollable project list */}
      <div className="max-h-[35vh]  overflow-y-auto py-2 space-y-1">
        {projects.length > 0 ? (
          projects.map((project) => (
            <div 
              onClick={() => {
                setOpen(false);
                navigate(`/project/${project.projectId}/board`);
              }}
              key={project.projectId}
              className="flex items-center gap-3 px-3 py-2 hover:bg-blue-50 rounded transition cursor-pointer"
            >
              <img
                src={project.image || '/placeholder.png'}
                alt={project.title}
                className="w-8 h-8 sm:w-9 sm:h-9 object-cover rounded-full border border-gray-200"
              />
              <span className="text-sm font-medium text-gray-800 truncate group-hover:text-blue-700">
                {project.title}
              </span>
            </div>
          ))
        ) : (
          <p className="text-sm text-gray-500 px-3 py-2">No projects found</p>
        )}
      </div>

      {/* Fixed bottom action buttons */}
      <div className="border-t border-gray-200 px-4 py-3 flex justify-between bg-gray-50 rounded-b-lg">
        <Link
          to="/projects"
          onClick={() => setOpen(false)}
          className="text-sm font-medium text-blue-600 hover:text-blue-800 transition-colors">
          View All
        </Link>
        <Link
          to="/create"
          onClick={() => {
            setOpen(false);
          }}
          className="text-sm font-medium text-blue-600 hover:text-blue-800 transition-colors">
          Create New
        </Link>
      </div>
    </div>
  );
}

export default ProjectDropDown;