
import React, { useMemo } from 'react';
import { ApolloProvider } from '@apollo/client';
import { useAuth } from '@/context/AuthContext';
import { createProjectClient } from '../Clients/apolloClients';

const ProjectApolloProvider = ({ children }) => {

  const { accessToken } = useAuth();

  const client = useMemo(() => createProjectClient(accessToken), [accessToken]);

  return <ApolloProvider client={client}>{children}</ApolloProvider>;
};

export default ProjectApolloProvider;
