import "./App.css";
import NavBar from "./components/Common/NavBar";
import PublicRoutes from "./routes/PublicRoutes";
import UserRoutes from "./routes/UserRoutes";
import { useAuth } from "./context/AuthContext";
function App() {
  const {user} = useAuth();
  return (
    <div className=" h-[100dvh] w-full overflow-hidden">
     
     {user &&  <div className=" h-[10%] row-span-1">
        { <NavBar />}
        </div> 
        }
        
      <div className=" h-[90%] row-span-1 overflow-y-auto">
        <PublicRoutes />
        <UserRoutes />
      </div>
    </div>
  );
}

export default App;
