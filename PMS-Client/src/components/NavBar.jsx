import React, { useState } from 'react';
import { User, ChevronDown, Bell, HelpCircle, Settings, Menu, ChevronRight } from 'lucide-react';
import { Link } from 'react-router-dom';

function NavBar() {
  const [showMore, setShowMore] = useState(false);

  return (
    <nav className='flex  items-center justify-between w-dvw h-[10dvh] bg-gray-50 px-4 py-2'>
      
      {/* Left Side */}
      <div className='flex items-center gap-6 w-[70%]'>
        <h1 className="text-2xl font-semibold ml-[5%]">PSM</h1>

        {/* Full Menu - hidden on small screens */}
        <div className='hidden md:flex gap-4 w-[90%] justify-evenly '>
          <div className='flex items-center gap-1'>
            <p>Project</p>
            <ChevronDown className="w-4 h-4" />
          </div>
          <div className='flex items-center gap-1'>
            <p>Your Work</p>
            <ChevronDown className="w-4 h-4" />
          </div>
          <div className='flex items-center gap-1'>
            <p>Dashboards</p>
            <ChevronDown className="w-4 h-4" />
          </div>
          <div className='flex items-center'>
            <p>Create</p>
          </div>
        </div>

        {/* More menu - only on small screens */}
        <div className='md:hidden relative'>
          <button onClick={() => setShowMore(!showMore)} className="flex items-center gap-1 text-sm">
            More
            <ChevronDown className="w-4 h-4" />
          </button>
          {showMore && (
            <div className="absolute mt-2 bg-white shadow rounded p-3 z-50">
                <div className='flex items-center gap-1'>
                    <p>Project</p>
                    <ChevronRight className="w-4 h-4" />
                </div>
                <div className='flex items-center gap-1'>
                    <p>Your Work</p>
                    <ChevronRight className="w-4 h-4" />
                </div>
                <div className='flex items-center gap-1'>
                    <p>Dashboards</p>
                    <ChevronRight className="w-4 h-4" />
                </div>
                <div className='flex items-center'>
                    <p>Create</p>
                </div>
              </div> )}
        </div>
      </div>

      {/* Right Side */}
      <div className="flex items-center justify-evenly  w-[25%] ml-[5%] ">
        <Link to="/notification" className="flex items-center">
          <Bell className="w-6 h-6" />
        </Link>
        <Link to="/support">
          <HelpCircle className="w-5 h-5" />
        </Link>
        <Link to="/setting">
          <Settings className="w-5 h-5 text-gray-600 hover:text-blue-500 cursor-pointer" />
        </Link>
        <Link to="/profile">
          <User className="w-5 h-5 text-gray-600" />
        </Link>
      </div>
    </nav>
  );
}

export default NavBar;
