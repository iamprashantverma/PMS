import React, { useState } from "react";
import { useMutation } from "@apollo/client";
import { CREATE_PROJECT } from "@/graphql/Mutation/project-service";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

function CreateProjectForm() {
  const navigate = useNavigate();
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
    <div className="max-w-3xl mx-auto px-4 py-6 sm:px-6 lg:px-8">
      <div className="bg-white shadow-md rounded-lg p-6">
        <h2 className="text-2xl font-semibold text-gray-800 mb-4">🚀 Create New Project</h2>

        <form onSubmit={handleSubmit} className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          {/* Title */}
          <div className="col-span-1 sm:col-span-2">
            <label className="text-sm font-medium text-gray-700">Project Title</label>
            <input
              type="text"
              name="title"
              value={formData.title}
              onChange={handleChange}
              required
              className="mt-1 block w-full p-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              placeholder="Enter project title"
            />
          </div>

          {/* Description */}
          <div className="col-span-1 sm:col-span-2">
            <label className="text-sm font-medium text-gray-700">Description</label>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleChange}
              rows={4}
              required
              className="mt-1 block w-full p-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              placeholder="Project description..."
            ></textarea>
          </div>

          {/* Start Date */}
          <div>
            <label className="text-sm font-medium text-gray-700">Start Date</label>
            <input
              type="date"
              name="startDate"
              value={formData.startDate}
              onChange={handleChange}
              required
              className="mt-1 block w-full p-2 border border-gray-300 rounded-md"
            />
          </div>

          {/* Deadline */}
          <div>
            <label className="text-sm font-medium text-gray-700">Deadline</label>
            <input
              type="date"
              name="deadline"
              value={formData.deadline}
              onChange={handleChange}
              required
              className="mt-1 block w-full p-2 border border-gray-300 rounded-md"
            />
          </div>

          {/* Status */}
          <div>
            <label className="text-sm font-medium text-gray-700">Status</label>
            <select
              name="status"
              value={formData.status}
              onChange={handleChange}
              className="mt-1 block w-full p-2 border border-gray-300 rounded-md"
            >
              <option value="PLANNED">Planned</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="COMPLETED">Completed</option>
              <option value="ON_HOLD">On Hold</option>
            </select>
          </div>

          {/* Priority */}
          <div>
            <label className="text-sm font-medium text-gray-700">Priority</label>
            <select
              name="priority"
              value={formData.priority}
              onChange={handleChange}
              className="mt-1 block w-full p-2 border border-gray-300 rounded-md"
            >
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
              <option value="CRITICAL">Critical</option>
            </select>
          </div>

          {/* Client ID */}
          <div className="col-span-1 sm:col-span-2">
            <label className="text-sm font-medium text-gray-700">Client ID</label>
            <input
              type="text"
              name="clientId"
              value={formData.clientId}
              onChange={handleChange}
              className="mt-1 block w-full p-2 border border-gray-300 rounded-md"
              placeholder="Enter Client ID"
            />
          </div>

          {/* Submit Button */}
          <div className="col-span-1 sm:col-span-2 flex justify-end">
            <button
              type="submit"
              disabled={loading}
              className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-md transition-all"
            >
              {loading ? "Creating..." : "Create Project"}
            </button>
          </div>

          {/* Error */}
          {error && (
            <p className="col-span-2 text-red-500 text-sm mt-2">
              ⚠️ Error creating project: {error.message}
            </p>
          )}
        </form>
      </div>
    </div>
  );
}

export default CreateProjectForm;
