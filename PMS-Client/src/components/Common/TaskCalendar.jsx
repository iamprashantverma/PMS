import React, { useEffect, useState } from 'react';
import Calendar from 'react-calendar';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { useParams } from 'react-router-dom';
import { getCalendarEvents } from '@/services/activity-tracker-service';
import { useAuth } from '@/context/AuthContext';


const calendarStyles = `
  .react-calendar {
    width: 100%;
    border: none;
    font-family: Arial, sans-serif;
  }
  
  .react-calendar__navigation {
    height: 60px;
    margin-bottom: 20px;
  }
  
  .react-calendar__navigation button {
    min-width: 44px;
    font-size: 18px;
    background: none;
  }
  
  .react-calendar__navigation button:enabled:hover,
  .react-calendar__navigation button:enabled:focus {
    background-color: #f0f0f0;
    border-radius: 8px;
  }
  
  .react-calendar__month-view__weekdays {
    font-weight: bold;
    font-size: 1rem;
    padding: 8px 0;
  }
  
  .react-calendar__tile {
    height: 80px;
    padding: 16px 6px;
    font-size: 16px;
    position: relative;
  }
  
  .react-calendar__tile:enabled:hover,
  .react-calendar__tile:enabled:focus,
  .react-calendar__tile--active {
    background-color: #f0f9ff;
    border-radius: 8px;
  }
  
  .react-calendar__tile--now {
    background-color: #e6f7ff;
    border-radius: 8px;
  }
`;

