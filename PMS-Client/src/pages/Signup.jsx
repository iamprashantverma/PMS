import { useState } from 'react';
import { toast } from 'react-toastify';
import { signup } from '@/services/AuthService';
import { Languages, User, Mail, Phone, Home, Calendar, Lock, Eye, EyeOff } from 'lucide-react';
import { Link } from 'react-router-dom';

function Signup() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [showPassword, setShowPassword] = useState(false);
  const [userForm, setUserForm] = useState({
    name: '',
    email: '',
    phoneNo: '',
    address: '',
    gender: '',
    dob: '',
    password: '',
    language: 'English'
  });

  const formHandler = (e) => {
    const { name, value } = e.target;
    setUserForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSignup = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const resp = await signup(userForm);
      toast.success(resp.data.message);
    } catch (error) {
      setError(error?.message);
    } finally {
      setLoading(false);
    }
  };

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  return (
    <div className="min-h-screen w-full overflow-x-hidden bg-gray-50 flex flex-col">
      {/* Navbar */}
      <nav className="w-full bg-white shadow-md px-4 sm:px-6 py-3 sm:py-4 flex justify-between items-center">
        <h2 className="text-2xl sm:text-3xl font-bold text-blue-600">PMS</h2>
        <div className="flex items-center gap-1 sm:gap-2 text-xs sm:text-sm md:text-base">
          <p className="text-gray-700">Already have an account?</p>
          <Link to="/login" className="text-blue-600 font-medium hover:underline">Login</Link>
        </div>
      </nav>

      {/* Signup Form */}
      <div className="flex-grow flex items-center justify-center p-3 sm:p-4 md:p-6">
        <form
          onSubmit={handleSignup}
          className="bg-white shadow-lg rounded-xl p-4 sm:p-6 md:p-8 w-full max-w-lg mx-auto"
        >
          <h2 className="text-xl sm:text-2xl md:text-3xl font-semibold text-center text-gray-800 mb-4 sm:mb-6">
            Create your account
          </h2>

          {/* Name */}
          <div className="mb-3 sm:mb-4">
            <label className="block mb-1 font-medium text-sm sm:text-base text-gray-700">Name</label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-gray-500">
                <User size={18} />
              </div>
              <input
                type="text"
                name="name"
                value={userForm.name}
                onChange={formHandler}
                placeholder="Enter your name"
                required
                className="w-full pl-10 pr-4 py-2 sm:py-3 text-sm sm:text-base border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
              />
            </div>
          </div>

          {/* Email */}
          <div className="mb-3 sm:mb-4">
            <label className="block mb-1 font-medium text-sm sm:text-base text-gray-700">Email</label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-gray-500">
                <Mail size={18} />
              </div>
              <input
                type="email"
                name="email"
                value={userForm.email}
                onChange={formHandler}
                placeholder="Enter your email"
                required
                className="w-full pl-10 pr-4 py-2 sm:py-3 text-sm sm:text-base border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
              />
            </div>
          </div>

          {/* Phone Number */}
          <div className="mb-3 sm:mb-4">
            <label className="block mb-1 font-medium text-sm sm:text-base text-gray-700">Phone Number</label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-gray-500">
                <Phone size={18} />
              </div>
              <input
                type="tel"
                name="phoneNo"
                value={userForm.phoneNo}
                onChange={formHandler}
                placeholder="Enter your phone number"
                required
                className="w-full pl-10 pr-4 py-2 sm:py-3 text-sm sm:text-base border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
              />
            </div>
          </div>

          {/* Address */}
          <div className="mb-3 sm:mb-4">
            <label className="block mb-1 font-medium text-sm sm:text-base text-gray-700">Address</label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-gray-500">
                <Home size={18} />
              </div>
              <input
                type="text"
                name="address"
                value={userForm.address}
                onChange={formHandler}
                placeholder="Enter your address"
                required
                className="w-full pl-10 pr-4 py-2 sm:py-3 text-sm sm:text-base border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
              />
            </div>
          </div>

          {/* Two columns on larger screens */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 sm:gap-4">
            {/* Gender */}
            <div className="mb-3 sm:mb-4">
              <label className="block mb-1 font-medium text-sm sm:text-base text-gray-700">Gender</label>
              <select
                name="gender"
                value={userForm.gender}
                onChange={formHandler}
                required
                className="w-full px-4 py-2 sm:py-3 text-sm sm:text-base border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
              >
                <option value="">Select gender</option>
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Other">Other</option>
              </select>
            </div>

            {/* DOB */}
            <div className="mb-3 sm:mb-4">
              <label className="block mb-1 font-medium text-sm sm:text-base text-gray-700">Date of Birth</label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-gray-500">
                  <Calendar size={18} />
                </div>
                <input
                  type="date"
                  name="dob"
                  value={userForm.dob}
                  onChange={formHandler}
                  required
                  className="w-full pl-10 pr-4 py-2 sm:py-3 text-sm sm:text-base border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
                />
              </div>
            </div>
          </div>

          {/* Password */}
          <div className="mb-4 sm:mb-6">
            <label className="block mb-1 font-medium text-sm sm:text-base text-gray-700">Password</label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-gray-500">
                <Lock size={18} />
              </div>
              <input
                type={showPassword ? "text" : "password"}
                name="password"
                value={userForm.password}
                onChange={formHandler}
                placeholder="Enter a password"
                minLength={6}
                required
                className="w-full pl-10 pr-12 py-2 sm:py-3 text-sm sm:text-base border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
              />
              <button
                type="button"
                onClick={togglePasswordVisibility}
                className="absolute inset-y-0 right-0 flex items-center pr-3 text-gray-500 hover:text-gray-700"
              >
                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
            <p className="text-xs text-gray-500 mt-1">Password must be at least 6 characters</p>
          </div>

          {/* Submit Button */}
          <button
            type="submit"
            disabled={loading}
            className={`w-full py-2 sm:py-3 mt-2 rounded-lg font-semibold text-sm sm:text-base text-white transition duration-200 ${
              loading ? 'bg-gray-400 cursor-not-allowed' : 'bg-blue-600 hover:bg-blue-700'
            }`}
          >
            {loading ? 'Signing up...' : 'Sign Up'}
          </button>

          {/* Error Message */}
          {error && (
            <div className="mt-4 text-xs sm:text-sm bg-red-50 border border-red-200 text-red-600 py-2 px-3 rounded text-center">
              {error}
            </div>
          )}
        </form>
      </div>

      {/* Footer */}
      <footer className="text-center py-3 text-xs sm:text-sm text-gray-500 bg-white shadow-inner">
        <p>© 2025 Bordio. All rights reserved.</p>
      </footer>
    </div>
  );
}

export default Signup;