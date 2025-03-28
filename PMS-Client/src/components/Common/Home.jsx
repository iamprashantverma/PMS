import React, { useState, useRef, useEffect } from 'react';
import { Outlet } from 'react-router-dom';
import {
  ChevronRight,
  LayoutPanelTop,
  CalendarDays,
  ClipboardList,
  FileText,
  ListOrdered,
  Plus,
  Code2,
  FileCog,
  PlusCircle,
  Settings,
  Archive,
  Menu,
  X,
  LayoutDashboard
} from 'lucide-react';

function Home() {
  const [planning, setPlanning] = useState(true);
  const [development, setDevelopment] = useState(true);
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [sidebarWidth, setSidebarWidth] = useState(256); // 64 * 4 px
  const isResizing = useRef(false);
  const sidebarRef = useRef();

  const sideBarHandler = (section) => {
    if (section === 'planning') setPlanning(!planning);
    else if (section === 'development') setDevelopment(!development);
  };

  useEffect(() => {
    const handleMouseMove = (e) => {
      if (isResizing.current) {
        const newWidth = Math.max(64, Math.min(320, e.clientX));
        setSidebarWidth(newWidth);
      }
    };
    const handleMouseUp = () => {
      isResizing.current = false;
    };
    window.addEventListener('mousemove', handleMouseMove);
    window.addEventListener('mouseup', handleMouseUp);
    return () => {
      window.removeEventListener('mousemove', handleMouseMove);
      window.removeEventListener('mouseup', handleMouseUp);
    };
  }, []);

  return (
    <div className="flex h-screen w-full overflow-hidden">
      {/* Sidebar */}
      <div
        ref={sidebarRef}
        className="bg-white text-gray-900 border-r border-gray-300 p-4 flex flex-col transition-all duration-300 overflow-y-auto h-full"
        style={{ width: sidebarOpen ? sidebarWidth : 64 }}
      >
        <button
          onClick={() => setSidebarOpen(!sidebarOpen)}
          className="mb-4 flex items-center justify-end text-gray-700 hover:text-gray-500"
        >
          {sidebarOpen ? <X size={20} /> : <Menu size={20} />}
        </button>

        {/* Planning Section */}
        <div className="mb-4">
          <button
            onClick={() => sideBarHandler('planning')}
            className="flex items-center justify-between w-full px-2 py-1 text-left font-semibold text-blue-500 hover:text-blue-400"
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
            <div className="ml-6 mt-2 space-y-1 text-gray-700">
              <div className="flex items-center gap-2 cursor-pointer hover:text-blue-500"><FileText size={16} /> Summary</div>
              <div className="flex items-center gap-2 cursor-pointer hover:text-blue-500"><CalendarDays size={16} /> Timeline</div>
              <div className="flex items-center gap-2 cursor-pointer hover:text-blue-500"><LayoutDashboard size={16}  /> Board</div>
              <div className="flex items-center gap-2 cursor-pointer hover:text-blue-500"><CalendarDays size={16} /> Calendar</div>
              <div className="flex items-center gap-2 cursor-pointer hover:text-blue-500"><ListOrdered size={16} /> List</div>
              <div className="flex items-center gap-2 cursor-pointer hover:text-blue-500"><ClipboardList size={16} /> Forms</div>
            </div>
          )}
        </div>

        {/* Add View - Independent */}
        {sidebarOpen && (
          <div className="mb-4 text-blue-500 cursor-pointer flex items-center gap-2 ml-6 hover:text-blue-400">
            <Plus size={16} /> Add View
          </div>
        )}

        {/* Development Section */}
        <div className="mb-4">
          <button
            onClick={() => sideBarHandler('development')}
            className="flex items-center justify-between w-full px-2 py-1 text-left font-semibold text-green-500 hover:text-green-400"
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
            <div className="ml-6 mt-2 space-y-1 text-gray-700">
              <div className="flex items-center gap-2 cursor-pointer hover:text-green-500"><Code2 size={16} /> Code</div>
            </div>
          )}
        </div>

        {/* Other Independent Items */}
        {sidebarOpen && (
          <div className="ml-6 space-y-2 text-gray-700">
            <div className="flex items-center gap-2 cursor-pointer hover:text-indigo-500"><FileCog size={16} /> Project Pages</div>
            <div className="flex items-center gap-2 cursor-pointer hover:text-indigo-500"><PlusCircle size={16} /> Add Shortcut</div>
            <div className="flex items-center gap-2 cursor-pointer hover:text-indigo-500"><Settings size={16} /> Project Settings</div>
            <div className="flex items-center gap-2 text-red-500 cursor-pointer hover:text-red-400"><Archive size={16} /> Archived Issues</div>
          </div>
        )}
      </div>

      {/* Draggable Divider */}
      <div
        onMouseDown={() => (isResizing.current = true)}
        className="w-1 cursor-col-resize bg-gray-300 hover:bg-gray-400"
      ></div>

      {/* Main Content */}
      <div className="flex-1 bg-gray-50 p-4 overflow-hidden">
        <Outlet />
      </div>
    </div>
  );
}

export default Home;