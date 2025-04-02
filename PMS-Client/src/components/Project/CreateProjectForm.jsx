import React, { useState } from "react";
import { useMutation } from "@apollo/client";
import { CREATE_PROJECT } from "@/graphql/Mutation/project-service";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { useApolloClients } from "@/graphql/Clients/ApolloClientContext";
import { Rocket, Calendar, Flag, Gauge, User, Loader2 } from "lucide-react";

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
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-50 px-4 py-8 flex items-center justify-center">
      <div className="w-full max-w-2xl bg-white rounded-2xl shadow-xl overflow-hidden border border-gray-100">
        {/* Header */}
        <div className="bg-gradient-to-r from-blue-600 to-indigo-600 p-6 text-white">
          <div className="flex items-center justify-center gap-3">
            <Rocket size={32} className="text-yellow-300" />
            <h2 className="text-3xl font-bold tracking-tight">Launch New Project</h2>
          </div>
          <p className="text-center text-blue-100 mt-2">
            Fill in the details to get your project off the ground
          </p>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="p-6 sm:p-8 grid grid-cols-1 gap-6">
          {/* Title */}
          <div className="space-y-2">
            <label className="block text-lg font-semibold text-gray-800">
              Project Title
            </label>
            <div className="relative">
              <input
                type="text"
                name="title"
                value={formData.title}
                onChange={handleChange}
                required
                className="w-full border-2 border-gray-200 rounded-xl px-5 py-3 text-gray-700 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all"
                placeholder="e.g. Website Redesign Project"
              />
            </div>
          </div>

          {/* Description */}
          <div className="space-y-2">
            <label className="block text-lg font-semibold text-gray-800">
              Description
            </label>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleChange}
              rows={4}
              required
              className="w-full border-2 border-gray-200 rounded-xl px-5 py-3 text-gray-700 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all"
              placeholder="Describe your project goals and objectives..."
            ></textarea>
          </div>

          {/* Date Fields */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
            {/* Start Date */}
            <div className="space-y-2">
              <label className="flex items-center gap-2 text-lg font-semibold text-gray-800">
                <Calendar size={20} className="text-blue-600" />
                Start Date
              </label>
              <input
                type="date"
                name="startDate"
                value={formData.startDate}
                onChange={handleChange}
                required
                className="w-full border-2 border-gray-200 rounded-xl px-5 py-3 text-gray-700 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all"
              />
            </div>

            {/* Deadline */}
            <div className="space-y-2">
              <label className="flex items-center gap-2 text-lg font-semibold text-gray-800">
                <Calendar size={20} className="text-red-500" />
                Deadline
              </label>
              <input
                type="date"
                name="deadline"
                value={formData.deadline}
                onChange={handleChange}
                required
                className="w-full border-2 border-gray-200 rounded-xl px-5 py-3 text-gray-700 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all"
              />
            </div>
          </div>

          {/* Status & Priority */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
            {/* Status */}
            <div className="space-y-2">
              <label className="flex items-center gap-2 text-lg font-semibold text-gray-800">
                <Flag size={20} className="text-green-600" />
                Status
              </label>
              <select
                name="status"
                value={formData.status}
                onChange={handleChange}
                className="w-full border-2 border-gray-200 rounded-xl px-5 py-3 text-gray-700 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all"
              >
                <option value="PLANNED">📅 Planned</option>
                <option value="IN_PROGRESS">🚧 In Progress</option>
                <option value="COMPLETED">✅ Completed</option>
                <option value="ON_HOLD">⏸️ On Hold</option>
              </select>
            </div>

            {/* Priority */}
            <div className="space-y-2">
              <label className="flex items-center gap-2 text-lg font-semibold text-gray-800">
                <Gauge size={20} className="text-orange-500" />
                Priority
              </label>
              <select
                name="priority"
                value={formData.priority}
                onChange={handleChange}
                className="w-full border-2 border-gray-200 rounded-xl px-5 py-3 text-gray-700 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all"
              >
                <option value="LOW">🐢 Low</option>
                <option value="MEDIUM">🚶 Medium</option>
                <option value="HIGH">🚀 High</option>
                <option value="CRITICAL">🔥 Critical</option>
              </select>
            </div>
          </div>

          {/* Client ID */}
          <div className="space-y-2">
            <label className="flex items-center gap-2 text-lg font-semibold text-gray-800">
              <User size={20} className="text-purple-600" />
              Client ID (Optional)
            </label>
            <input
              type="text"
              name="clientId"
              value={formData.clientId}
              onChange={handleChange}
              className="w-full border-2 border-gray-200 rounded-xl px-5 py-3 text-gray-700 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all"
              placeholder="Enter client identifier"
            />
          </div>

          {/* Submit Button */}
          <div className="pt-4">
            <button
              type="submit"
              disabled={loading}
              className="w-full bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white font-bold py-4 px-6 rounded-xl shadow-lg hover:shadow-xl transition-all duration-300 flex items-center justify-center gap-2"
            >
              {loading ? (
                <>
                  <Loader2 size={20} className="animate-spin" />
                  Creating Project...
                </>
              ) : (
                <>
                  <Rocket size={20} />
                  Launch Project
                </>
              )}
            </button>
          </div>

          {/* Error */}
          {error && (
            <div className="mt-4 p-4 bg-red-50 border-l-4 border-red-500 rounded-lg">
              <p className="text-red-700 font-medium">
                ⚠️ Error creating project: {error.message}
              </p>
            </div>
          )}
        </form>
      </div>
    </div>
  );
}

export default CreateProjectForm;