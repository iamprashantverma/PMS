import React from 'react';
import { useQuery } from '@apollo/client';
import { FIND_ALL_PROJECT_BY_USER } from '../../graphql/Queries/project-service';
import { useAuth } from '@/context/AuthContext';
import { useAppContext } from '@/context/AppContext';
import { Link } from 'react-router-dom';

function ProjectDropDown() {
  const { user } = useAuth();
  const userId = user?.id;
  const { dropDown, open } = useAppContext();
  const page = 0;

  const { data, loading, error } = useQuery(FIND_ALL_PROJECT_BY_USER, {
    variables: { userId,pageNo:page },
    skip: !userId, 
  });

  if (dropDown !== 'project' || !open) return null;
  console.log(user);
  const containerClasses = `
    absolute z-50 bg-white border border-gray-200 rounded-md shadow-md 
    top-[110%] left-[10%] 
    sm:left-[20%] sm:top-[115%] 
    md:left-[25%] 
    lg:left-[10%]
    w-[70vw] sm:w-[50vw] md:w-[40vw] lg:w-[16rem]
  `;

  if (loading)
    return (
      <div className={containerClasses + ' p-2'}>
        <p className="text-xs sm:text-sm">Loading...</p>
      </div>
    );

  if (error)
    return (
      <div className={containerClasses + ' p-2'}>
        <p className="text-xs sm:text-sm text-red-500">{error.message}</p>
      </div>
    );

  const projects = data?.findAllProject || [];

  return (
    <div className={containerClasses}>
      {/* Scrollable project list */}
      <div className="max-h-[35vh] overflow-y-auto p-2 space-y-1">
        {projects.map((project) => (
          <div
            key={project.projectId}
            className="flex items-center gap-2 p-1.5 hover:bg-gray-100 rounded transition"
          >
            <img
              src={project.image || '/placeholder.png'}
              alt={project.title}
              className="w-8 h-8 sm:w-9 sm:h-9 md:w-10 md:h-10 object-cover rounded-full"
            />
            <span className="text-sm sm:text-[18px] font-medium text-gray-800 truncate hover:text-blue-600 transition-colors duration-150">
              {project.title}
            </span>

          </div>
        ))}
      </div>

      {/* Fixed bottom action buttons */}
      <div className="border-t border-gray-200 px-3 py-2 flex flex-col gap-2 bg-white">
        <Link to="/allproject" className="text-xs sm:text-sm text-blue-600 hover:underline text-left">
          View All Projects
        </Link>
        <button className="text-xs sm:text-sm text-blue-600 hover:underline text-left">
          Create Project
        </button>
      </div>
    </div>
  );
}

export default ProjectDropDown;
