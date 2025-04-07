import React, { useState, useEffect } from 'react';
import { useApolloClients } from '@/graphql/Clients/ApolloClientContext';
import { GET_SUBTASKS_ASSIGNED_TO_USER, GET_TASKS_ASSIGNED_TO_USER } from '@/graphql/Queries/task-service';
import { useAuth } from '@/context/AuthContext';
import { useQuery } from '@apollo/client';
import { Link, useParams } from 'react-router-dom';

function WorkDropDown() {
  const { taskClient } = useApolloClients();
  const { user } = useAuth();
  const userId = user?.id;
  const [isOpen, setIsOpen] = useState(false);
  const { loading, error, data } = useQuery(GET_TASKS_ASSIGNED_TO_USER, {
    variables: { userId},
    client: taskClient,
    fetchPolicy: "network-only",
  });

  const { data: subtasksData } = useQuery(GET_SUBTASKS_ASSIGNED_TO_USER, {
    variables: { userId,  },
    client: taskClient,
    fetchPolicy: "network-only",
  });
  console.log(data)
  const toggleDropdown = () => setIsOpen(!isOpen);

  const getFilteredTasks = () => {
    const tasks = data?.getTasksAssignedToUser|| [];
    const subtasks = subtasksData?.getSubTasksAssignedToUser || [];

    const combinedTasks = [...tasks, ...subtasks]
      .filter(task => task.status === "IN_PROGRESS")
      .sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt))
      .slice(0, 5);

    return combinedTasks;
  };


  useEffect(() => {
    const handleClickOutside = (event) => {
      if (isOpen && !event.target.closest('.work-dropdown')) {
        setIsOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [isOpen]);

  const filteredTasks = getFilteredTasks();

  return (
    <div className="work-dropdown relative">
      <button 
        onClick={toggleDropdown}
        className="flex items-center text-gray-700 hover:text-blue-600 focus:outline-none px-3 py-2"
      >
        <span className="mr-1 font-medium">Your Work</span>
        <svg className="w-4 h-4 fill-current" viewBox="0 0 20 20">
          <path 
            fillRule="evenodd" 
            d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" 
            clipRule="evenodd"
          />
        </svg>
      </button>

      {isOpen && (
        <div className="absolute z-50 mt-1 w-72 md:w-80 lg:w-96 bg-white rounded-md shadow-lg border border-gray-200">
          <div className="py-1">
            <div className="flex items-center justify-between px-4 py-2 border-b border-gray-200">
              <div className="flex space-x-4">
                <button className="text-blue-600 font-medium text-sm md:text-base border-b-2 border-blue-600 pb-1">
                  Assigned to me
                </button>
                <button className="text-gray-500 hover:text-gray-700 text-sm md:text-base">
                  Recent
                </button>
                <button className="text-gray-500 hover:text-gray-700 text-sm md:text-base">
                  Boards
                </button>
              </div>
            </div>

            <div className="px-4 py-2">
              <p className="text-sm text-gray-500 mb-2">in progress</p>

              {loading ? (
                <div className="text-gray-500 text-sm py-2">Loading tasks...</div>
              ) : error ? (
                <div className="text-red-500 text-sm py-2">Error loading tasks</div>
              ) : filteredTasks.length === 0 ? (
                <div className="text-gray-500 text-sm py-2">No tasks in progress</div>
              ) : (
                filteredTasks.map((task) => (
                  <div key={task.id} className="py-2">
                    <div  className="flex items-start">
                      <div className="flex-shrink-0 mt-1">
                        <div className="w-5 h-5 bg-blue-400 rounded-sm flex items-center justify-center">
                          <svg className="w-3 h-3 text-white" fill="currentColor" viewBox="0 0 20 20">
                            <path d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" />
                          </svg>
                        </div>
                      </div>
                      <div className="ml-3">
                      <Link to={`/your-work/Task/${task.id}` } className="text-sm md:text-base font-medium text-gray-800">
                      {task.title}
                    </Link>

                        <p className="text-xs md:text-sm text-gray-500">
                          {task.projectCode || 'TASK'}-{task.id.slice(0, 4)} • {task.projectName || 'Project'}
                        </p>
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>

            <div className="border-t border-gray-200 px-4 py-2">
              <a 
                href="/your-work" 
                className="block text-sm text-gray-700 hover:text-blue-600"
              >
                Go to Your Work page
              </a>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default WorkDropDown;