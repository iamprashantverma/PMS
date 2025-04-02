import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useQuery, useMutation } from '@apollo/client';
import { FIND_PROJECT_BY_ID } from '@/graphql/Queries/project-service';
import { ADD_MEMBERS_TO_PROJECT, REMOVE_USER_FROM_PROJECT } from '@/graphql/Mutation/project-service';
import { toast } from 'react-toastify';
import { useAuth } from '@/context/AuthContext';
import { getUserDetails } from '@/services/UserService';
import { useApolloClients } from '@/graphql/Clients/ApolloClientContext';
import { EnvelopeIcon } from '@heroicons/react/24/outline';

function TeamsSettings() {
  const { projectId } = useParams();
  const [newMemberId, setNewMemberId] = useState('');
  const [email, setEmail] = useState('');
  const [members, setMembers] = useState([]);
  const [activeTab, setActiveTab] = useState('id'); // 'id' or 'email'
  const { accessToken } = useAuth();
  const { projectClient } = useApolloClients();
  
  const { data, refetch } = useQuery(FIND_PROJECT_BY_ID, {
    client: projectClient,
    variables: { projectId },
    skip: !projectId,
  });

  const [addMembersToProject] = useMutation(ADD_MEMBERS_TO_PROJECT, { client: projectClient });
  const [removeUserFromProject] = useMutation(REMOVE_USER_FROM_PROJECT, { client: projectClient });

  useEffect(() => {
    const fetchMemberDetails = async () => {
      if (data?.getProject?.memberIds) {
        const results = await Promise.allSettled(
          data.getProject.memberIds.map((id) => getUserDetails(id, accessToken))
        );
        const successfulMembers = results
          .filter(result => result.status === 'fulfilled')
          .map(result => result.value.data);
        setMembers(successfulMembers);
      }
    };
    fetchMemberDetails();
  }, [data, accessToken]);

  const handleAddMember = async () => {
    if (activeTab === 'id' && !newMemberId) {
      return toast.warning('Please enter a Member ID');
    }
    if (activeTab === 'email' && !email) {
      return toast.warning('Please enter a valid email');
    }

    try {
      // In a real app, you would send the email invitation here
      // For demo purposes, we'll just show a success message
      if (activeTab === 'email') {
        toast.success(`Invitation sent to ${email}`);
        setEmail('');
        return;
      }

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

  const handleRemoveMember = async (member) => {
    try {
      await removeUserFromProject({
        variables: { projectId, memberId: member.userId },
      });
      toast.success('Member removed');
      refetch();
    } catch (err) {
      toast.error(err.message);
    }
  };

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <h2 className="text-2xl font-bold text-gray-800 mb-8">Team Management</h2>

      {/* Add Member Tabs */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-8">
        <div className="flex border-b border-gray-200 mb-6">
          <button
            className={`px-4 py-2 font-medium text-sm ${activeTab === 'id' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-500 hover:text-gray-700'}`}
            onClick={() => setActiveTab('id')}
          >
            Add by User ID
          </button>
          <button
            className={`px-4 py-2 font-medium text-sm ${activeTab === 'email' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-500 hover:text-gray-700'}`}
            onClick={() => setActiveTab('email')}
          >
            Invite by Email
          </button>
        </div>

        {activeTab === 'id' ? (
          <div className="flex flex-col sm:flex-row gap-3">
            <input
              type="text"
              value={newMemberId}
              onChange={(e) => setNewMemberId(e.target.value)}
              placeholder="Enter User ID"
              className="flex-grow px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
            <button
              onClick={handleAddMember}
              className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
            >
              Add Member
            </button>
          </div>
        ) : (
          <div className="flex flex-col sm:flex-row gap-3">
            <div className="relative flex-grow">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <EnvelopeIcon className="h-5 w-5 text-gray-400" />
              </div>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Enter email address"
                className="pl-10 w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
            <button
              onClick={handleAddMember}
              className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
            >
              Send Invite
            </button>
          </div>
        )}
      </div>

      {/* Members List */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
        <div className="px-6 py-4 border-b border-gray-200">
          <h3 className="text-lg font-medium text-gray-800">Current Team Members</h3>
          {members.length === 0 && (
            <p className="text-gray-500 mt-2">No members have been added yet</p>
          )}
        </div>

        {members.length > 0 && (
          <ul className="divide-y divide-gray-200">
            {members.map((member) => (
              <li key={member.userId} className="px-6 py-4">
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-4">
                    <img
                      src={member.image || 'https://ui-avatars.com/api/?name=' + encodeURIComponent(member.name || 'U')}
                      alt={member.name}
                      className="w-10 h-10 rounded-full object-cover"
                    />
                    <div>
                      <p className="font-medium text-gray-900">{member.name || 'Unnamed User'}</p>
                      <p className="text-sm text-gray-500">ID: {member.userId}</p>
                    </div>
                  </div>
                  <button
                    onClick={() => handleRemoveMember(member)}
                    className="px-3 py-1 text-sm text-red-600 hover:text-red-800 border border-red-200 hover:border-red-300 rounded-md transition-colors"
                  >
                    Remove
                  </button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}

export default TeamsSettings;