import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getUserDetails, updateUserDetails } from '@/services/UserService';
import { useAuth } from '@/context/AuthContext';
import { Pencil, Upload, User, Calendar, MapPin, Phone, Mail, Globe, UserCircle } from 'lucide-react';
import { toast } from 'react-toastify';

function ProfileSettings() {
  const navigate = useNavigate();
  const { user, accessToken } = useAuth();
  const [userDetails, setUserDetails] = useState(null);
  const [loading, setLoading] = useState(true);
  const [editableField, setEditableField] = useState(null);
  const [previewImage, setPreviewImage] = useState(null);
  const [updating, setUpdating] = useState(false);

  useEffect(() => {
    if (!user) {
      navigate('/login', { replace: true });
      return;
    }

    const fetchUserDetails = async () => {
      try {
        const userId = user?.userId;
        const { data } = await getUserDetails(userId, accessToken);
        setUserDetails(data);
      } catch (error) {
        console.error('Error fetching user details:', error);
        toast.error('Could not load profile information');
      } finally {
        setLoading(false);
      }
    };

    fetchUserDetails();
  }, []);

  if (loading || !userDetails) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="animate-pulse text-indigo-600 text-lg font-medium">Loading your profile...</div>
      </div>
    );
  }

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setPreviewImage(URL.createObjectURL(file));
      setUserDetails((prev) => ({
        ...prev,
        imageFile: file,
      }));
    }
  };

  const formHandler = (e) => {
    const { name, value } = e.target;
    setUserDetails((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const submitHandler = async (e) => {
    e.preventDefault();
    setUpdating(true);
    try {
      const formData = new FormData(); 
      formData.append('image', userDetails.imageFile);
      formData.append("userData", new Blob([JSON.stringify(userDetails)], {type:"application/json"}));
      const data = await updateUserDetails(formData, accessToken);
      toast.success(data?.message || 'Profile updated successfully');
      setEditableField(null);
    } catch (err) {
      console.log(err);
      toast.error(err?.message || 'Failed to update profile');
    } finally {
      setUpdating(false);
    }
  };

  const getFieldIcon = (name) => {
    switch(name) {
      case 'email': return <Mail className="w-5 h-5 text-indigo-500" />;
      case 'address': return <MapPin className="w-5 h-5 text-indigo-500" />;
      case 'dob': return <Calendar className="w-5 h-5 text-indigo-500" />;
      case 'phoneNo': return <Phone className="w-5 h-5 text-indigo-500" />;
      case 'gender': return <UserCircle className="w-5 h-5 text-indigo-500" />;
      case 'language': return <Globe className="w-5 h-5 text-indigo-500" />;
      default: return <User className="w-5 h-5 text-indigo-500" />;
    }
  };

  const renderField = (label, name, type = 'text') => (
    <div className="bg-white rounded-lg shadow-sm p-4 transition-all duration-300 hover:shadow-md">
      <label className="font-medium flex items-center justify-between text-sm sm:text-base mb-2 text-gray-700">
        <div className="flex items-center gap-2">
          {getFieldIcon(name)}
          <span>{label}</span>
        </div>
        <button 
          type="button" 
          onClick={() => setEditableField(name)}
          className="p-1.5 rounded-full hover:bg-indigo-50 group transition-colors"
        >
          <Pencil className="w-4 h-4 text-gray-400 group-hover:text-indigo-500" />
        </button>
      </label>
      {editableField === name ? (
        <input
          type={type}
          name={name}
          value={userDetails[name] || ''}
          onChange={formHandler}
          placeholder={`Enter ${label}`}
          className="border border-indigo-200 rounded-md px-3 py-2.5 w-full mt-1 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent transition-all"
          autoFocus
        />
      ) : (
        <p className="px-3 py-2.5 bg-gray-50 rounded-md mt-1 text-sm text-gray-700 border border-gray-100">
          {userDetails[name] || '—'}
        </p>
      )}
    </div>
  );

  return (
    <div className="bg-gradient-to-br from-indigo-50 to-purple-50 min-h-screen py-8">
      <div className="max-w-4xl mx-auto bg-white rounded-xl shadow-lg overflow-hidden">
        {/* Header */}
        <div className="bg-gradient-to-r from-indigo-600 to-purple-600 text-white p-6">
          <h1 className="text-2xl font-bold text-center">Profile Settings</h1>
        </div>
        
        <div className="p-6">
          {/* Profile Header */}
          <div className="flex flex-col sm:flex-row items-center gap-6 pb-6 border-b border-gray-200">
            <div className="relative group">
              <div className="w-32 h-32 rounded-full overflow-hidden border-4 border-white shadow-lg">
                <img
                  src={previewImage || userDetails?.image || '/default-avatar.png'}
                  alt="Profile"
                  className="w-full h-full object-cover"
                />
              </div>
              <label className="absolute bottom-0 right-0 bg-white rounded-full p-2.5 cursor-pointer shadow-md transition-transform duration-300 hover:scale-110 hover:bg-indigo-50 group-hover:bg-indigo-50">
                <Upload size={18} className="text-indigo-600" />
                <input
                  type="file"
                  accept="image/*"
                  onChange={handleImageChange}
                  className="hidden"
                />
              </label>
            </div>
            <div className="text-center sm:text-left flex-1">
              <h2 className="text-2xl font-bold text-gray-800">{userDetails.name}</h2>
              <div className="mt-2 space-y-1">
                <p className="text-sm text-gray-500 flex items-center justify-center sm:justify-start gap-1">
                  <Calendar className="w-4 h-4" />
                  <span>Joined: {userDetails?.joinedAt || 'Not available'}</span>
                </p>
                <p className="text-sm text-gray-500 flex items-center justify-center sm:justify-start gap-1">
                  <User className="w-4 h-4" />
                  <span>ID: {userDetails?.userId?.substring(0, 8)}...</span>
                </p>
              </div>
            </div>
          </div>

          {/* Form */}
          <form onSubmit={submitHandler} className="mt-8">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
              {renderField('Email Address', 'email', 'email')}
              {renderField('Home Address', 'address')}
              {renderField('Date of Birth', 'dob', 'date')}
              {renderField('Phone Number', 'phoneNo', 'tel')}
              {renderField('Gender', 'gender')}
              {renderField('Preferred Language', 'language')}
            </div>

            <div className="flex justify-center mt-8">
              <button
                type="submit"
                disabled={updating}
                className={`px-8 py-3 rounded-lg font-medium text-white shadow-lg transition-all duration-300 ${
                  updating 
                    ? 'bg-gray-400 cursor-not-allowed' 
                    : 'bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-700 hover:to-purple-700 hover:shadow-xl'
                }`}
              >
                {updating ? (
                  <div className="flex items-center gap-2">
                    <svg className="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    <span>Updating...</span>
                  </div>
                ) : (
                  'Save Changes'
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default ProfileSettings;