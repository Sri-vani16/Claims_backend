import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import { ClaimProvider } from './context/ClaimContext';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <ClaimProvider>
      <App />
    </ClaimProvider>
  </React.StrictMode>
);