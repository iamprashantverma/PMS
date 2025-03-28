import "./App.css";
import NavBar from "./components/Common/NavBar";
import PublicRoutes from "./routes/PublicRoutes";
import UserRoutes from "./routes/UserRoutes";

function App() {
  return (
    <div className="grid grid-rows-[10%_90%] h-[100dvh] w-full overflow-hidden">
      {/* Row 1 - NavBar */}
      <div className="row-span-1">
        <NavBar />
      </div>

      {/* Row 2 - Routes Content */}
      <div className="row-span-1 overflow-y-auto">
        <PublicRoutes />
        <UserRoutes />
      </div>
    </div>
  );
}

export default App;
