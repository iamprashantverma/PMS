import React, { useState, useEffect, useRef } from "react";
import { useAppContext } from "@/context/AppContext";
import { useNavigate } from "react-router-dom";
import WorkDropDown from "../Work/WorkDropDown";
import NotificationDropDown from "./NotificationDropDown";
import UserCustomSettings from "./UserCustomSettings";
import {
  User,
  ChevronDown,
  Bell,
  HelpCircle,
  Settings,
  ChevronRight,
} from "lucide-react";
import { Link } from "react-router-dom";
import ProjectDropDown from "../Project/ProjectDropDown";
import CreateTaskForm from "./CreateTaskForm";
import { useAuth } from "@/context/AuthContext";
function NavBar() {
  const navigate = useNavigate();
  const [showMore, setShowMore] = useState(false);
  const [notificationCount, setNotificationCount] = useState(6);
  const { setDropDown, dropDown, setOpen, open } = useAppContext();
  const dropdownRef = useRef(null);
  const [createOpen, setCreateOpen] = useState(false);
  const { user } = useAuth();

  const handleDropDownToggle = (menu) => {
    if (dropDown === menu && open) {
      setOpen(false);
      setDropDown(null);
    } else {
      setDropDown(menu);
      setOpen(true);
    }
  };

  return (
    <nav
      className="flex items-center justify-between z-50 h-[10dvh] w-full bg-gray-50 text-gray-800 shadow-md px-4 sm:px-8 fixed"
      ref={dropdownRef}
    >
      {/* Left Side */}
      <div className="flex items-center flex-1 gap-4">
        <Link
          to="/"
          className="text-xl sm:text-2xl font-semibold text-blue-600"
        >
          PSM
        </Link>
        {createOpen && <CreateTaskForm setCreateOpen={setCreateOpen} />}
        {/* Full Menu for Desktop */}
        <div className="ml-[40px] hidden md:flex gap-9 text-md">
          <WorkDropDown />
          {["Project", "Dashboards"].map((item) => (
            <div
              key={item}
              className="flex items-center text-gray-700 hover:text-blue-600 focus:outline-none px-3 py-2"
              onClick={() => handleDropDownToggle(item.toLowerCase())}
            >
              <p className="mr-1 font-medium">{item}</p>
              <ChevronDown className="w-4 h-4" />
            </div>
          ))}

          <button
            onClick={() => setCreateOpen(true)}
            className="bg-blue-600 text-white px-4 py-1 rounded-md hover:bg-blue-700 transition"
          >
            Create
          </button>
        </div>
      </div>

      {/* Right Side */}
      <div className="flex items-center justify-end gap-8 md:w-[30%] w-[50%] relative">
        {/* Mobile More Dropdown */}
        <div className="md:hidden relative">
          <button
            onClick={() => setShowMore(!showMore)}
            className="flex items-center gap-1 text-sm"
          >
            More <ChevronDown className="w-4 h-4" />
          </button>
          {showMore && (
            <div className="absolute right-0 top-10 bg-white shadow-lg rounded-md w-48 z-50">
              {["Project", "Dashboards", "Create"].map((item) => (
                <div
                  key={item}
                  className="flex items-center justify-between px-4 py-2 text-sm cursor-pointer hover:bg-gray-100"
                  onClick={() => {
                    if (item === "Create") {
                      setCreateOpen(true);
                    } else {
                      handleDropDownToggle(item.toLowerCase());
                    }
                    setShowMore(false);
                  }}
                >
                  <p>{item}</p>
                  {item !== "Create" && <ChevronRight className="w-4 h-4" />}
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Right Icons */}

        <NotificationDropDown />
        {/* </Link> */}
        <Link to="/support">
          <HelpCircle className="w-6 h-6 sm:w-5 sm:h-5 text-gray-600 hover:text-blue-600" />
        </Link>
        <Link to="/profile">
          <Settings className="w-6 h-6 sm:w-5 sm:h-5 text-gray-600 hover:text-blue-600" />
        </Link>
        {user?.id && (
          <div className="flex items-center space-x-2 bg-gray-100 px-3 py-3 rounded-full shadow-sm hover:bg-gray-200 transition-all">
            <User className="w-5 h-5 text-gray-600" />
            <span className="text-sm font-medium text-gray-800">
              {user.name}
            </span>
          </div>
        )}
      </div>

      {/* Dropdown Components */}
      {dropDown === "project" && open && <ProjectDropDown />}
    </nav>
  );
}

export default NavBar;
