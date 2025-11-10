import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import SubmitClaim from './components/SubmitClaim';
import './App.css';

function App() {
  return (
    <Router>
      <div className="App">
        <header className="App-header">
          <h1>Claims Fraud Detection System</h1>
        </header>
        <main>
          <Routes>
            <Route path="/" element={<SubmitClaim />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;