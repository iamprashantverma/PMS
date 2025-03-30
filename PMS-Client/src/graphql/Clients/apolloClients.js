import { ApolloClient, InMemoryCache, HttpLink, split } from '@apollo/client';
import { setContext } from '@apollo/client/link/context';
import { GraphQLWsLink } from '@apollo/client/link/subscriptions';
import { createClient } from 'graphql-ws'; // GraphQL WebSocket Client
import { getMainDefinition } from '@apollo/client/utilities';

// Function to create Project Apollo Client (only HTTP)
export const createProjectClient = (accessToken) => {
  const httpLink = new HttpLink({
    uri: 'http://localhost:8080/api/v1/projects/graphql', // Your HTTP endpoint
  });

  // Authorization link for HTTP
  const authLink = setContext((_, { headers }) => ({
    headers: {
      ...headers,
      Authorization: accessToken ? `Bearer ${accessToken}` : '',
    },
  }));

  return new ApolloClient({
    link: authLink.concat(httpLink), 
    cache: new InMemoryCache(),
  });
};


export const createTaskClient = (accessToken) => {
  const httpLink = new HttpLink({
    uri: 'http://localhost:8080/api/v1/tasks/graphql', 
  });


  const wsLink = new GraphQLWsLink(
    createClient({
      url: 'ws://localhost:8080/api/v1/tasks/graphql', 
      connectionParams: {
        headers: {
          Authorization: accessToken ? `Bearer ${accessToken}` : '', 
        },
      },
    })
  );

  // Authorization link for HTTP
  const authLink = setContext((_, { headers }) => ({
    headers: {
      ...headers,
      Authorization: accessToken ? `Bearer ${accessToken}` : '',
    },
  }));

  // Split HTTP and WebSocket requests (only use WebSocket for subscriptions)
  const splitLink = split(
    ({ query }) => {
      const definition = getMainDefinition(query);
      return definition.kind === 'OperationDefinition' && definition.operation === 'subscription';
    },
    wsLink,  
    authLink.concat(httpLink) 
  );

  return new ApolloClient({
    link: splitLink, 
    cache: new InMemoryCache(),
  });
};
