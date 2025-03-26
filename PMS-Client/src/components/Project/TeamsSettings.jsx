import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useQuery, useMutation } from '@apollo/client';
import { FIND_PROJECT_BY_ID } from '@/graphql/Queries/project-service';
import { ADD_MEMBERS_TO_PROJECT, REMOVE_USER_FROM_PROJECT } from '@/graphql/Mutation/project-service';
import { toast } from 'react-toastify';

function TeamsSettings() {
  const { projectId } = useParams();
  const [newMemberId, setNewMemberId] = useState('');
  const [members, setMembers] = useState([]);

  const { data, refetch } = useQuery(FIND_PROJECT_BY_ID, {
    variables: { projectId },
    skip: !projectId,
  });

  const [addMembersToProject] = useMutation(ADD_MEMBERS_TO_PROJECT);
  const [removeUserFromProject] = useMutation(REMOVE_USER_FROM_PROJECT);

  useEffect(() => {
    console.log('Fetched Members:', data?.getProject?.membersIds);
    if (data?.getProject?.memberIds) {
      setMembers(data.getProject.memberIds);
    }
  }, [data]);

  const handleAddMember = async () => {
    if (!newMemberId) return toast.warning('Please enter a Member ID');

    try {
      await addMembersToProject({
        variables: {
          projectId,
          members: [newMemberId],
        },
      });
      toast.success('Member added successfully');
      setNewMemberId('');
      refetch();
    } catch (err) {
      toast.error(err.message);
    }
  };

  const handleRemoveMember = async (memberId) => {
    try {
      console.log(memberId);
      await removeUserFromProject({
        variables: { projectId, memberId },
      });
      toast.success('Member removed');
      refetch();
    } catch (err) {
      toast.error(err.message);
    }
  };

  return (
    <div className="max-w-5xl mx-auto px-4 py-6">
      <h2 className="text-3xl font-bold text-blue-900 mb-6 text-center">Project Team Members</h2>

      {/* Add New Member */}
      <div className="flex flex-col sm:flex-row items-center gap-3 mb-8">
        <input
          type="text"
          value={newMemberId}
          onChange={(e) => setNewMemberId(e.target.value)}
          placeholder="Enter Member ID"
          className="flex-grow w-full sm:w-2/3 px-4 py-2 border border-blue-300 rounded-md focus:ring-2 focus:ring-blue-500"
        />
        <button
          onClick={handleAddMember}
          className="w-full sm:w-auto px-5 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition"
        >
          Add Member
        </button>
      </div>

      {/* Members List */}
      {members.length === 0 ? (
        <p className="text-center text-gray-600">No members found.</p>
      ) : (
        <div className="grid sm:grid-cols-2 md:grid-cols-3 gap-5">
          {members.map((member) => (
            <div
              key={member.userId}
              className="flex items-center justify-between p-4 bg-blue-50 border border-blue-200 rounded-xl shadow-sm"
            >
              <div className="flex items-center gap-3">
                <img
                  src={member.avatarUrl || 'https://via.placeholder.com/40'}
                  alt={member.name}
                  className="w-12 h-12 rounded-full object-cover border"
                />
                <div>
                  <p className="font-semibold text-blue-900">{member.name || 'Unnamed User'}</p>
                  <p className="text-sm text-blue-700">ID: {member.userId}</p>
                </div>
              </div>
              <button
                onClick={() => handleRemoveMember(member.userId)}
                className="text-xs bg-red-500 text-white px-3 py-1 rounded-md hover:bg-red-600"
              >
                Remove
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default TeamsSettings;
