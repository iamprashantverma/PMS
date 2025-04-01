import { ApolloClient, InMemoryCache, HttpLink, split } from '@apollo/client';
import { setContext } from '@apollo/client/link/context';
import { GraphQLWsLink } from '@apollo/client/link/subscriptions';
import { createClient } from 'graphql-ws';
import { getMainDefinition } from '@apollo/client/utilities';
import { createUploadLink } from 'apollo-upload-client';

// Function to create Project Apollo Client
export const createProjectClient = (accessToken) => {
  // Use createUploadLink instead of HttpLink for file upload support
  const httpLink = createUploadLink({
    uri: 'http://localhost:8080/api/v1/projects/graphql',
    headers: {
      Authorization: accessToken ? `Bearer ${accessToken}` : '',
    },
  });

  return new ApolloClient({
    link: httpLink,
    cache: new InMemoryCache(),
  });
};

export const createTaskClient = (accessToken) => {
  // HTTP link with upload support
  const httpLink = createUploadLink({
    uri: 'http://localhost:8080/api/v1/tasks/graphql',
    headers: {
      Authorization: accessToken ? `Bearer ${accessToken}` : '',
    },
  });

  // WebSocket link for subscriptions (remains unchanged)
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

  // Split HTTP and WebSocket requests
  const splitLink = split(
    ({ query }) => {
      const definition = getMainDefinition(query);
      return definition.kind === 'OperationDefinition' && definition.operation === 'subscription';
    },
    wsLink,
    httpLink // No need for authLink.concat() since we're using createUploadLink
  );

  return new ApolloClient({
    link: splitLink,
    cache: new InMemoryCache(),
  });
};