import React, { useEffect, useState } from 'react';
import { Search, ChevronLeft, ChevronRight } from 'lucide-react';
import { useParams } from 'react-router-dom';
import { getCalendarEvents } from '@/services/activity-tracker-service';
import { useAuth } from '@/context/AuthContext';

const TaskCalendar = () => {
    const { accessToken } = useAuth();
    const { projectId } = useParams();

    const [calendarEvents, setCalendarEvents] = useState([]);
    const [currentMonth, setCurrentMonth] = useState(new Date().getMonth());
    const [currentYear, setCurrentYear] = useState(new Date().getFullYear());
    const [searchTerm, setSearchTerm] = useState('');
    const [statusFilter, setStatusFilter] = useState('All');
    const [eventTypeFilter, setEventTypeFilter] = useState('All');
  
    useEffect(() => {
        const fetchEvents = async () => {
            try {
                const {data} = await getCalendarEvents(projectId, accessToken);
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
            }
        };

        if (projectId) {
            fetchEvents();
        }
    }, [projectId, accessToken]);
    
    const getDaysInMonth = (month, year) => new Date(year, month + 1, 0).getDate();
    const getFirstDayOfMonth = (month, year) => new Date(year, month, 1).getDay();

    const prevMonth = () => {
        if (currentMonth === 0) {
            setCurrentMonth(11);
            setCurrentYear(currentYear - 1);
        } else {
            setCurrentMonth(currentMonth - 1);
        }
    };

    const nextMonth = () => {
        if (currentMonth === 11) {
            setCurrentMonth(0);
            setCurrentYear(currentYear + 1);
        } else {
            setCurrentMonth(currentMonth + 1);
        }
    };

    const monthNames = [
        'January', 'February', 'March', 'April', 'May', 'June',
        'July', 'August', 'September', 'October', 'November', 'December'
    ];

    const filteredTasks = (tasks) => {
        return tasks.filter(task => 
            (statusFilter === 'All' || task.status === statusFilter) &&
            (eventTypeFilter === 'All' || task.eventType === eventTypeFilter) &&
            (searchTerm === '' || task.title.toLowerCase().includes(searchTerm.toLowerCase()))
        );
    };

    const generateCalendarDays = () => {
        const daysInMonth = getDaysInMonth(currentMonth, currentYear);
        const firstDay = getFirstDayOfMonth(currentMonth, currentYear);
        const days = [];

        for (let i = 0; i < firstDay; i++) {
            days.push(<div key={`empty-${i}`} className="h-48 border p-1 bg-gray-50"></div>);
        }

        for (let day = 1; day <= daysInMonth; day++) {
            const dateString = `${currentYear}-${String(currentMonth + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
            const dayTasks = filteredTasks(calendarEvents.filter(event => event.deadline === dateString));

            days.push(
                <div key={day} className="h-48 border p-2 overflow-y-auto">
                    <div className="text-right text-gray-500 text-sm mb-2">{day}</div>
                    {dayTasks.map(task => (
                        <div key={task.id} className="mb-2 p-2 rounded text-sm shadow-sm" style={{ 
                            backgroundColor: getEventTypeColor(task.eventType),
                            borderLeft: `4px solid ${getPriorityColor(task.priority)}`
                        }}>
                            <div className="font-medium truncate mb-1">{task.title}</div>
                            
                            {/* Event Type Badge */}
                            <div className="flex items-center gap-1 mb-1 flex-wrap">
                                <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${getEventTypeBadgeColor(task.eventType)}`}>
                                    {task.eventType}
                                </span>
                            </div>
                            
                            {/* Status Indicator */}
                            <div className="flex items-center gap-1 mb-1 flex-wrap">
                                <span className="text-xs text-gray-600">Status:</span>
                                <span className={`px-1 text-xs py-0.5 rounded-full ${getStatusBadgeColor(task.status)}`}>
                                    {task.status}
                                </span>
                            </div>
                            
                            {/* Completion Percentage */}
                            <div className="flex items-center gap-1 flex-wrap">
                                <span className="text-xs text-gray-600">Progress:</span>
                                <div className="flex-1 flex items-center gap-1">
                                    <div className="w-full h-2 bg-gray-200 rounded-full">
                                        <div 
                                            className="h-2 rounded-full" 
                                            style={{ 
                                                width: `${task.completionPercent || 0}%`,
                                                backgroundColor: getCompletionColor(task.completionPercent)
                                            }}
                                        ></div>
                                    </div>
                                    <span className="text-xs font-medium whitespace-nowrap">
                                        {task.completionPercent || 0}%
                                    </span>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            );
        }

        return days;
    };

    const getEventTypeColor = (eventType) => {
        switch (eventType) {
            case 'BUG': return 'rgba(239, 68, 68, 0.1)';
            case 'TASK': return 'rgba(59, 130, 246, 0.1)';
            case 'SUBTASK': return 'rgba(34, 197, 94, 0.1)';
            default: return 'rgba(209, 213, 219, 0.1)';
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

    const getCompletionColor = (percent) => {
        if (percent === null || percent === undefined) return '#EF4444';
        if (percent >= 90) return '#10B981'; 
        if (percent >= 50) return '#3B82F6';
        if (percent > 0) return '#F59E0B';
        return '#EF4444'; 
    };

    return (
        <div className="container mx-auto p-4 max-w-7xl">
            <h1 className="text-2xl font-bold text-gray-700 mb-6">Calendar</h1>

            <div className="flex flex-col md:flex-row justify-between items-center mb-6 gap-4">
                <div className="relative w-full md:w-64">
                    <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                        <Search size={18} className="text-gray-400" />
                    </div>
                    <input 
                        type="text" 
                        className="bg-white border border-gray-300 text-gray-900 text-sm rounded-lg w-full pl-10 p-2.5"
                        placeholder="Search tasks..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </div>

                <div className="flex flex-wrap gap-3 w-full md:w-auto">
                    <select 
                        className="bg-white border border-gray-300 text-gray-900 text-sm rounded-lg p-2.5"
                        value={statusFilter}
                        onChange={(e) => setStatusFilter(e.target.value)}
                    >
                        <option value="All">All Status</option>
                        <option value="TODO">TO DO</option>
                        <option value="IN_PROGRESS">IN Progress</option>
                        <option value="COMPLETED">Completed</option>
                    </select>

                    <select 
                        className="bg-white border border-gray-300 text-gray-900 text-sm rounded-lg p-2.5"
                        value={eventTypeFilter}
                        onChange={(e) => setEventTypeFilter(e.target.value)}
                    >
                        <option value="All">All Event Types</option>
                        <option value="TASK">Task</option>
                        <option value="SUBTASK">SubTask</option>
                        <option value="BUG">Bug</option>
                    </select>
                </div>

                <div className="flex items-center gap-4 w-full md:w-auto justify-end">
                    <button className="p-2 rounded-full hover:bg-gray-100" onClick={prevMonth}>
                        <ChevronLeft size={20} />
                    </button>
                    <div className="text-lg font-medium">
                        {monthNames[currentMonth]} {currentYear}
                    </div>
                    <button className="p-2 rounded-full hover:bg-gray-100" onClick={nextMonth}>
                        <ChevronRight size={20} />
                    </button>
                </div>
            </div>

            <div className="grid grid-cols-7 gap-px bg-gray-100 border border-gray-100 rounded-lg overflow-hidden">
                {['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'].map(day => (
                    <div key={day} className="text-center font-medium p-3 bg-gray-100 text-gray-700">
                        {day}
                    </div>
                ))}
                {generateCalendarDays()}
            </div>
        </div>
    );
};

export default TaskCalendar;