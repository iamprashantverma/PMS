import React, { useState } from 'react';
import { ChevronDown, Users } from 'lucide-react';
import { useQuery } from '@apollo/client';
import { useApolloClients } from '@/graphql/Clients/ApolloClientContext';
import { GET_ALL_EPICS_BY_PROJECT_ID } from '@/graphql/Queries/task-service';

function Board({ projectId }) {
  const { taskClient } = useApolloClients();
  const [showEpics, setShowEpics] = useState(false);
  const [epicId,setEpicId] = useState('');

  const { loading, error, data: epicData } = useQuery(GET_ALL_EPICS_BY_PROJECT_ID, {
    client: taskClient,
    variables: { projectId },
  });

  const toggleEpicsVisibility = () => {
    setShowEpics((prev) => !prev);
  };

  if (loading) return <p>Loading...</p>;
  
  if (error) return <p>Error: {error.message}</p>;

  return (
    <div className="h-screen flex flex-col">
      {/* Fixed Top Section */}
      <div className="h-[25vh] w-full flex items-center justify-between p-4 bg-pink-200">
        <div className="flex flex-col w-[50%]">
          <h3 className="text-xl font-bold">CSS Board</h3>
          <div className="flex flex-row items-center justify-between p-4">
            <input
              placeholder="Search"
              className="p-2 border rounded h-[30px] w-[20%]"
            />
            <Users className="w-[8%]" size={24} color="black" />

            {/* Epic Button with Relative Wrapper */}
            <div className="relative w-[40%]">
              <button
                onClick={toggleEpicsVisibility}
                className="flex flex-row items-center space-x-1 w-full  p-2 "
              >
                <span className="text-xs">Epic</span>
                <ChevronDown size={16} />
              </button>

              {/* Epics List (toggle visibility) */}
              {showEpics && (
                <div 
                  className="absolute left-0 w-[250px] bg-white p-2 shadow-lg rounded-lg overflow-y-auto mt-1 border"
                  style={{ maxHeight: '200px', zIndex: 10 }}>
                  {epicData?.getAllEpicsByProjectId?.map((epic) => (
                    <div key={epic.id} onClick={()=>{setEpicId(epic.id);toggleEpicsVisibility()}} className="border-b py-1 text-sm text-gray-800">
                      {epic.title}
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>

        <div className="w-[50%] p-4 text-white">
          <p>hello</p>
        </div>
      </div>

      {/* Scrollable Bottom Section */}
      <div className="flex-1 mt-[1vh] overflow-y-auto bg-yellow-100 p-4">
        {/* Content for scrollable section */}
      </div>
    </div>
  );
}

export default Board;
