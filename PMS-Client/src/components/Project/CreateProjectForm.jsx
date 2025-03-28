import React, { useState } from "react";
import { useMutation } from "@apollo/client";
import { CREATE_PROJECT } from "@/graphql/Mutation/project-service";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { useApolloClients } from "@/graphql/Clients/ApolloClientContext";

function CreateProjectForm() {
  const navigate = useNavigate();
  const { projectClient } = useApolloClients();
  const [formData, setFormData] = useState({
    title: "",
    description: "",
    startDate: "",
    deadline: "",
    status: "PLANNED",
    priority: "MEDIUM",
    clientId: "",
  });

  const [createProject, { loading, error }] = useMutation(CREATE_PROJECT, {
    client: projectClient,
    onCompleted: (data) => {
      toast.success("🎉 Project created successfully!");
      navigate(`/projects/${data.createProject.projectId}`);
    },
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    createProject({
      variables: {
        project: {
          ...formData,
          projectId: null,
        },
      },
    });
  };

  return (
    <div className="min-h-screen bg-gray-50 px-4 py-8 flex items-center justify-center">
      <div className="w-full max-w-4xl bg-white rounded-2xl shadow-lg p-8 sm:p-10 border border-gray-200">
        <h2 className="text-3xl font-bold text-blue-700 mb-8 text-center">🚀 Create New Project</h2>

        <form onSubmit={handleSubmit} className="grid grid-cols-1 sm:grid-cols-2 gap-6">
          {/* Title */}
          <div className="sm:col-span-2">
            <label className="block text-gray-700 font-medium mb-1">Project Title</label>
            <input
              type="text"
              name="title"
              value={formData.title}
              onChange={handleChange}
              required
              className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Enter project title"
            />
          </div>

          {/* Description */}
          <div className="sm:col-span-2">
            <label className="block text-gray-700 font-medium mb-1">Description</label>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleChange}
              rows={4}
              required
              className="w-full border border-gray-300 rounded-lg px-4 py-2 resize-none focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Project description..."
            ></textarea>
          </div>

          {/* Start Date */}
          <div>
            <label className="block text-gray-700 font-medium mb-1">Start Date</label>
            <input
              type="date"
              name="startDate"
              value={formData.startDate}
              onChange={handleChange}
              required
              className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          {/* Deadline */}
          <div>
            <label className="block text-gray-700 font-medium mb-1">Deadline</label>
            <input
              type="date"
              name="deadline"
              value={formData.deadline}
              onChange={handleChange}
              required
              className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          {/* Status */}
          <div>
            <label className="block text-gray-700 font-medium mb-1">Status</label>
            <select
              name="status"
              value={formData.status}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="PLANNED">Planned</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="COMPLETED">Completed</option>
              <option value="ON_HOLD">On Hold</option>
            </select>
          </div>

          {/* Priority */}
          <div>
            <label className="block text-gray-700 font-medium mb-1">Priority</label>
            <select
              name="priority"
              value={formData.priority}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
              <option value="CRITICAL">Critical</option>
            </select>
          </div>

          {/* Client ID */}
          <div className="sm:col-span-2">
            <label className="block text-gray-700 font-medium mb-1">Client ID</label>
            <input
              type="text"
              name="clientId"
              value={formData.clientId}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Enter Client ID"
            />
          </div>

          {/* Submit Button */}
          <div className="sm:col-span-2 flex justify-end">
            <button
              type="submit"
              disabled={loading}
              className="bg-blue-600 hover:bg-blue-700 text-white font-medium px-6 py-2 rounded-lg transition-all"
            >
              {loading ? "Creating..." : "Create Project"}
            </button>
          </div>

          {/* Error */}
          {error && (
            <p className="sm:col-span-2 text-red-500 text-sm mt-2">
              ⚠️ Error creating project: {error.message}
            </p>
          )}
        </form>
      </div>
    </div>
  );
}

export default CreateProjectForm;
