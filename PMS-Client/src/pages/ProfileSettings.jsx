import React, { useEffect, useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { getUserDetails, updateUserDetails } from '@/services/UserService';
import { AuthContext } from '@/context/AuthContext';
import { Pencil, Upload } from 'lucide-react';
import { toast } from 'react-toastify';

function ProfileSettings() {
  const navigate = useNavigate();
  const { user, accessToken } = useContext(AuthContext);
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
      } finally {
        setLoading(false);
      }
    };

    fetchUserDetails();
  }, []);

  if (loading || !userDetails) {
    return <div className="text-center mt-10">Loading...</div>;
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
        formData.append("userData", new Blob([JSON.stringify(userDetails)],{type:"application/json"}));
        const data = await updateUserDetails(formData, accessToken);
        toast.success(data?.message || 'Profile updated');
    } catch (err) {
        console.log(err);
      toast.error(err?.message || 'Failed to update profile');
    } finally{
        setUpdating(false);
    }
  };

  const renderField = (label, name, type = 'text') => (
    <div>
      <label className="font-medium flex items-center justify-between text-sm sm:text-base">
        {label}
        <Pencil
          className="w-4 h-4 ml-2 cursor-pointer text-gray-500 hover:text-blue-500"
          onClick={() => setEditableField(name)}
        />
      </label>
      {editableField === name ? (
        <input
          type={type}
          name={name}
          value={userDetails[name] || ''}
          onChange={formHandler}
          placeholder={`Enter ${editableField}`}
          className="border border-gray-300 rounded px-3 py-2 w-full mt-1 text-sm"
        />
      ) : (
        <p className="border border-gray-200 rounded px-3 py-2 bg-gray-50 mt-1 text-sm">
          {userDetails[name] || '—'}
        </p>
      )}
    </div>
  );

  return (
    <div className="flex flex-col items-center w-full h-full pt-[10px]">
      {/* Header */}
      <header className=" flex flex-col sm:flex-row items-center sm:justify-around w-[80%]  p-3 rounded-md gap-4">
        <div className="relative w-32 h-32">
          <img
            src={previewImage || userDetails?.image || '/default-avatar.png'}
            alt="Profile"
            className="w-32 h-32 rounded-full object-cover border-2 border-gray-300"
          />
          <label className="absolute bottom-0 right-0 bg-white rounded-full p-1 cursor-pointer shadow">
            <Upload size={16} />
            <input
              type="file"
              accept="image/*"
              onChange={handleImageChange}
              className="hidden"
            />
          </label>
        </div>
        <div className="text-center sm:text-left">
          <h1 className="text-xl sm:text-2xl font-semibold flex items-center justify-center sm:justify-start gap-2">
            {userDetails.name}
          </h1>
          <p className="text-sm text-gray-600">Joined: {userDetails?.joinedAt || 'NA'}</p>
          <p className="text-sm text-gray-600">User ID: {userDetails?.userId}</p>
        </div>
      </header>

      {/* Form */}
      <form
        onSubmit={submitHandler}
        className="  mt-6 grid grid-cols-1 sm:grid-cols-2 gap-4 w-[80%] px-2"
      >
        {renderField('Email', 'email', 'email')}
        {renderField('Address', 'address')}
        {renderField('DOB', 'dob', 'date')}
        {renderField('Phone No', 'phoneNo', 'tel')}
        {renderField('Gender', 'gender')}
        {renderField('Language', 'language')}

        <div className="col-span-1 sm:col-span-2 flex justify-center mt-4">
        <button
            type="submit"
            disabled={updating}
            className={`px-6 py-2 rounded shadow text-white ${
            updating ? 'bg-gray-400 cursor-not-allowed' : 'bg-blue-500 hover:bg-blue-600'
            }`}
        >
        {updating ? 'Updating...' : 'Submit'}
        </button>
        </div>
      </form>
    </div>
  );
}

export default ProfileSettings;
