import React, { useState, useEffect, useRef } from "react";
import { useAppContext } from "@/context/AppContext";
import { useNavigate } from "react-router-dom";
import WorkDropDown from "../Work/WorkDropDown";
import NotificationDropDown from "./NotificationDropDown";
import { CheckSquare } from "lucide-react";
import {
  User,
  ChevronDown,
  Bell,
  HelpCircle,
  Settings,
  ChevronRight,
  LogOut as LogOutIcon,
} from "lucide-react";
import { Link } from "react-router-dom";
import ProjectDropDown from "../Project/ProjectDropDown";
import CreateTaskForm from "./CreateTaskForm";
import { useAuth } from "@/context/AuthContext";
import { toast } from 'react-toastify';

function NavBar() {
  const navigate = useNavigate();
  const [showMore, setShowMore] = useState(false);
  const [showUserDropdown, setShowUserDropdown] = useState(false);
  const [showLogOut, setShowLogOut] = useState(false);

  const { setDropDown, dropDown, setOpen, open } = useAppContext();
  const dropdownRef = useRef(null);
  const userDropdownRef = useRef(null);
  const [createOpen, setCreateOpen] = useState(false);
  const { user,logout } = useAuth();

  const handleDropDownToggle = (menu) => {
    if (dropDown === menu && open) {
      setOpen(false);
      setDropDown(null);
    } else {
      setDropDown(menu);
      setOpen(true);
    }
  };

  // Navigate to dashboard route
  const handleDashboardClick = () => {
    navigate("/dashboard");
    setShowMore(false);
  };

  // Handle user icon click
  const handleUserIconClick = () => {
    setShowUserDropdown(!showUserDropdown);
  };

 const handleLogoutClick = () => {
  setShowUserDropdown(false);
  logout()
};


  // Close dropdowns when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setShowMore(false);
      }
      if (userDropdownRef.current && !userDropdownRef.current.contains(event.target)) {
        setShowUserDropdown(false);
      }
    };
    
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  // Render logout page if showLogOut is true
  if (showLogOut) {
    return <LogOut />;
  }

  return (
    <nav
      className="flex items-center justify-between z-50 h-[8dvh] sm:h-[9dvh] md:h-[10dvh] w-full bg-gray-50 text-gray-800 shadow-md px-2 sm:px-4 md:px-8 fixed"
      ref={dropdownRef}
    >
      {/* Left Side */}
      <div className="flex items-center flex-1 gap-2 sm:gap-4">
        <div className="flex items-center space-x-1 sm:space-x-2">
          <CheckSquare className="h-5 w-5 sm:h-6 sm:w-6 md:h-8 md:w-8" />
          <span className="text-sm sm:text-base md:text-xl font-bold">TaskFlow</span>
        </div>
        
        {createOpen && <CreateTaskForm setCreateOpen={setCreateOpen} />}
        
        {/* Full Menu for Desktop */}
        <div className="ml-2 sm:ml-4 md:ml-[40px] hidden md:flex gap-4 lg:gap-9 text-sm lg:text-md">
          <WorkDropDown />
          
          <div
            key="Project"
            className="flex items-center text-gray-700 hover:text-blue-600 focus:outline-none px-2 py-1 md:px-3 md:py-2"
            onClick={() => handleDropDownToggle("project")}
          >
            <p className="mr-1 font-medium">Project</p>
            <ChevronDown className="w-3 h-3 md:w-4 md:h-4" />
          </div>
          
          <div
            key="Dashboards"
            className="flex items-center text-gray-700 hover:text-blue-600 focus:outline-none px-2 py-1 md:px-3 md:py-2"
            onClick={handleDashboardClick}
          >
            <p className="mr-1 font-medium">Dashboards</p>
            
          </div>

          <button
            onClick={() => setCreateOpen(true)}
            className="bg-blue-600 text-white px-3 py-1 rounded-md hover:bg-blue-700 transition text-xs sm:text-sm"
          >
            Create
          </button>
        </div>
      </div>

      {/* Right Side */}
      <div className="flex items-center justify-end gap-2 xs:gap-4 sm:gap-6 md:gap-8 w-auto sm:w-[40%] md:w-[30%] relative">
        {/* Mobile More Dropdown */}
        <div className="md:hidden relative">
          <button
            onClick={() => setShowMore(!showMore)}
            className="flex items-center gap-1 text-xs sm:text-sm"
          >
            More <ChevronDown className="w-3 h-3 sm:w-4 sm:h-4" />
          </button>
          {showMore && (
            <div className="absolute right-0 top-8 bg-white shadow-lg rounded-md w-36 sm:w-48 z-50">
              <div className="py-1">
                <div
                  className="flex items-center justify-between px-3 py-2 text-xs sm:text-sm cursor-pointer hover:bg-gray-100"
                  onClick={() => {
                    WorkDropDown();
                    setShowMore(false);
                  }}
                >
                  <p>Work</p>
                  <ChevronRight className="w-3 h-3 sm:w-4 sm:h-4" />
                </div>
                
                <div
                  className="flex items-center justify-between px-3 py-2 text-xs sm:text-sm cursor-pointer hover:bg-gray-100"
                  onClick={() => {
                    handleDropDownToggle("project");
                    setShowMore(false);
                  }}
                >
                  <p>Project</p>
                  <ChevronRight className="w-3 h-3 sm:w-4 sm:h-4" />
                </div>
                
                <div
                  className="flex items-center justify-between px-3 py-2 text-xs sm:text-sm cursor-pointer hover:bg-gray-100"
                  onClick={handleDashboardClick}
                >
                  <p>Dashboards</p>
                  <ChevronRight className="w-3 h-3 sm:w-4 sm:h-4" />
                </div>
                
                <div
                  className="flex items-center px-3 py-2 text-xs sm:text-sm cursor-pointer hover:bg-gray-100"
                  onClick={() => {
                    setCreateOpen(true);
                    setShowMore(false);
                  }}
                >
                  <p>Create</p>
                </div>
              </div>
            </div>
          )}
        </div>

        {/* Right Icons */}
        <div >
          <NotificationDropDown />
        </div>
        
        <Link to="/support" className="hidden sm:block">
          <HelpCircle className="w-4 h-4 sm:w-5 sm:h-5 text-gray-600 hover:text-blue-600" />
        </Link>
        
        <Link to="/profile">
          <Settings className="w-4 h-4 sm:w-5 sm:h-5 text-gray-600 hover:text-blue-600" />
        </Link>
        
        {user?.id && (
          <div className="relative" ref={userDropdownRef}>
            <div 
              className="flex items-center space-x-1 sm:space-x-2 bg-gray-100 px-2 py-2 sm:px-3 rounded-full shadow-sm hover:bg-gray-200 transition-all cursor-pointer"
              onClick={handleUserIconClick}
            >
              <User className="w-4 h-4 sm:w-5 sm:h-5 text-gray-600" />
              <span className="text-xs sm:text-sm font-medium text-gray-800 hidden xs:block">
                {user.name}
              </span>
              <ChevronDown className="w-3 h-3 sm:w-4 sm:h-4 text-gray-600" />
            </div>
            
            {/* User Dropdown */}
            {showUserDropdown && (
              <div className="absolute right-0 top-12 bg-white shadow-lg rounded-md w-40 sm:w-48 z-50 border">
                <div className="py-1">
                  <div
                    className="flex items-center gap-2 px-3 py-2 text-xs sm:text-sm cursor-pointer hover:bg-gray-100 text-red-600 hover:text-red-700"
                    onClick={handleLogoutClick}
                  >
                    <LogOutIcon className="w-3 h-3 sm:w-4 sm:h-4" />
                    <p>Logout</p>
                  </div>
                </div>
              </div>
            )}
          </div>
        )}
      </div>

     
      {dropDown === "project" && open && <ProjectDropDown />}
    </nav>
  );
}

export default NavBar;