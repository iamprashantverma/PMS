import React, { useMemo } from 'react';
import { ApolloClientsContext } from '../Clients/ApolloClientContext';
import { createProjectClient, createTaskClient } from '../Clients/apolloClients';
import { useAuth } from '@/context/AuthContext';

const MultiApolloProvider = ({ children }) => {
  const { accessToken } = useAuth();

  const { projectClient, taskClient } = useMemo(() => {
    return {
      projectClient: createProjectClient(accessToken),
      taskClient: createTaskClient(accessToken),
    };
  }, [accessToken]);

  return (
    <ApolloClientsContext.Provider value={{ projectClient, taskClient }}>
      {children}
    </ApolloClientsContext.Provider>
  );
};

export default MultiApolloProvider;
