import React, { useRef, useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { toast } from 'react-toastify';
import { useQuery, useMutation } from '@apollo/client';
import { FIND_PROJECT_BY_ID } from '@/graphql/Queries/project-service';
import { UPDATE_PROJECT_DETAILS } from '@/graphql/Mutation/project-service';

const DetailSettings = () => {
  const { projectId } = useParams();
  const [project, setProject] = useState(null);
  const fileInputRef = useRef(null);

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

  const enableField = (fieldName) => {
    setEditableFields((prev) => ({ ...prev, [fieldName]: true }));
  };

  const { data, loading, error } = useQuery(FIND_PROJECT_BY_ID, {
    variables: { projectId },
    skip: !projectId,
  });

  const [updateProjectDetails, { loading: updating }] = useMutation(UPDATE_PROJECT_DETAILS);

  useEffect(() => {
    if (data?.getProject) {
      const proj = data.getProject;
      const initial = {
        title: proj.title || '',
        description: proj.description || '',
        status: proj.status || '',
        priority: proj.priority || '',
        clientId: proj.clientId || '',
        image: null,
      };
      setProject(proj);
      setFormData(initial);
      setInitialFormData(initial);
      setImagePreview(proj.imageUrl || null);
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
      await updateProjectDetails({
        variables: {
          project: {
            projectId,
            title: formData.title || null,
            description: formData.description || null,
            status: formData.status || null,
            priority: formData.priority || null,
            clientId: formData.clientId || null,
          },
        },
      });

      toast.success('Project updated successfully!');
      setInitialFormData({ ...formData, image: null });
      setEditableFields({});
      setIsChanged(false);
    } catch (err) {
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

  if (loading) return <p className="text-center mt-6">Loading...</p>;
  if (error) return <p className="text-center mt-6 text-red-500">{error.message}</p>;

  return (
    <div className="max-w-3xl mx-auto p-4 sm:p-6 md:p-8 bg-white shadow-md sm:shadow-lg rounded-xl">
      {/* Image Preview */}
      <div className="flex justify-center mb-6">
        <div
          onClick={triggerImageUpload}
          className="cursor-pointer w-24 h-24 sm:w-28 sm:h-28 md:w-32 md:h-32 rounded-full overflow-hidden shadow-md border border-gray-300 hover:shadow-lg transition"
          title="Click to change image"
        >
          <img
            src={imagePreview || 'https://via.placeholder.com/150'}
            alt="Project"
            className="w-full h-full object-cover"
          />
        </div>
        <input
          type="file"
          accept="image/*"
          ref={fileInputRef}
          onChange={handleImageUpload}
          className="hidden"
        />
      </div>

      <h2 className="text-lg sm:text-xl md:text-2xl font-bold text-gray-800 mb-6 text-center">
        Project Details
      </h2>

      <form onSubmit={handleSubmit} className="space-y-5">
        {/* Fields */}
        {[
          { label: 'Title', name: 'title', type: 'text' },
          { label: 'Description', name: 'description', type: 'textarea' },
          { label: 'Status', name: 'status', type: 'select', options: ['INITIATED', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'DELIVERED', 'ARCHIVED'] },
          { label: 'Priority', name: 'priority', type: 'select', options: ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'] },
          { label: 'Client ID', name: 'clientId', type: 'text' },
        ].map(({ label, name, type, options }) => (
          <div key={name}>
            <label className="block text-sm sm:text-base font-semibold text-gray-700 mb-1">{label}</label>
            {type === 'textarea' ? (
              <textarea
                name={name}
                value={formData[name]}
                onChange={handleChange}
                readOnly={!editableFields[name]}
                onFocus={() => enableField(name)}
                className={`w-full px-3 py-2 border rounded-md text-sm sm:text-base focus:ring-2 focus:ring-blue-400 ${
                  !editableFields[name] ? 'bg-gray-100 cursor-pointer' : ''
                }`}
                rows="3"
                placeholder={`Enter ${label.toLowerCase()}`}
              ></textarea>
            ) : type === 'select' ? (
              <div
                onClick={() => enableField(name)}
                className={`relative w-full border rounded-md text-sm sm:text-base ${
                  !editableFields[name] ? 'bg-gray-100 cursor-pointer' : ''
                }`}
              >
                <select
                  name={name}
                  value={formData[name]}
                  onChange={handleChange}
                  className={`w-full px-3 py-2 appearance-none focus:ring-2 focus:ring-blue-400 rounded-md bg-transparent ${
                    !editableFields[name] ? 'text-gray-500' : 'text-gray-800'
                  }`}
                >
                  <option value="">Select {label.toLowerCase()}</option>
                  {options.map((opt) => (
                    <option key={opt} value={opt}>{opt}</option>
                  ))}
                </select>
              </div>
            ) : (
              <input
                type="text"
                name={name}
                value={formData[name]}
                onChange={handleChange}
                readOnly={!editableFields[name]}
                onFocus={() => enableField(name)}
                className={`w-full px-3 py-2 border rounded-md text-sm sm:text-base focus:ring-2 focus:ring-blue-400 ${
                  !editableFields[name] ? 'bg-gray-100 cursor-pointer' : ''
                }`}
                placeholder={`Enter ${label.toLowerCase()}`}
              />
            )}
          </div>
        ))}

        {/* Buttons */}
        <div className="flex flex-col sm:flex-row justify-end gap-3 mt-6">
          <button
            type="button"
            onClick={handleCancel}
            className="w-full sm:w-auto px-4 py-2 bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400 transition"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={!isChanged || updating}
            className={`w-full sm:w-auto px-4 py-2 rounded-md transition ${
              isChanged && !updating
                ? 'bg-blue-600 text-white hover:bg-blue-700'
                : 'bg-gray-300 text-gray-500 cursor-not-allowed'
            }`}
          >
            {updating ? 'Updating...' : 'Save Changes'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default DetailSettings;
