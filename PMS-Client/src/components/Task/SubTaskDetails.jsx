import React, { useState, useEffect } from 'react'
import { useQuery, useMutation } from '@apollo/client'
import { GET_SUBTASK_BY_ID } from '@/graphql/Queries/task-service'
import { UNASSIGN_MEMBER_TO_SUBTASK } from '@/graphql/Mutation/task-service'
import { ASSIGN_MEMBER_TO_SUBTASK } from '@/graphql/Mutation/task-service'
import { CHANGE_SUBTASK_STATUS } from '@/graphql/Mutation/task-service'
import { getUserDetails } from '@/services/UserService'
import { useAuth } from '@/context/AuthContext'
import { useApolloClients } from '@/graphql/Clients/ApolloClientContext'
import { useParams } from 'react-router-dom'
import { 
  Clock, 
  User, 
  Calendar, 
  AlertCircle, 
  CheckCircle, 
  XCircle, 
  Plus, 
  UserMinus 
} from 'lucide-react'

function subtaskDetails() {
  const { id } = useParams();
  const { user } = useAuth();
  const { taskServiceClient } = useApolloClients();
  const [subtaskDetails, setSubtaskDetails] = useState(null);
  const [assignedMembers, setAssignedMembers] = useState([]);
  const [availableMembers, setAvailableMembers] = useState([]);
  const [selectedMember, setSelectedMember] = useState("");
  const [isAssigningMember, setIsAssigningMember] = useState(false);
  
  const statusOptions = [
    { value: "PENDING", label: "Pending", icon: <Clock className="h-4 w-4 text-yellow-500" /> },
    { value: "IN_PROGRESS", label: "In Progress", icon: <AlertCircle className="h-4 w-4 text-blue-500" /> },
    { value: "COMPLETED", label: "Completed", icon: <CheckCircle className="h-4 w-4 text-green-500" /> },
    { value: "BLOCKED", label: "Blocked", icon: <XCircle className="h-4 w-4 text-red-500" /> }
  ];

  // Fetch subtask details
  const { loading, error, data, refetch } = useQuery(GET_SUBTASK_BY_ID, {
    variables: { subtaskId: id },
    client: taskServiceClient,
    fetchPolicy: "network-only",
  });

  // Mutations
  const [assignMember] = useMutation(ASSIGN_MEMBER_TO_SUBTASK, {
    client: taskServiceClient,
    onCompleted: () => refetch()
  });

  const [unassignMember] = useMutation(UNASSIGN_MEMBER_TO_SUBTASK, {
    client: taskServiceClient,
    onCompleted: () => refetch()
  });

  const [changeStatus] = useMutation(CHANGE_SUBTASK_STATUS, {
    client: taskServiceClient,
    onCompleted: () => refetch()
  });

  // Process subtask data when received
  useEffect(() => {
    if (data && data.getSubtaskById) {
      setSubtaskDetails(data.getSubtaskById);
      
      // Get assigned members
      if (data.getSubtaskById.assignedMembers && data.getSubtaskById.assignedMembers.length > 0) {
        const fetchMemberDetails = async () => {
          try {
            const memberPromises = data.getSubtaskById.assignedMembers.map(memberId => 
              getUserDetails(memberId)
            );
            const memberDetails = await Promise.all(memberPromises);
            setAssignedMembers(memberDetails.filter(member => member !== null));
          } catch (error) {
            console.error("Error fetching member details:", error);
          }
        };
        
        fetchMemberDetails();
      }
    }
  }, [data]);

  // Fetch available team members who aren't already assigned
  const fetchAvailableMembers = async () => {
    try {
      // This is a placeholder - replace with your actual API call to get team members
      const teamMembers = await getUserDetails(user.teamId);
      const filteredMembers = teamMembers.filter(
        member => !assignedMembers.some(assigned => assigned.id === member.id)
      );
      setAvailableMembers(filteredMembers);
    } catch (error) {
      console.error("Error fetching available members:", error);
    }
  };

  const handleAssignMember = async () => {
    if (!selectedMember) return;
    
    try {
      await assignMember({
        variables: {
          subtaskId: id,
          userId: selectedMember
        }
      });
      setSelectedMember("");
      setIsAssigningMember(false);
    } catch (error) {
      console.error("Error assigning member:", error);
    }
  };

  const handleUnassignMember = async (userId) => {
    try {
      await unassignMember({
        variables: {
          subtaskId: id,
          userId: userId
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
          subtaskId: id,
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
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  };

  if (loading) return <div className="flex justify-center items-center h-full">Loading subtask details...</div>;
  if (error) return <div className="text-red-500 p-4">Error loading subtask: {error.message}</div>;
  if (!subtaskDetails) return <div className="p-4">No subtask found with the given ID.</div>;

  return (
    <div className="p-4 max-w-7xl mx-auto">
      {/* Subtask Header */}
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 mb-6">
        <div className="flex flex-col md:flex-row md:items-center justify-between">
          <div className="mb-4 md:mb-0">
            <h1 className="text-2xl font-bold text-gray-900 dark:text-white">
              {subtaskDetails.title}
            </h1>
            <p className="text-gray-600 dark:text-gray-300 mt-2">
              {subtaskDetails.description}
            </p>
          </div>
          
          {/* Status Dropdown */}
          <div className="flex items-center">
            <div className="mr-2 text-sm font-medium text-gray-700 dark:text-gray-300">Status:</div>
            <div className="relative">
              <select 
                value={subtaskDetails.status}
                onChange={(e) => handleStatusChange(e.target.value)}
                className="block appearance-none bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-md py-2 px-4 pr-8 shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
              >
                {statusOptions.map(option => (
                  <option key={option.value} value={option.value}>{option.label}</option>
                ))}
              </select>
              <div className="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700 dark:text-gray-300">
                <svg className="fill-current h-4 w-4" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20">
                  <path d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" />
                </svg>
              </div>
            </div>
          </div>
        </div>
        
        {/* Subtask Metadata */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-6">
          <div className="flex items-center">
            <Calendar className="h-5 w-5 text-gray-500 dark:text-gray-400 mr-2" />
            <div>
              <div className="text-sm font-medium text-gray-700 dark:text-gray-300">Due Date</div>
              <div className="text-sm text-gray-600 dark:text-gray-400">{formatDate(subtaskDetails.dueDate)}</div>
            </div>
          </div>
          
          <div className="flex items-center">
            <Clock className="h-5 w-5 text-gray-500 dark:text-gray-400 mr-2" />
            <div>
              <div className="text-sm font-medium text-gray-700 dark:text-gray-300">Created At</div>
              <div className="text-sm text-gray-600 dark:text-gray-400">{formatDate(subtaskDetails.createdAt)}</div>
            </div>
          </div>
          
          <div className="flex items-center">
            <User className="h-5 w-5 text-gray-500 dark:text-gray-400 mr-2" />
            <div>
              <div className="text-sm font-medium text-gray-700 dark:text-gray-300">Created By</div>
              <div className="text-sm text-gray-600 dark:text-gray-400">{subtaskDetails.createdBy || "Unknown"}</div>
            </div>
          </div>
        </div>
      </div>
      
      {/* Assigned Members Section */}
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 mb-6">
        <div className="flex flex-col md:flex-row md:items-center justify-between mb-4">
          <h2 className="text-lg font-semibold text-gray-900 dark:text-white">Assigned Members</h2>
          <button 
            onClick={() => {
              setIsAssigningMember(true);
              fetchAvailableMembers();
            }}
            className="mt-2 md:mt-0 flex items-center px-3 py-2 bg-blue-600 text-white text-sm rounded-md hover:bg-blue-700 transition"
          >
            <Plus className="h-4 w-4 mr-1" />
            Assign Member
          </button>
        </div>
        
        {/* Member Assignment Modal */}
        {isAssigningMember && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-lg max-w-md w-full">
              <h3 className="text-lg font-semibold mb-4 text-gray-900 dark:text-white">Assign Member</h3>
              <select
                value={selectedMember}
                onChange={(e) => setSelectedMember(e.target.value)}
                className="block w-full bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-md py-2 px-3 mb-4 shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">Select a member</option>
                {availableMembers.map(member => (
                  <option key={member.id} value={member.id}>
                    {member.name || member.email}
                  </option>
                ))}
              </select>
              <div className="flex justify-end space-x-2">
                <button
                  onClick={() => setIsAssigningMember(false)}
                  className="px-4 py-2 bg-gray-200 dark:bg-gray-700 text-gray-800 dark:text-gray-200 rounded-md hover:bg-gray-300 dark:hover:bg-gray-600 transition"
                >
                  Cancel
                </button>
                <button
                  onClick={handleAssignMember}
                  className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition"
                >
                  Assign
                </button>
              </div>
            </div>
          </div>
        )}
        
        {/* Members List */}
        <div className="space-y-4">
          {assignedMembers.length === 0 ? (
            <p className="text-gray-600 dark:text-gray-400 italic">No members assigned yet.</p>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {assignedMembers.map(member => (
                <div 
                  key={member.id} 
                  className="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-700 rounded-md"
                >
                  <div className="flex items-center">
                    <div className="h-8 w-8 rounded-full bg-blue-100 dark:bg-blue-800 flex items-center justify-center text-blue-600 dark:text-blue-200">
                      {member.name ? member.name.charAt(0).toUpperCase() : member.email.charAt(0).toUpperCase()}
                    </div>
                    <div className="ml-3">
                      <div className="text-sm font-medium text-gray-900 dark:text-white">
                        {member.name || "Unnamed User"}
                      </div>
                      <div className="text-xs text-gray-500 dark:text-gray-400">
                        {member.email}
                      </div>
                    </div>
                  </div>
                  <button
                    onClick={() => handleUnassignMember(member.id)}
                    className="text-red-500 hover:text-red-700 transition"
                    title="Remove member"
                  >
                    <UserMinus className="h-5 w-5" />
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
      
      {/* Additional Subtask Information */}
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
        <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">Bug Status Details</h2>
        
        {/* Bug Status Information */}
        <div className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="p-4 border border-gray-200 dark:border-gray-700 rounded-md">
              <div className="text-sm font-medium text-gray-700 dark:text-gray-300">Priority</div>
              <div className="mt-1 flex items-center">
                {subtaskDetails.priority === "HIGH" && (
                  <span className="px-2 py-1 text-xs font-medium bg-red-100 text-red-800 rounded-full">High</span>
                )}
                {subtaskDetails.priority === "MEDIUM" && (
                  <span className="px-2 py-1 text-xs font-medium bg-yellow-100 text-yellow-800 rounded-full">Medium</span>
                )}
                {subtaskDetails.priority === "LOW" && (
                  <span className="px-2 py-1 text-xs font-medium bg-green-100 text-green-800 rounded-full">Low</span>
                )}
              </div>
            </div>
            
            <div className="p-4 border border-gray-200 dark:border-gray-700 rounded-md">
              <div className="text-sm font-medium text-gray-700 dark:text-gray-300">Current Status</div>
              <div className="mt-1 flex items-center">
                {statusOptions.find(option => option.value === subtaskDetails.status)?.icon}
                <span className="ml-2 text-gray-800 dark:text-gray-200">
                  {statusOptions.find(option => option.value === subtaskDetails.status)?.label}
                </span>
              </div>
            </div>
          </div>
          
          {/* Progress/Timeline */}
          <div className="p-4 border border-gray-200 dark:border-gray-700 rounded-md">
            <div className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Progress Timeline</div>
            <div className="relative">
              <div className="overflow-hidden h-2 mb-4 text-xs flex rounded bg-gray-200 dark:bg-gray-700">
                <div 
                  style={{ 
                    width: subtaskDetails.status === "COMPLETED" ? "100%" : 
                           subtaskDetails.status === "IN_PROGRESS" ? "50%" : 
                           subtaskDetails.status === "BLOCKED" ? "25%" : "10%"
                  }} 
                  className={`shadow-none flex flex-col text-center whitespace-nowrap text-white justify-center ${
                    subtaskDetails.status === "COMPLETED" ? "bg-green-500" : 
                    subtaskDetails.status === "IN_PROGRESS" ? "bg-blue-500" : 
                    subtaskDetails.status === "BLOCKED" ? "bg-red-500" : "bg-yellow-500"
                  }`}
                ></div>
              </div>
              <div className="flex justify-between text-xs text-gray-600 dark:text-gray-400">
                <span>Created</span>
                <span>In Progress</span>
                <span>Completed</span>
              </div>
            </div>
          </div>
          
          {/* Notes/Description */}
          <div className="p-4 border border-gray-200 dark:border-gray-700 rounded-md">
            <div className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Bug Description</div>
            <p className="text-gray-600 dark:text-gray-400 whitespace-pre-line">
              {subtaskDetails.bugDescription || subtaskDetails.description || "No detailed description available."}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default subtaskDetails;