import React, { useState, useEffect } from 'react';
import { ChevronDown, Users } from 'lucide-react';
import { useLazyQuery, useQuery } from '@apollo/client';
import { useApolloClients } from '@/graphql/Clients/ApolloClientContext';
import { GET_ALL_EPICS_BY_PROJECT_ID, GET_TASKS_BY_STATUS_AND_EPIC } from '@/graphql/Queries/task-service';
import IssueCard from './IssueCard';
import { Link } from 'react-router-dom';
import { FIND_PROJECT_BY_ID } from '@/graphql/Queries/project-service';
import { getUserDetails } from '@/services/UserService';
import { useAuth } from '@/context/AuthContext';

function Board({ projectId }) {
  
  const{accessToken} = useAuth();
  const { taskClient, projectClient } = useApolloClients();
  const [showEpics, setShowEpics] = useState(false);
  const [epicId, setEpicId] = useState('');
  const [toDoTasks, setToDoTasks] = useState([]);
  const [inProgressTasks, setInProgressTasks] = useState([]);
  const [completeTasks, setCompleteTasks] = useState([]);
  const [members,setMembers] = useState([]);
  
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

  // Fetch epics using useQuery (changed from useLazyQuery)
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

  return (
    <div className="h-screen flex flex-col">
      {/* Fixed Top Section */}
      <div className="h-[25vh] w-full flex flex-col sm:flex-row items-center justify-between p-4 bg-gray-100 text-xs sm:text-sm md:text-base">
        <div className="flex flex-col w-full sm:w-[50%]">
          <div className="flex items-center space-x-2 text-sm text-gray-700">
            <Link to="/projects" className="text-blue-600 hover:text-blue-800 font-semibold transition-colors duration-300">Projects</Link>
            <span className="text-gray-500">/</span>
            <Link to={`/project/${projectId}`} className="text-blue-600 hover:text-blue-800 font-semibold transition-colors duration-300">{project?.getProject?.title}</Link>
          </div>

          <h3 className="text-base sm:text-lg md:text-xl font-bold">CSS Board</h3>
          <div className="flex flex-row items-center justify-between p-2">
            <input placeholder="Search" className="p-2 border rounded h-[30px] w-[50%] sm:w-[30%]" />
            <Users className="w-6 sm:w-8" size={20} />
            <div className="relative w-[50%]">
              <button onClick={toggleEpicsVisibility} className="flex flex-row items-center space-x-1 w-full p-2">
                <span className="text-xs sm:text-sm">Epic</span>
                <ChevronDown size={16} />
              </button>
              {showEpics && (
                <div className="absolute left-0 w-[200px] sm:w-[250px] bg-white p-2 shadow-lg rounded-lg overflow-y-auto mt-1 border" style={{ maxHeight: '200px', zIndex: 10 }}>
                  {epicData?.getAllEpicsByProjectId?.map((epic) => (
                    <div key={epic.id} onClick={() => handleEpicChange(epic.id)} className="border-b py-1 text-xs sm:text-sm text-gray-800 cursor-pointer hover:bg-gray-100">
                      {epic.title}
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Task Columns */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 p-2 sm:p-4">
        {[
          { title: 'To Do', tasks: toDoTasks },
          { title: 'In Progress', tasks: inProgressTasks },
          { title: 'Complete', tasks: completeTasks }
        ].map(({ title, tasks }, index) => (
          <div key={index} className="flex flex-col w-full">
            <h3 className="text-sm sm:text-md md:text-lg font-bold bg-slate-100 text-slate-800 p-2 rounded-lg text-center">
              {title}
            </h3>
            <div className="flex-1 bg-white max-h-[70vh] overflow-y-auto p-2">
              {tasks.map((task) => (
                <IssueCard key={task.id} task={task} />
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default Board;