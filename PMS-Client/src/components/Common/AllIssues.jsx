import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useQuery } from '@apollo/client';
import { useApolloClients } from '@/graphql/Clients/ApolloClientContext';
import { useAuth } from '@/context/AuthContext';
import { 
  GET_ALL_EPICS_BY_PROJECT_ID,
  GET_ALL_STORIES_BY_PROJECT_ID,
  GET_BUGS_BY_PROJECT_ID,
  GET_ALL_TASKS_BY_PROJECT_ID,
} from '@/graphql/Queries/task-service';
import { getUserDetails } from '@/services/UserService';

function AllIssues() {
  const { accessToken } = useAuth();
  const { taskClient } = useApolloClients();
  const { projectId } = useParams();
  
  const [issues, setIssues] = useState([]);
  const [filteredIssues, setFilteredIssues] = useState([]);
  const [assigneeNames, setAssigneeNames] = useState({});
  const [currentAssigneeIndices, setCurrentAssigneeIndices] = useState({});
  const [activeFilters, setActiveFilters] = useState({
    epic: true,
    story: true,
    task: true,
    bug: true,
    status: 'all'
  });

  const getAssigneeNames = async (issueId, assigneeIds) => {
    try {
      // If no assignees, return early
      if (!Array.isArray(assigneeIds) || assigneeIds.length === 0) {
        setAssigneeNames(prev => ({
          ...prev,
          [issueId]: ["Unassigned"]
        }));
        return;
      }
  
      // Fetch details for each ID in parallel
      const assigneePromises = assigneeIds.map(id => getUserDetails(id, accessToken));
      
      // Wait for all API calls to complete
      const assigneeData = await Promise.all(assigneePromises);
      
      // Extract names from the response data
      const names = assigneeData.map(response => response.data?.name || "Unknown");
      
      // Store the names in state
      setAssigneeNames(prev => ({
        ...prev,
        [issueId]: names
      }));
      
      // Initialize current index for this issue
      setCurrentAssigneeIndices(prev => ({
        ...prev,
        [issueId]: 0
      }));
    } catch (err) {
      console.error("Error fetching assignee names:", err);
      setAssigneeNames(prev => ({
        ...prev,
        [issueId]: ["Error loading"]
      }));
    }
  };
  
  // Navigate between assignees
  const navigateAssignee = (issueId, direction) => {
    setCurrentAssigneeIndices(prev => {
      const names = assigneeNames[issueId] || [];
      if (names.length <= 1) return prev;
      
      let newIndex = prev[issueId] + direction;
      if (newIndex < 0) newIndex = names.length - 1;
      if (newIndex >= names.length) newIndex = 0;
      
      return {
        ...prev,
        [issueId]: newIndex
      };
    });
  };
  
  // Display current assignee with navigation if multiple assignees exist
  const renderAssigneeWithNavigation = (issueId) => {
    const names = assigneeNames[issueId] || ["Loading..."];
    const currentIndex = currentAssigneeIndices[issueId] || 0;
    
    if (names.length <= 1) {
      return <span>{names[0]}</span>;
    }
    
    return (
      <div className="flex items-center space-x-1">
        <button 
          onClick={(e) => {
            e.preventDefault();
            navigateAssignee(issueId, -1);
          }}
          className="text-gray-500 hover:text-blue-600"
        >
          &lt;
        </button>
        <span>{names[currentIndex]}</span>
        <button 
          onClick={(e) => {
            e.preventDefault();
            navigateAssignee(issueId, 1);
          }}
          className="text-gray-500 hover:text-blue-600"
        >
          &gt;
        </button>
        <span className="text-xs text-gray-400">
          {currentIndex + 1}/{names.length}
        </span>
      </div>
    );
  };
  
  // Fetch epics
  const { data: epicsData, loading: epicsLoading } = useQuery(GET_ALL_EPICS_BY_PROJECT_ID, {
    variables: { projectId },
    client: taskClient,
    skip: !projectId
  });
  
  // Fetch stories
  const { data: storiesData, loading: storiesLoading } = useQuery(GET_ALL_STORIES_BY_PROJECT_ID, {
    variables: { projectId },
    client: taskClient,
    skip: !projectId
  });
  
  // Fetch bugs
  const { data: bugsData, loading: bugsLoading, error: bugError } = useQuery(GET_BUGS_BY_PROJECT_ID, {
    variables: { projectId },
    client: taskClient,
    skip: !projectId
  });

  // Fetch tasks
  const { data: tasksData, loading: tasksLoading } = useQuery(GET_ALL_TASKS_BY_PROJECT_ID, {
    variables: { projectId },
    client: taskClient,
    skip: !projectId
  });

  // Process and combine data when it's available
  useEffect(() => {
    const allIssues = [];
    
    if (epicsData?.getAllEpicsByProjectId) {
      const formattedEpics = epicsData.getAllEpicsByProjectId.map(epic => ({
        ...epic,
        type: 'Epic',
        priority: epic.priority || 'Medium'
      }));
      allIssues.push(...formattedEpics);
    }
    
    if (storiesData?.getAllStoriesByProjectId) {
      const formattedStories = storiesData.getAllStoriesByProjectId.map(story => ({
        ...story,
        type: 'Story',
        priority: story.priority || 'Medium'
      }));
      allIssues.push(...formattedStories);
    }
    
    if (bugsData?.getBugsByProjectId) {
      const formattedBugs = bugsData.getBugsByProjectId.map(bug => ({
        ...bug,
        type: 'Bug',
        priority: bug.priority || 'Medium'
      }));
      allIssues.push(...formattedBugs);
    }
    
    if (tasksData?.getAllTaskByProjectId) {
      const formattedTasks = tasksData?.getAllTaskByProjectId?.map(task => ({
        ...task,
        type: 'Task',
        priority: task.priority || 'Medium'
      }));
      allIssues.push(...formattedTasks);
    }
    
    setIssues(allIssues);
  }, [epicsData, storiesData, bugsData, tasksData]);
  
  // Fetch assignee names for each issue
  useEffect(() => {
    issues.forEach(issue => {
      getAssigneeNames(issue.id, issue.assignees);
    });
  }, [issues]);
  
  // Apply filters when issues or activeFilters change
  useEffect(() => {
    let filtered = [...issues];
    
    // Filter by type
    filtered = filtered.filter(issue => {
      if (issue.type === 'Epic') return activeFilters.epic;
      if (issue.type === 'Story') return activeFilters.story;
      if (issue.type === 'Task') return activeFilters.task;
      if (issue.type === 'Bug') return activeFilters.bug;
      return true;
    });
    
    // Filter by status
    if (activeFilters.status !== 'all') {
      filtered = filtered.filter(issue => {
        if (activeFilters.status === 'COMPLETED') return issue.status === 'COMPLETED' || issue.status === 'COMPLETED';
        if (activeFilters.status === 'inProgress') return issue.status === 'IN_PROGRESS' || issue.status === 'In Progress';
        if (activeFilters.status === 'TODO') return issue.status === 'TODO' || issue.status === 'To Do';
        return true;
      });
    }
    
    setFilteredIssues(filtered);
  }, [issues, activeFilters]);
  
  const handleFilterChange = (filterType, value) => {
    setActiveFilters(prev => ({
      ...prev,
      [filterType]: value
    }));
  };
  
  const isLoading = epicsLoading || storiesLoading || bugsLoading || tasksLoading;
  
  // Format date function
  const formatDate = (dateString) => {
    if (!dateString) return 'None';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  };
  
  return (
    <div className="p-4">
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-xl font-bold">Issues</h1>
        <div className="flex space-x-2">
          <button 
            className={`px-4 py-2 rounded ${activeFilters.status === 'all' ? 'bg-blue-500 text-white' : 'bg-gray-200'}`}
            onClick={() => handleFilterChange('status', 'all')}
          >
            All
          </button>
          <button 
            className={`px-4 py-2 rounded ${activeFilters.status === 'TODO' ? 'bg-blue-500 text-white' : 'bg-gray-200'}`}
            onClick={() => handleFilterChange('status', 'TODO')}
          >
            To Do
          </button>
          <button 
            className={`px-4 py-2 rounded ${activeFilters.status === 'inProgress' ? 'bg-blue-500 text-white' : 'bg-gray-200'}`}
            onClick={() => handleFilterChange('status', 'inProgress')}
          >
            In Progress
          </button>
          <button 
            className={`px-4 py-2 rounded ${activeFilters.status === 'COMPLETED' ? 'bg-blue-500 text-white' : 'bg-gray-200'}`}
            onClick={() => handleFilterChange('status', 'COMPLETED')}
          >
           Completed
          </button>
        </div>
      </div>
      
      <div className="mb-4 flex space-x-4">
        <label className="inline-flex items-center">
          <input 
            type="checkbox" 
            checked={activeFilters.epic} 
            onChange={(e) => handleFilterChange('epic', e.target.checked)}
            className="form-checkbox h-5 w-5 text-blue-600"
          />
          <span className="ml-2">Epic</span>
        </label>
        <label className="inline-flex items-center">
          <input 
            type="checkbox" 
            checked={activeFilters.story} 
            onChange={(e) => handleFilterChange('story', e.target.checked)}
            className="form-checkbox h-5 w-5 text-blue-600"
          />
          <span className="ml-2">Story</span>
        </label>
        <label className="inline-flex items-center">
          <input 
            type="checkbox" 
            checked={activeFilters.task} 
            onChange={(e) => handleFilterChange('task', e.target.checked)}
            className="form-checkbox h-5 w-5 text-blue-600"
          />
          <span className="ml-2">Task</span>
        </label>
        <label className="inline-flex items-center">
          <input 
            type="checkbox" 
            checked={activeFilters.bug} 
            onChange={(e) => handleFilterChange('bug', e.target.checked)}
            className="form-checkbox h-5 w-5 text-blue-600"
          />
          <span className="ml-2">Bug</span>
        </label>
      </div>
      
      {isLoading ? (
        <div className="flex justify-center p-8">
          <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
        </div>
      ) : (
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Summary</th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Assignee</th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Reporter</th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Priority</th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Created</th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Updated</th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Due Date</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredIssues.map((issue) => (
                <tr key={issue.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                      issue.type === 'Epic' ? 'bg-purple-100 text-purple-800' : 
                      issue.type === 'Story' ? 'bg-blue-100 text-blue-800' : 
                      issue.type === 'Bug' ? 'bg-red-100 text-red-800' : 
                      'bg-green-100 text-green-800'
                    }`}>
                      {issue.type}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <Link to={`/issue/${issue.id}`} className="text-blue-600 hover:text-blue-900">
                      {issue.id.substring(0, 8)}
                    </Link>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    {issue.title || issue.summary || 'No Title'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    {renderAssigneeWithNavigation(issue.id)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    {issue?.reporter || issue.reporterName || 'Unknown'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                      issue.priority === 'High' ? 'bg-red-100 text-red-800' : 
                      issue.priority === 'Medium' ? 'bg-yellow-100 text-yellow-800' : 
                      'bg-green-100 text-green-800'
                    }`}>
                      {issue.priority}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                      (issue.status === 'DONE' || issue.status === 'Done') ? 'bg-green-100 text-green-800' : 
                      (issue.status === 'IN_PROGRESS' || issue.status === 'In Progress') ? 'bg-blue-100 text-blue-800' : 
                      'bg-gray-100 text-gray-800'
                    }`}>
                      {issue.status}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    {formatDate(issue.createdAt)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    {formatDate(issue.updatedAt)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    {formatDate(issue.dueDate)}
                  </td>
                </tr>
              ))}
              {filteredIssues.length === 0 && !isLoading && (
                <tr>
                  <td colSpan="10" className="px-6 py-4 text-center text-sm text-gray-500">
                    No issues found. Adjust filters or create a new issue.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}
      
      <div className="mt-4 flex justify-between items-center">
        <div>
          <span className="text-sm text-gray-700">
            Showing <span className="font-medium">{filteredIssues.length}</span> of <span className="font-medium">{issues.length}</span> issues
          </span>
        </div>
        
      </div>
    </div>
  );
}

export default AllIssues;