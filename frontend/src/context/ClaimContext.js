import React, { createContext, useContext, useReducer } from 'react';

const ClaimContext = createContext(null);

const initialState = {
  currentClaim: null,
  submissionResult: null,
  loading: false,
  error: null
};

function claimReducer(state, action) {
  switch (action.type) {
    case 'SET_LOADING':
      return { ...state, loading: action.payload };
    case 'SET_ERROR':
      return { ...state, error: action.payload, loading: false };
    case 'SET_CURRENT_CLAIM':
      return { ...state, currentClaim: action.payload };
    case 'SET_SUBMISSION_RESULT':
      return { ...state, submissionResult: action.payload, loading: false };
    case 'CLEAR_ERROR':
      return { ...state, error: null };
    case 'RESET':
      return initialState;
    default:
      return state;
  }
}

export function ClaimProvider({ children }) {
  const [state, dispatch] = useReducer(claimReducer, initialState);

  const value = {
    ...state,
    dispatch
  };

  return (
    <ClaimContext.Provider value={value}>
      {children}
    </ClaimContext.Provider>
  );
}

export function useClaim() {
  const context = useContext(ClaimContext);
  if (context === undefined || context === null) {
    throw new Error('useClaim must be used within a ClaimProvider');
  }
  return context;
}