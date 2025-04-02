import React, { useRef, useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { toast } from 'react-toastify';
import { useQuery, useMutation } from '@apollo/client';
import { useApolloClients } from "@/graphql/Clients/ApolloClientContext";
import { FIND_PROJECT_BY_ID } from '@/graphql/Queries/project-service';
import { UPDATE_PROJECT_DETAILS } from '@/graphql/Mutation/project-service';
import { useOutletContext } from 'react-router-dom';
import { Camera, Edit2, Check, X, AlertTriangle, Loader } from 'lucide-react';

const DetailSettings = () => {
  const { projectClient } = useApolloClients();
  const { projectId } = useParams();
  const [project, setProject] = useState(null);
  const fileInputRef = useRef(null);
  const { refetchProject } = useOutletContext();

  const [formData, setFormData] = useState({
    title: '',
    description: '',
    status: '',
    priority: '',
    clientId: '',
    image: null,
  });

  const [initialFormData, setInitialFormData] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);
  const [editableFields, setEditableFields] = useState({});
  const [isChanged, setIsChanged] = useState(false);
  const [showImageHover, setShowImageHover] = useState(false);

  const priorityColors = {
    LOW: 'bg-emerald-100 text-emerald-800',
    MEDIUM: 'bg-blue-100 text-blue-800',
    HIGH: 'bg-amber-100 text-amber-800',
    CRITICAL: 'bg-rose-100 text-rose-800',
  };

  const statusColors = {
    INITIATED: 'bg-indigo-100 text-indigo-800',
    IN_PROGRESS: 'bg-sky-100 text-sky-800',
    ON_HOLD: 'bg-amber-100 text-amber-800',
    COMPLETED: 'bg-emerald-100 text-emerald-800',
    DELIVERED: 'bg-violet-100 text-violet-800',
    ARCHIVED: 'bg-slate-100 text-slate-800',
  };

  const enableField = (fieldName) => {
    setEditableFields((prev) => ({ ...prev, [fieldName]: true }));
  };

  const { data, loading, error } = useQuery(FIND_PROJECT_BY_ID, {
    client: projectClient,
    variables: { projectId },
    skip: !projectId,
  });

  const [updateProjectDetails, { loading: updating }] = useMutation(UPDATE_PROJECT_DETAILS, { client: projectClient });

  useEffect(() => {
    if (data?.getProject) {
      const proj = data.getProject;
      const initial = {
        title: proj.title || '',
        description: proj.description || '',
        status: proj.status || '',
        priority: proj.priority || '',
        clientId: proj.clientId || '',
        image: proj.image,
      };
      setProject(proj);
      setFormData(initial);
      setInitialFormData(initial);
      setImagePreview(proj.image || null);
      setIsChanged(false);
    }
  }, [data]);

  const hasFormChanged = (newFormData, oldFormData) => {
    for (let key in oldFormData) {
      if (key !== 'image') {
        if (newFormData[key] !== oldFormData[key]) return true;
      }
    }
    return !!newFormData.image;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    const updatedForm = { ...formData, [name]: value };
    setFormData(updatedForm);
    setIsChanged(hasFormChanged(updatedForm, initialFormData));
  };

  const handleImageUpload = (e) => {
    const file = e.target.files[0];
    if (file) {
      const updatedForm = { ...formData, image: file };
      setFormData(updatedForm);
      setImagePreview(URL.createObjectURL(file));
      setIsChanged(true);
    }
  };

  const triggerImageUpload = () => {
    fileInputRef.current.click();
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
  
    try {
      const variables = {
        project: {
          projectId,
          title: formData.title || null,
          description: formData.description || null,
          status: formData.status || null,
          priority: formData.priority || null,
          clientId: formData.clientId || null,
        },
      };
  
      // Only attach the image if it's a new file
      if (formData.image instanceof File) {
        variables.image = formData.image;
      }
  
      await updateProjectDetails({ variables });
  
      toast.success('Project updated successfully!');
      setInitialFormData({ ...formData, image: null });
      setEditableFields({});
      await refetchProject();
      setIsChanged(false);
    } catch (err) {
      console.error(err);
      toast.error('Update failed: ' + err.message);
    }
  };
  
  const handleCancel = () => {
    if (initialFormData) {
      setFormData(initialFormData);
      setEditableFields({});
      setIsChanged(false);
      if (project?.imageUrl) {
        setImagePreview(project.imageUrl);
      } else {
        setImagePreview(null);
      }
    }
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center h-64">
        <Loader className="w-8 h-8 text-indigo-600 animate-spin" />
        <p className="mt-4 text-indigo-600 font-medium">Loading project details...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex flex-col items-center justify-center h-64 p-8 bg-red-50 rounded-lg">
        <AlertTriangle className="w-12 h-12 text-red-500" />
        <p className="mt-4 text-red-700 font-medium">{error.message}</p>
        <p className="text-red-600">Please try refreshing the page</p>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto bg-gradient-to-b from-white to-indigo-50 rounded-2xl shadow-xl overflow-hidden">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-indigo-600 to-purple-600 p-6 text-white">
        <h2 className="text-2xl font-bold text-center">Project Settings</h2>
      </div>

      <div className="p-6 sm:p-8">
        {/* Image Section */}
        <div className="flex justify-center mb-8">
          <div 
            className="relative"
            onMouseEnter={() => setShowImageHover(true)}
            onMouseLeave={() => setShowImageHover(false)}
          >
            <div 
              onClick={triggerImageUpload}
              className="cursor-pointer w-32 h-32 rounded-full overflow-hidden border-4 border-white shadow-lg transition-all duration-300 hover:shadow-xl relative"
            >
              <img
                src={imagePreview || 'https://via.placeholder.com/150?text=Project'}
                alt="Project"
                className="w-full h-full object-cover"
              />
              {showImageHover && (
                <div className="absolute inset-0 bg-black bg-opacity-40 flex items-center justify-center transition-opacity duration-200">
                  <Camera className="w-8 h-8 text-white" />
                </div>
              )}
            </div>
            <input
              type="file"
              accept="image/*"
              ref={fileInputRef}
              onChange={handleImageUpload}
              className="hidden"
            />
            {formData.title && (
              <div className="absolute -bottom-2 left-1/2 transform -translate-x-1/2 bg-indigo-600 text-white px-4 py-1 rounded-full text-sm font-medium shadow-md">
                {formData.status}
              </div>
            )}
          </div>
        </div>

        {/* Project Title */}
        <div className="mb-8 text-center">
          <h1 className="text-2xl font-bold text-gray-800">
            {formData.title || 'Untitled Project'}
          </h1>
          {formData.priority && (
            <span className={`inline-block mt-2 px-3 py-1 rounded-full text-xs font-medium ${priorityColors[formData.priority]}`}>
              {formData.priority} Priority
            </span>
          )}
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Fields */}
          {[
            { label: 'Project Title', name: 'title', type: 'text', icon: <Edit2 className="w-4 h-4" /> },
            { 
              label: 'Project Description', 
              name: 'description', 
              type: 'textarea',
              icon: <Edit2 className="w-4 h-4" />
            },
            { 
              label: 'Status', 
              name: 'status', 
              type: 'select', 
              options: ['INITIATED', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'DELIVERED', 'ARCHIVED'],
              renderOption: (opt) => (
                <span className={`block px-2 py-1 rounded-md text-sm ${statusColors[opt]}`}>
                  {opt.replace(/_/g, ' ')}
                </span>
              ),
              icon: <Edit2 className="w-4 h-4" />
            },
            { 
              label: 'Priority', 
              name: 'priority', 
              type: 'select', 
              options: ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'],
              renderOption: (opt) => (
                <span className={`block px-2 py-1 rounded-md text-sm ${priorityColors[opt]}`}>
                  {opt}
                </span>
              ),
              icon: <Edit2 className="w-4 h-4" />
            },
            { label: 'Client ID', name: 'clientId', type: 'text', icon: <Edit2 className="w-4 h-4" /> },
          ].map(({ label, name, type, options, renderOption, icon }) => (
            <div key={name} className="relative bg-white rounded-lg shadow-sm hover:shadow-md transition-all duration-300 p-4">
              <label className="block text-sm font-semibold text-gray-700 mb-2 flex items-center">
                {icon}
                <span className="ml-2">{label}</span>
              </label>
              
              {type === 'textarea' ? (
                <div className="relative">
                  <textarea
                    name={name}
                    value={formData[name]}
                    onChange={handleChange}
                    readOnly={!editableFields[name]}
                    onFocus={() => enableField(name)}
                    className={`w-full px-4 py-3 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all ${
                      !editableFields[name] ? 'bg-gray-50 cursor-pointer' : 'bg-white'
                    }`}
                    rows="4"
                    placeholder={`Enter ${label.toLowerCase()}`}
                  ></textarea>
                  {!editableFields[name] && (
                    <div className="absolute right-3 top-3 w-6 h-6 bg-indigo-100 rounded-full flex items-center justify-center">
                      <Edit2 className="w-3 h-3 text-indigo-600" />
                    </div>
                  )}
                </div>
              ) : type === 'select' ? (
                <div
                  onClick={() => enableField(name)}
                  className={`relative w-full border border-gray-200 rounded-lg ${
                    !editableFields[name] ? 'bg-gray-50 cursor-pointer' : 'bg-white'
                  }`}
                >
                  <select
                    name={name}
                    value={formData[name]}
                    onChange={handleChange}
                    className={`w-full px-4 py-3 appearance-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 rounded-lg bg-transparent ${
                      !editableFields[name] ? 'text-gray-600' : 'text-gray-800'
                    }`}
                  >
                    <option value="">Select {label.toLowerCase()}</option>
                    {options.map((opt) => (
                      <option key={opt} value={opt} className="py-2">
                        {opt.replace(/_/g, ' ')}
                      </option>
                    ))}
                  </select>
                  {formData[name] && renderOption && (
                    <div className="absolute right-12 top-1/2 transform -translate-y-1/2">
                      {renderOption(formData[name])}
                    </div>
                  )}
                  {!editableFields[name] && (
                    <div className="absolute right-3 top-1/2 transform -translate-y-1/2 w-6 h-6 bg-indigo-100 rounded-full flex items-center justify-center">
                      <Edit2 className="w-3 h-3 text-indigo-600" />
                    </div>
                  )}
                </div>
              ) : (
                <div className="relative">
                  <input
                    type="text"
                    name={name}
                    value={formData[name]}
                    onChange={handleChange}
                    readOnly={!editableFields[name]}
                    onFocus={() => enableField(name)}
                    className={`w-full px-4 py-3 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all ${
                      !editableFields[name] ? 'bg-gray-50 cursor-pointer' : 'bg-white'
                    }`}
                    placeholder={`Enter ${label.toLowerCase()}`}
                  />
                  {!editableFields[name] && (
                    <div className="absolute right-3 top-1/2 transform -translate-y-1/2 w-6 h-6 bg-indigo-100 rounded-full flex items-center justify-center">
                      <Edit2 className="w-3 h-3 text-indigo-600" />
                    </div>
                  )}
                </div>
              )}
            </div>
          ))}

          {/* Action Buttons */}
          <div className="flex flex-col sm:flex-row justify-end gap-3 mt-8 pt-4 border-t border-gray-200">
            <button
              type="button"
              onClick={handleCancel}
              disabled={!isChanged}
              className={`flex items-center justify-center px-5 py-2.5 rounded-lg transition-all duration-300 ${
                isChanged 
                  ? 'bg-white border border-gray-300 text-gray-700 hover:bg-gray-50' 
                  : 'bg-gray-100 text-gray-400 cursor-not-allowed'
              }`}
            >
              <X className="w-4 h-4 mr-2" />
              Cancel
            </button>
            <button
              type="submit"
              disabled={!isChanged || updating}
              className={`flex items-center justify-center px-5 py-2.5 rounded-lg font-medium transition-all duration-300 ${
                isChanged && !updating
                  ? 'bg-gradient-to-r from-indigo-600 to-purple-600 text-white hover:from-indigo-700 hover:to-purple-700 shadow-md hover:shadow-lg'
                  : 'bg-gray-200 text-gray-400 cursor-not-allowed'
              }`}
            >
              {updating ? (
                <>
                  <Loader className="w-4 h-4 mr-2 animate-spin" />
                  Updating...
                </>
              ) : (
                <>
                  <Check className="w-4 h-4 mr-2" />
                  Save Changes
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default DetailSettings;