import React, { useState, useEffect } from 'react';
import { useQuery, useMutation } from '@apollo/client';
import { GET_BUG_BY_ID } from '@/graphql/Queries/task-service';
import { CHANGE_BUG_STATUS } from '@/graphql/Mutation/task-service';
import { useParams } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';
import { getUserDetails } from '@/services/UserService';
import { useApolloClients } from '@/graphql/Clients/ApolloClientContext';
import { ASSIGN_BUG_TO_USER } from '@/graphql/Mutation/task-service';
import { toast } from 'react-hot-toast';
import {
  Clock,
  AlertCircle,
  Tag,
  CheckCircle,
  XCircle,
  Calendar,
  User,
  FileText,
  LinkIcon,
  Flag,
  Loader,
  ChevronDown,
  Plus,
  Image
} from 'lucide-react';
import { formatDistance, format } from 'date-fns';

function BugDetails() {
  const { bugId } = useParams();
  const { projectClient, taskClient } = useApolloClients();
  const { accessToken } = useAuth();
  const [assigneeDetails, setAssigneeDetails] = useState([]);
  const [newUserId, setNewUserId] = useState('');
  const [isStatusDropdownOpen, setIsStatusDropdownOpen] = useState(false);
  const [isImageModalOpen, setIsImageModalOpen] = useState(false);
  
  // Bug status options
  const statusOptions = [
    { value: 'TODO', label: 'TODO', color: 'bg-yellow-500' },
    { value: 'IN_PROGRESS', label: 'IN_PROGRESS', color: 'bg-blue-500' },
    { value: 'COMPLETED', label: 'COMPLETED', color: 'bg-green-500' },
    { value: 'ARCHIVED', label: 'ARCHIVED', color: 'bg-gray-500' },
  ];

  // Priority badge colors
  const priorityColors = {
    LOW: 'bg-blue-100 text-blue-800',
    MEDIUM: 'bg-yellow-100 text-yellow-800',
    HIGH: 'bg-orange-100 text-orange-800',
    CRITICAL: 'bg-red-100 text-red-800'
  };

  // Query for bug details
  const { loading, error, data } = useQuery(GET_BUG_BY_ID, {
    variables: { bugId },
    client: taskClient,
    fetchPolicy: 'network-only',
    skip: !bugId
  });
  console.log(data);
  // Mutation for changing bug status
  const [changeBugStatus, { loading: statusLoading }] = useMutation(CHANGE_BUG_STATUS, {
    client: taskClient,
    refetchQueries: [{ query: GET_BUG_BY_ID, variables: { bugId } }],
    onCompleted: (data) => {
      toast.success(`${data.changeBugStatus.message}`);
      setIsStatusDropdownOpen(false);
    },
    onError: (error) => {
      toast.error(`Failed to change status: ${error.message}`);
    }
  });

  // Mutation for assigning user to bug
  const [assignBugToUser, { loading: assignLoading }] = useMutation(ASSIGN_BUG_TO_USER, {
    client: taskClient,
    refetchQueries: [{ query: GET_BUG_BY_ID, variables: { bugId } }],
    onCompleted: (data) => {
      toast.success("User assigned to bug successfully");
      setNewUserId('');
    },
    onError: (error) => {
      toast.error(`Failed to assign user: ${error.message}`);
    }
  });

  // Fetch assignee details when data loads
  useEffect(() => {
    const fetchAssigneeDetails = async () => {
      if (data?.getBugById?.assignees?.length) {
        try {
          const assigneePromises = data.getBugById.assignees.map(userId => 
            getUserDetails(userId, accessToken)
          );
          const assignees = await Promise.all(assigneePromises);
          const assigneesDetails = assignees.map((a) => a.data);
          setAssigneeDetails(assigneesDetails);

        } catch (error) {
          console.error("Error fetching assignee details:", error);
          toast.error("Failed to load assignee details");
        }
      }
    };

    fetchAssigneeDetails();
  }, [data, accessToken]);

  // Handle status change
  const handleStatusChange = async (newStatus) => {
    try {
      await changeBugStatus({
        variables: {
          bugId,
          status: newStatus
        }
      });
    } catch (error) {
      console.error("Error changing bug status:", error);
    }
  };

  // Handle assigning a new user
  const handleAssignUser = async (e) => {
    e.preventDefault();
    if (!newUserId.trim()) {
      toast.error("Please enter a valid user ID");
      return;
    }

    try {
      await assignBugToUser({
        variables: {
          bugId,
          userId: newUserId.trim()
        }
      });
    } catch (error) {
      console.error("Error assigning user:", error);
    }
  };

  // Loading state
  if (loading) {
    return (
      <div className="flex justify-center items-center h-96">
        <Loader className="w-8 h-8 animate-spin text-blue-600" />
      </div>
    );
  }

  // Error state
  if (error) {
    return (
      <div className="bg-red-50 p-4 rounded-md text-red-700">
        <div className="flex items-center mb-2">
          <AlertCircle className="w-5 h-5 mr-2" />
          <h3 className="font-semibold">Error loading bug details</h3>
        </div>
        <p>{error.message}</p>
      </div>
    );
  }

  const bug = data?.getBugById;
  if (!bug) return <div className="p-4">Bug not found</div>;

  // Determine the parent task type and ID
  let parentType = null;
  let parentId = null;

  if (bug.epicId) {
    parentType = "Epic";
    parentId = bug.epicId;
  } else if (bug.storyId) {
    parentType = "Story";
    parentId = bug.storyId;
  } else if (bug.taskId) {
    parentType = "Task";
    parentId = bug.taskId;
  }

  const getStatusBadge = (status) => {
    const statusOption = statusOptions.find(opt => opt.value === status);
    return (
      <span className={`px-3 py-1 rounded-full text-white text-sm ${statusOption?.color || 'bg-gray-500'}`}>
        {statusOption?.label || status}
      </span>
    );
  };

  return (
    <div className="bg-white rounded-lg shadow-md p-4 md:p-6 max-w-4xl mx-auto">
      {/* Header Section */}
      <div className="border-b pb-4 mb-6">
        <div className="flex flex-col sm:flex-row justify-between items-start gap-4">
          <div>
            <div className="flex items-center gap-2 mb-2 flex-wrap">
              <span className="px-3 py-1 rounded-full bg-red-100 text-red-700 text-sm border border-red-200">
                Bug
              </span>
              {bug.tag && (
                <span className="px-3 py-1 rounded-full bg-purple-100 text-purple-700 text-sm border border-purple-200 flex items-center">
                  <Tag className="w-3 h-3 mr-1" />
                  {bug.tag}
                </span>
              )}
            </div>
            <h1 className="text-xl md:text-2xl font-bold text-gray-800">{bug.title}</h1>
          </div>
          
          <div className="flex items-center gap-3 self-start">
            <div className="text-sm text-gray-500">
              <div className="flex items-center gap-1">
                <span className="font-medium">Status:</span>
                {getStatusBadge(bug.status)}
              </div>
            </div>
            
            {/* Status Dropdown */}
            <div className="relative">
              <button 
                className="px-3 py-1.5 md:px-4 md:py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors flex items-center text-sm md:text-base"
                onClick={() => setIsStatusDropdownOpen(!isStatusDropdownOpen)}
                disabled={statusLoading}
              >
                {statusLoading ? (
                  <Loader className="w-4 h-4 mr-2 animate-spin" />
                ) : null}
                Change Status
                <ChevronDown className="w-4 h-4 ml-2" />
              </button>
              
              {isStatusDropdownOpen && (
                <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg z-10 py-1 border border-gray-200">
                  {statusOptions.map(option => (
                    <button
                      key={option.value}
                      className="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                      onClick={() => handleStatusChange(option.value)}
                    >
                      <div className="flex items-center">
                        <span className={`w-3 h-3 rounded-full ${option.color} mr-2`}></span>
                        {option.label}
                      </div>
                    </button>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
        
        <div className="mt-4 flex flex-wrap gap-4 text-sm text-gray-600">
          <div className="flex items-center gap-1">
            <Calendar className="w-4 h-4" />
            Created: {format(new Date(bug.createdAt || Date.now()), 'MMM d, yyyy')}
          </div>
          {bug.deadline && (
            <div className="flex items-center gap-1">
              <Clock className="w-4 h-4 text-red-500" />
              Due: {format(new Date(bug.deadline), 'MMM d, yyyy')}
            </div>
          )}
          <div className="flex items-center gap-1">
            <Flag className="w-4 h-4" />
            Priority: 
            <span className={`px-2 py-0.5 rounded ${priorityColors[bug.priority] || 'bg-gray-100'}`}>
              {bug.priority}
            </span>
          </div>
          {parentType && parentId && (
            <div className="flex items-center gap-1">
              <LinkIcon className="w-4 h-4" />
              {parentType}: {parentId}
            </div>
          )}
        </div>
      </div>

      {/* Main Content */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          {/* Description */}
          <div className="mb-6">
            <h2 className="text-lg font-semibold mb-3 text-gray-800 flex items-center">
              <FileText className="w-5 h-5 mr-2 text-blue-600" />
              Description
            </h2>
            <div className="bg-gray-50 p-4 rounded-md whitespace-pre-wrap text-gray-700">
              {bug.description || "No description provided"}
            </div>
          </div>

          {/* Expected vs Actual Outcome */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
            <div>
              <h2 className="text-lg font-semibold mb-3 text-gray-800 flex items-center">
                <CheckCircle className="w-5 h-5 mr-2 text-green-600" />
                Expected Outcome
              </h2>
              <div className="bg-gray-50 p-4 rounded-md whitespace-pre-wrap text-gray-700 border-l-4 border-green-400">
                {bug.expectedOutcome || "No expected outcome specified"}
              </div>
            </div>
            <div>
              <h2 className="text-lg font-semibold mb-3 text-gray-800 flex items-center">
                <XCircle className="w-5 h-5 mr-2 text-red-600" />
                Actual Outcome
              </h2>
              <div className="bg-gray-50 p-4 rounded-md whitespace-pre-wrap text-gray-700 border-l-4 border-red-400">
                {bug.actualOutcome || "No actual outcome specified"}
              </div>
            </div>
          </div>

          {/* Bug Image */}
          {bug.image && (
            <div className="mb-6">
              <h2 className="text-lg font-semibold mb-3 text-gray-800 flex items-center">
                <Image className="w-5 h-5 mr-2 text-blue-600" />
                Bug Screenshot
              </h2>
              <div className="bg-gray-50 p-2 rounded-md">
                <div className="relative">
                  <img 
                    src={bug.image} 
                    alt="Bug screenshot" 
                    className="w-full h-auto rounded cursor-pointer object-contain max-h-96"
                    onClick={() => setIsImageModalOpen(true)}
                  />
                  <div className="absolute bottom-2 right-2">
                    <button 
                      className="bg-white/80 hover:bg-white p-1.5 rounded-full shadow-md text-gray-700 transition-colors"
                      onClick={() => setIsImageModalOpen(true)}
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <path d="M15 3h6v6M9 21H3v-6M21 3l-7 7M3 21l7-7"/>
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>

        {/* Sidebar */}
        <div className="bg-gray-50 p-4 rounded-md">
          {/* Assignees */}
          <div className="mb-6">
            <h2 className="text-lg font-semibold mb-3 text-gray-800 flex items-center">
              <User className="w-5 h-5 mr-2 text-blue-600" />
              Assignees
            </h2>
            
            {/* Add new assignee */}
            <form onSubmit={handleAssignUser} className="mb-4">
              <div className="flex gap-2">
                <input
                  type="text"
                  value={newUserId}
                  onChange={(e) => setNewUserId(e.target.value)}
                  placeholder="Enter User ID"
                  className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
                />
                <button
                  type="submit"
                  className="px-3 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors flex items-center"
                  disabled={assignLoading}
                >
                  {assignLoading ? (
                    <Loader className="w-4 h-4 animate-spin" />
                  ) : (
                    <Plus className="w-4 h-4" />
                  )}
                </button>
              </div>
            </form>
            
            {assigneeDetails.length > 0 ? (
              <div className="space-y-3">
                {assigneeDetails.map((user) => (
                  <div key={user.id} className="flex items-center gap-3 p-2 bg-white rounded-md shadow-sm">
                    <div className="w-8 h-8 rounded-full bg-blue-500 text-white flex items-center justify-center overflow-hidden">
                      {user.profilePicture ? (
                        <img src={user.profilePicture} alt={user.name || user.email} className="w-full h-full object-cover" />
                      ) : (
                        <span>{(user.name || user.email || "U").charAt(0).toUpperCase()}</span>
                      )}
                    </div>
                    <div>
                      <div className="font-medium">{user.name || "N/A"}</div>
                      <div className="text-sm text-gray-500">{user.email}</div>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="text-gray-500 italic">No assignees</div>
            )}
          </div>

          {/* Details */}
          <div>
            <h2 className="text-lg font-semibold mb-3 text-gray-800">Details</h2>
            <dl className="space-y-2">
              <div className="grid grid-cols-3 gap-1">
                <dt className="text-gray-500">Bug ID:</dt>
                <dd className="col-span-2 font-mono text-sm">{bug.id}</dd>
              </div>
              <div className="grid grid-cols-3 gap-1">
                <dt className="text-gray-500">Project:</dt>
                <dd className="col-span-2">{bug.projectId}</dd>
              </div>
              {bug.label && (
                <div className="grid grid-cols-3 gap-1">
                  <dt className="text-gray-500">Label:</dt>
                  <dd className="col-span-2">{bug.label}</dd>
                </div>
              )}
              <div className="grid grid-cols-3 gap-1">
                <dt className="text-gray-500">Last Updated:</dt>
                <dd className="col-span-2">{format(new Date(bug.updatedAt || Date.now()), 'MMM d, yyyy HH:mm')}</dd>
              </div>
            </dl>
          </div>
        </div>
      </div>

      {/* Image Modal */}
      {isImageModalOpen && bug.image && (
        <div className="fixed inset-0 bg-black/70 flex items-center justify-center z-50 p-4" onClick={() => setIsImageModalOpen(false)}>
          <div className="relative max-w-4xl w-full mx-auto" onClick={(e) => e.stopPropagation()}>
            <button 
              className="absolute top-4 right-4 bg-white/20 hover:bg-white/40 rounded-full p-2 text-white transition-colors"
              onClick={() => setIsImageModalOpen(false)}
            >
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
              </svg>
            </button>
            <img 
              src={bug.image} 
              alt="Bug screenshot" 
              className="w-full h-auto object-contain max-h-screen"
            />
          </div>
        </div>
      )}
    </div>
  );
}

export default BugDetails;