import "./App.css";
import NavBar from "./components/Common/NavBar";
import PublicRoutes from "./routes/PublicRoutes";
import UserRoutes from "./routes/UserRoutes";
import { useAuth } from "./context/AuthContext";
function App() {
  const {user} = useAuth();
  return (
    <div className="grid grid-rows-[10%_90%] h-[100dvh] w-full overflow-hidden">
     
    { <div className="row-span-1">
        { <NavBar />}
      </div>}
      <div className="row-span-1 overflow-y-auto">
        <PublicRoutes />
        <UserRoutes />
      </div>
    </div>
  );
}

export default App;
