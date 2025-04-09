import React, { useState } from 'react';
import { useApolloClients } from '@/graphql/Clients/ApolloClientContext';
import { CREATE_SUBTASK } from '@/graphql/Mutation/task-service';
import { useMutation } from '@apollo/client';
import { toast } from 'react-hot-toast';

function CreateSubTask({ taskId, onChange }) {
  const { taskClient } = useApolloClients();
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    status: 'TODO',
    priority: 'MEDIUM',
    deadline: '',
    label: ''
  });

  const [createSubTask, { loading }] = useMutation(CREATE_SUBTASK, {
    client: taskClient,
    onCompleted: () => {
      toast.success('SubTask created successfully!');
      onChange(true); 
    },
    onError: (error) => {
      toast.error(`Failed to create subtask: ${error.message}`);
    }
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    createSubTask({
      variables: {
        subTask: {
          ...formData,
          taskId: taskId
        }
      }
    });
  };

  // Priority colors for visual indication
  const priorityColors = {
    LOW: 'bg-green-100 text-green-800',
    MEDIUM: 'bg-yellow-100 text-yellow-800',
    HIGH: 'bg-red-100 text-red-800'
  };

  // Status colors for visual indication
  const statusColors = {
    TODO: 'bg-gray-100 text-gray-800',
    IN_PROGRESS: 'bg-blue-100 text-blue-800',
    IN_PLANNED: 'bg-purple-100 text-purple-800'
  };

  return (
    <div className="p-4 sm:p-6 bg-white rounded-lg shadow-md border border-gray-100">
      <h2 className="text-xl font-semibold mb-6 text-gray-800">Create Sub Task</h2>
      
      <form onSubmit={handleSubmit} className="space-y-5">
        <div>
          <label className="block text-sm font-medium mb-1.5 text-gray-700">Title</label>
          <input
            type="text"
            name="title"
            value={formData.title}
            onChange={handleChange}
            className="w-full p-2.5 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
            placeholder="Enter task title"
            required
          />
        </div>
        
        <div>
          <label className="block text-sm font-medium mb-1.5 text-gray-700">Description</label>
          <textarea
            name="description"
            value={formData.description}
            onChange={handleChange}
            className="w-full p-2.5 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
            rows="3"
            placeholder="Describe the subtask..."
          />
        </div>
        
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
          <div>
            <label className="block text-sm font-medium mb-1.5 text-gray-700">Status</label>
            <select
              name="status"
              value={formData.status}
              onChange={handleChange}
              className={`w-full p-2.5 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${statusColors[formData.status]} transition-all`}
            >
              <option value="TODO">To Do</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="IN_PLANNED">In Planned</option>
            </select>
          </div>
          
          <div>
            <label className="block text-sm font-medium mb-1.5 text-gray-700">Priority</label>
            <select
              name="priority"
              value={formData.priority}
              onChange={handleChange}
              className={`w-full p-2.5 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${priorityColors[formData.priority]} transition-all`}
            >
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
            </select>
          </div>
        </div>
        
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
          <div>
            <label className="block text-sm font-medium mb-1.5 text-gray-700">Deadline</label>
            <input
              type="date"
              name="deadline"
              value={formData.deadline}
              onChange={handleChange}
              className="w-full p-2.5 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium mb-1.5 text-gray-700">Label</label>
            <input
              type="text"
              name="label"
              value={formData.label}
              onChange={handleChange}
              className="w-full p-2.5 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
              placeholder="e.g. Frontend, Design, API"
            />
          </div>
        </div>
        
        <div className="flex flex-col-reverse sm:flex-row sm:justify-end gap-3 pt-2">
          <button
            type="button"
            onClick={() => onChange(false)}
            className="px-5 py-2.5 border border-gray-300 rounded-md text-gray-700 font-medium hover:bg-gray-50 transition-colors"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={loading}
            className="px-5 py-2.5 bg-blue-600 text-white rounded-md font-medium hover:bg-blue-700 focus:ring-4 focus:ring-blue-300 disabled:opacity-70 transition-colors"
          >
            {loading ? 'Creating...' : 'Create SubTask'}
          </button>
        </div>
      </form>
    </div>
  );
}

export default CreateSubTask;