import React, { createContext, useContext, useState } from 'react';

const AppContext = createContext();


export function AppProvider({ children }) {
  const [dropDown, setDropDown] = useState('');
  const [open,setOpen] = useState(false);

  return (
    <AppContext.Provider value={{ dropDown, setDropDown,open,setOpen }}>
      {children}
    </AppContext.Provider>
  );
}


export const  useAppContext =()=>useContext(AppContext);

