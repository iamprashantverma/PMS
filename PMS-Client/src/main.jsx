// main.jsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import App from './App.jsx';
import AuthProvider from './context/AuthContext'; 
import { AppProvider } from './context/AppContext.jsx';
import MultiApolloProvider from './graphql/ApolloProvider/MultiAploProvider.jsx';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <AppProvider>
          <MultiApolloProvider>
            <App />
            <ToastContainer />
          </MultiApolloProvider>
        </AppProvider>
      </AuthProvider>
    </BrowserRouter>
  </React.StrictMode>
);
