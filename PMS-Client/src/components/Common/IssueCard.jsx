import React from 'react';

function IssueCard({ task }) {
    if (!task) return null;

    return (
        <div className="p-3 sm:p-4 md:p-5 border-2 rounded-lg shadow-md bg-white border-blue-500 w-full max-w-xs sm:max-w-sm md:max-w-md lg:max-w-lg xl:max-w-xl">
            {/* Title */}
            <h3 className="text-sm sm:text-base md:text-lg lg:text-xl font-bold text-blue-700 mb-2">
                {task.title}
            </h3>
            
            {/* Priority */}
            <p className="text-xs sm:text-sm md:text-md font-semibold text-red-500">
                Priority: {task.priority}
            </p>
            
            {/* Description */}
            <p className="text-xs sm:text-sm text-gray-700 mt-1">
                {task.description}
            </p>
            
            {/* Deadline */}
            <p className="text-xs sm:text-sm text-purple-600 mt-1">
                Deadline: {task.deadline || 'Not set'}
            </p>
            
            {/* Assignee */}
            <div className="mt-2 sm:mt-3">
                {task.assignees && task.assignees.length > 0 ? (
                    <div className="flex items-center gap-2">
                        <img src={task.assignees[0].image} alt={task.assignees[0].name} 
                            className="w-6 h-6 sm:w-8 sm:h-8 rounded-full border border-gray-300" />
                        <span className="text-xs sm:text-sm font-medium text-gray-800">
                            {task.assignees[0].name}
                        </span>
                    </div>
                ) : (
                    <div className="flex justify-center items-center w-8 h-8 sm:w-10 sm:h-10 bg-gray-200 rounded-full">
                        <span className="text-gray-500 text-sm sm:text-md">👤</span>
                    </div>
                )}
            </div>
        </div>
    );
}

export default IssueCard;