import React, { useState, useRef, useEffect } from 'react';
import { Link, Outlet } from 'react-router-dom';
import { FIND_PROJECT_BY_ID } from '@/graphql/Queries/project-service';
import { useQuery } from '@apollo/client';
import { useParams } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';
import { useApolloClients } from '@/graphql/Clients/ApolloClientContext';
import {
  ChevronRight,
  LayoutPanelTop,
  CalendarDays,
  ClipboardList,
  FileText,
  LayoutDashboard,
  ListOrdered,
  Plus,
  Code2,
  FileCog,
  PlusCircle,
  Settings,
  Archive,
  Menu,
  X,
  ChevronLeft
} from 'lucide-react';
import TaskDetails from '../Task/TaskDetails';
import Timeline from '../Task/Timeline';


function Home() {
  const navigate = useNavigate();
  const {user} = useAuth();
  const { projectId } = useParams();
  const { projectClient } = useApolloClients();
  const [planning, setPlanning] = useState(true);
  const [development, setDevelopment] = useState(true);
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [sidebarWidth, setSidebarWidth] = useState(256); 
  const isResizing = useRef(false);
  const sidebarRef = useRef();  

  // Fetch project data
  const { data, loading, error } = useQuery(FIND_PROJECT_BY_ID, {
    client: projectClient,
    variables: { projectId },
    skip: !projectId, 
  });
  
  const project = data?.getProject || {};
  console.log(user, project);

  const sideBarHandler = (section) => {
    if (section === 'planning') setPlanning(!planning);
    else if (section === 'development') setDevelopment(!development);
  };

  return (
    <div className="flex h-full w-full overflow-hidden">
      {/* Sidebar */}
      <div
        ref={sidebarRef}
        className="bg-white text-gray-900 border-r border-gray-200 flex flex-col transition-all duration-300 overflow-y-auto h-full shadow-sm"
        style={{ width: sidebarOpen ? sidebarWidth : 64 }}
      >
        {/* Project Info and Toggle Button */}
        <div className={`flex items-center justify-between ${sidebarOpen ? 'p-4' : 'p-3'} border-b border-gray-100`}>
          {sidebarOpen ? (
            <>
              {loading ? (
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-gray-200 rounded-md animate-pulse"></div>
                  <div className="w-24 h-5 bg-gray-200 rounded animate-pulse"></div>
                </div>
              ) : error ? (
                <div className="text-red-500 text-sm">Error fetching project</div>
              ) : (
                <div className="flex items-center gap-3">
                  <img
                    src={project.image || 'https://via.placeholder.com/40'}
                    alt="Project Logo"
                    className="w-10 h-10 rounded-md object-cover border border-gray-200 shadow-sm"
                  />
                  <div className="flex flex-col">
                    <span className="font-bold text-gray-800">{project.title || 'Unnamed Project'}</span>
                    <span className="text-xs text-gray-500">ID: {projectId.substring(0, 8)}</span>
                  </div>
                </div>
              )}
              <button
                onClick={() => setSidebarOpen(false)}
                className="p-1.5 rounded-full hover:bg-gray-100 text-gray-500 hover:text-gray-700 transition-colors"
                title="Collapse sidebar"
              >
                <ChevronLeft size={18} />
              </button>
            </>
          ) : (
            <button
              onClick={() => setSidebarOpen(true)}
              className="p-1.5 rounded-full hover:bg-gray-100 text-gray-500 hover:text-gray-700 transition-colors mx-auto"
              title="Expand sidebar"
            >
              <Menu size={20} />
            </button>
          )}
        </div>

        {/* Planning Section */}
        <div className="mt-4 px-3">
          <button
            onClick={() => sideBarHandler('planning')}
            className="flex items-center justify-between w-full px-2 py-1.5 rounded hover:bg-blue-50 text-left font-semibold text-blue-600 hover:text-blue-700 transition-colors"
          >
            <span className="flex items-center gap-2">
              <LayoutPanelTop size={16} />
              {sidebarOpen && 'Planning'}
            </span>
            {sidebarOpen && (
              <ChevronRight className={`transition-transform ${planning ? 'rotate-90' : ''}`} size={16} />
            )}
          </button>
          {planning && sidebarOpen && (
            <div className="ml-6 mt-2 space-y-1.5 text-gray-700">
              <div className="flex items-center gap-2 cursor-pointer hover:text-blue-600 py-1 px-2 rounded hover:bg-gray-50">
                <FileText size={16} /> <span className="text-sm">Summary</span>
              </div>
              <div className="flex items-center gap-2 cursor-pointer hover:text-blue-600 py-1 px-2 rounded hover:bg-gray-50">
                <CalendarDays size={16} />  <Link to='timeline' className="text-sm">TimeLine</Link>
              </div>
              <Link to={`board`} className="flex items-center gap-2 cursor-pointer hover:text-blue-600 py-1 px-2 rounded hover:bg-gray-50">
                <LayoutDashboard size={16} /> <span className="text-sm">Board</span>
              </Link>
              <Link to={`calendar`} className="flex items-center gap-2 cursor-pointer hover:text-blue-600 py-1 px-2 rounded hover:bg-gray-50">
                <CalendarDays size={16} /> <span className="text-sm">Calendar</span>
              </Link>
              <div className="flex items-center gap-2 cursor-pointer hover:text-blue-600 py-1 px-2 rounded hover:bg-gray-50">
                <ListOrdered size={16} /> <Link to='issues' className="text-sm">Issues</Link>
              </div>
              <div className="flex items-center gap-2 cursor-pointer hover:text-blue-600 py-1 px-2 rounded hover:bg-gray-50">
                <ClipboardList size={16} /> <span className="text-sm">Forms</span>
              </div>
            </div>
          )}
        </div>

        {/* Add View - Independent */}
        {sidebarOpen && (
          <div className="px-3 mt-2">
            <div className="ml-6 text-blue-600 cursor-pointer flex items-center gap-2 py-1 px-2 rounded hover:bg-blue-50 text-sm hover:text-blue-700 transition-colors">
              <Plus size={16} /> Add View
            </div>
          </div>
        )}

        {/* Development Section */}
        <div className="mt-4 px-3">
          <button
            onClick={() => sideBarHandler('development')}
            className="flex items-center justify-between w-full px-2 py-1.5 rounded hover:bg-green-50 text-left font-semibold text-green-600 hover:text-green-700 transition-colors"
          >
            <span className="flex items-center gap-2">
              <Code2 size={16} />
              {sidebarOpen && 'Development'}
            </span>
            {sidebarOpen && (
              <ChevronRight className={`transition-transform ${development ? 'rotate-90' : ''}`} size={16} />
            )}
          </button>
          {development && sidebarOpen && (
            <div className="ml-6 mt-2 space-y-1.5 text-gray-700">
              <div className="flex items-center gap-2 cursor-pointer hover:text-green-600 py-1 px-2 rounded hover:bg-gray-50">
                <Code2 size={16} /> <span className="text-sm">Code</span>
              </div>
            </div>
          )}
        </div>

        {/* Other Independent Items */}
        {sidebarOpen && (
          <div className="px-3 mt-4 space-y-1.5 text-gray-700 border-t border-gray-100 pt-4">
            <div className="ml-2 flex items-center gap-2 cursor-pointer hover:text-indigo-600 py-1 px-2 rounded hover:bg-gray-50">
              <FileCog size={16} /> <span className="text-sm">Project Pages</span>
            </div>
            <div className="ml-2 flex items-center gap-2 cursor-pointer hover:text-indigo-600 py-1 px-2 rounded hover:bg-gray-50">
              <PlusCircle size={16} /> <span className="text-sm">Add Shortcut</span>
            </div>
            {user.userId === project.projectCreator && (
              <div 
                onClick={() => {navigate(`/projects/settings/${project.projectId}`)}} 
                className="ml-2 flex items-center gap-2 cursor-pointer hover:text-indigo-600 py-1 px-2 rounded hover:bg-gray-50"
              >
                <Settings size={16} /> <span className="text-sm">Project Settings</span>
              </div>
            )}
            <div className="ml-2 flex items-center gap-2 text-red-500 cursor-pointer hover:text-red-600 py-1 px-2 rounded hover:bg-red-50">
              <Archive size={16} /> <span className="text-sm">Archived Issues</span>
            </div>
          </div>
        )}
      </div>

      {/* Draggable Divider */}
      <div
        onMouseDown={() => (isResizing.current = true)}
        className="w-1 cursor-col-resize bg-gray-200 hover:bg-gray-400 transition-colors"
      ></div>

      {/* Main Content */}
      <div className="flex-1 bg-gray-50 p-4 overflow-y-auto">
        <Outlet />
        
      </div>
    </div>
  );
}

export default Home;