import React, { useState, useEffect, useRef } from 'react';
import { X, Send, Plus, GitBranch, GitCommit, Settings, ChevronDown, Delete, Trash } from 'lucide-react';
import { useParams } from 'react-router-dom';
import { useMutation, useQuery, useSubscription } from '@apollo/client';

import { useApolloClients } from '@/graphql/Clients/ApolloClientContext';
import { SUBSCRIBE_TO_COMMENT_UPDATES } from '@/graphql/Subscription/task-service';
import { GET_TASK_BY_ID,GET_EPIC_BY_ID,GET_STORY_BY_ID   } from '@/graphql/Queries/task-service';
import { ADD_COMMENT,DELETE_COMMENT,CHANGE_TASK_STATUS,ASSIGN_MEMBER_TO_TASK, UNASSIGN_MEMBER_TO_TASK  } from '@/graphql/Mutation/task-service';
import { useAuth } from '@/context/AuthContext';
import { getUserDetails } from '@/services/UserService';

import { toast } from 'react-toastify';

function TaskDetails({ task = {}, onClose }) {
  
  const { taskId: paramTaskId } = useParams();
  const taskId = task?.id || paramTaskId;

  const { taskClient,projectClient } = useApolloClients();
  const { user } = useAuth();
  const userId = user.userId;
  const commentsContainerRef = useRef(null);
  const { accessToken } = useAuth();

  const { data: fetchedTaskData, loading, error } = useQuery(GET_TASK_BY_ID, {
    variables: { taskId },
    client: taskClient,
    skip: !taskId, 
  });

  task = fetchedTaskData?.getTaskById || task;
  const [taskData, setTaskData] = useState(task || {
    id: 'sample-123',
    title: '(Sample) Develop Frontend Interface',
    description: 'The frontend interface should be user-friendly and responsive, allowing users to interact with the chatbot seamlessly.',
    status: 'To Do',
    assignee: null,
    labels: [],
    parent: { id: 'CCS-7', title: 'CCS-7 this is' },
    team: null,
    comments: []
  });
  const [epicData, setEpicData] = useState(null);
  const [storyData, setStoryData] = useState(null);
  const [reporterData, setReporterData] = useState(null);
  const [assigneeDetails, setAssigneeDetails] = useState([]);
  const [commentInput, setCommentInput] = useState('');
  const [activeTab, setActiveTab] = useState('All');
  const [showDetails, setShowDetails] = useState(true);
  const [showActivity, setShowActivity] = useState(true);
  const [newAssignee, setNewAssignee] = useState("");
  const [submittingComment, setSubmittingComment] = useState(false);
  const [statusDropdownOpen, setStatusDropdownOpen] = useState(false);
    
  const STATUS_OPTIONS = [
    "TODO",
    "IN_PLANNED",
    "IN_PROGRESS",
    "COMPLETED",
    "IN_QA",
    "DELIVERED",
   
  ];

  const [changeTaskStatus] = useMutation(CHANGE_TASK_STATUS, {
    client: taskClient,
    onCompleted: (data) => {
      if (data?.changeTaskStatus) {
        setTaskData(prev => ({
          ...prev,
          status: data.changeTaskStatus.status
        }));
        toast.success("Task status updated successfully!");
      }
    },
    onError: (error) => {
      console.error("Error changing task status:", error);
      toast.error("Failed to update task status");
    }
  });

  const handleStatusChange = (newStatus) => {
    changeTaskStatus({
      variables: {
        taskId,
        status: newStatus
      }
    });
    setStatusDropdownOpen(false);
  };

  const getStatusColor = (status) => {
    switch(status?.toUpperCase()) {
      case 'IN_PROGRESS':
        return 'bg-blue-100 text-blue-800';
      case 'COMPLETED':
        return 'bg-green-100 text-green-800';
      case 'DELIVERED':
        return 'bg-purple-100 text-purple-800';
      case 'IN_QA':
        return 'bg-yellow-100 text-yellow-800';
      case 'ON_HOLD':
        return 'bg-orange-100 text-orange-800';
      case 'CANCELED':
        return 'bg-red-100 text-red-800';
      case 'ARCHIVED':
        return 'bg-gray-100 text-gray-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusDisplayName = (status) => {
    return status?.replace(/_/g, ' ') || 'To Do';
  };

  const { loading: taskLoading, error: taskError, data: taskQueryData } = useQuery(GET_TASK_BY_ID, {
    variables: { taskId },
    client: taskClient,
    skip: !taskId,
    fetchPolicy: 'network-only'
  });

  // Epic query if epicId exists
  const { loading: epicLoading, data: epicQueryData } = useQuery(GET_EPIC_BY_ID, {
    variables: { epicId: taskData?.epicId },
    client: taskClient,
    skip: !taskData?.epicId,
    onCompleted: (data) => {
      if (data?.getEpicById) {
        setEpicData(data.getEpicById);
      }
    }
  });
  
  // Story query if storyId exists
  const { loading: storyLoading, data: storyQueryData } = useQuery(GET_STORY_BY_ID, {
    variables: { storyId: taskData?.storyId },
    client: taskClient,
    skip: !taskData?.storyId,
    onCompleted: (data) => {
      if (data?.getStoryById) {
        setStoryData(data.getStoryById);
      }
    }
  });
  
  // Fetch reporter data if reporterId exists
  useEffect(() => {
    const fetchReporterData = async () => {
      if (taskData?.reporter) {
        try {
          const reporterDetails = await getUserDetails(taskData.reporter, accessToken);
          setReporterData(reporterDetails);
        } catch (error) {
          console.error("Error fetching reporter details:", error);
        }
      }
    };
    
    fetchReporterData();
  }, [taskData?.reporter, accessToken]);

  // Fetch assignee details
  useEffect(() => {
    const fetchAssigneeDetails = async () => {
      if (taskData.assignees && Array.isArray(taskData.assignees) && taskData.assignees.length > 0) {
        try {
          const assigneePromises = taskData.assignees.map(assigneeId => 
            getUserDetails(assigneeId, accessToken)
          );

          const assigneeData = await Promise.all(assigneePromises);
          // Extract only the `data` from each response
          const extractedData = assigneeData.map(response => response.data);

          setAssigneeDetails(extractedData);

        } catch (error) {
          console.error("Error fetching assignee details:", error);
        }
      } else if (taskData.assignees && !Array.isArray(taskData.assignees)) {
        // Handle case where assignees is a single ID instead of an array
        try {
          const assigneeData = await getUserDetails(taskData.assignees, accessToken);
          setAssigneeDetails([assigneeData]);
        } catch (error) {
          console.error("Error fetching assignee details:", error);
        }
      } else {
        setAssigneeDetails([]);
      }
    };
    
    fetchAssigneeDetails();
  }, [taskData.assignees, accessToken]);

  const [addComment] = useMutation(ADD_COMMENT, {
    client: taskClient,
    onCompleted: (data) => {
      setCommentInput('');
      setSubmittingComment(false);
    },
    onError: (error) => {
      console.error("Error adding comment:", error);
      setSubmittingComment(false);
    }
  });
  
  const [deleteComment] = useMutation(DELETE_COMMENT, {
    client: taskClient,
    onCompleted: (data) => {
      if (data?.deleteComment) {
        setTaskData(prev => ({
          ...prev,
          comments: prev.comments.filter(c => c.commentId !== data.deleteComment)
        }));
      }
    },
    onError: (error) => {
      console.error("Error deleting comment:", error);
    }
  });

  const [assignMemberToTask] = useMutation(ASSIGN_MEMBER_TO_TASK, {
    client: taskClient,
    onCompleted: (data) => {
      if (data && data.assignMemberToTask) {
        
        setTaskData(prev => ({
          ...prev,
          assignees: data.assignMemberToTask.assignees 
        }));
      } else {
        console.error("No assignees data found in response:", data);
      }
    },
    onError: (error) => {
      console.error("Error assigning member:", error);
    }
  });
  

  const [unassignMemberFromTask] = useMutation(UNASSIGN_MEMBER_TO_TASK, {
    client: taskClient,
    onCompleted: (data) => {
      const updatedAssignees = Array.isArray(taskData.assignees)
        ? taskData.assignees.filter(id => id !== data.id)
        : null;
      
      setTaskData(prev => ({
        ...prev,
        assignees: updatedAssignees
      }));
    },
    onError: (error) => {
      console.error("Error unassigning member:", error);
    }
  });

  const { data: commentSubscriptionData, error: subsError } = useSubscription(SUBSCRIBE_TO_COMMENT_UPDATES, {
    variables: { taskId },
    client: taskClient,
    skip: !taskId,
    onSubscriptionData: ({ subscriptionData }) => {
      const handleNewComment = async (comment) => {
        if (comment.taskId !== taskId) return;
  
        try {
          const {data} = await getUserDetails(comment.userId, accessToken);
          setTaskData(prev => ({
            ...prev,
            comments: [...(prev.comments || []), { ...comment, name: data.name }]
          }));
        } catch (error) {
          // console.error("Error fetching user details:", error);
        }
      };
  
      if (subscriptionData.data?.subscribeToCommentUpdates) {
        const comment = subscriptionData.data.subscribeToCommentUpdates;
        handleNewComment(comment);
      }
    }
  });

  useEffect(() => {
    if (taskQueryData?.getTaskById) {
      setTaskData(taskQueryData.getTaskById);
    }
  }, [taskQueryData]);

  useEffect(() => {
    if (commentsContainerRef.current) {
      commentsContainerRef.current.scrollTop = commentsContainerRef.current.scrollHeight;
    }
  }, [taskData.comments]);

  const getInitials = (name) => {
    return name ? name.charAt(0).toUpperCase() : '';
  };

  const handleCommentChange = (e) => {
    setCommentInput(e.target.value);
  };

  const handleQuickResponse = (text) => {
    setCommentInput(text);
  };

  const handleSubmitComment = (e) => {
    e.preventDefault();
    if (!commentInput.trim()) return;
    setSubmittingComment(true);
    addComment({
      variables: {
        taskId,
        userId: userId,
        message: commentInput.trim()
      }
    });
  };

  const handleDeleteComment = (commentId) => {
    const commentIdInt = parseInt(commentId, 10); 
  
    if (isNaN(commentIdInt)) {
      console.error("Invalid commentId:", commentId);
      return;
    }
    deleteComment({
      variables: { commentId: commentIdInt } 
    });
  };

  const handleAssignToMe = () => {
    const trimmedAssignee = newAssignee.trim();
    if (!trimmedAssignee) {
      toast.error("Assignee name cannot be empty!");
      return; 
    }
    assignMemberToTask({
      variables: {
        taskId,
        memberId: trimmedAssignee
      }
    })
    setNewAssignee("");
  };
  
  

  const handleUnassign = (memberId) => {
    unassignMemberFromTask({
      variables: {
        taskId,
        memberId: memberId.userId 
      },
      onCompleted:(d)=>{
      }
    });
  };

  const toggleDetailsSection = () => {
    setShowDetails(!showDetails);
  };

  const toggleActivitySection = () => {
    setShowActivity(!showActivity);
  };

  // Get parent information (epic or story)
  const getParentInfo = () => {
    if (taskData.epicId && epicData) {
      return {
        id: taskData.epicId,
        title: epicData.title || "Epic"
      };
    } else if (taskData.storyId && storyData) {
      return {
        id: taskData.storyId,
        title: storyData.title || "Story"
      };
    } else if (taskData.parent) {
      return taskData.parent;
    }
    return null;
  };

  const parentInfo = getParentInfo();
  
  return (
    <div className="task-details-container flex flex-col h-full bg-gray-50">
      {/* Header */}
      <div className="task-header flex items-center justify-between p-4 border-b bg-white shadow-sm sticky top-0 z-10">
        <div className="task-identifiers flex items-center">
          <div className="badge-container flex items-center gap-2">
            <span className="badge bg-purple-600 text-white px-2 py-1 rounded text-sm font-medium">{parentInfo?.id || 'None '}</span>
            {parentInfo && (
              <>
                <span className="divider text-gray-400">/</span>
                <span className="badge bg-blue-500 text-white px-2 py-1 rounded text-sm font-medium">{taskData?.id }</span>
              </>
            )}
          </div>
        </div>
        <div className="task-actions flex items-center gap-3">
          <button className="icon-btn hover:bg-gray-100 p-1 rounded">
            <span role="img" aria-label="lock">🔒</span>
          </button>
          <button className="icon-btn hover:bg-gray-100 p-1 rounded flex items-center">
            <span role="img" aria-label="watch" className="mr-1">👁️</span> 
            <span className="text-xs">1</span>
          </button>
          <button className="icon-btn hover:bg-gray-100 p-1 rounded">
            <span role="img" aria-label="like">👍</span>
          </button>
          <button className="icon-btn hover:bg-gray-100 p-1 rounded">
            <span role="img" aria-label="share">🔗</span>
          </button>
          <button className="icon-btn hover:bg-gray-100 p-1 rounded">
            <Settings size={16} />
          </button>
          {!paramTaskId && <button className="icon-btn p-2 hover:bg-gray-100 rounded-full transition-colors" onClick={onClose}>
            <X size={18} />
          </button>}
        </div>
      </div>

      {/* Task Content */}
      <div className="task-content-container flex flex-1 overflow-hidden">
        <div className="task-content flex-1 p-4 overflow-y-auto bg-white shadow-sm m-2 rounded-lg">
          <h1 className="text-xl font-semibold mb-6">{taskData.title}</h1>
          
          <div className="task-description mb-8">
            <h2 className="text-base font-semibold mb-2">Description</h2>
            <p className="text-gray-700">{taskData.description}</p>
          </div>
          
          <div className="task-activity mt-8">
            <div className="activity-header flex items-center justify-between mb-4">
              <div className="flex items-center">
                <h2 className="text-base font-semibold">Activity</h2>
                <button 
                  className="ml-2 p-1 rounded-full hover:bg-gray-100"
                  onClick={toggleActivitySection}
                >
                  <span role="img" aria-label="collapse" className="inline-block transform transition-transform">
                    {showActivity ? '▾' : '▸'}
                  </span>
                </button>
              </div>
              {showActivity && (
                <div className="activity-filters flex items-center">
                  <span className="mr-2 text-sm text-gray-600">Show:</span>
                  <div className="tabs flex bg-gray-100 rounded">
                    {['All', 'Comments', 'History', 'Work log'].map(tab => (
                      <button 
                        key={tab}
                        className={`px-3 py-1 text-sm transition-all ${activeTab === tab ? 'bg-white rounded shadow text-blue-600' : 'text-gray-600'}`}
                        onClick={() => setActiveTab(tab)}
                      >
                        {tab}
                      </button>
                    ))}
                  </div>
                </div>
              )}
            </div>
            
            {showActivity && (
              <>
                <div className="comment-input-container border rounded-lg shadow-sm mb-4">
                  <form onSubmit={handleSubmitComment}>
                    <div className="comment-input-wrapper flex p-4">
                      <div className="user-avatar mr-4">
                        <div className="w-8 h-8 rounded-full bg-orange-500 flex items-center justify-center text-white">
                          {getInitials(user.name)}
                        </div>
                      </div>
                      <div className="comment-input-area flex-1">
                        <div className="flex">
                          <input 
                            type="text" 
                            placeholder="Add a comment..." 
                            className="w-full p-2 mb-2 border rounded-l focus:outline-none focus:ring-2 focus:ring-blue-300"
                            value={commentInput}
                            onChange={handleCommentChange}
                          />
                          <button 
                            type="submit"
                            disabled={submittingComment || !commentInput.trim()}
                            className={`p-2 border-l-0 rounded-r ${submittingComment || !commentInput.trim() ? 'bg-gray-200 text-gray-400' : 'bg-blue-500 text-white hover:bg-blue-600'} transition-colors`}
                          >
                            <Send size={16} />
                          </button>
                        </div>
                        <div className="quick-responses flex gap-2 mt-1">
                          {["Who is working on this?", "Status update:", "Thanks!"].map(text => (
                            <button
                              key={text}
                              type="button"
                              className="px-3 py-1 bg-gray-100 hover:bg-gray-200 rounded text-sm transition-colors"
                              onClick={() => handleQuickResponse(text)}
                            >
                              {text}
                            </button>
                          ))}
                        </div>
                      </div>
                    </div>
                  </form>
                  <div className="comment-tips p-2 text-xs text-gray-500 border-t flex justify-between bg-gray-50 rounded-b-lg">
                    <span>Pro tip: press <kbd className="px-1 py-0.5 bg-gray-200 rounded">M</kbd> to comment</span>
                    <button>
                      <ChevronDown size={12} />
                    </button>
                  </div>
                </div>
                
                <div ref={commentsContainerRef} className="comments-container max-h-96 overflow-y-auto pr-2">
                  {taskData.comments?.length > 0 ? (
                    <div className="comments-list space-y-4">
                      {taskData.comments.map(comment => (
                        <div key={comment.id || comment.commentId} className="comment-item p-3 bg-gray-50 rounded-lg">
                          <div className="comment-header flex items-center justify-between mb-2">
                            <div className="flex items-center">
                              <div className="w-6 h-6 rounded-full bg-blue-500 flex items-center justify-center text-white text-xs mr-2">
                                {getInitials(comment?.name|| "User")}
                              </div>
                              <span className="font-medium text-sm">{comment?.name || "User"}</span>
                              <span className="text-xs text-gray-500 ml-2">
                                {new Date(comment.createdAt).toLocaleString()}
                              </span>
                            </div>
                            {comment.userId === userId && (
                              <button 
                                onClick={() => handleDeleteComment(comment.commentId)}
                                className="text-gray-400 hover:text-red-500 transition-colors"
                              >
                                <Trash size={14} />
                              </button>
                            )}
                          </div>
                          <div className="comment-content ml-8">
                            <p className="text-sm">{comment.message}</p>
                          </div>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <div className="no-comments text-center text-gray-500 py-8">
                      No comments yet
                    </div>
                  )}
                </div>
              </>
            )}
          </div>
        </div>
        
        {/* Sidebar - positioned to stay in place */}
        <div className="task-sidebar w-72 bg-white shadow-sm m-2 ml-0 rounded-lg overflow-y-auto sticky top-16">
          <div className="sidebar-section p-4">
            <div className="section-header flex items-center justify-between mb-4">
              <h3 className="font-semibold text-gray-700">Details</h3>
              <button 
                className="text-gray-500 p-1 hover:bg-gray-100 rounded-full transition-colors"
                onClick={toggleDetailsSection}
              >
                <span role="img" aria-label="collapse" className="inline-block transform transition-transform">
                  {showDetails ? '▾' : '▸'}
                </span>
              </button>
            </div>
            
            {showDetails && (
                <>
                  <div className="mb-4 relative status-dropdown-container">
                    <div className="text-sm text-gray-500 mb-1">
                      Status
                    </div>
                    <button 
                      onClick={() => setStatusDropdownOpen(!statusDropdownOpen)} 
                      className={`flex items-center justify-between w-full px-3 py-2 rounded ${getStatusColor(taskData.status)}`}
                    >
                      <span>{getStatusDisplayName(taskData.status)}</span>
                      <ChevronDown size={16} />
                    </button>
                    
                    {statusDropdownOpen && (
                      <div className="absolute w-full mt-1 bg-white border rounded-md shadow-lg z-10 max-h-60 overflow-y-auto">
                        {STATUS_OPTIONS.map((status) => (
                          <div 
                            key={status}
                            className={`px-3 py-2 hover:bg-gray-100 cursor-pointer text-sm ${taskData.status === status ? 'bg-blue-50' : ''}`}
                            onClick={() => handleStatusChange(status)}
                          >
                            <div className={`px-2 py-1 rounded inline-block ${getStatusColor(status)}`}>
                              {getStatusDisplayName(status)}
                            </div>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                  
                  <div className="mb-4">
                    <div className="text-sm text-gray-500 mb-1">
                      Assignee{assigneeDetails.length > 1 ? "s" : ""}
                    </div>

                    {assigneeDetails.length > 0 ? (
                      <div className="space-y-2">
                        {assigneeDetails.map((assignee) => (
                          <div key={assignee.userId} className="flex items-center justify-between group">
                            <div className="flex items-center">
                              <div className="w-6 h-6 rounded-full bg-blue-500 text-white flex items-center justify-center text-xs font-medium mr-2">
                                {getInitials(assignee.name)}
                              </div>
                              <span className="text-sm">{assignee.name}</span>
                            </div>
                            <button 
                              onClick={() => handleUnassign(assignee)}
                              className="text-gray-400 opacity-0 group-hover:opacity-100 hover:text-red-500 transition-all"
                            >
                              <X size={14} />
                            </button>
                          </div>
                        ))}
                      </div>
                    ) : (
                      <div className="flex items-center text-sm text-gray-500">
                        <div className="w-6 h-6 rounded-full bg-gray-200 flex items-center justify-center mr-2">
                          ?
                        </div>
                        Unassigned
                      </div>
                    )}

                    {/* Input to add new user */}
                    <div className="mt-2 flex space-x-2">
                      <input
                      value={newAssignee}
                      onChange={(e) => setNewAssignee(e.target.value)}
                      className="border rounded px-2 py-0.5 text-sm w-full h-8"
                      placeholder="Enter assignee id"
                    />
                      <button 
                        onClick={handleAssignToMe}
                        className="bg-blue-600 text-white px-2 py-1 rounded text-sm hover:bg-blue-700"
                      >
                        Add 
                      </button>
                    </div>

                    {/* Assign to me button */}
                    <button 
                      onClick={() => setNewAssignee(userId)}
                      className="mt-2 w-full bg-gray-200 hover:bg-gray-300 text-gray-700 px-3 py-1 rounded text-sm"
                    >
                      Assign to me
                    </button>
                  </div>

                  <div className="mb-4">
                    <div className="text-sm text-gray-500 mb-1">Label</div>
                    <div className="text-sm">
                      {taskData.label || "NA"}
                    </div>
                  </div>
                  
                  <div className="mb-4">
                    <div className="text-sm text-gray-500 mb-1">Parent</div>
                    {parentInfo ? (
                      <div className="flex items-center text-sm">
                        <span className="bg-purple-100 text-purple-800 px-2 py-1 rounded text-xs mr-2">
                          {parentInfo.id}
                        </span>
                        <span>{parentInfo.title}</span>
                        <span className="ml-1 text-xs text-gray-500">
                          {taskData?.storyId ? "Story" : taskData?.epicId ? "Epic" : ""}
                        </span>
                      </div>
                    ) : (
                      <span className="text-sm">None</span>
                    )}
                  </div>
                  
                  <div className="mb-4">
                    <div className="text-sm text-gray-500 mb-1">Reporter</div>
                    <div className="flex items-center">
                      {reporterData ? (
                        <div className="flex items-center">
                          <div className="w-6 h-6 rounded-full bg-green-500 text-white flex items-center justify-center text-xs font-medium mr-2">
                            {getInitials(reporterData.data.name || "Reporter")}
                          </div>
                          <span className="text-sm">{reporterData?.data?.name}</span>
                        </div>
                      ) : (
                        <span className="text-sm">
                          {taskData?.reporter || "Not assigned"}
                        </span>
                      )}
                    </div>
                  </div>
                  
                  <div className="mb-4">
                    <div className="text-sm text-gray-500 mb-1">Development</div>
                    <div className="space-y-2 mt-1">
                      <button className="flex items-center text-sm text-blue-600 hover:text-blue-800">
                        <div className="mr-1">
                          <GitBranch size={14} />
                        </div>
                        Create branch
                      </button>
                      <button className="flex items-center text-sm text-blue-600 hover:text-blue-800">
                        <div className="mr-1">
                          <GitCommit size={14} />
                        </div>
                        Create commit
                      </button>
                    </div>
                  </div>
                  
                  <div className="mb-4 p-3 bg-gray-50 rounded-lg shadow-sm">
                    <button className="flex items-center text-sm font-medium text-blue-600 hover:text-blue-800">
                      <Settings size={14} className="mr-2" />
                      {task?.createdAt ? (
                        <span>Created on: {new  Date(task?.createdAt).toLocaleDateString()}</span>
                      ) : (
                        <span className="text-gray-400 italic">Creation date not available</span>
                      )}
                    </button>
                  </div>
                </>
              )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default TaskDetails;