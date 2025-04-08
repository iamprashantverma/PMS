import React, { useState } from 'react'
import UserCustomSettings from '@/components/Common/UserCustomSettings'
import ProfileSettings from '@/pages/ProfileSettings'

function UserSetting() {
  const [activeTab, setActiveTab] = useState('profile')
  const [sidebarOpen, setSidebarOpen] = useState(false)

  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen)
  }

  return (
    <div className="flex flex-col md:flex-row min-h-[90dvh] bg-gray-50">
      {/* Mobile Header with Menu Toggle */}
      <div className="md:hidden bg-white shadow-sm p-4 flex items-center justify-between">
        <h2 className="text-lg font-bold text-gray-800">Account Settings</h2>
        <button 
          onClick={toggleSidebar}
          className="p-2 text-gray-600 hover:bg-gray-100 rounded-lg"
        >
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 6h16M4 12h16M4 18h16"></path>
          </svg>
        </button>
      </div>

      {/* Sidebar - Fixed on desktop, slideout on mobile */}
      <div className={`
        ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'} 
        md:translate-x-0
        fixed md:static top-0 left-0 h-full z-40 md:z-0
        w-64 bg-white shadow-sm flex-shrink-0 border-r border-gray-100
        transition-transform duration-300 ease-in-out
      `}>
        <div className="p-6">
          <h2 className="text-xl font-bold text-gray-800 mb-6">Account Settings</h2>
          <button 
            onClick={toggleSidebar}
            className="md:hidden absolute top-4 right-4 p-2 text-gray-600 hover:bg-gray-100 rounded-lg"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>
        <nav className="px-3">
          <button
            className={`w-full flex items-center px-4 py-3 mb-2 rounded-lg transition-all duration-200 ${
              activeTab === 'profile'
                ? 'bg-blue-50 text-blue-600 font-medium border-l-4 border-blue-500'
                : 'text-gray-600 hover:bg-gray-50'
            }`}
            onClick={() => {
              setActiveTab('profile')
              if (window.innerWidth < 768) setSidebarOpen(false)
            }}
          >
            <svg className="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
            </svg>
            Profile Settings
          </button>
          <button
            className={`w-full flex items-center px-4 py-3 mb-2 rounded-lg transition-all duration-200 ${
              activeTab === 'custom'
                ? 'bg-blue-50 text-blue-600 font-medium border-l-4 border-blue-500'
                : 'text-gray-600 hover:bg-gray-50'
            }`}
            onClick={() => {
              setActiveTab('custom')
              if (window.innerWidth < 768) setSidebarOpen(false)
            }}
          >
            <svg className="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"></path>
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
            </svg>
            Custom Settings
          </button>
        </nav>
      </div>

      {/* Overlay for mobile sidebar */}
      {sidebarOpen && (
        <div 
          className="md:hidden fixed inset-0 bg-black bg-opacity-50 z-30"
          onClick={() => setSidebarOpen(false)}
        ></div>
      )}

      {/* Content Area */}
      <div className="flex-1 overflow-y-auto">
        <div className="p-4 md:p-6">
          <div className="overflow-y-auto">
            {activeTab === 'profile' ? (
              <div className="transition-opacity duration-300 ease-in-out">
                <ProfileSettings />
              </div>
            ) : (
              <div className="transition-opacity duration-300 ease-in-out">
                <UserCustomSettings />
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}

export default UserSetting