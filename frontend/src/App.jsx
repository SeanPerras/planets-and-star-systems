import { Routes, Route, NavLink } from "react-router-dom";
import { useState } from "react";
import Home from "./pages/Home";
import StarSystems from "./pages/StarSystems";
import StarSystemDetails from "./pages/StarSystemDetails";
import Planets from "./pages/Planets";
import PlanetDetails from "./pages/PlanetDetails";
import LoginModal from "./components/modals/LoginModal";

const activeStyle = { color: "yellow", textShadow: "0 0 4px #fff7b2, 0 0 8px #ffd700, 0 0 14px #ffae00" };

function App()
{
  const [isLoginVisible, setLoginVisible] = useState(false),
    [token, setToken] = useState(""),
    [user, setUser] = useState("");

  const getToken = () => token;

  function logOut()
  {
    setUser("");
    setToken("");
  }

  return (
    <div id="app" className="min-vh-100 d-flex flex-column bg-stars">
      <nav className="navbar navbar-expand-lg navbar-dark sticky-top flex-shrink-0 justify-content-center">
        <div className="navbar-nav align-items-center">
          <NavLink style={({ isActive }) => isActive ? activeStyle : {}}
            to="/" className="navbar-brand nav-link bg-dark bg-opacity-75 rounded">
            Home
          </NavLink>
          <NavLink style={({ isActive }) => isActive ? activeStyle : {}}
            to="/star-systems" className="navbar-brand nav-link bg-dark bg-opacity-75 rounded">
            Star Systems
          </NavLink>
          <NavLink style={({ isActive }) => isActive ? activeStyle : {}}
            to="/planets" className="navbar-brand nav-link bg-dark bg-opacity-75 rounded">
            Planets
          </NavLink>
        </div>
        <div className="nav-item position-absolute end-0 me-3">
          {user? <button type="button" onClick={logOut} className="btn btn-outline-light">{user}</button> :
            <button type="button" onClick={() => setLoginVisible(true)}
              className="btn btn-outline-light">
              Login
            </button>
          }
        </div>
      </nav>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/star-systems" element={<StarSystems currentUser={user} getToken={getToken} />} />
        <Route path="/star-systems/:ID" element={<StarSystemDetails currentUser={user} getToken={getToken} />} />
        <Route path="/planets" element={<Planets currentUser={user} getToken={getToken} />} />
        <Route path="/planets/:ID" element={<PlanetDetails currentUser={user} getToken={getToken} />} />
      </Routes>
      {isLoginVisible &&
        <>
        <LoginModal onClose={() => setLoginVisible(false)}
          onLogin={(tkn, usr) => {
            setToken(tkn);
            setUser(usr);
            setLoginVisible(false);
          }}/>
          {/* <div className="modal-backdrop show" /> */}
        </>
      }
    </div>
  );
}

export default App;


