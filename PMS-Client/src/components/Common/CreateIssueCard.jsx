import React, { useState } from 'react';


function CreateIssueCard() {
    const [input, setInput] = useState('');

    const handleCreate = () => {
        if (input.trim()) {
            console.log("Issue Created:", input);
            setInput(''); 
        }
    };

    return (
        <div className="max-w-[250px] p-2 border-2 border-blue-500 rounded-md">
            <input
                value={input}
                onChange={(e) => setInput(e.target.value)}
                name="input"
                placeholder="Enter issue"
                className="w-full p-1 border border-blue-300 rounded-md mb-2"
            />
            <button
                onClick={handleCreate}
                className="w-full p-1 bg-blue-500 text-white rounded-md hover:bg-blue-600"
            >
                Create
            </button>
        </div>
    );
}

export default CreateIssueCard;
