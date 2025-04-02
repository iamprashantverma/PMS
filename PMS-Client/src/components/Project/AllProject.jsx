import React, { useState } from 'react';
import { useQuery, useMutation } from '@apollo/client';
import { useNavigate } from 'react-router-dom';
import { MoreVertical, Plus } from 'lucide-react';
import { useAuth } from '@/context/AuthContext';
import { Link } from 'react-router-dom';
import { FIND_ALL_PROJECT_BY_USER } from '@/graphql/Queries/project-service';
import { DELETE_PROJECT } from '@/graphql/Mutation/project-service';
import { toast } from 'react-toastify';
import { useApolloClients } from '@/graphql/Clients/ApolloClientContext';

function AllProject() {
  const { user } = useAuth();
  const userId = user?.id;
  const navigate = useNavigate();
  const { projectClient } = useApolloClients(); 
  const [page, setPage] = useState(0);
  const [openActionMenu, setOpenActionMenu] = useState(null);

  const { data, loading, error, refetch } = useQuery(FIND_ALL_PROJECT_BY_USER, {
    client: projectClient,
    variables: { pageNo: page },
    skip: !userId,
  });

  const [deleteProject] = useMutation(DELETE_PROJECT, {
    client: projectClient,
    onCompleted: () => {
      toast.success('Project deleted successfully');
      refetch();
    },
    onError: (err) => {
      toast.error('Error deleting project: ' + err.message);
    },
  });

  const handleDelete = async (projectId) => {
    const confirm = window.confirm('Are you sure you want to delete this project?');
    if (confirm) {
      await deleteProject({ variables: { projectId } });
    }
  };

  const projectHandler = (projectId) => {
    navigate(`/projects/settings/${projectId}`);
  };

  const toggleActionMenu = (id) => {
    setOpenActionMenu(openActionMenu === id ? null : id);
  };

  const projects = data?.findAllProject || [];

  const handlePrev = () => {
    if (page > 0) setPage(page - 1);
  };

  const handleNext = () => {
    setPage(page + 1);
  };

  // Status and Priority color mapping
  const statusColors = {
    ACTIVE: 'bg-green-100 text-green-800',
    INACTIVE: 'bg-gray-100 text-gray-800',
    COMPLETED: 'bg-blue-100 text-blue-800',
    ON_HOLD: 'bg-yellow-100 text-yellow-800'
  };

  const priorityColors = {
    HIGH: 'bg-red-100 text-red-800',
    MEDIUM: 'bg-amber-100 text-amber-800',
    LOW: 'bg-green-100 text-green-800'
  };

  return (
    <div className="p-4 max-w-7xl mx-auto font-sans">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-2xl sm:text-[32px] font-bold text-gray-600">Projects</h1>
        <button 
          onClick={() => navigate('/create')}
          className="flex items-center gap-1 bg-blue-600 hover:bg-blue-700 text-white px-1 py-2  rounded-lg text-sm sm:text-base transition-all shadow-md hover:shadow-lg"
        >
          <Plus size={19} />
          Create Project
        </button>
      </div>

      <div className="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Project ID</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Title</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Description</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Priority</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Creator</th>
              <th className="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {projects.map((project) => (
              <tr 
                key={project.projectId} 
                className="hover:bg-gray-50 transition-colors cursor-pointer"
                onClick={() => navigate(`/project/${project.projectId}/board`)}
              >
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                  {project.projectId}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                  {project.title}
                </td>
                <td className="px-6 py-4 text-sm text-gray-500 max-w-xs truncate">
                  {project.description || 'No description'}
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`px-2 py-1 text-xs font-semibold rounded-full ${statusColors[project.status] || 'bg-gray-100 text-gray-800'}`}>
                    {project.status}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`px-2 py-1 text-xs font-semibold rounded-full ${priorityColors[project.priority] || 'bg-gray-100 text-gray-800'}`}>
                    {project.priority}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {project.projectCreator}
                </td>
                <td 
                  className="px-6 py-4 whitespace-nowrap text-sm text-center relative"
                  onClick={(e) => e.stopPropagation()}
                >
                  {project.projectCreator === user.id && (
                    <>
                      <button 
                        onClick={() => toggleActionMenu(project.projectId)}
                        className="text-gray-400 hover:text-gray-600 transition-colors"
                      >
                        <MoreVertical size={20} />
                      </button>
                      {openActionMenu === project.projectId && (
                        <div className="absolute right-6 top-10 bg-white border border-gray-200 rounded-md shadow-lg z-10 w-40 overflow-hidden">
                          <button
                            onClick={() => projectHandler(project.projectId)}
                            className="block w-full px-4 py-2 text-sm text-left text-gray-700 hover:bg-gray-100 transition-colors"
                          >
                            Project Settings
                          </button>
                          <button
                            onClick={() => handleDelete(project.projectId)}
                            className="block w-full px-4 py-2 text-sm text-left text-red-600 hover:bg-red-50 transition-colors"
                          >
                            Delete Project
                          </button>
                        </div>
                      )}
                    </>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Empty State */}
      {!loading && projects.length === 0 && (
        <div className="mt-8 text-center py-12 bg-gray-50 rounded-lg">
          <h3 className="text-lg font-medium text-gray-700">No projects found</h3>
          <p className="mt-2 text-gray-500">Create your first project to get started</p>
          <button 
            onClick={() => navigate('/projects/new')}
            className="mt-4 inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none"
          >
            Create Project
          </button>
        </div>
      )}

      {/* Pagination */}
      {projects.length > 0 && (
        <div className="flex items-center justify-between mt-6 px-2">
          <button
            onClick={handlePrev}
            disabled={page === 0}
            className={`inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md ${
              page === 0
                ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                : 'bg-white text-gray-700 hover:bg-gray-50'
            }`}
          >
            Previous
          </button>
          <span className="text-sm text-gray-700">
            Page <span className="font-medium">{page + 1}</span>
          </span>
          <button
            onClick={handleNext}
            disabled={!loading && projects.length !== 5}
            className={`inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md ${
              !loading && projects.length !== 5
                ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                : 'bg-white text-gray-700 hover:bg-gray-50'
            }`}
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}

export default AllProject;