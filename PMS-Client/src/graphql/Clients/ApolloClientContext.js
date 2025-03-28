// src/graphql/ApolloProvider/MultiApolloContext.js
import { createContext, useContext } from 'react';

export const ApolloClientsContext = createContext(null);

export const useApolloClients = () => {
  const context = useContext(ApolloClientsContext);
  if (!context) throw new Error('useApolloClients must be used inside MultiApolloProvider');
  return context;
};
