import React, { useEffect, useState } from "react";
import { useQuery, useMutation } from "@apollo/client";
import {
  GET_ALL_EPICS,
  GET_ALL_STORIES_BY_PROJECT_ID,
} from "@/graphql/Queries/task-service";
import {
  CREATE_TASK,
  CREATE_BUG,
  CREATE_EPIC,
} from "@/graphql/Mutation/task-service";
import { FIND_ALL_PROJECT_BY_USER } from "@/graphql/Queries/project-service";
import { useApolloClients } from "@/graphql/Clients/ApolloClientContext";
import { useAuth } from "@/context/AuthContext";
import { FaCloudUploadAlt } from "react-icons/fa";
import toast from "react-hot-toast";

function CreateTaskForm() {
  const { user } = useAuth();
  const { projectClient, taskClient } = useApolloClients();

  const [projects, setProjects] = useState([]);
  const [epics, setEpics] = useState([]);
  const [stories, setStories] = useState([]);
  const [selectedProject, setSelectedProject] = useState("");
  const [issueType, setIssueType] = useState("Task");
  const [status, setStatus] = useState("To Do");
  const [summary, setSummary] = useState("");
  const [description, setDescription] = useState("");
  const [label, setLabel] = useState("");
  const [parent, setParent] = useState(null);
  const [startDate, setStartDate] = useState("");
  const [dueDate, setDueDate] = useState("");
  const [attachment, setAttachment] = useState(null);
  const [reporter] = useState(user?.userId || "");

  const { data: projectData } = useQuery(FIND_ALL_PROJECT_BY_USER, {
    client: projectClient,
    variables: { userId: user.userId },
    fetchPolicy: "network-only",
  });

  const { data: epicData } = useQuery(GET_ALL_EPICS, {
    client: taskClient,
    fetchPolicy: "network-only",
  });

  const { data: storyData } = useQuery(GET_ALL_STORIES_BY_PROJECT_ID, {
    client: taskClient,
    variables: { projectId: selectedProject },
    skip: issueType !== "Task",
    fetchPolicy: "network-only",
  });

  const [createEpic] = useMutation(CREATE_EPIC, { client: taskClient });
  const [createStory] = useMutation(CREATE_TASK, { client: taskClient }); 
  const [createTask] = useMutation(CREATE_TASK, { client: taskClient });
  const [createBug] = useMutation(CREATE_BUG, { client: taskClient });

  useEffect(() => {
    if (projectData?.findAllProject) {
      setProjects(projectData.findAllProject);
    }
  }, [projectData]);

  useEffect(() => {
    if (epicData?.getAllEpics) {
      setEpics(epicData.getAllEpics);
    }
  }, [epicData]);

  useEffect(() => {
    if (storyData?.getAllStoriesByProjectId) {
      setStories(storyData.getAllStoriesByProjectId);
    }
  }, [storyData]);

  const handleFileChange = (e) => {
    setAttachment(e.target.files[0]);
  };

  const handleCreateIssue = async () => {
    if (!selectedProject || !summary || !status || !startDate || !dueDate) {
      toast.error("Please fill all required fields.");
      return;
    }

    const input = {
      title: summary,
      description,
      label,
      startDate,
      dueDate,
      projectId: selectedProject,
      status,
      reporterId: reporter,
      parentId: parent || null,
    };

    try {
      if (issueType === "Epic") {
        await createEpic({ variables: { input } });
        toast.success("Epic created successfully!");
      } else if (issueType === "Story") {
        await createStory({
          variables: { input: { ...input, issueType: "Story" } },
        });
        toast.success("Story created successfully!");
      } else if (issueType === "Task") {
        await createTask({
          variables: { input: { ...input, issueType: "Task" } },
        });
        toast.success("Task created successfully!");
      } else if (issueType === "Bug") {
        await createBug({
          variables: { input: { ...input, issueType: "Bug" } },
        });
        toast.success("Bug created successfully!");
      }

      // Optional: Reset form
      setSummary("");
      setDescription("");
      setLabel("");
      setParent(null);
      setStartDate("");
      setDueDate("");
      setSelectedProject("");
      setIssueType("Task");
      setStatus("To Do");
      setAttachment(null);
    } catch (err) {
      console.error(err);
      toast.error("Failed to create issue.");
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/20 pt-[20px]">
      <div className="w-full max-w-3xl bg-white rounded-xl shadow-2xl p-10 overflow-y-auto max-h-[90vh] scrollbar-thin scrollbar-thumb-gray-300 scrollbar-track-transparent font-sans text-[14px]">
        <div className="flex justify-between items-center mb-5">
          <h3 className="text-xl font-bold text-gray-800">📝 Create Issue</h3>
          <button className="text-gray-400 hover:text-black text-2xl">×</button>
        </div>

        <p className="text-xs text-gray-500 mb-6">
          Required fields are marked with an asterisk *
        </p>

        <div className="space-y-5">
          {/* Project */}
          <div>
            <label className="block font-medium mb-1 text-gray-700">
              Project *
            </label>
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
            <label className="block font-medium mb-1 text-gray-700">
              Issue Type *
            </label>
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
            <label className="block font-medium mb-1 text-gray-700">
              Status *
            </label>
            <select
              className="w-1/2 border px-3 py-2 rounded focus:outline-blue-500"
              value={status}
              onChange={(e) => setStatus(e.target.value)}
            >
              <option>🟡 To Do</option>
              <option>🟠 In Progress</option>
              <option>✅ Completed</option>
            </select>
          </div>

          {/* Summary */}
          <div>
            <label className="block font-medium mb-1 text-gray-700">
              Summary *
            </label>
            <input
              type="text"
              className="w-full border px-3 py-2 rounded focus:outline-blue-500"
              value={summary}
              onChange={(e) => setSummary(e.target.value)}
              placeholder="Enter summary"
            />
          </div>

          {/* Description */}
          <div>
            <label className="block font-medium mb-1 text-gray-700">
              Description
            </label>
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
              className="w-1/2 border px-3 py-2 rounded focus:outline-blue-500"
              value={label}
              onChange={(e) => setLabel(e.target.value)}
              placeholder="Enter labels (comma separated)"
            />
          </div>

          {/* Parent Selection */}
          {issueType === "Task" && (
            <div>
              <label className="block font-medium mb-1 text-gray-700">
                Story *
              </label>
              <select
                className="w-1/2 border px-3 py-2 rounded focus:outline-blue-500"
                value={parent}
                onChange={(e) => setParent(e.target.value)}
              >
                <option value="">Select a story</option>
                {stories.map((story) => (
                  <option key={story.id} value={story.id}>
                    {story.title}
                  </option>
                ))}
              </select>
            </div>
          )}

          {issueType === "Story" && (
            <div>
              <label className="block font-medium mb-1 text-gray-700">
                Epic *
              </label>
              <select
                className="w-1/2 border px-3 py-2 rounded focus:outline-blue-500"
                value={parent}
                onChange={(e) => setParent(e.target.value)}
              >
                <option value="">Select an epic</option>
                {epics.map((epic) => (
                  <option key={epic.id} value={epic.id}>
                    {epic.title}
                  </option>
                ))}
              </select>
            </div>
          )}

          {/* Dates */}
          <div className="flex gap-4">
            <div className="flex-1">
              <label className="block font-medium mb-1 text-gray-700">
                Start Date *
              </label>
              <input
                type="date"
                className="w-full border px-3 py-2 rounded focus:outline-blue-500"
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
              />
            </div>
            <div className="flex-1">
              <label className="block font-medium mb-1 text-gray-700">
                Due Date *
              </label>
              <input
                type="date"
                className="w-full border px-3 py-2 rounded focus:outline-blue-500"
                value={dueDate}
                onChange={(e) => setDueDate(e.target.value)}
              />
            </div>
          </div>

          {/* Attachment */}
          <div>
            <label className="block font-medium mb-1 text-gray-700">
              Attachment
            </label>
            <div className="flex items-center gap-3">
              <label className="flex items-center gap-2 px-3 py-2 border rounded cursor-pointer hover:bg-gray-50">
                <FaCloudUploadAlt />
                Upload File
                <input type="file" className="hidden" onChange={handleFileChange} />
              </label>
              {attachment && (
                <span className="text-gray-600 text-sm">{attachment.name}</span>
              )}
            </div>
          </div>

          {/* Create Button */}
          <div className="mt-6 flex justify-end">
            <button
              className="px-4 py-2 rounded bg-blue-600 text-white hover:bg-blue-700"
              onClick={handleCreateIssue}
            >
              Create
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default CreateTaskForm;
