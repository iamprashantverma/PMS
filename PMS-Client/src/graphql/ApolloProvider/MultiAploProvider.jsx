import React, { useMemo } from 'react';
import { ApolloClientsContext } from '../Clients/ApolloClientContext';
import { createProjectClient, createTaskClient, createNotificationClient } from '../Clients/apolloClients';
import { useAuth } from '@/context/AuthContext';

const MultiApolloProvider = ({ children }) => {
  const { accessToken } = useAuth();

  const { projectClient, taskClient, notificationClient } = useMemo(() => {
    return {
      projectClient: createProjectClient(accessToken),
      taskClient: createTaskClient(accessToken),
      notificationClient: createNotificationClient(accessToken),
    };
  }, [accessToken]);

  return (
    <ApolloClientsContext.Provider value={{ projectClient, taskClient, notificationClient }}>
      {children}
    </ApolloClientsContext.Provider>
  );
};

export default MultiApolloProvider;