const TaskCalendar = () => {
    const { accessToken } = useAuth();
    const { projectId } = useParams();

    // State management
    const [calendarEvents, setCalendarEvents] = useState([]);
    const [currentDate, setCurrentDate] = useState(new Date());
    const [isLoading, setIsLoading] = useState(true);
    const [filters, setFilters] = useState({
        status: 'All',
        eventType: 'All'
    });

    // Fetch events data
    useEffect(() => {
        const fetchEvents = async () => {
            if (!projectId) return;
            
            setIsLoading(true);
            try {
                const { data } = await getCalendarEvents(projectId, accessToken);
               
                const formattedEvents = Array.isArray(data) ? data.map(task => ({
                    id: task.id,
                    title: task.title || "Untitled Task",
                    status: task.newStatus || "TODO",
                    priority: task.priority || "Low",
                    deadline: task.deadLine,
                    completionPercent: task.completionPercent || 0,
                    eventType: task.eventType || "TASK",
                })) : [];
                setCalendarEvents(formattedEvents);
            } catch (error) {
                console.error('Failed to fetch calendar events:', error);
                setCalendarEvents([]);
            } finally {
                setIsLoading(false);
            }
        };

        fetchEvents();
    }, [projectId, accessToken]);

    // Filter tasks based on current criteria
    const filteredTasks = calendarEvents.filter(task => 
        (filters.status === 'All' || task.status === filters.status) &&
        (filters.eventType === 'All' || task.eventType === filters.eventType)
    );

    // Get tasks for a specific date
    const getTasksForDate = (date) => {
        const dateString = date.toISOString().split('T')[0];
        return filteredTasks.filter(task => task.deadline === dateString);
    };

    // Handle date change
    const handleDateChange = (date) => {
        setCurrentDate(date);
    };

    // Calendar tile content - shows dots for dates with tasks
    const tileContent = ({ date, view }) => {
        if (view !== 'month') return null;
        
        const dateString = date.toISOString().split('T')[0];
        const tasksForDay = filteredTasks.filter(task => task.deadline === dateString);
        
        if (tasksForDay.length === 0) return null;
        
        return (
            <div className="absolute bottom-2 left-0 right-0 flex justify-center">
                {tasksForDay.slice(0, 4).map((task, index) => (
                    <div 
                        key={index}
                        className="w-2.5 h-2.5 rounded-full mx-0.5"
                        style={{ backgroundColor: getEventTypeColor(task.eventType, true) }}
                    />
                ))}
                {tasksForDay.length > 4 && (
                    <div className="text-xs font-medium ml-1">+{tasksForDay.length - 4}</div>
                )}
            </div>
        );
    };

    // Style helpers
    const getEventTypeColor = (eventType, solid = false) => {
        const opacity = solid ? '1' : '0.1';
        switch (eventType) {
            case 'BUG': return `rgba(239, 68, 68, ${opacity})`;
            case 'TASK': return `rgba(59, 130, 246, ${opacity})`;
            case 'SUBTASK': return `rgba(34, 197, 94, ${opacity})`;
            default: return `rgba(209, 213, 219, ${opacity})`;
        }
    };

    const getEventTypeBadgeColor = (eventType) => {
        switch (eventType) {
            case 'BUG': return 'bg-red-100 text-red-800';
            case 'TASK': return 'bg-blue-100 text-blue-800';
            case 'SUBTASK': return 'bg-green-100 text-green-800';
            default: return 'bg-gray-100 text-gray-800';
        }
    };

    const getStatusBadgeColor = (status) => {
        switch (status) {
            case 'COMPLETED': return 'bg-green-100 text-green-800';
            case 'IN_PROGRESS': return 'bg-yellow-100 text-yellow-800';
            case 'TODO': return 'bg-gray-100 text-gray-800';
            default: return 'bg-gray-100 text-gray-800';
        }
    };

    const getPriorityColor = (priority) => {
        if (!priority) return 'rgb(107, 114, 128)';
        
        switch (priority) {
            case 'High': return 'rgb(239, 68, 68)';
            case 'Medium': return 'rgb(245, 158, 11)';
            case 'Low': return 'rgb(34, 197, 94)';
            default: return 'rgb(107, 114, 128)';
        }
    };

    // Get tasks for the selected date
    const selectedDateTasks = getTasksForDate(currentDate);
    const formattedSelectedDate = currentDate.toLocaleDateString('en-US', {
        weekday: 'short',
        month: 'short',
        day: 'numeric'
    });

    return (
        <div className="container mx-auto py-6 px-4 overflow-hidden">
            <style>{calendarStyles}</style>
            
            <div className="bg-white rounded-xl shadow-lg overflow-hidden">
                <div className="grid grid-cols-1 lg:grid-cols-4">
                    {/* Calendar - Larger size */}
                    <div className="lg:col-span-3 p-6">
                        <div className="flex justify-between items-center mb-6">
                            <h1 className="text-2xl font-bold text-gray-800">Project Calendar</h1>
                            <div className="flex gap-2">
                                <select 
                                    className="bg-white border border-gray-300 text-gray-800 text-sm rounded-lg p-2"
                                    value={filters.status}
                                    onChange={(e) => setFilters({...filters, status: e.target.value})}
                                >
                                    <option value="All">All Status</option>
                                    <option value="TODO">TO DO</option>
                                    <option value="IN_PROGRESS">IN Progress</option>
                                    <option value="COMPLETED">Completed</option>
                                </select>
                                
                                <select 
                                    className="bg-white border border-gray-300 text-gray-800 text-sm rounded-lg p-2"
                                    value={filters.eventType}
                                    onChange={(e) => setFilters({...filters, eventType: e.target.value})}
                                >
                                    <option value="All">All Types</option>
                                    <option value="TASK">Task</option>
                                    <option value="SUBTASK">SubTask</option>
                                    <option value="BUG">Bug</option>
                                </select>
                            </div>
                        </div>
                        
                        <Calendar
                            onChange={handleDateChange}
                            value={currentDate}
                            tileContent={tileContent}
                            className="w-full h-full"
                            nextLabel={<ChevronRight size={24} />}
                            prevLabel={<ChevronLeft size={24} />}
                            next2Label={null}
                            prev2Label={null}
                        />
                    </div>
                    
                    {/* Task Details - Compact sidebar */}
                    <div className="lg:col-span-1 border-l p-4 bg-gray-50">
                        <h2 className="text-lg font-semibold mb-3 flex items-center">
                            <span className="w-3 h-3 rounded-full bg-blue-500 mr-2"></span>
                            {formattedSelectedDate}
                        </h2>
                        
                        {isLoading ? (
                            <div className="flex justify-center items-center h-20">
                                <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-gray-900"></div>
                            </div>
                        ) : selectedDateTasks.length > 0 ? (
                            <div className="space-y-2 max-h-96 overflow-y-auto pr-2">
                                {selectedDateTasks.map(task => (
                                    <div 
                                        key={task.id} 
                                        className="p-3 rounded-lg border-l-4 shadow-sm bg-white" 
                                        style={{ borderLeftColor: getPriorityColor(task.priority) }}
                                    >
                                        <div className="font-medium text-sm mb-1 truncate">{task.title}</div>
                                        
                                        <div className="flex flex-wrap gap-1 mb-1">
                                            <span className={`text-xs px-1.5 py-0.5 rounded-full font-medium ${getEventTypeBadgeColor(task.eventType)}`}>
                                                {task.eventType}
                                            </span>
                                            
                                            <span className={`text-xs px-1.5 py-0.5 rounded-full ${getStatusBadgeColor(task.status)}`}>
                                                {task.status}
                                            </span>
                                            
                                        </div>
                                        
                                        <div className="w-full bg-gray-200 rounded-full h-1.5">
                                            <div 
                                                className="h-1.5 rounded-full" 
                                                style={{ 
                                                    width: `${task.completionPercent}%`,
                                                    backgroundColor: getPriorityColor(task.priority) 
                                                }}
                                            />
                                        </div>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <div className="flex flex-col items-center justify-center h-24 text-gray-500 text-sm">
                                <p>No tasks for this date</p>
                            </div>
                        )}
                        
                        <div className="mt-4 border-t pt-4">
                            <div className="text-xs text-gray-500 mb-2">Task Types:</div>
                            <div className="flex flex-wrap gap-2">
                                <div className="flex items-center gap-1">
                                    <div className="w-2.5 h-2.5 rounded-full" style={{backgroundColor: getEventTypeColor('TASK', true)}}></div>
                                    <span className="text-xs">Task</span>
                                </div>
                                <div className="flex items-center gap-1">
                                    <div className="w-2.5 h-2.5 rounded-full" style={{backgroundColor: getEventTypeColor('SUBTASK', true)}}></div>
                                    <span className="text-xs">Subtask</span>
                                </div>
                                <div className="flex items-center gap-1">
                                    <div className="w-2.5 h-2.5 rounded-full" style={{backgroundColor: getEventTypeColor('BUG', true)}}></div>
                                    <span className="text-xs">Bug</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default TaskCalendar;