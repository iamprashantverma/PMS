import React, { useState, useEffect } from 'react'
import { useQuery } from '@apollo/client'
import { GET_TASKS_ASSIGNED_TO_USER } from '@/graphql/Queries/task-service'
import { useApolloClients } from '@/graphql/Clients/ApolloClientContext'
import { useAuth } from '@/context/AuthContext'
import { format } from 'date-fns'
import { Outlet, useNavigate } from 'react-router-dom'
import { Search, Filter, Calendar, Image as ImageIcon, Clock, ArrowUp, ArrowDown } from 'lucide-react'

function AllWorks() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const userId = user?.id;
  const { taskClient } = useApolloClients();
  
  // State for tasks
  const [tasks, setTasks] = useState([]);
  const [filteredTasks, setFilteredTasks] = useState([]);
  
  // State for filters
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const [sortBy, setSortBy] = useState('dueDate');
  const [sortOrder, setSortOrder] = useState('asc');
  
  // Fetching tasks
  const { loading: tasksLoading, error: tasksError, data: tasksData } = useQuery(
    GET_TASKS_ASSIGNED_TO_USER,
    {
      variables: { userId },
      client: taskClient,
      skip: !userId,
    }
  );
  
  // Process data when it's loaded
  useEffect(() => {
    if (tasksData?.getTasksAssignedToUser) {
      setTasks(tasksData.getTasksAssignedToUser);
    }
  }, [tasksData]);
  
  // Apply filters and sorting
  useEffect(() => {
    // Filter by search term
    let filtered = tasks.filter(task => 
      task.title?.toLowerCase().includes(searchTerm.toLowerCase())
    );
    
    // Filter by status
    if (statusFilter !== 'all') {
      filtered = filtered.filter(task => task.status === statusFilter);
    }
    
    // Sort items
    filtered.sort((a, b) => {
      if (sortBy === 'dueDate') {
        const dateA = new Date(a.dueDate || '9999-12-31');
        const dateB = new Date(b.dueDate || '9999-12-31');
        return sortOrder === 'asc' ? dateA - dateB : dateB - dateA;
      } else if (sortBy === 'createdDate') {
        const dateA = new Date(a.createdAt || '1970-01-01');
        const dateB = new Date(b.createdAt || '1970-01-01');
        return sortOrder === 'asc' ? dateA - dateB : dateB - dateA;
      } else if (sortBy === 'title') {
        return sortOrder === 'asc' 
          ? (a.title || '').localeCompare(b.title || '')
          : (b.title || '').localeCompare(a.title || '');
      }
      return 0;
    });
    
    setFilteredTasks(filtered);
  }, [tasks, searchTerm, statusFilter, sortBy, sortOrder]);
  
  // Calculate time remaining
  const getTimeRemaining = (dueDate) => {
    if (!dueDate) return 'No deadline';
    
    const now = new Date();
    const due = new Date(dueDate);
    const diffTime = due - now;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays < 0) return 'Overdue';
    if (diffDays === 0) return 'Due today';
    if (diffDays === 1) return 'Due tomorrow';
    if (diffDays <= 7) return `Due in ${diffDays} days`;
    return format(due, 'MMM dd, yyyy');
  };
  
  // Get CSS class for status
  const getStatusClass = (status) => {
    switch (status?.toLowerCase()) {
      case 'completed':
        return 'bg-green-500 text-white';
      case 'in_progress':
        return 'bg-blue-500 text-white';
      case 'pending':
        return 'bg-yellow-500 text-white';
      case 'todo':
        return 'bg-red-500 text-white';
      default:
        return 'bg-gray-500 text-white';
    }
  };
  
  // Get CSS class for priority
  const getPriorityClass = (priority) => {
    switch (priority?.toLowerCase()) {
      case 'high':
        return 'bg-red-500 text-white';
      case 'medium':
        return 'bg-orange-500 text-white';
      case 'low':
        return 'bg-green-500 text-white';
      default:
        return 'bg-gray-500 text-white';
    }
  };
  
  // Loading state
  if (tasksLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-xl font-semibold text-gray-600">Loading tasks...</div>
      </div>
    );
  }
  
  // Error state
  if (tasksError) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-xl font-semibold text-red-600">
          Error loading tasks. Please try again later.
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-50 to-purple-50">
      {/* Header with gradient */}
      <div className="bg-gradient-to-r from-indigo-300 to-purple-600 text-white py-8 px-6 shadow-lg">
        <div className="container mx-auto max-w-7xl">
          <h1 className="text-4xl font-bold font-serif">Your Works</h1>
        </div>
      </div>
      
      <div className="container mx-auto px-4 py-8 max-w-7xl">
        {/* Stats Overview */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
          <div className="bg-white rounded-lg shadow-md p-5 flex items-center border-l-4 border-indigo-500">
            <div className="mr-4 p-3 bg-indigo-100 rounded-lg">
              <Clock className="h-6 w-6 text-indigo-500" />
            </div>
            <div>
              <div className="text-3xl font-bold text-gray-800">
                {filteredTasks.filter(t => t.status?.toLowerCase() === 'in_planned').length}
              </div>
              <div className="text-sm text-gray-500">Planned Tasks</div>
            </div>
          </div>
          
          <div className="bg-white rounded-lg shadow-md p-5 flex items-center border-l-4 border-blue-500">
            <div className="mr-4 p-3 bg-blue-100 rounded-lg">
              <Clock className="h-6 w-6 text-blue-500" />
            </div>
            <div>
              <div className="text-3xl font-bold text-gray-800">
                {filteredTasks.filter(t => t.status?.toLowerCase() === 'in_progress').length}
              </div>
              <div className="text-sm text-gray-500">In Progress</div>
            </div>
          </div>
          
          <div className="bg-white rounded-lg shadow-md p-5 flex items-center border-l-4 border-green-500">
            <div className="mr-4 p-3 bg-green-100 rounded-lg">
              <Clock className="h-6 w-6 text-green-500" />
            </div>
            <div>
              <div className="text-3xl font-bold text-gray-800">
                {filteredTasks.filter(t => t.status?.toLowerCase() === 'completed').length}
              </div>
              <div className="text-sm text-gray-500">Completed</div>
            </div>
          </div>
          
          <div className="bg-white rounded-lg shadow-md p-5 flex items-center border-l-4 border-red-500">
            <div className="mr-4 p-3 bg-red-100 rounded-lg">
              <Clock className="h-6 w-6 text-red-500" />
            </div>
            <div>
              <div className="text-3xl font-bold text-gray-800">
                {filteredTasks.filter(t => t.status?.toLowerCase() === 'todo').length}
              </div>
              <div className="text-sm text-gray-500">To Do</div>
            </div>
          </div>
        </div>
        
        {/* Filters and Search */}
        <div className="bg-white rounded-lg shadow-lg p-6 mb-8 backdrop-blur-sm bg-opacity-90">
          <div className="flex flex-col md:flex-row gap-4 md:items-end">
            {/* Search */}
            <div className="relative flex-1">
              <label className="block text-sm font-medium text-gray-700 mb-1">Search Tasks</label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <Search className="h-5 w-5 text-gray-400" />
                </div>
                <input
                  type="text"
                  placeholder="Type to search..."
                  className="pl-10 pr-4 py-3 w-full border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 font-sans"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>
            </div>
            
            {/* Status Filter */}
            <div className="md:w-60">
              <label className="block text-sm font-medium text-gray-700 mb-1">Status</label>
              <div className="flex items-center bg-white border border-gray-300 rounded-lg">
                <div className="pl-3">
                  <Filter className="h-5 w-5 text-gray-400" />
                </div>
                <select
                  className="flex-1 py-3 px-2 bg-transparent focus:outline-none focus:ring-2 focus:ring-indigo-500 rounded-lg font-sans"
                  value={statusFilter}
                  onChange={(e) => setStatusFilter(e.target.value)}
                >
                  <option value="all">All Statuses</option>
                  <option value="IN_PLANNED">In Planned</option>
                  <option value="IN_PROGRESS">In Progress</option>
                  <option value="COMPLETED">Completed</option>
                  <option value="TODO">To Do</option>
                </select>
              </div>
            </div>
            
            {/* Sort Options */}
            <div className="md:w-60">
              <label className="block text-sm font-medium text-gray-700 mb-1">Sort By</label>
              <div className="flex items-center bg-white border border-gray-300 rounded-lg">
                <select
                  className="flex-1 py-3 px-3 bg-transparent focus:outline-none focus:ring-2 focus:ring-indigo-500 rounded-lg font-sans"
                  value={sortBy}
                  onChange={(e) => setSortBy(e.target.value)}
                >
                  <option value="dueDate">Deadline</option>
                  <option value="createdDate">Created Date</option>
                  <option value="title">Title</option>
                </select>
                <button 
                  className="px-3 py-3 focus:outline-none"
                  onClick={() => setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc')}
                >
                  {sortOrder === 'asc' ? 
                    <ArrowUp className="h-5 w-5 text-gray-500" /> : 
                    <ArrowDown className="h-5 w-5 text-gray-500" />
                  }
                </button>
              </div>
            </div>
          </div>
        </div>
        <Outlet/>
        {/* Tasks List */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredTasks.length > 0 ? (
            filteredTasks.map(task => (
              <div 
                key={task.id} 
                className="bg-white rounded-lg shadow-lg overflow-hidden hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1 border-t-4 border-indigo-500"
              >
                {/* Task Image */}
                <div className="relative h-40 bg-gray-200 overflow-hidden">
                  {task?.image ? (
                    <img 
                      src={task.image} 
                      alt={task.title} 
                      className="w-full h-full object-cover"
                      onError={(e) => {
                        e.target.onerror = null;
                        e.target.src = "/api/placeholder/400/160";
                      }}
                    />
                  ) : (
                    <div className="h-full flex items-center justify-center bg-gradient-to-r from-indigo-100 to-purple-100">
                      <ImageIcon className="h-16 w-16 text-gray-400" />
                    </div>
                  )}
                  
                  {/* Status badge */}
                  {task.status && (
                    <span className={`absolute top-4 right-4 px-3 py-1 rounded-full text-xs font-medium ${getStatusClass(task.status)}`}>
                      {task.status}
                    </span>
                  )}
                </div>
                
                {/* Task content */}
                <div className="p-5">
                  <h3 className="text-xl font-semibold text-gray-800 mb-2 line-clamp-1">{task.title}</h3>
                  
                  {task.description && (
                    <p className="text-gray-600 mb-4 line-clamp-2">{task.description}</p>
                  )}
                  
                  <div className="flex justify-between items-center">
                    {task.priority && (
                      <span className={`px-3 py-1 rounded-full text-xs font-medium ${getPriorityClass(task.priority)}`}>
                        {task.priority} Priority
                      </span>
                    )}
                    
                    {task.deadline && (
                      <div className="flex items-center text-sm text-gray-500">
                        <Calendar className="h-4 w-4 mr-1" />
                        <span>{getTimeRemaining(task.deadline)}</span>
                      </div>
                    )}
                  </div>
                </div>
                
                {/* Task footer */}
                <div className="bg-gray-50 px-5 py-3 border-t border-gray-100 flex justify-between items-center">
                  <div className="text-xs text-gray-500">
                    {task.createdAt && `Created: ${format(new Date(task.createdAt), 'MMM dd')}`}
                  </div>
                  <button  onClick={() => navigate(`/your-work/Task/${task.id}`)}className="bg-indigo-500 hover:bg-indigo-600 text-white text-sm py-1 px-3 rounded transition-colors">
                    View Details
                  </button>
                </div>
              </div>
            ))
          ) : (
            <div className="col-span-full bg-white rounded-lg shadow-lg p-12 text-center">
              <h3 className="text-2xl font-medium text-gray-700 font-serif">No tasks found</h3>
              <p className="text-gray-500 mt-3 font-sans">
                {searchTerm || statusFilter !== 'all' 
                  ? "Try adjusting your filters to see more results" 
                  : "You don't have any tasks assigned to you"}
              </p>
            </div>
          )}
        </div>
 
      </div>
    </div>
  );
}

export default AllWorks;