import React, { useState, useEffect } from 'react'
import { useQuery, useMutation } from '@apollo/client'
import { GET_SUBTASK_BY_ID } from '@/graphql/Queries/task-service'
import { UNASSIGN_MEMBER_TO_SUBTASK } from '@/graphql/Mutation/task-service'
import { ASSIGN_MEMBER_TO_SUBTASK } from '@/graphql/Mutation/task-service'
import { CHANGE_SUBTASK_STATUS } from '@/graphql/Mutation/task-service'
import { getUserDetails } from '@/services/UserService'
import { useAuth } from '@/context/AuthContext'
import { useApolloClients } from '@/graphql/Clients/ApolloClientContext'

import { 
  Clock, 
  User, 
  Calendar, 
  AlertCircle, 
  CheckCircle, 
  XCircle, 
  Plus, 
  UserMinus,
  X,
  Check
} from 'lucide-react'

function SubtaskDetails({subTaskId, onClose}) {
  
  const {accessToken} = useAuth();
  const {taskClient} = useApolloClients();
  const { user } = useAuth();
  const [subtaskDetails, setSubtaskDetails] = useState(null);
  const [assignedMembers, setAssignedMembers] = useState([]);
  const [userId, setUserId] = useState("");
  const [isAssigningMember, setIsAssigningMember] = useState(false);
  const [toast, setToast] = useState({ show: false, message: '', type: '' });
  
  const statusOptions = [
    { value: "PENDING", label: "Pending", color: "bg-amber-100 text-amber-700", icon: <Clock className="h-4 w-4 text-amber-500" /> },
    { value: "IN_PROGRESS", label: "In Progress", color: "bg-blue-100 text-blue-700", icon: <AlertCircle className="h-4 w-4 text-blue-500" /> },
    { value: "COMPLETED", label: "Completed", color: "bg-green-100 text-green-700", icon: <CheckCircle className="h-4 w-4 text-green-500" /> },
    { value: "BLOCKED", label: "Blocked", color: "bg-red-100 text-red-700", icon: <XCircle className="h-4 w-4 text-red-500" /> }
  ];

  // Toast notification
  const showToast = (message, type = 'success') => {
    setToast({ show: true, message, type });
    setTimeout(() => {
      setToast({ show: false, message: '', type: '' });
    }, 3000);
  };

  // Fetch subtask details
  const { loading, error, data, refetch } = useQuery(GET_SUBTASK_BY_ID, {
    variables: { subTaskId },
    client: taskClient,
    fetchPolicy: "network-only",
    skip: !subTaskId, 
  });
  // Re-fetch when subTaskId changes
  useEffect(() => {
    if (subTaskId) {
      refetch({ subTaskId });
    }
  }, [subTaskId, refetch]);

  // Mutations
  const [assignMember] = useMutation(ASSIGN_MEMBER_TO_SUBTASK, {
    client: taskClient,
    onCompleted: () => {
      refetch();
      showToast("Member assigned successfully!");
    },
    onError: (error) => {
      showToast(`Failed to assign member: ${error.message}`, 'error');
    }
  });
  const [unassignMember] = useMutation(UNASSIGN_MEMBER_TO_SUBTASK, {
    client: taskClient,
    onCompleted: () => {
      refetch();
      showToast("Member unassigned successfully!");
    },
    onError: (error) => {
      showToast(`Failed to unassign member: ${error.message}`, 'error');
    }
  });

  const [changeStatus] = useMutation(CHANGE_SUBTASK_STATUS, {
    client: taskClient,
    onCompleted: () => {
      refetch();
      showToast("Status updated successfully!");
    },
    onError: (error) => {
      showToast(`Failed to update status: ${error.message}`, 'error');
    }
  });

  // Process subtask data when received
  useEffect(() => {
    if (data) {
      // Check both possible property names - case sensitivity matters in GraphQL responses
      const subtaskData = data.getSubTaskById || data.getSubtaskById;
      
      if (subtaskData) {
        setSubtaskDetails(subtaskData);
      
        // Get assigned members
        if (subtaskData.assignees && subtaskData.assignees.length > 0) {
          const fetchMemberDetails = async () => {
            try {
              const memberPromises = subtaskData.assignees.map(memberId => 
                getUserDetails(memberId,accessToken)
              );
              const memberDetails = await Promise.all(memberPromises);
              const membersData = memberDetails.map((s) => s.data);
              setAssignedMembers(membersData);

            } catch (error) {
              console.error("Error fetching member details:", error);
            }
          };
          
          fetchMemberDetails();
        } else {
          setAssignedMembers([]);
        }
      }
    }
  }, [data]);

  const handleAssignMember = async () => {
    if (!userId) {
      showToast("Please enter a User ID", "error");
      return;
    }
    
    try {
      await assignMember({
        variables: {
          taskId: subTaskId,
          memberId: userId
        }
      });
      setUserId("");
      setIsAssigningMember(false);
    } catch (error) {
      console.error("Error assigning member:", error);
    }
  };

  const handleUnassignMember = async (userId) => {
    try {
      
      await unassignMember({
        variables: {
          taskId: subTaskId,
          memberId: userId
        }
      });
    } catch (error) {
      console.error("Error unassigning member:", error);
    }
  };

  const handleStatusChange = async (newStatus) => {
    try {
      await changeStatus({
        variables: {
          subTaskId: subTaskId,
          status: newStatus
        }
      });
    } catch (error) {
      console.error("Error changing status:", error);
    }
  };

  // Format date for display
  const formatDate = (dateString) => {
    if (!dateString) return "N/A";
    return new Date(dateString).toLocaleDateString();
  };

  // Handle missing subTaskId
  if (!subTaskId) return (
    <div className="p-6 bg-rose-50 border border-rose-200 rounded-lg shadow-sm">
      <div className="flex justify-between mb-4">
        <h2 className="text-xl font-bold text-rose-600">Error</h2>
        <button onClick={onClose} className="text-rose-400 hover:text-rose-600 transition-colors">
          <X />
        </button>
      </div>
      <p className="text-rose-600">No subtask ID provided. Please select a subtask to view details.</p>
    </div>
  );

  if (loading) return (
    <div className="p-6 bg-blue-50 border border-blue-200 rounded-lg shadow-sm">
      <div className="flex justify-between mb-4">
        <h2 className="text-xl font-bold text-blue-600">Loading</h2>
        <button onClick={onClose} className="text-blue-400 hover:text-blue-600 transition-colors">
          <X />
        </button>
      </div>
      <div className="text-center p-4 text-blue-600">Loading subtask details...</div>
    </div>
  );

  if (error) return (
    <div className="p-6 bg-rose-50 border border-rose-200 rounded-lg shadow-sm">
      <div className="flex justify-between mb-4">
        <h2 className="text-xl font-bold text-rose-600">Error</h2>
        <button onClick={onClose} className="text-rose-400 hover:text-rose-600 transition-colors">
          <X />
        </button>
      </div>
      <div className="text-rose-600 p-2">Error loading subtask: {error.message}</div>
    </div>
  );

  if (!subtaskDetails) return (
    <div className="p-6 bg-amber-50 border border-amber-200 rounded-lg shadow-sm">
      <div className="flex justify-between mb-4">
        <h2 className="text-xl font-bold text-amber-600">Not Found</h2>
        <button onClick={onClose} className="text-amber-400 hover:text-amber-600 transition-colors">
          <X />
        </button>
      </div>
      <div className="p-2 text-amber-600">No subtask found with the given ID.</div>
    </div>
  );

  // Find current status option
  const currentStatus = statusOptions.find(option => option.value === subtaskDetails.status) || statusOptions[0];

  return (
    <div className="p-6 bg-blue-50 border border-blue-100 rounded-lg shadow-md relative">
      {/* Toast Notification */}
      {toast.show && (
        <div className={`absolute top-4 right-4 px-4 py-2 rounded-md shadow-md flex items-center 
        ${toast.type === 'error' ? 'bg-rose-100 text-rose-700 border border-rose-200' : 'bg-emerald-100 text-emerald-700 border border-emerald-200'}`}>
          {toast.type === 'error' ? 
            <XCircle className="h-4 w-4 mr-2" /> : 
            <CheckCircle className="h-4 w-4 mr-2" />
          }
          <span>{toast.message}</span>
        </div>
      )}
      
      {/* Header */}
      <div className="flex justify-between mb-6 pb-4 border-b border-blue-200">
        <h1 className="text-2xl font-bold text-blue-800">
          {subtaskDetails.title}
        </h1>
        <button 
          onClick={onClose} 
          className="p-1 rounded-full bg-blue-100 text-blue-500 hover:bg-blue-200 transition-colors"
        >
          <X className="h-5 w-5" />
        </button>
      </div>
      
      {/* Description */}
      <div className="mb-6 p-4 bg-white rounded-lg border border-blue-100 shadow-sm">
        <p className="text-gray-700">
          {subtaskDetails.description}
        </p>
      </div>
      
      {/* Status Section */}
      <div className="mb-6 p-4 bg-indigo-50 rounded-lg border border-indigo-100 shadow-sm">
        <h2 className="text-lg font-bold mb-3 text-indigo-700">Current Status</h2>
        <div className="flex items-center">
          <div className={`px-3 py-1 rounded-md mr-4 flex items-center ${currentStatus.color}`}>
            {currentStatus.icon}
            <span className="ml-1">{currentStatus.label}</span>
          </div>
          
          <div className="flex items-center">
            <span className="mr-2 text-gray-600">Change to:</span>
            <select 
              value={subtaskDetails.status}
              onChange={(e) => handleStatusChange(e.target.value)}
              className="border border-indigo-200 rounded-md p-1.5 bg-white text-gray-700 focus:outline-none focus:ring-2 focus:ring-indigo-300"
            >
              {statusOptions.map(option => (
                <option key={option.value} value={option.value}>{option.label}</option>
              ))}
            </select>
          </div>
        </div>
      </div>
      
      {/* Basic Info */}
      <div className="mb-6 p-4 bg-teal-50 rounded-lg border border-teal-100 shadow-sm">
        <h2 className="text-lg font-bold mb-3 text-teal-700">Task Details</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="flex items-center p-3 bg-white rounded-md border border-teal-100">
            <Calendar className="mr-2 text-teal-500" />
            <div>
              <div className="text-sm text-gray-500">Due Date</div>
              <div className="font-medium">{formatDate(subtaskDetails.deadline)}</div>
            </div>
          </div>
          
          <div className="flex items-center p-3 bg-white rounded-md border border-teal-100">
            <Clock className="mr-2 text-teal-500" />
            <div>
              <div className="text-sm text-gray-500">Created At</div>
              <div className="font-medium">{formatDate(subtaskDetails.createdAt)}</div>
            </div>
          </div>
          
          <div className="flex items-center p-3 bg-white rounded-md border border-teal-100">
            <User className="mr-2 text-teal-500" />
            <div>
              <div className="text-sm text-gray-500">Created By</div>
              <div className="font-medium">{subtaskDetails.creator || "Unknown"}</div>
            </div>
          </div>
        </div>
      </div>
      
      {/* Assigned Members */}
      <div className="mb-6 p-4 bg-purple-50 rounded-lg border border-purple-100 shadow-sm">
        <div className="flex justify-between mb-4">
          <h2 className="text-lg font-bold text-purple-700">Assigned Members</h2>
          <button 
            onClick={() => setIsAssigningMember(true)}
            className="bg-purple-500 hover:bg-purple-600 text-white px-3 py-1.5 rounded-md transition-colors flex items-center"
          >
            <Plus className="h-4 w-4 mr-1" />
            Assign Member
          </button>
        </div>
        
        {/* Member Assignment Form */}
        {isAssigningMember && (
          <div className="mb-4 p-4 bg-white rounded-lg border border-purple-200 shadow-sm">
            <h3 className="text-md font-bold mb-3 text-purple-700">Add Member by ID</h3>
            <div className="flex items-center">
              <input
                type="text"
                value={userId}
                onChange={(e) => setUserId(e.target.value)}
                placeholder="Enter User ID"
                className="border border-purple-200 rounded-md p-2 mr-2 flex-grow focus:outline-none focus:ring-2 focus:ring-purple-300"
              />
              <button
                onClick={handleAssignMember}
                className="bg-green-500 hover:bg-green-600 text-white px-3 py-2 rounded-md transition-colors mr-2 flex items-center"
              >
                <Check className="h-4 w-4 mr-1" />
                Add
              </button>
              <button
                onClick={() => setIsAssigningMember(false)}
                className="bg-gray-300 hover:bg-gray-400 text-gray-700 px-3 py-2 rounded-md transition-colors"
              >
                Cancel
              </button>
            </div>
          </div>
        )}
        
        {/* Members List */}
        {assignedMembers.length === 0 ? (
          <div className="p-6 bg-white rounded-lg border border-purple-100 text-center text-gray-500 italic">
            No members assigned yet.
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
            {assignedMembers.map(member => (
              <div 
                key={member.id} 
                className="flex items-center justify-between p-3 bg-white rounded-lg border border-purple-100 hover:shadow-md transition-shadow"
              >
                <div className="flex items-center">
                  <div className="h-10 w-10 rounded-full bg-purple-100 flex items-center justify-center text-purple-700 font-bold mr-3">
                    {member.name ? member.name.charAt(0).toUpperCase() : member.email.charAt(0).toUpperCase()}
                  </div>
                  <div>
                    <div className="font-medium text-gray-800">
                      {member.name || "Unnamed User"}
                    </div>
                    <div className="text-sm text-gray-500">
                      {member.email || "No email provided"}
                    </div>
                    <div className="text-xs text-purple-600">
                      ID: {member.userId}
                    </div>
                  </div>
                </div>
                <button
                  onClick={() => handleUnassignMember(member.userId)}
                  className="text-rose-400 hover:text-rose-600 hover:bg-rose-50 p-2 rounded-full transition-colors"
                  title="Remove member"
                >
                  <UserMinus className="h-5 w-5" />
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
      
      {/* Additional Details */}
      <div className="p-4 bg-amber-50 rounded-lg border border-amber-100 shadow-sm">
        <h2 className="text-lg font-bold mb-3 text-amber-700">Additional Details</h2>
        
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
          <div className="p-3 bg-white rounded-lg border border-amber-100">
            <div className="text-sm text-gray-500 mb-2">Priority</div>
            <div>
              {subtaskDetails.priority === "HIGH" && (
                <span className="px-3 py-1 bg-rose-100 text-rose-700 rounded-md">High Priority</span>
              )}
              {subtaskDetails.priority === "MEDIUM" && (
                <span className="px-3 py-1 bg-amber-100 text-amber-700 rounded-md">Medium Priority</span>
              )}
              {subtaskDetails.priority === "LOW" && (
                <span className="px-3 py-1 bg-emerald-100 text-emerald-700 rounded-md">Low Priority</span>
              )}
              {!subtaskDetails.priority && (
                <span className="px-3 py-1 bg-gray-100 text-gray-700 rounded-md">No Priority Set</span>
              )}
            </div>
          </div>
          
          <div className="p-3 bg-white rounded-lg border border-amber-100">
            <div className="text-sm text-gray-500 mb-2">Progress</div>
            <div className="w-full bg-gray-200 rounded-full h-2.5">
              <div 
                className={`h-2.5 rounded-full ${
                  subtaskDetails.status === "COMPLETED" ? "bg-green-500" : 
                  subtaskDetails.status === "IN_PROGRESS" ? "bg-blue-500" : 
                  subtaskDetails.status === "BLOCKED" ? "bg-rose-500" : "bg-amber-500"
                }`}
                style={{ 
                  width: subtaskDetails.status === "COMPLETED" ? "100%" : 
                         subtaskDetails.status === "IN_PROGRESS" ? "50%" : 
                         subtaskDetails.status === "BLOCKED" ? "25%" : "10%"
                }}
              ></div>
            </div>
          </div>
        </div>
        
        <div className="p-3 bg-white rounded-lg border border-amber-100">
          <div className="text-sm text-gray-500 mb-2">Bug Description</div>
          <p className="whitespace-pre-line text-gray-700">
            {subtaskDetails.bugDescription || subtaskDetails.description || "No detailed description available."}
          </p>
        </div>
      </div>
    </div>
  );
}

export default SubtaskDetails;