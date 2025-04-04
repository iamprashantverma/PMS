import React, { useState, useEffect, useRef } from 'react';
import { X, Send, Plus, GitBranch, GitCommit, Settings, ChevronDown, Delete, Trash } from 'lucide-react';
import { useParams } from 'react-router-dom';
import { useMutation, useQuery, useSubscription } from '@apollo/client';
import { GET_TASK_BY_ID } from '@/graphql/Queries/task-service';
import { useApolloClients } from '@/graphql/Clients/ApolloClientContext';
import { SUBSCRIBE_TO_COMMENT_UPDATES } from '@/graphql/Subscription/task-service';
import { ADD_COMMENT, DELETE_COMMENT, ASSIGN_MEMBER_TO_TASK, UNASSIGN_MEMBER_TO_TASK } from '@/graphql/Mutation/task-service';
import { useAuth } from '@/context/AuthContext';
import { getUserDetails } from '@/services/UserService';
import { GET_EPIC_BY_ID, GET_STORY_BY_ID } from '@/graphql/Queries/task-service';

function TaskDetails({ task = {}, onClose }) {
  const taskId = task?.id;
  const { taskClient } = useApolloClients();
  const { user } = useAuth();
  const userId = user.userId;
  const commentsContainerRef = useRef(null);
  const { accessToken } = useAuth();

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
  const [submittingComment, setSubmittingComment] = useState(false);
  
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
      // Update local state with the new assignee
      const updatedAssignees = Array.isArray(taskData.assignees) 
        ? [...taskData.assignees, userId]
        : taskData.assignees 
          ? [taskData.assignees, userId] 
          : [userId];
      
      setTaskData(prev => ({
        ...prev,
        assignees: updatedAssignees
      }));
    },
    onError: (error) => {
      console.error("Error assigning member:", error);
    }
  });

  const [unassignMemberFromTask] = useMutation(UNASSIGN_MEMBER_TO_TASK, {
    client: taskClient,
    onCompleted: (data) => {
      // Remove the unassigned member from local state
      const updatedAssignees = Array.isArray(taskData.assignees)
        ? taskData.assignees.filter(id => id !== data.unassignMemberFromTask)
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
      if (subscriptionData.data?.subscribeToCommentUpdates) {
        const comment = subscriptionData.data.subscribeToCommentUpdates;
        if (comment.taskId !== taskId) return;
        
        setTaskData(prev => ({
          ...prev,
          comments: [...(prev.comments || []), comment]
        }));
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
    assignMemberToTask({
      variables: {
        taskId,
        memberId: userId
      }
    });
  };

  const handleUnassign = (memberId) => {
    unassignMemberFromTask({
      variables: {
        taskId,
        memberId: memberId || userId
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
            <span className="badge bg-purple-600 text-white px-2 py-1 rounded text-sm font-medium">{parentInfo?.id || ' '}</span>
            {parentInfo && (
              <>
                <span className="divider text-gray-400">/</span>
                <span className="badge bg-blue-500 text-white px-2 py-1 rounded text-sm font-medium">{taskData.id }</span>
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
          <button className="icon-btn p-2 hover:bg-gray-100 rounded-full transition-colors" onClick={onClose}>
            <X size={18} />
          </button>
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
                                {getInitials(comment.author?.name || "User")}
                              </div>
                              <span className="font-medium text-sm">{comment.author?.name || "User"}</span>
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
                <div className="status-section mb-4">
                  <div className="status-dropdown flex items-center justify-between px-3 py-2 bg-gray-100 hover:bg-gray-200 rounded cursor-pointer transition-colors">
                    <div className="flex items-center">
                      <span className={`w-2 h-2 rounded-full ${
                        taskData.status === 'To Do' ? 'bg-gray-500' : 
                        taskData.status === 'In Progress' ? 'bg-blue-500' : 
                        taskData.status === 'Done' ? 'bg-green-500' : 'bg-purple-500'
                      } mr-2`}></span>
                      <span className="text-sm">{taskData.status || 'To Do'}</span>
                    </div>
                    <ChevronDown size={14} />
                  </div>
                </div>
                
                <div className="assignee-section mb-4">
                  <label className="block text-sm text-gray-600 mb-2">
                    Assignee{assigneeDetails.length > 1 ? 's' : ''}
                  </label>
                  {assigneeDetails.length > 0 ? (
                    <div className="flex flex-col space-y-2">
                      {assigneeDetails.map((assignee, index) => (
                        <div key={assignee.id || index} className="flex items-center justify-between group">
                          <div className="flex items-center">
                            <div className="w-6 h-6 rounded-full bg-blue-500 mr-2 flex items-center justify-center text-white text-xs">
                              {getInitials(assignee.name || "User")}
                            </div>
                            <span className="text-sm">{assignee.name || 'Assigned User'}</span>
                          </div>
                          <button 
                            onClick={() => handleUnassign(assignee.id)}
                            className="text-gray-400 opacity-0 group-hover:opacity-100 hover:text-red-500 transition-all"
                          >
                            <X size={14} />
                          </button>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <div className="flex items-center">
                      <div className="w-6 h-6 rounded-full bg-gray-200 mr-2 flex items-center justify-center">
                        <span role="img" aria-label="unassigned" className="text-xs">?</span>
                      </div>
                      <span className="text-sm text-gray-500">Unassigned</span>
                    </div>
                  )}
                  <button 
                    className="text-blue-500 text-sm mt-2 hover:underline"
                    onClick={handleAssignToMe}
                  >
                    Assign to me
                  </button>
                </div>
                
                <div className="labels-section mb-4">
                  <label className="block text-sm text-gray-600 mb-2">Label</label>
                  <div className="flex items-center">
                    <span className="text-sm text-gray-500">{taskData.label || "NA"}</span>
                  </div>
                </div>
                
                <div className="parent-section mb-4">
                  <label className="block text-sm text-gray-600 mb-2">Parent</label>
                  {parentInfo ? (
                    <div className="flex items-center bg-gray-50 p-2 rounded">
                      <span className="badge bg-purple-600 text-white px-2 py-0.5 rounded text-xs mr-2">
                        {parentInfo.id}
                      </span>
                      <span className="text-sm truncate">{parentInfo.title}</span>
                      <span
                          className={`text-sm ml-[20px] truncate ${
                            taskData?.storyId ? "text-blue-500" : taskData?.epicId ? "text-green-500" : ""
                          }`}
                        >
                          {taskData?.storyId ? "Story" : taskData?.epicId ? "Epic" : ""}
                        </span>

                    </div>
                  ) : (
                    <span className="text-sm text-gray-500">None</span>
                  )}
                </div>
                
                <div className="team-section mb-4">
                  <label className="block text-sm text-gray-600 mb-2">Reporter</label>
                  <div className="flex items-center">
                    {reporterData ? (
                      <div className="flex items-center">
                        <div className="w-6 h-6 rounded-full bg-green-500 mr-2 flex items-center justify-center text-white text-xs">
                          {getInitials(reporterData.data.name || "Reporter")}
                        </div>
                        <span className="text-sm">{reporterData?.data?.name}</span>
                      </div>
                    ) : (
                      <span className="text-sm text-gray-500">
                        {taskData?.reporter || "Not assigned"}
                      </span>
                    )}
                  </div>
                </div>
                
                <div className="development-section mb-4">
                  <label className="block text-sm text-gray-600 mb-2">Development</label>
                  <div className="dev-actions space-y-2">
                    <button className="text-blue-500 flex items-center justify-between p-2 bg-blue-50 hover:bg-blue-100 rounded transition-colors w-full">
                      <div className="flex items-center">
                        <GitBranch size={14} className="mr-2" />
                        <span className="text-sm">Create branch</span>
                      </div>
                      <ChevronDown size={14} />
                    </button>
                    <button className="text-blue-500 flex items-center justify-between p-2 bg-blue-50 hover:bg-blue-100 rounded transition-colors w-full">
                      <div className="flex items-center">
                        <GitCommit size={14} className="mr-2" />
                        <span className="text-sm">Create commit</span>
                      </div>
                      <ChevronDown size={14} />
                    </button>
                  </div>
                </div>
                
                <div className="actions-section mb-4">
                  <div className="actions-dropdown flex items-center justify-between px-3 py-2 bg-gray-100 hover:bg-gray-200 rounded cursor-pointer transition-colors">
                    <span className="text-sm">Actions</span>
                    <ChevronDown size={14} />
                  </div>
                  <button className="improve-issue-btn px-3 py-2 w-full flex items-center justify-center bg-gray-50 hover:bg-gray-100 border rounded mt-2 transition-colors">
                    <Settings size={14} className="mr-2" />
                    <span className="text-sm">Improve issue</span>
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