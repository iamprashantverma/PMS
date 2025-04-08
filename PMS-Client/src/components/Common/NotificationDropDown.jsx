import React, { useState, useEffect, useRef } from 'react'
import { useAuth } from '@/context/AuthContext'
import { READ_SINGLE_NOTIFICATION, READ_ALL_NOTIFICATION } from '@/graphql/Mutation/notification-service'
import { useApolloClients } from '@/graphql/Clients/ApolloClientContext'
import { GET_LATEST_NOTIFICATION } from '@/graphql/Subscription/notification-service'
import { getUserDetails } from '@/services/UserService'
import { useMutation, useSubscription } from '@apollo/client'
import { Bell, Check, Trash, X, AlertCircle, Clock, ArrowRight } from 'lucide-react'

function NotificationDropDown() {
  const { user, accessToken } = useAuth()
  const userId = user?.id
  const { notificationClient } = useApolloClients()
  
  const [notifications, setNotifications] = useState([])
  const [isOpen, setIsOpen] = useState(false)
  const [userNames, setUserNames] = useState({})
  const dropdownRef = useRef(null)

  // Subscription: Listen for latest notification
  const { data: subscriptionData } = useSubscription(GET_LATEST_NOTIFICATION, {
    client: notificationClient,
    variables: { userId },
    onData: ({ data }) => {
      const latest = data?.data?.getLatestNotification
      if (latest) {
        setNotifications(prev => [latest, ...prev])
        fetchUserName(latest.updatedBy)
      }
    }
  })

  // Mutations
  const [readSingleNotification] = useMutation(READ_SINGLE_NOTIFICATION, { client: notificationClient })
  const [readAllNotifications] = useMutation(READ_ALL_NOTIFICATION, { client: notificationClient })

  const fetchUserName = async (updatedById) => {
    if (!updatedById || userNames[updatedById]) return
    try {
      const { data } = await getUserDetails(updatedById, accessToken)
      setUserNames(prev => ({
        ...prev,
        [updatedById]: data.name || 'Unknown User',
      }))
    } catch (error) {
      console.error(`Error fetching user details for ID ${updatedById}:`, error)
      setUserNames(prev => ({
        ...prev,
        [updatedById]: 'Unknown User',
      }))
    }
  }

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false)
      }
    }
    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  const handleReadSingleNotification = async (notificationId) => {
    try {
      const id = parseInt(notificationId, 10) 
      await readSingleNotification({ variables: { id } })
      setNotifications(prev => prev.filter(n => n.notificationId !== notificationId))
    } catch (error) {
      console.error('Error marking notification as read:', error)
    }
  }
  

  const handleReadAllNotifications = async () => {
    try {
      await readAllNotifications({ variables: { userId } })
      setNotifications([])
    } catch (error) {
      console.error('Error marking all notifications as read:', error)
    }
  }

  const formatRelativeTime = (dateString) => {
    if (!dateString) return 'recently'
    const date = new Date(dateString)
    const now = new Date()
    const diff = Math.floor((now - date) / 1000)
    if (diff < 60) return `${diff}s ago`
    if (diff < 3600) return `${Math.floor(diff / 60)}m ago`
    if (diff < 86400) return `${Math.floor(diff / 3600)}h ago`
    return `${Math.floor(diff / 86400)}d ago`
  }

  const getNotificationMessage = (notification) => {
    const updatedByName = userNames[notification.updatedBy] || 'Someone'
    switch (notification.action) {
      case 'STATUS_CHANGED':
        return (
          <span>
            <span className="font-medium">{notification.eventType}</span> status changed from{' '}
            <span className="font-medium">{notification.oldStatus.replace(/_/g, ' ')}</span> to{' '}
            <span className="font-medium">{notification.newStatus.replace(/_/g, ' ')}</span> by {updatedByName}
          </span>
        )
      case 'ASSIGNED':
        return (
          <span>
            You were assigned to <span className="font-medium">{notification.eventType}</span>{' '}
            "{notification.title}" by {updatedByName}
          </span>
        )
      case 'UNASSIGNED':
        return (
          <span>
            You were unassigned from <span className="font-medium">{notification.eventType}</span>{' '}
            "{notification.title}" by {updatedByName}
          </span>
        )
      case 'PRIORITY_CHANGED':
        return (
          <span>
            <span className="font-medium">{notification.eventType}</span> priority changed to{' '}
            <span className="font-medium">{notification.priority}</span> by {updatedByName}
          </span>
        )
      case 'COMMENTED':
        return (
          <span>
            {updatedByName} commented on <span className="font-medium">{notification.eventType}</span>{' '}
            "{notification.title}"
          </span>
        )
      default:
        return (
          <span>
            New notification for <span className="font-medium">{notification.eventType}</span>{' '}
            "{notification.title}"
          </span>
        )
    }
  }

  const getNotificationIcon = (notification) => {
    switch (notification.action) {
      case 'STATUS_CHANGED':
        return <ArrowRight className="text-blue-500" size={18} />
      case 'ASSIGNED':
      case 'UNASSIGNED':
        return <Check className="text-green-500" size={18} />
      case 'PRIORITY_CHANGED':
        return <AlertCircle className={getPriorityColor(notification.priority)} size={18} />
      case 'COMMENTED':
        return <Bell className="text-purple-500" size={18} />
      default:
        return <Bell className="text-gray-500" size={18} />
    }
  }

  const getPriorityColor = (priority) => {
    switch (priority) {
      case 'HIGH': return 'text-red-500'
      case 'MEDIUM': return 'text-orange-500'
      case 'LOW': return 'text-green-500'
      default: return 'text-gray-500'
    }
  }

  return (
    <div className="relative" ref={dropdownRef}>
      <button 
        className="relative p-2 text-gray-600 hover:text-blue-500 transition-colors rounded-full hover:bg-gray-100 focus:outline-none"
        onClick={() => setIsOpen(!isOpen)}
        aria-label="Notifications"
      >
        <Bell size={24} />
        {notifications.length > 0 && (
          <span className="absolute top-0 right-0 flex items-center justify-center w-5 h-5 bg-red-500 text-white text-xs font-bold rounded-full">
            {notifications.length > 9 ? '9+' : notifications.length}
          </span>
        )}
      </button>

      {isOpen && (
        <div className="absolute right-0 mt-2 w-80 md:w-96 bg-white rounded-lg shadow-lg overflow-hidden z-50 border border-gray-200">
          <div className="flex items-center justify-between px-4 py-3 bg-gray-50 border-b border-gray-200">
            <h3 className="font-medium text-gray-700">Notifications</h3>
            {notifications.length > 0 && (
              <button 
                onClick={handleReadAllNotifications}
                className="text-xs text-blue-500 hover:text-blue-700 flex items-center gap-1"
              >
                <Check size={14} />
                Mark all as read
              </button>
            )}
          </div>

          <div className="max-h-80 overflow-y-auto">
            {notifications.length === 0 ? (
              <div className="flex flex-col items-center justify-center p-8 text-gray-500">
                <Bell size={36} className="text-gray-300 mb-2" />
                <p>No new notifications</p>
              </div>
            ) : (
              notifications.map(notification => (
                <div 
                  key={notification.notificationId} 
                  className="p-4 border-b border-gray-100 hover:bg-gray-50 transition-colors"
                >
                  <div className="flex">
                    <div className="flex-shrink-0 mr-3">
                      {getNotificationIcon(notification)}
                    </div>
                    <div className="flex-1">
                      <div className="flex justify-between">
                        <div className="pr-8">
                          <p className="text-sm text-gray-800">
                            {getNotificationMessage(notification)}
                          </p>
                          <div className="flex items-center mt-1">
                            {notification.deadline && (
                              <>
                                <Clock size={12} className="text-gray-400 mr-1" />
                                <span className="text-xs text-gray-400 mr-2">
                                  Due: {new Date(notification.deadline).toLocaleDateString()}
                                </span>
                              </>
                            )}
                            <span className="text-xs text-gray-400">
                              {formatRelativeTime(notification.updatedDate || notification.createdDate)}
                            </span>
                          </div>
                        </div>
                        <button 
                          onClick={() => handleReadSingleNotification(notification.notificationId)}
                          className="ml-1 text-gray-400 hover:text-gray-600 flex-shrink-0"
                          title="Mark as read"
                        >
                          <X size={16} />
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>

          {notifications.length > 0 && (
            <div className="p-3 bg-gray-50 border-t border-gray-100 text-center">
              <button 
                onClick={handleReadAllNotifications}
                className="w-full py-2 px-4 bg-blue-500 hover:bg-blue-600 text-white text-sm font-medium rounded transition-colors flex items-center justify-center gap-2"
              >
                <Trash size={16} />
                Clear all notifications
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  )
}

export default NotificationDropDown
