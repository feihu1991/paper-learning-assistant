
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './pages/Home/Home';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/paper/:id" element={<div>Paper Detail Page</div>} />
        <Route path="/profile" element={<div>User Profile Page</div>} />
      </Routes>
    </Router>
  );
}

export default App;