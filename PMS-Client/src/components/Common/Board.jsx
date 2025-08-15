import React, { useState, useEffect } from 'react';
import { ChevronDown, UserRoundPlus, Users, Search, Plus, Filter, X } from 'lucide-react';
import { useLazyQuery, useQuery } from '@apollo/client';
import { useApolloClients } from '@/graphql/Clients/ApolloClientContext';
import { GET_ALL_EPICS_BY_PROJECT_ID, GET_TASKS_BY_STATUS_AND_EPIC } from '@/graphql/Queries/task-service';
import { getUserDetails } from '@/services/UserService';
import { Link, useParams } from 'react-router-dom';
import { FIND_PROJECT_BY_ID } from '@/graphql/Queries/project-service';
import { useAuth } from '@/context/AuthContext';
import { useNavigate } from 'react-router-dom';
import TaskDetails from '../Task/TaskDetails';

function Board() {
  const { projectId } = useParams();
  const navigate = useNavigate();
  const { taskClient, projectClient } = useApolloClients();
  const [showEpics, setShowEpics] = useState(false);
  const [epicId, setEpicId] = useState('');
  const [toDoTasks, setToDoTasks] = useState([]);
  const [inProgressTasks, setInProgressTasks] = useState([]);
  const [completeTasks, setCompleteTasks] = useState([]);
  const [members, setMembers] = useState([]);
  const [showDetails, setShowDetails] = useState(false);
  const [selectedTask, setSelectedTask] = useState(null);
  
  // Fetch project data
  const { data: project } = useQuery(FIND_PROJECT_BY_ID, {
    client: projectClient,  
    variables: { projectId },
    fetchPolicy: "network-only",
    skip: !projectId,
  });
 
  useEffect(() => {
    if (project?.getProject?.memberIds) {
      setMembers(project.getProject.memberIds);
    }
  }, [project]);

  // Fetch epics using useQuery
  const { data: epicData } = useQuery(GET_ALL_EPICS_BY_PROJECT_ID, {
    client: taskClient,
    variables: { projectId },
    fetchPolicy: "network-only",
    skip: !projectId,
  });
  
  // Lazy queries for tasks
  const [fetchToDoTasks, { data: todoData }] = useLazyQuery(GET_TASKS_BY_STATUS_AND_EPIC, {
    client: taskClient, 
    fetchPolicy: "network-only"
  });
  
  const [fetchInProgressTasks, { data: inProgressData }] = useLazyQuery(GET_TASKS_BY_STATUS_AND_EPIC, {
    client: taskClient, 
    fetchPolicy: "network-only"
  });

  const [fetchCompleteTasks, { data: completeData }] = useLazyQuery(GET_TASKS_BY_STATUS_AND_EPIC, {
    client: taskClient, 
    fetchPolicy: "network-only"
  });

  // Set first epic as default when epics are loaded
  useEffect(() => {
    if (epicData?.getAllEpicsByProjectId?.length > 0) {
      const firstEpic = epicData.getAllEpicsByProjectId[0];
      setEpicId(firstEpic.id);
      fetchToDoTasks({ variables: { epicId: firstEpic.id, status: 'TODO' } });
      fetchInProgressTasks({ variables: { epicId: firstEpic.id, status: 'IN_PROGRESS' } });
      fetchCompleteTasks({ variables: { epicId: firstEpic.id, status: 'COMPLETED' } });
    } else {
      setEpicId(''); 
      setToDoTasks([]);
      setInProgressTasks([]);
      setCompleteTasks([]);
    }
  }, [epicData, projectId]); 
  
  useEffect(() => {
    if (todoData) setToDoTasks(todoData.getTasksByStatusAndEpic);
  }, [todoData]);

  useEffect(() => {
    if (inProgressData) setInProgressTasks(inProgressData.getTasksByStatusAndEpic);
  }, [inProgressData]);

  useEffect(() => {
    if (completeData) setCompleteTasks(completeData.getTasksByStatusAndEpic);
  }, [completeData]);

  const toggleEpicsVisibility = () => {
    setShowEpics((prev) => !prev);
  };

  const handleEpicChange = (id) => {
    setEpicId(id);
    fetchToDoTasks({ variables: { epicId: id, status: 'TODO' } });
    fetchInProgressTasks({ variables: { epicId: id, status: 'IN_PROGRESS' } });
    fetchCompleteTasks({ variables: { epicId: id, status: 'COMPLETED' } });
  };

  // Function to handle task click
  const handleTaskClick = (task) => {
    setSelectedTask(task);
    setShowDetails(true);
  };
  
  // Function to close task details
  const handleCloseDetails = () => {
    setShowDetails(false);
    setSelectedTask(null);
  };

  // Get current epic title
  const currentEpic = epicData?.getAllEpicsByProjectId?.find(epic => epic.id === epicId);

  // Modified TaskCard component with click handler
  const TaskCard = ({ task }) => (
    
    
    <div 
      className="bg-white rounded-md shadow-sm border border-gray-200 p-3 cursor-pointer hover:shadow-md transition-shadow"
      onClick={() => handleTaskClick(task)}
    >
      
      <div className="flex items-center justify-between mb-2">
        <span className="text-xs font-medium text-gray-500">{task.id}</span>
        <div className="flex items-center gap-1">
          {task.priority && (
            <span className={`px-1.5 py-0.5 text-xs rounded ${
              task.priority === 'HIGH' ? 'bg-red-100 text-red-700' : 
              task.priority === 'MEDIUM' ? 'bg-yellow-100 text-yellow-700' : 
              'bg-blue-100 text-blue-700'
            }`}>
              {task.priority}
            </span>
          )}
        </div>
      </div>

      <h4 className="font-medium text-gray-800 mb-2">{task.title}</h4>
      <p className="text-xs text-gray-500 line-clamp-2 mb-3">{task.description}</p>
      <div className="flex items-center justify-between">
        <div className="flex -space-x-2">
          {task.assignees && task.assignees.length > 0 && (
          <span className="px-2 py-1 text-xs font-semibold text-white bg-blue-500 rounded-full">
            {task.assignees.length} {task.assignees.length === 1 ? 'Member' : 'Members'}
          </span>
        )}


        </div>
        {task.dueDate && (
          <span className="text-xs text-gray-500">
            {new Date(task.dueDate).toLocaleDateString()}
          </span>
        )}
      </div>
    </div>
  );

  return (
    <div className="h-full flex flex-col bg-gray-50 relative">
      {/* Task Details Panel (Appears in the center) */}
      {showDetails && (
        <div className="fixed inset-0 mt-[50px] bg-opacity-30 z-40 flex items-center justify-center">
          <div className="bg-white rounded-lg shadow-xl w-4/5 max-w-5xl max-h-[80vh] overflow-auto relative">
            {/* Close button in top-right corner */}
            <button 
              onClick={handleCloseDetails}
              className="absolute top-4 right-4 p-2 rounded-full hover:bg-gray-100 transition-colors"
            >
              <X size={20} className="text-gray-500" />
            </button>
            
            <TaskDetails task={selectedTask} onClose={handleCloseDetails} />
          </div>
        </div>
      )}
      
      {/* Header Section */}
      <div className="bg-white border-b border-gray-200 shadow-sm">
        <div className="p-4 md:p-6">
          {/* Breadcrumb Navigation */}
          <div className="flex items-center space-x-2 text-sm mb-2">
            <Link to="/projects" className="text-blue-600 hover:text-blue-800 font-medium transition-colors">
              Projects
            </Link>
            <span className="text-gray-400">/</span>
            <Link to={`/project/${projectId}/board`} className="text-blue-600 hover:text-blue-800 font-medium transition-colors">
              {project?.getProject?.title || "Loading..."}
            </Link>
            <span className="text-gray-400">/</span>
            <span className="text-gray-600 font-medium">Board</span>
          </div>
          
          {/* Title and Controls */}
          <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
            <h1 className="text-2xl font-bold text-gray-700">
              Board {currentEpic ? `- ${currentEpic.title}` : ''}
            </h1>
          </div>
        </div>
        
        {/* Toolbar */}
        <div className="px-4 py-3 border-t border-gray-100 flex flex-wrap items-center gap-3 bg-gray-50">
          {/* Search */}
          <div className="relative">
            <Search size={16} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
            <input 
              placeholder="Search tasks..." 
              className="pl-9 pr-4 py-1.5 border border-gray-300 rounded-md text-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors w-48 md:w-64"
            />
          </div>
          
          {/* Epic Selector */}
          <div className="relative">
            <button 
              onClick={toggleEpicsVisibility} 
              className={`flex items-center gap-2 px-3 py-1.5 border ${showEpics ? 'border-blue-500 bg-blue-50' : 'border-gray-300 bg-white'} rounded-md text-sm transition-colors`}
            >
              <span className="font-medium text-gray-700">Epic</span>
              <ChevronDown size={14} className={`text-gray-500 transition-transform ${showEpics ? 'rotate-180' : ''}`} />
            </button>
            
            {showEpics && (
              <div className="absolute left-0 top-full mt-1 w-56 bg-white rounded-md shadow-lg border border-gray-200 z-20 overflow-hidden">
                <div className="p-1 max-h-64 overflow-y-auto">
                  {epicData?.getAllEpicsByProjectId?.length > 0 ? (
                    epicData.getAllEpicsByProjectId.map((epic) => (
                      <div 
                        key={epic.id} 
                        onClick={() => {handleEpicChange(epic.id); setShowEpics(false);}} 
                        className={`px-3 py-2 text-sm cursor-pointer hover:bg-gray-100 transition-colors ${epic.id === epicId ? 'bg-blue-50 text-blue-700 font-medium' : 'text-gray-700'}`}
                      >
                        {epic.title}
                      </div>
                    ))
                  ) : (
                    <div className="px-3 py-2 text-sm text-gray-500">No epics found</div>
                  )}
                </div>
              </div>
            )}
          </div>
          
          {/* Filter Button */}
          <button className="flex items-center gap-2 px-3 py-1.5 border border-gray-300 rounded-md text-sm bg-white hover:bg-gray-50 transition-colors">
            <Filter size={14} className="text-gray-500" />
            <span className="font-medium text-gray-700">Filter</span>
          </button>
          
          {/* Team Management */}
          <div className="ml-auto flex items-center gap-3">
            <button className="p-2 rounded-full border border-gray-300 bg-white hover:bg-gray-50 transition-colors"
             onClick={() => navigate(`/projects/settings/${projectId}/teams`)}>
              <Users size={16} className="text-gray-700" />
            </button>
            <button 
              onClick={() => navigate(`/projects/settings/${projectId}/teams`)}
              className="p-2 rounded-full border border-gray-300 bg-white hover:bg-gray-50 transition-colors"
              title="Add team members"
            >
              <UserRoundPlus size={16} className="text-gray-700" />
            </button>
          </div>
        </div>
      </div>

      {/* Task Columns */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 p-6 flex-1">
        {[
          { title: 'To Do', tasks: toDoTasks, color: 'blue' },
          { title: 'In Progress', tasks: inProgressTasks, color: 'amber' },
          { title: 'Complete', tasks: completeTasks, color: 'green' }
        ].map(({ title, tasks, color }, index) => (
          <div key={index} className="flex flex-col bg-gray-100 rounded-lg shadow-sm overflow-hidden">
            {/* Column Header */}
            <div className={`bg-${color}-50 border-b border-${color}-100 px-4 py-3 flex items-center justify-between`}>
              <div className="flex items-center gap-2">
                <div className={`w-3 h-3 rounded-full bg-${color}-500`}></div>
                <h3 className={`font-semibold text-${color}-900`}>{title} <span className="text-gray-500 font-normal">({tasks.length})</span></h3>
              </div>
              <button className="p-1 rounded-full hover:bg-white transition-colors">
                <Plus size={16} className="text-gray-500" />
              </button>
            </div>
            
            {/* Tasks */}
            <div className="flex-1 overflow-y-auto p-3 space-y-3" style={{ maxHeight: 'calc(100vh - 240px)' }}>
              {tasks.length > 0 ? (
                tasks.map((task) => (
                  
                  <TaskCard key={task.id} task={task} />
                ))
              ) : (
                <div className="flex flex-col items-center justify-center h-32 text-center">
                  <p className="text-gray-500 text-sm mb-2">No tasks in this column</p>
                  {/* <button className="text-blue-600 hover:text-blue-800 text-sm font-medium flex items-center gap-1">
                    <Plus size={14} />
                    Add a task
                  </button> */}
                </div>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default Board;