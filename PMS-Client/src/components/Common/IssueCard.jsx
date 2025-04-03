import React, { useEffect, useState } from 'react';
import TaskDetails from '../Task/TaskDetails';

function IssueCard({ task }) {

    if (!task) return null;
    
   

    // Priority color mapping
    const priorityColors = {
        high: 'text-red-600',
        medium: 'text-amber-600',
        low: 'text-emerald-600',
        default: 'text-gray-600'
    };
    

    const priorityColor = priorityColors[task.priority?.toLowerCase()] || priorityColors.default;

    return (
        <div className="p-4 border rounded-lg shadow-sm bg-white border-gray-200 hover:border-blue-300 transition-colors duration-200 w-full max-w-md">
            {/* Title */}
            <h3 className="text-base md:text-lg font-semibold text-gray-800 mb-2 line-clamp-2">
                {task.title}
            </h3>
            
            {/* Priority */}
            <div className="flex items-center gap-2 mb-2">
                <span className={`text-xs font-medium ${priorityColor}`}>
                    {task.priority || 'No priority'}
                </span>
                <span className="text-xs text-gray-400">•</span>
                <span className="text-xs font-medium text-gray-500">
                    {task.status || 'No status'}
                </span>
            </div>
            
            {/* Description */}
            {task.description && (
                <p className="text-sm text-gray-600 mb-3 line-clamp-3">
                    {task.description}
                </p>
            )}
            
            {/* Deadline */}
            {task.deadline && (
                <div className="flex items-center gap-1.5 mb-3">
                    <svg className="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                    <span className="text-xs font-medium text-gray-500">
                        Due {task.deadline}
                    </span>
                </div>
            )}
            
            {/* Assignee */}
            <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                    {task.assignees && task.assignees.length > 0 ? (
                        <>
                            <img 
                                src={task.assignees[0].image} 
                                alt={task.assignees[0].name} 
                                className="w-6 h-6 rounded-full border border-gray-200 object-cover" 
                            />
                            <span className="text-xs font-medium text-gray-700">
                                {task.assignees[0].name}
                            </span>
                        </>
                    ) : (
                        <div className="flex items-center justify-center w-6 h-6 bg-gray-100 rounded-full">
                            <span className="text-gray-400 text-xs">👤</span>
                        </div>
                    )}
                </div>
                
                {/* Completion percentage (if applicable) */}
                {task.completionPercent !== undefined && (
                    <div className="flex items-center gap-1.5">
                        <div className="w-16 h-1.5 bg-gray-200 rounded-full overflow-hidden">
                            <div 
                                className={`h-full rounded-full ${
                                    task.completionPercent >= 90 ? 'bg-emerald-500' :
                                    task.completionPercent >= 50 ? 'bg-blue-500' :
                                    task.completionPercent > 0 ? 'bg-amber-500' : 'bg-gray-400'
                                }`}
                                style={{ width: `${task.completionPercent}%` }}
                            />
                        </div>
                        <span className="text-xs font-medium text-gray-500">
                            {task.completionPercent}%
                        </span>
                    </div>
                )}
            </div>  
        </div>
    );
}

export default IssueCard;