import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import App from './App.jsx';
import AuthProvider from './context/AuthContext'; 
import { proejctClient } from './graphql/Clients/apolloClients.js';
import { ApolloProvider } from '@apollo/client';
import { AppProvider } from './context/AppContext.jsx';


ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <AppProvider>
        <ApolloProvider client={proejctClient}>
              <App/>
          </ApolloProvider>
          <ToastContainer />
        </AppProvider>
      </AuthProvider>
    </BrowserRouter>
  </React.StrictMode>
);
