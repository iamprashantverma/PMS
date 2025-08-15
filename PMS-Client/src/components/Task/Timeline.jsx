import React, { useState, useEffect } from 'react';
import { useQuery } from '@apollo/client';
import { GET_ALL_EPICS_BY_PROJECT_ID } from '@/graphql/Queries/task-service';
import { useApolloClients } from '@/graphql/Clients/ApolloClientContext';
import { useParams } from 'react-router-dom';
import { ChevronDown, ChevronRight, Filter, Search, Settings, Share, MessageSquare, Download, MoreHorizontal } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
function Timeline() {
  const { projectId } = useParams();
  const { taskClient } = useApolloClients();
  const [expandedEpics, setExpandedEpics] = useState({});
  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [selectedEpic, setSelectedEpic] = useState("");
  const [selectedLabel, setSelectedLabel] = useState("");
  
  const navigate = useNavigate();

  // Fetch epics data
  const { loading, error, data } = useQuery(GET_ALL_EPICS_BY_PROJECT_ID, {
    variables: { projectId },
    client: taskClient,
    fetchPolicy:"network-only",
  });
  
  const toggleEpicExpansion = (epicId) => {
    setExpandedEpics(prev => ({
      ...prev,
      [epicId]: !prev[epicId]
    }));
  };
  
  // Sample data structure (replace with actual data from your query)
  const epics = data?.getAllEpicsByProjectId || [];

  // Filter epics based on search and filters
  const filteredEpics = epics.filter(epic => {
    const matchesSearch = searchTerm === "" || 
      epic.title.toLowerCase().includes(searchTerm.toLowerCase()) || 
      epic.id.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesStatus = statusFilter === "" || epic.status === statusFilter;
    const matchesEpic = selectedEpic === "" || epic.id === selectedEpic;
    
    return matchesSearch && matchesStatus && matchesEpic;
  });

  // Calculate the timeline periods based on the data
  const periods = ["MAR", "APR - JUN", "JUL - SEP", "OCT - DEC"];
  
  // Improved timeline bar calculation function
  const calculateTimelineBar = (startDate, endDate) => {
    // Handle cases when dates might be undefined
    if (!startDate || !endDate) {
      return { left: "0%", width: "0%" };
    }
    
    try {
      const start = new Date(startDate);
      const end = new Date(endDate);
      
      // Check if dates are valid
      if (isNaN(start.getTime()) || isNaN(end.getTime())) {
        return { left: "0%", width: "0%" };
      }
      
      // Define the timeline range (March to December)
      const timelineStart = 2; // March = 2 (0-indexed)
      const timelineEnd = 11; // December = 11 (0-indexed)
      const timelineWidth = timelineEnd - timelineStart + 1; // 10 months
      
      // Calculate the position and width
      let startMonth = start.getMonth();
      let endMonth = end.getMonth();
      
      // Ensure months are within our visible range
      startMonth = Math.max(startMonth, timelineStart);
      endMonth = Math.min(endMonth, timelineEnd);
      
      // Calculate position (adjusted to percentage within visible timeline)
      const startPos = ((startMonth - timelineStart) / timelineWidth) * 100;
      
      // Calculate width (as percentage of visible timeline)
      // Use at least 5% width for better visibility
      const width = Math.max(((endMonth - startMonth + 1) / timelineWidth) * 100, 5);
      
      return {
        left: `${startPos}%`,
        width: `${width}%`,
      };
    } catch (error) {
      console.error("Error calculating timeline bar:", error);
      return { left: "0%", width: "0%" };
    }
  };

  // Calculate current month marker position
  const calculateCurrentMonthPosition = () => {
    const currentDate = new Date();
    const currentMonth = currentDate.getMonth();
    
    // Define the timeline range (March to December)
    const timelineStart = 2; // March = 2 (0-indexed)
    const timelineEnd = 11; // December = 11 (0-indexed)
    const timelineWidth = timelineEnd - timelineStart + 1; // 10 months
    
    // Only show if current month is within our visible range
    if (currentMonth < timelineStart || currentMonth > timelineEnd) {
      return null;
    }
    
    // Calculate position (adjusted to percentage within visible timeline)
    const position = ((currentMonth - timelineStart) / timelineWidth) * 100;
    
    return `${position}%`;
  };

  // Get current month position
  const currentMonthPosition = calculateCurrentMonthPosition();

  return (
    <div className="flex flex-col w-full min-h-screen bg-gray-50">
      {/* Header with breadcrumbs and actions */}
      <div className="px-6 py-4 flex flex-col">
        <div className="flex items-center text-gray-500 text-sm mb-2">
          <span>Projects</span>
          <span className="mx-2">/</span>
          <span>Chatbot for Customer Support</span>
        </div>
        
        <div className="flex justify-between items-center">
          <h1 className="text-2xl font-bold text-gray-800">Timeline</h1>
          <div className="flex gap-3">
            <button className="flex items-center gap-1 px-3 py-1 border rounded-md bg-white">
              <MessageSquare size={16} />
              <span>Give feedback</span>
            </button>
            <button className="flex items-center gap-1 px-3 py-1 border rounded-md bg-white">
              <Share size={16} />
              <span>Share</span>
            </button>
            <button className="flex items-center gap-1 px-3 py-1 border rounded-md bg-white">
              <Download size={16} />
              <span>Export</span>
            </button>
            {/* <button className="px-2 py-1 border rounded-md bg-white">
              <MoreHorizontal size={16} />
            </button> */}
          </div>
        </div>
      </div>
      
      {/* Search and filters */}
      <div className="px-6 py-2 flex justify-between">
        <div className="flex items-center gap-4">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={16} />
            <input
              type="text"
              placeholder="Search timeline..."
              className="pl-10 pr-4 py-2 border rounded-md w-48"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
          
          <div className="flex items-center gap-2">
            <div className="flex">
              <div className="w-8 h-8 bg-orange-400 rounded-full flex items-center justify-center text-white">
                PV
              </div>
              <div className="w-8 h-8 bg-purple-600 rounded-full flex items-center justify-center text-white -ml-2">
                P
              </div>
              <div className="w-8 h-8 bg-gray-400 rounded-full flex items-center justify-center text-white -ml-2">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M12 2C6.48 2 2 6.48 2 12C2 17.52 6.48 22 12 22C17.52 22 22 17.52 22 12C22 6.48 17.52 2 12 2ZM12 6C13.93 6 15.5 7.57 15.5 9.5C15.5 11.43 13.93 13 12 13C10.07 13 8.5 11.43 8.5 9.5C8.5 7.57 10.07 6 12 6ZM12 20C9.97 20 8.1 19.33 6.66 18.12C6.23 17.78 5.96 17.28 5.96 16.76C5.96 14.55 7.76 12.75 9.96 12.75H14.04C16.24 12.75 18.04 14.55 18.04 16.76C18.04 17.28 17.77 17.78 17.34 18.12C15.9 19.33 14.03 20 12 20Z" fill="currentColor"/>
                </svg>
              </div>
              <button className="w-8 h-8 rounded-full border flex items-center justify-center bg-white ml-1">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M12 6C13.1 6 14 5.1 14 4C14 2.9 13.1 2 12 2C10.9 2 10 2.9 10 4C10 5.1 10.9 6 12 6ZM12 8C10.9 8 10 8.9 10 10C10 11.1 10.9 12 12 12C13.1 12 14 11.1 14 10C14 8.9 13.1 8 12 8ZM12 14C10.9 14 10 14.9 10 16C10 17.1 10.9 18 12 18C13.1 18 14 17.1 14 16C14 14.9 13.1 14 12 14Z" fill="currentColor"/>
                </svg>
              </button>
            </div>
          </div>
        </div>
        
        <div className="flex items-center gap-4">
          <div className="relative">
            <select 
              className="pl-4 pr-8 py-2 border rounded-md appearance-none bg-white"
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
            >
              <option value="">Status category</option>
              <option value="TODO">To Do</option>
              <option value="IN_Progress">In Progress</option>
              <option value="Completed">Completed</option>
            </select>
            <ChevronDown className="absolute right-3 top-1/2 transform -translate-y-1/2" size={16} />
          </div>
          
          <div className="relative">
            <select 
              className="pl-4 pr-8 py-2 border rounded-md appearance-none bg-white"
              value={selectedEpic}
              onChange={(e) => setSelectedEpic(e.target.value)}
            >
              <option value="">Epic</option>
              {epics.map(epic => (
                <option key={epic.id} value={epic.id}>{epic.id}</option>
              ))}
            </select>
            <ChevronDown className="absolute right-3 top-1/2 transform -translate-y-1/2" size={16} />
          </div>
          
          <div className="relative">
            <select 
              className="pl-4 pr-8 py-2 border rounded-md appearance-none bg-white"
              value={selectedLabel}
              onChange={(e) => setSelectedLabel(e.target.value)}
            >
              <option value="">Label</option>
              <option value="Frontend">Frontend</option>
              <option value="Backend">Backend</option>
              <option value="Design">Design</option>
            </select>
            <ChevronDown className="absolute right-3 top-1/2 transform -translate-y-1/2" size={16} />
          </div>
          
          {/* <button className="flex items-center gap-1 px-3 py-2 border rounded-md bg-white">
            <Settings size={16} />
            <span>View settings</span>
          </button> */}
        </div>
      </div>
      
      {/* Timeline grid */}
      <div className="px-6 py-4 flex-grow">
        <div className="border rounded-md overflow-hidden bg-white">
          {/* Timeline headers */}
          <div className="grid grid-cols-4 gap-0">
            <div className="col-span-1 border-r p-4">
              {/* Task name column */}
            </div>
            <div className="col-span-3 grid grid-cols-4 divide-x relative">
              {/* Current month indicator (blue vertical line) - positioned relative to the entire timeline */}
              {currentMonthPosition && (
                <div className="absolute h-full" style={{ left: currentMonthPosition }}>
                  <div className="h-full border-l-2 border-blue-500">
                    <div className="absolute -top-1 -ml-1 w-2 h-2 bg-blue-500 transform rotate-45"></div>
                  </div>
                </div>
              )}
              
              {periods.map((period, index) => (
                <div key={index} className="p-4 text-center text-sm font-medium text-gray-600">
                  {period}
                </div>
              ))}
            </div>
          </div>
          
          {/* Timeline content */}
          <div>
            {filteredEpics.map((epic) => (
              <React.Fragment key={epic.id}>
                {/* Epic row */}
                <div className="grid grid-cols-4 border-t hover:bg-gray-50">
                  <div className="col-span-1 border-r p-4 flex items-center">
                    <input type="checkbox" className="mr-2" />
                    {epic.tasks && epic.tasks.length > 0 ? (
                      <button 
                        onClick={() => toggleEpicExpansion(epic.id)}
                        className="mr-2 focus:outline-none"
                      >
                        {expandedEpics[epic.id] ? 
                          <ChevronDown size={16} /> : 
                          <ChevronRight size={16} />
                        }
                      </button>
                    ) : <div className="w-4 mr-2"></div>}
                    <div className="mr-2 text-purple-500">
                      {/* Epic icon would go here */}
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M3.9 12c0-1.71 1.39-3.1 3.1-3.1h4V7H7c-2.76 0-5 2.24-5 5s2.24 5 5 5h4v-1.9H7c-1.71 0-3.1-1.39-3.1-3.1zM8 13h8v-2H8v2zm9-6h-4v1.9h4c1.71 0 3.1 1.39 3.1 3.1s-1.39 3.1-3.1 3.1h-4V17h4c2.76 0 5-2.24 5-5s-2.24-5-5-5z" fill="currentColor"/>
                      </svg>
                    </div>
                    <div>
                      <span className="text-sm font-medium text-gray-700">{epic.id}</span>
                      <span className="ml-2 text-sm text-gray-600">{epic.title}</span>
                    </div>
                  </div>
                  
                  <div className="col-span-3 relative">
                    {/* Timeline bar for the epic */}
                    <div className="absolute inset-y-0 flex items-center w-full px-4">
                      <div 
                        className="h-6 bg-purple-300 rounded-md z-10"
                        style={calculateTimelineBar(epic.createdDate, epic.deadline)}
                      ></div>
                    </div>
                    
                    {/* Empty columns for the grid */}
                    <div className="grid grid-cols-4 h-full">
                      {periods.map((_, index) => (
                        <div key={index} className="border-l first:border-l-0 p-4 "></div>
                      ))}
                    </div>
                  </div>
                </div>
                
                {/* Tasks for the epic (shown when expanded) */}
                {expandedEpics[epic.id] && epic.tasks && epic.tasks.map(task => (
                  <div key={task.id} className="grid grid-cols-4 border-t bg-gray-50">
                    <div className="col-span-1 border-r  pl-8 flex items-center">
                      <input type="checkbox" className="mr-2" />
                      <div className="mr-2 text-blue-500">
                          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M19 3H5C3.9 3 3 3.9 3 5V19C3 20.1 3.9 21 5 21H19C20.1 21 21 20.1 21 19V5C21 3.9 20.1 3 19 3ZM9 17H7V7H9V17ZM13 17H11V10H13V17ZM17 17H15V13H17V17Z" fill="currentColor"/>
                          </svg>
                      </div>
                      <div>
                        <span onClick={()=>navigate(`/project/${projectId}/task/${task.id}`)} className="text-xs font-medium text-gray-700">{task.id}</span>
                        <span className="ml-2 text-xs text-gray-600">{task.title}</span>
                      </div>
                    </div>
                    
                    <div className="col-span-3 relative">
                      {/* Timeline bar for the task */}
                      <div className="absolute inset-y-0 flex items-center w-full px-4">
                        <div 
                          className="h-3 bg-blue-300 rounded-md z-10"
                          style={calculateTimelineBar(task.startDate, task.endDate)}
                        ></div>
                      </div>
                      
                      {/* Empty columns for the grid */}
                      <div className="grid grid-cols-4 h-full">
                        {periods.map((_, index) => (
                          <div key={index} className="border-l first:border-l-0 p-3 h-8"></div>
                        ))}
                      </div>
                    </div>
                  </div>
                ))}
              </React.Fragment>
            ))}
            
          </div>
        </div>
      </div>
    </div>
  );
}

export default Timeline;