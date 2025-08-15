import { ApolloClient, InMemoryCache, split } from '@apollo/client';
import { createUploadLink } from 'apollo-upload-client';
import { GraphQLWsLink } from '@apollo/client/link/subscriptions';
import { createClient } from 'graphql-ws';
import { getMainDefinition } from '@apollo/client/utilities';
import toast from 'react-hot-toast';

export const createProjectClient = (accessToken) => {
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
  const httpLink = createUploadLink({
    uri: 'http://localhost:8080/api/v1/tasks/graphql',
    headers: {
      Authorization: accessToken ? `Bearer ${accessToken}` : '',
    },
  });

  const wsLink = new GraphQLWsLink(
    createClient({
      url: `ws://localhost:8080/api/v1/tasks/graphql?Bearer=${accessToken}`,
      connectionParams: async () => ({
        headers: {
          Authorization: accessToken ? `Bearer ${accessToken}` : '',
        }
      }),
      lazy: true,
      retryAttempts: Infinity,
      shouldRetry: () => true,
      retryWait: async (retryCount) => {
        const delay = Math.min(1000 * Math.pow(2, retryCount), 30000);
        await new Promise(resolve => setTimeout(resolve, delay));
      },
      keepAlive: 30000,
      connectionAckTimeout: 10000,
      on: {
        connected: () => toast.success(" Live Task Feedback ON!"),
        closed: () => toast.error("Live Task Feedback OFF!. Reconnecting..."),
        error: (err) => toast.error("Task WebSocket error:", err),
      }
    })
  );

  const splitLink = split(
    ({ query }) => {
      const definition = getMainDefinition(query);
      return definition.kind === 'OperationDefinition' && definition.operation === 'subscription';
    },
    wsLink,
    httpLink
  );

  return new ApolloClient({
    link: splitLink,
    cache: new InMemoryCache(),
  });
};

export const createNotificationClient = (accessToken) => {
  const httpLink = createUploadLink({
    uri: 'http://localhost:8080/api/v1/notification/graphql',
    headers: {
      Authorization: accessToken ? `Bearer ${accessToken}` : '',
    },
  });

  const wsLink = new GraphQLWsLink(
    createClient({
      url: `ws://localhost:8080/api/v1/notification/graphql?Bearer=${accessToken}`,
      connectionParams: async () => ({
        headers: {
          Authorization: accessToken ? `Bearer ${accessToken}` : '',
        }
      }),
      lazy: true,
      retryAttempts: Infinity,
      shouldRetry: () => true,
      retryWait: async (retryCount) => {
        const delay = Math.min(1000 * Math.pow(2, retryCount), 30000);
        await new Promise(resolve => setTimeout(resolve, delay));
      },
      keepAlive: 30000,
      connectionAckTimeout: 10000,
      on: {
        connected: () => toast.success("Live Notification ON!"),
        closed: () => toast.error("Live Notification OFF!. Reconnecting..."),
        error: (err) => toast.error("Notification WebSocket error:", err),
      }
    })
  );

  const splitLink = split(
    ({ query }) => {
      const definition = getMainDefinition(query);
      return definition.kind === 'OperationDefinition' && definition.operation === 'subscription';
    },
    wsLink,
    httpLink
  );

  return new ApolloClient({
    link: splitLink,
    cache: new InMemoryCache(),
  });
};
