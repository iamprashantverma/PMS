import React, { useEffect, useState } from "react";
import { useQuery, useMutation } from "@apollo/client";
import {
  GET_ALL_EPICS,
  GET_ALL_STORIES_BY_PROJECT_ID,
  GET_ALL_EPICS_BY_PROJECT_ID,
  GET_ALL_TASKS_BY_PROJECT_ID
} from "@/graphql/Queries/task-service";
import {
  CREATE_TASK,
  CREATE_BUG,
  CREATE_EPIC,
  CREATE_STORY,
} from "@/graphql/Mutation/task-service";
import { FIND_ALL_PROJECT_BY_USER } from "@/graphql/Queries/project-service";
import { useApolloClients } from "@/graphql/Clients/ApolloClientContext";
import { useAuth } from "@/context/AuthContext";
import { FaCloudUploadAlt } from "react-icons/fa";
import toast from "react-hot-toast";

function CreateTaskForm({setCreateOpen}) {
  const { user } = useAuth();
  const { projectClient, taskClient } = useApolloClients();

  const [projects, setProjects] = useState([]);
  const [epics, setEpics] = useState([]);
  const [stories, setStories] = useState([]);

  const [selectedProject, setSelectedProject] = useState("");
  const [title, setTitle] = useState("");
  const [epicId, setEpicId] = useState("");
  const [storyId, setStoryId] = useState("");
  const [priority, setPriority] = useState("LOW");
  const [deadline, setDeadline] = useState("");
  const [label, setLabel] = useState("");
  const [reporter] = useState(user?.userId || "");
  const [description, setDescription] = useState("");
  const [issueType, setIssueType] = useState("Task");
  const [status, setStatus] = useState("TODO");
  const [image, setImage] = useState(null);
  const [expectedOutcome, setExpectedOutcome] = useState("");
  const [actualOutcome, setActualOutcome] = useState("");




  const { data: projectData } = useQuery(FIND_ALL_PROJECT_BY_USER, {
    client: projectClient,
    variables: { userId: user.userId },
    fetchPolicy: "network-only",
  });

  const { data: epicData } = useQuery(GET_ALL_EPICS_BY_PROJECT_ID, {
    client: taskClient,
    variables: { projectId: selectedProject },
    skip: !selectedProject,
    fetchPolicy: "network-only",
  });
  
  

  const { data: storyData } = useQuery(GET_ALL_STORIES_BY_PROJECT_ID, {
    client: taskClient,
    variables: { projectId: selectedProject },
    skip: !selectedProject,
    fetchPolicy: "network-only",
  });

  const [createEpic] = useMutation(CREATE_EPIC, { client: taskClient });
  const [createStory] = useMutation(CREATE_STORY, { client: taskClient });
  const [createTask] = useMutation(CREATE_TASK, { client: taskClient });
  const [createBug] = useMutation(CREATE_BUG, { client: taskClient });

  useEffect(() => {
    if (projectData?.findAllProject) setProjects(projectData.findAllProject);
  }, [projectData]);

  useEffect(() => {
    if (epicData?.getAllEpics) setEpics(epicData.getAllEpics);
  }, [epicData]);

  useEffect(() => {
    if (storyData?.getAllStoriesByProjectId) {
      setStories(storyData.getAllStoriesByProjectId);
    }
  }, [storyData]);
  useEffect(() => {
    if (epicData?.getAllEpicsByProjectId) {
      setEpics(epicData.getAllEpicsByProjectId);
    }
  }, [epicData]);
  


  useEffect(() => {
    setEpicId("");
    setStoryId("");
  }, [issueType]);

  const handleFileChange = (e) => {
    setImage(e.target.files[0]);
  };
  console.log(epicData)

  const handleCreateIssue = async () => {
    try {
      const basePayload = {
        title,
        description,
        status: status.toUpperCase(),
        priority: priority.toUpperCase(),
        label,
        deadline,
        reporter,
        projectId: selectedProject,
        createdAt: new Date().toISOString().split("T")[0],
      };

      let response;

      if (issueType === "Epic") {
        response = await createEpic({
          variables: {
            epic: basePayload,
            image,
          },
        });
      } else if (issueType === "Story") {
        response = await createStory({
          variables: {
            story: {
              ...basePayload,
              epicId,
            },
            image,
          },
        });
      } else if (issueType === "Task") {
        response = await createTask({
          variables: {
            task: {
              ...basePayload,
              epicId: epicId || null,
              storyId: storyId || null,
            },
            image,
          },
        });
      } else if (issueType === "Bug") {

        response = await createBug({
          variables: {
            bugInput: {
              ...basePayload,
              epicId: epicId || null,
              storyId: storyId || null,
              expectedOutcome,
              actualOutcome,
            },
            image,
          },
        });        
      }

      if (response?.data) {
        toast.success(`${issueType} created successfully!`);
      }
    } catch (err) {
      console.error(err);
      toast.error(`Failed to create ${issueType}`);
    }
  };

  return (
    <div className=" fixed inset-0 z-50 flex items-center justify-center bg-black/20 pt-[20px]">
      <div className="w-full max-w-2xl bg-white rounded-xl shadow-2xl font-sans text-[14px]">
        
        <div className="flex justify-between items-center p-4">
          <h3 className="text-xl font-bold text-gray-800">Create</h3>
          <button className="text-gray-400 hover:text-black text-2xl" onClick={()=>{setCreateOpen(false)}} >×</button>
        </div>

        <p className="text-xs text-gray-500 mb-6 pl-5">
          Required fields are marked with an asterisk *
        </p>

        <div className="space-y-5 pl-5 w-full scrollbar-thin scrollbar-thumb-gray-200 scrollbar-track-transparent overflow-y-auto max-h-[55vh]  scrollbar-thin scrollbar-thumb-gray-200 scrollbar-track-transparent">
          {/* Project */}
          <div>
            <label className="block font-medium mb-1 text-gray-700">Project *</label>
            <select
              className="w-1/2 border px-3 py-2 rounded focus:outline-blue-500"
              value={selectedProject}
              onChange={(e) => setSelectedProject(e.target.value)}
            >
              <option value="">Select a project</option>
              {projects.map((project) => (
                <option key={project.projectId} value={project.projectId}>
                  {project.title}
                </option>
              ))}
            </select>
          </div>

          {/* Issue Type */}
          <div>
            <label className="block font-medium mb-1 text-gray-700">Issue Type *</label>
            <select
              className="w-1/2 border px-3 py-2 rounded focus:outline-blue-500"
              value={issueType}
              onChange={(e) => setIssueType(e.target.value)}
            >
              <option>Epic</option>
              <option>Story</option>
              <option>Task</option>
              <option>Bug</option>
            </select>
          </div>

          {/* Status */}
          <div>
            <label className="block font-medium mb-1 text-gray-700">Status *</label>
            <select
              className="w-1/2 border px-3 py-2 rounded focus:outline-blue-500"
              value={status}
              onChange={(e) => setStatus(e.target.value)}
            >
              <option value="TODO">🟡 To Do</option>
              <option value="In_Progress">🟠 In Progress</option>
              <option value="Completed">✅ Completed</option>
            </select>
          </div>

          {/* Title */}
          <div>
            <label className="block font-medium mb-1 text-gray-700">Title *</label>
            <input
              type="text"
              className="w-full border px-3 py-2 rounded focus:outline-blue-500"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="Enter Title"
            />
          </div>

          {/* Description */}
          <div>
            <label className="block font-medium mb-1 text-gray-700">Description</label>
            <textarea
              className="w-full border px-3 py-2 rounded focus:outline-blue-500"
              rows="4"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Enter description"
            ></textarea>
          </div>

          {/* Label */}
          <div>
            <label className="block font-medium mb-1 text-gray-700">Label</label>
            <input
              type="text"
              className="w-full border px-3 py-2 rounded focus:outline-blue-500"
              value={label}
              onChange={(e) => setLabel(e.target.value)}
              placeholder="Enter label"
            />
          </div>

          {/* Priority */}
          <div>
            <label className="block font-medium mb-1 text-gray-700">Priority</label>
            <select
              className="w-1/2 border px-3 py-2 rounded focus:outline-blue-500"
              value={priority}
              onChange={(e) => setPriority(e.target.value)}
            >
              <option value="">Select priority</option>
              <option>Low</option>
              <option>Medium</option>
              <option>High</option>
            </select>
          </div>

          {/* Deadline */}
          <div>
            <label className="block font-medium mb-1 text-gray-700">Deadline</label>
            <input
              type="date"
              className="w-1/2 border px-3 py-2 rounded focus:outline-blue-500"
              value={deadline}
              onChange={(e) => setDeadline(e.target.value)}
            />
          </div>

          {/* Epic dropdown */}
          {["Story", "Task", "Bug"].includes(issueType) && (
            <div>
              <label className="block font-medium mb-1 text-gray-700">Parent Epic</label>
              <select
                className="w-1/2 border px-3 py-2 rounded focus:outline-blue-500"
                value={epicId}
                onChange={(e) => {
                  setEpicId(e.target.value);
                  setStoryId("");
                }}
              >
                <option value="">Select an Epic</option>
                {epics.map((epic) => (
                  <option key={epic.id} value={epic.id}>
                    {epic.title}
                  </option>
                ))}
              </select>
            </div>
          )}

          {/* Story dropdown */}
          {["Task", "Bug"].includes(issueType) && (
            <div>
              <label className="block font-medium mb-1 text-gray-700">Parent Story</label>
              <select
                className="w-1/2 border px-3 py-2 rounded focus:outline-blue-500"
                value={storyId}
                onChange={(e) => {
                  setStoryId(e.target.value);
                  setEpicId("");
                }}
              >
                <option value="">Select a Story</option>
                {stories.map((story) => (
                  <option key={story.id} value={story.id}>
                    {story.title}
                  </option>
                ))}
              </select>
            </div>
          )}
          {issueType === "Bug" && (
            <>
              {/* Expected Outcome */}
              <div>
                <label className="block font-medium mb-1 text-gray-700">Expected Outcome *</label>
                <textarea
                  className="w-full border px-3 py-2 rounded focus:outline-blue-500"
                  rows="2"
                  value={expectedOutcome}
                  onChange={(e) => setExpectedOutcome(e.target.value)}
                  placeholder="Enter expected outcome"
                ></textarea>
              </div>

              {/* Actual Outcome */}
                  <div>
                    <label className="block font-medium mb-1 text-gray-700">Actual Outcome *</label>
                    <textarea
                      className="w-full border px-3 py-2 rounded focus:outline-blue-500"
                      rows="2"
                      value={actualOutcome}
                      onChange={(e) => setActualOutcome(e.target.value)}
                      placeholder="Enter actual outcome"
                    ></textarea>
                  </div>
                </>
             )}

          {/* File upload */}
          <div>
            <label className="block font-medium mb-1 text-gray-700">Attachment</label>
            <label className="flex items-center gap-2 text-blue-600 cursor-pointer w-fit">
              <FaCloudUploadAlt className="text-lg" />
              Upload File
              <input
                type="file"
                className="hidden"
                onChange={handleFileChange}
              />
            </label>
          </div>

          {/* Submit button */}
          <div className="pt-4 ">
            <button
              className="bg-blue-600 text-white font-medium px-6 py-2 rounded hover:bg-blue-700 transition-all"
              onClick={handleCreateIssue}
            >
              Create {issueType}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default CreateTaskForm;
