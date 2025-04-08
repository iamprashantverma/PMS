import React, { useState, useEffect } from 'react';
import { useAuth } from '@/context/AuthContext';
import { 
  getUserDetails, 
  updateBugUpdates, 
  updateCommentMentions, 
  updateTaskUpdates, 
  updateEmailUpdates,
  updateSubTaskUpdates
} from '@/services/UserService';
import { Bell, MessageSquare, Bug, Mail, CheckCircle, XCircle, ListChecks } from 'lucide-react';

function UserCustomSettings() {
  const { user, accessToken } = useAuth();
  const [settings, setSettings] = useState({});
  const [isLoading, setIsLoading] = useState(true);
  const [pendingUpdates, setPendingUpdates] = useState({});
  const [notification, setNotification] = useState({ visible: false, message: '', type: '' });
  
  const SETTING_DEFINITIONS = [
    {
      key: 'commentMentions',
      icon: MessageSquare,
      title: 'Comment Mentions',
      description: 'Get notified when someone mentions you in a comment',
      updateFn: updateCommentMentions
    },
    {
      key: 'taskUpdates',
      icon: CheckCircle,
      title: 'Task Updates',
      description: 'Receive updates for tasks you\'re assigned to',
      updateFn: updateTaskUpdates
    },
    {
      key: 'subTaskUpdates',
      icon: ListChecks,
      title: 'Subtask Updates',
      description: 'Get notifications for subtask changes in your tasks',
      updateFn: updateSubTaskUpdates
    },
    {
      key: 'bugUpdates',
      icon: Bug,
      title: 'Bug Updates',
      description: 'Stay informed about bug reports and fixes',
      updateFn: updateBugUpdates
    },
    {
      key: 'emailUpdates',
      icon: Mail,
      title: 'Email Updates',
      description: 'Receive email notifications along with in-app alerts',
      updateFn: updateEmailUpdates
    }
  ];
  
  useEffect(() => {
    async function loadUserSettings() {
      if (!user?.id || !accessToken) return;
      
      try {
        setIsLoading(true);
        const response = await getUserDetails(user.id, accessToken);
        
        if (response?.data) {
          const userSettings = {};
          SETTING_DEFINITIONS.forEach(setting => {
            userSettings[setting.key] = response.data[setting.key] || false;
          });
          setSettings(userSettings);
        }
      } catch (error) {
        showNotification('Could not load your notification settings. Please try again.', 'error');
        console.error('Error loading user settings:', error);
      } finally {
        setIsLoading(false);
      }
    }

    loadUserSettings();
  }, [user?.id, accessToken]);

  const handleToggle = async (settingKey) => {
    if (!user?.id || !accessToken) {
      showNotification('Authentication error. Please log in again.', 'error');
      return;
    }
    
    if (pendingUpdates[settingKey]) return;

    const newValue = !settings[settingKey];
    const settingDef = SETTING_DEFINITIONS.find(s => s.key === settingKey);
    
    if (!settingDef || !settingDef.updateFn) {
      showNotification('Error: Setting update function not found.', 'error');
      return;
    }
    
    setSettings(prev => ({ ...prev, [settingKey]: newValue }));
    setPendingUpdates(prev => ({ ...prev, [settingKey]: true }));
    
    try {
      const response = await settingDef.updateFn(user.id, newValue, accessToken);
      
      if (response?.data) {
        const settingName = settingDef.title;
        showNotification(`${settingName} ${newValue ? 'enabled' : 'disabled'} successfully`, 'success');
      } else {
        setSettings(prev => ({ ...prev, [settingKey]: !newValue }));
        throw new Error('Failed to update setting');
      }
    } catch (error) {
      console.error(`Error updating ${settingKey}:`, error);
      showNotification(`Failed to update setting. Please try again.`, 'error');
      setSettings(prev => ({ ...prev, [settingKey]: !newValue }));
    } finally {
      setPendingUpdates(prev => ({ ...prev, [settingKey]: false }));
    }
  };

  const showNotification = (message, type) => {
    setNotification({ visible: true, message, type });
    setTimeout(() => {
      setNotification(prev => ({ ...prev, visible: false }));
    }, 3000);
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="flex flex-col items-center">
          <div className="animate-spin rounded-full h-10 w-10 border-4 border-indigo-500 border-t-transparent"></div>
          <p className="mt-4 text-gray-600">Loading your notification settings...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto p-6 bg-white rounded-lg shadow-md">
      <header className="flex items-center mb-8">
        <Bell className="text-indigo-600 mr-3" size={24} />
        <h2 className="text-2xl font-bold text-gray-800">Notification Settings</h2>
      </header>

      <div className="space-y-6">
        {SETTING_DEFINITIONS.map(({ key, icon: Icon, title, description }) => (
          <div key={key} className="p-4 bg-gray-50 rounded-lg transition duration-300 hover:bg-gray-100">
            <div className="flex justify-between items-center">
              <div className="flex items-start">
                <Icon className="text-indigo-500 mt-1 mr-3" size={20} />
                <div>
                  <h3 className="text-lg font-medium text-gray-800">{title}</h3>
                  <p className="text-gray-600 text-sm">{description}</p>
                </div>
              </div>
              <button
                onClick={() => handleToggle(key)}
                disabled={pendingUpdates[key]}
                className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 ${settings[key] ? 'bg-indigo-600' : 'bg-gray-300'}`}
                aria-label={`Toggle ${title}`}
              >
                {pendingUpdates[key] ? (
                  <span className="absolute inset-0 flex items-center justify-center">
                    <span className="h-4 w-4 rounded-full border-2 border-t-transparent border-white animate-spin"></span>
                  </span>
                ) : (
                  <span 
                    className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${settings[key] ? 'translate-x-6' : 'translate-x-1'}`} 
                  />
                )}
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Toast Notification */}
      {notification.visible && (
        <div 
          className={`fixed bottom-4 right-4 p-4 rounded-md shadow-lg transition-all duration-300 flex items-center ${
            notification.type === 'success' ? 'bg-green-500' : 'bg-red-500'
          }`}
          role="alert"
        >
          {notification.type === 'success' ? (
            <CheckCircle className="text-white mr-2" size={20} />
          ) : (
            <XCircle className="text-white mr-2" size={20} />
          )}
          <span className="text-white font-medium">{notification.message}</span>
        </div>
      )}
    </div>
  );
}

export default UserCustomSettings;