import { ApolloClient, InMemoryCache } from '@apollo/client';

export const proejctClient = new ApolloClient({
  uri: 'http://localhost:9020/project/graphql',
  cache: new InMemoryCache()
});

