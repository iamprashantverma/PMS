import "./App.css";
import NavBar from "./components/NavBar";
import DetailSettings from "./components/Project/DetailSettings";
import PublicRoutes from "./routes/PublicRoutes";
function App() {
  return (
    <div className="relative">
      <NavBar/>
      <PublicRoutes/>
      <DetailSettings/>
    </div>
  );
}

export default App;
