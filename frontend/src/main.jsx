import React from 'react';
import { createRoot } from 'react-dom/client';
import './style.css';

function App() {
  return (
    <main className="welcome">
      <p className="eyebrow">ENSET · Security Operations</p>
      <h1>Security Incident Management</h1>
      <p>React frontend scaffold. Authentication and role-based screens will be implemented next.</p>
    </main>
  );
}

createRoot(document.getElementById('root')).render(
  <React.StrictMode><App /></React.StrictMode>,
);
