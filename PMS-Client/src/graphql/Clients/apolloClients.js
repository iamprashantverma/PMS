// src/graphql/Clients/apolloClients.js
import { ApolloClient, InMemoryCache } from '@apollo/client';
import { createUploadLink } from 'apollo-upload-client';
import { setContext } from '@apollo/client/link/context';

export const createProjectClient = (accessToken) => {
  console.log(accessToken)
  const authLink = setContext((_, { headers }) => ({
    headers: {
      ...headers,
      Authorization: accessToken ? `Bearer ${accessToken}` : '',
    },
  }));

  const uploadLink = createUploadLink({
    uri: 'http://localhost:8080/api/v1/projects/graphql',
  });

  return new ApolloClient({
    link: authLink.concat(uploadLink),
    cache: new InMemoryCache(),
  });
};
