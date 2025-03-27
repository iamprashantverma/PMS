import React, { useState } from 'react';
import { useQuery, useMutation } from '@apollo/client';
import { useNavigate } from 'react-router-dom';
import { MoreVertical } from 'lucide-react';
import { useAuth } from '@/context/AuthContext';
import { FIND_ALL_PROJECT_BY_USER } from '@/graphql/Queries/project-service';
import { DELETE_PROJECT } from '@/graphql/Mutation/project-service';
import { toast } from 'react-toastify';

function AllProject() {
  const { user } = useAuth();
  const userId = user?.id;
  const navigate = useNavigate();

  const [page, setPage] = useState(0);
  const [openActionMenu, setOpenActionMenu] = useState(null);

  const { data, loading, error, refetch } = useQuery(FIND_ALL_PROJECT_BY_USER, {
    variables: { userId, pageNo: page },
    skip: !userId,
  });

  const [deleteProject] = useMutation(DELETE_PROJECT, {
    onCompleted: () => refetch(),
    onError: (err) => {
      alert('Error deleting project: ' + err.message);
    },
  });

  const handleDelete = async (projectId) => {
    const confirm = window.confirm('Are you sure you want to delete this project?');
    if (confirm) {
      await deleteProject({ variables: { projectId } });
    }
    toast.success("Project Delete Successfully");
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

  return (
    <div className="p-4 max-w-7xl mx-auto">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-xl sm:text-2xl md:text-3xl font-bold text-gray-700">Projects</h1>
        <button className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-xl text-sm sm:text-base transition">
          + Create Project
        </button>
      </div>

      <div className="overflow-x-auto rounded-xl border border-gray-200 bg-white shadow">
        <table className="min-w-full text-left text-sm sm:text-base">
          <thead className="bg-blue-50 text-blue-800 font-semibold">
            <tr>
              <th className="p-3">Project ID</th>
              <th className="p-3">Title</th>
              <th className="p-3">Description</th>
              <th className="p-3">Status</th>
              <th className="p-3">Priority</th>
              <th className="p-3">Project Creator</th>
              <th className="p-3 text-center">Actions</th>
            </tr>
          </thead>
          <tbody className="text-gray-700">
            {projects.map((project) => (
              <tr key={project.projectId} className="border-t hover:bg-gray-50 relative">
                <td className="p-3">{project.projectId}</td>
                <td className="p-3">{project.title}</td>
                <td className="p-3">{project.description}</td>
                <td className="p-3">{project.status}</td>
                <td className="p-3">{project.priority}</td>
                <td className="p-3">{project.projectCreator}</td>
                <td className="p-3 text-center relative">
                  <button onClick={() => toggleActionMenu(project.projectId)}>
                    <MoreVertical size={20} className="text-gray-600 hover:text-gray-900" />
                  </button>
                  {openActionMenu === project.projectId && (
                    <div className="absolute right-5 top-10 bg-white border rounded shadow-md z-10 w-36 text-sm">
                      <button
                        onClick={() => projectHandler(project.projectId)}
                        className="block w-full px-4 py-1 hover:bg-gray-100 text-left"
                      >
                        Project Settings
                      </button>
                      <button
                        onClick={() => handleDelete(project.projectId)}
                        className="block w-full px-4 py-1 hover:bg-gray-100 text-left text-red-500"
                      >
                        Delete
                      </button>
                    </div>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      <div className="flex justify-between mt-6">
        <button
          onClick={handlePrev}
          disabled={page === 0}
          className={`px-4 py-2 rounded-xl text-sm sm:text-base transition ${
            page === 0
              ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
              : 'bg-blue-600 hover:bg-blue-700 text-white'
          }`}
        >
          ← Prev
        </button>
        <span className="text-sm sm:text-base text-gray-600 self-center">Page: {page + 1}</span>
        <button
          onClick={handleNext}
          disabled={!loading && projects.length !== 5}
          className={`px-4 py-2 rounded-xl text-sm sm:text-base transition ${
            !loading && projects.length !== 5
              ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
              : 'bg-blue-600 hover:bg-blue-700 text-white'
          }`}
        >
          Next →
        </button>
      </div>
    </div>
  );
}

export default AllProject;
