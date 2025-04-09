import React, { useState, useEffect } from 'react';
import { useAuth } from '@/context/AuthContext';
import { getUserDetails } from '@/services/UserService';
import { User, Mail, Phone, Calendar, Clock, Activity, FileText, Settings, LogOut } from 'lucide-react';
import { Link } from 'react-router-dom';

function Dashboard() {
  const { user, accessToken } = useAuth();
  const userId = user.id;
  const [userInfo, setUserInfo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState('overview');

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        setLoading(true);
        const {data }= await getUserDetails(userId ,accessToken);
        setUserInfo(data);
        setLoading(false);
      } catch (err) {
        setError('Failed to load user information');
        setLoading(false);
      }
    };

    fetchUserData();
  }, [accessToken]);

  // Sample data for the dashboard widgets
  const recentActivities = [
    { id: 1, action: 'Completed task', name: 'Update profile', time: '2 hours ago' },
    { id: 2, action: 'Created project', name: 'Marketing Campaign', time: '1 day ago' },
    { id: 3, action: 'Edited document', name: 'Q2 Report', time: '3 days ago' }
  ];

  if (loading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 rounded">
          <p>{error}</p>
          <button 
            className="mt-2 bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700"
            onClick={() => window.location.reload()}
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  // For demo purposes, use mock data if API hasn't returned yet
  const userDetails = userInfo || {
    name: user?.name || 'John Doe',
    email: user?.email || 'john.doe@example.com',
    photo: user?.photoURL || '/api/placeholder/150/150',
    contactNumber: '+1 (555) 123-4567',
    joinedDate: 'January, 2023',
    role: 'Team Member'
  };

  return (
    <div className="min-h-screen bg-gray-50 pb-12">
      {/* Header section */}
      <div className="bg-white shadow">
        <div className="container mx-auto px-4 py-6">
          <h1 className="text-2xl font-bold text-gray-800">Dashboard</h1>
          <p className="text-gray-600">Welcome back, {userDetails.name}</p>
        </div>
      </div>

      <div className="container mx-auto px-4 mt-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* User profile card */}
          <div className="bg-white rounded-lg shadow p-6 lg:col-span-1">
            <div className="flex flex-col items-center">
              <div className="w-24 h-24 relative mb-4">
                <img 
                  src={userDetails.image} 
                  alt="Profile" 
                  className="rounded-full object-cover w-full h-full border-2 border-blue-100"
                />
              </div>
              <h2 className="text-xl font-bold text-gray-800">{userDetails.name}</h2>
              <p className="text-gray-600">{userDetails.role}</p>
              
              <div className="w-full mt-6 space-y-4">
                <div className="flex items-center">
                  <Mail className="w-5 h-5 text-gray-500 mr-3" />
                  <span className="text-gray-700">{userDetails.email}</span>
                </div>
                <div className="flex items-center">
                  <Phone className="w-5 h-5 text-gray-500 mr-3" />
                  <span className="text-gray-700">{userDetails.phoneNo}</span>
                </div>
                <div className="flex items-center">
                  <Calendar className="w-5 h-5 text-gray-500 mr-3" />
                  <span className="text-gray-700">Joined {userDetails.joinedAt}</span>
                </div>
              </div>
              
              <Link to="/profile" className="mt-6 w-full text-center bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition">
                Edit Profile
              </Link>
            </div>
          </div>
          
          {/* Main content area */}
          <div className="lg:col-span-2">
            {/* Tabs */}
            <div className="bg-white rounded-lg shadow">
              <div className="flex border-b">
                <button 
                  className={`px-6 py-3 font-medium ${activeTab === 'overview' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-600'}`}
                  onClick={() => setActiveTab('overview')}
                >
                  Overview
                </button>
                <button 
                  className={`px-6 py-3 font-medium ${activeTab === 'tasks' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-600'}`}
                  onClick={() => setActiveTab('tasks')}
                >
                  Tasks
                </button>
                <button 
                  className={`px-6 py-3 font-medium ${activeTab === 'projects' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-600'}`}
                  onClick={() => setActiveTab('projects')}
                >
                  Projects
                </button>
              </div>
              
              {/* Tab content */}
              <div className="p-6">
                {activeTab === 'overview' && (
                  <div>
                    <h3 className="text-lg font-medium text-gray-800 mb-4">Overview</h3>
                    
                    {/* Quick stats */}
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                      <div className="bg-blue-50 p-4 rounded-lg">
                        <div className="flex items-center">
                          <div className="bg-blue-100 p-2 rounded-lg mr-3">
                            <FileText className="text-blue-600 w-5 h-5" />
                          </div>
                          <div>
                            <p className="text-sm text-gray-600">Tasks</p>
                            <p className="text-xl font-semibold text-gray-800">12</p>
                          </div>
                        </div>
                      </div>
                      
                      <div className="bg-green-50 p-4 rounded-lg">
                        <div className="flex items-center">
                          <div className="bg-green-100 p-2 rounded-lg mr-3">
                            <Activity className="text-green-600 w-5 h-5" />
                          </div>
                          <div>
                            <p className="text-sm text-gray-600">Completed</p>
                            <p className="text-xl font-semibold text-gray-800">8</p>
                          </div>
                        </div>
                      </div>
                      
                      <div className="bg-purple-50 p-4 rounded-lg">
                        <div className="flex items-center">
                          <div className="bg-purple-100 p-2 rounded-lg mr-3">
                            <Clock className="text-purple-600 w-5 h-5" />
                          </div>
                          <div>
                            <p className="text-sm text-gray-600">In Progress</p>
                            <p className="text-xl font-semibold text-gray-800">4</p>
                          </div>
                        </div>
                      </div>
                    </div>
                    
                    {/* Recent activity */}
                    <h3 className="text-md font-medium text-gray-800 mb-4">Recent Activity</h3>
                    <div className="space-y-4">
                      {recentActivities.map(activity => (
                        <div key={activity.id} className="flex items-start border-b border-gray-100 pb-3">
                          <div className="bg-gray-100 p-2 rounded-full mr-3">
                            <Activity className="w-4 h-4 text-gray-600" />
                          </div>
                          <div>
                            <p className="text-sm font-medium text-gray-800">
                              {activity.action}: <span className="text-blue-600">{activity.name}</span>
                            </p>
                            <p className="text-xs text-gray-500">{activity.time}</p>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                )}
                
                {activeTab === 'tasks' && (
                  <div>
                    <h3 className="text-lg font-medium text-gray-800 mb-4">Your Tasks</h3>
                    <p className="text-gray-600">Tasks will appear here.</p>
                  </div>
                )}
                
                {activeTab === 'projects' && (
                  <div>
                    <h3 className="text-lg font-medium text-gray-800 mb-4">Your Projects</h3>
                    <p className="text-gray-600">Projects will appear here.</p>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;