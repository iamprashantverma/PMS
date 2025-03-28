import React, { useState, useEffect, useRef } from 'react';
import { useAppContext } from '@/context/AppContext';
import { useNavigate } from 'react-router-dom';
import {
  User,
  ChevronDown,
  Bell,
  HelpCircle,
  Settings,
  ChevronRight,
} from 'lucide-react';
import { Link } from 'react-router-dom';
import ProjectDropDown from '../Project/ProjectDropDown';

function NavBar() {

  const navigate  = useNavigate();
  const [showMore, setShowMore] = useState(false);
  const [notificationCount, setNotificationCount] = useState(6);
  const { setDropDown, dropDown, setOpen, open } = useAppContext();
  const dropdownRef = useRef(null);


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
      className="flex items-center justify-between z-50 h-[10dvh] w-full bg-gray-50 shadow-sm px-4 sm:px-8 fixed"
      ref={dropdownRef}
    >
      {/* Left Side */}
      <div className="flex items-center flex-1 gap-4">
        <h1 className="text-xl sm:text-2xl font-semibold">PSM</h1>

        {/* Full Menu for Desktop */}
        <div className="ml-[40px] hidden md:flex gap-9 text-md">
          {['Project', 'Your Work', 'Dashboards'].map((item) => (
            <div
              key={item}
              className="flex items-center gap-1 cursor-pointer hover:text-blue-500"
              onClick={() => handleDropDownToggle(item.toLowerCase())}
            >
              <p>{item}</p>
              <ChevronDown className="w-4 h-4" />
            </div>
          ))}
            <button
              className="bg-blue-500 text-white px-4 py-1 rounded-md hover:bg-blue-600 transition"
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
              {['Project', 'Your Work', 'Dashboards', 'Create'].map((item) => (
                <div
                  key={item}
                  className="flex items-center justify-between px-4 py-2 text-sm cursor-pointer hover:bg-gray-100"
                  onClick={() => handleDropDownToggle(item.toLowerCase())}
                >
                  <p>{item}</p>
                  {item !== 'Create' && <ChevronRight className="w-4 h-4" />}
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Right Icons */}
        <Link to="/notification" className="relative">
          <Bell className="w-6 h-6 sm:w-5 sm:h-5" />
          {notificationCount > 0 && (
            <span className="absolute -top-1 -right-2 bg-red-500 text-white text-[10px] px-1.5 py-0.5 rounded-full font-semibold">
              {notificationCount}
            </span>
          )}
        </Link>
        <Link to="/support">
          <HelpCircle className="w-6 h-6 sm:w-5 sm:h-5 text-gray-600 hover:text-blue-500" />
        </Link>
        <Link to="/setting">
          <Settings className="w-6 h-6 sm:w-5 sm:h-5 text-gray-600 hover:text-blue-500" />
        </Link>
        <Link to="/profile">
          <User className="w-6 h-6 sm:w-5 sm:h-5 text-gray-600 hover:text-blue-500" />
        </Link>
      </div>

      {dropDown === 'project' && open && <ProjectDropDown />}
    </nav>
  );
}

export default NavBar;
