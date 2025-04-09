import { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import { signup } from '@/services/AuthService';
import { User, Mail, Phone, Home, Calendar, Lock, Eye, EyeOff, CheckSquare } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';
import { useNavigate } from 'react-router-dom';
function Signup() {
  const navigate = useNavigate();
  const {user} = useAuth();
  useEffect(() => {
    if (user?.id) {
      navigate("/dashboard");
    }
  }, [user, navigate])
  
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
    <div className="min-h-screen bg-gradient-to-b from-indigo-50 to-white flex flex-col">
      {/* Logo Section */}
      <div className="pt-8 pb-4 flex justify-center">
        <Link to="/" className="flex items-center space-x-2">
          <CheckSquare className="h-8 w-8 text-indigo-600" />
          <span className="text-2xl font-bold text-indigo-600">TaskFlow</span>
        </Link>
      </div>

      {/* Signup Form */}
      <div className="flex-grow flex items-center justify-center p-4 sm:p-6 md:p-8">
        <div className="w-full max-w-lg">
          <div className="bg-white shadow-xl rounded-2xl overflow-hidden">
            {/* Form Header */}
            <div className="bg-gradient-to-r from-indigo-600 to-purple-600 p-6 text-white">
              <h2 className="text-2xl md:text-3xl font-bold text-center">
                Create Your Account
              </h2>
              <p className="text-center mt-2 opacity-90">Join thousands of teams using TaskFlow</p>
            </div>
            
            <form onSubmit={handleSignup} className="p-6 md:p-8 space-y-5">
              {/* Name */}
              <div>
                <label className="block mb-1.5 font-medium text-gray-700">Full Name</label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-indigo-500">
                    <User size={18} />
                  </div>
                  <input
                    type="text"
                    name="name"
                    value={userForm.name}
                    onChange={formHandler}
                    placeholder="Enter your name"
                    required
                    className="w-full pl-10 pr-4 py-3 text-gray-700 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent"
                  />
                </div>
              </div>

              {/* Email */}
              <div>
                <label className="block mb-1.5 font-medium text-gray-700">Email Address</label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-indigo-500">
                    <Mail size={18} />
                  </div>
                  <input
                    type="email"
                    name="email"
                    value={userForm.email}
                    onChange={formHandler}
                    placeholder="Enter your email"
                    required
                    className="w-full pl-10 pr-4 py-3 text-gray-700 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent"
                  />
                </div>
              </div>

              {/* Two columns layout */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                {/* Phone Number */}
                <div>
                  <label className="block mb-1.5 font-medium text-gray-700">Phone Number</label>
                  <div className="relative">
                    <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-indigo-500">
                      <Phone size={18} />
                    </div>
                    <input
                      type="tel"
                      name="phoneNo"
                      value={userForm.phoneNo}
                      onChange={formHandler}
                      placeholder="Enter phone number"
                      required
                      className="w-full pl-10 pr-4 py-3 text-gray-700 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent"
                    />
                  </div>
                </div>

                {/* Gender */}
                <div>
                  <label className="block mb-1.5 font-medium text-gray-700">Gender</label>
                  <select
                    name="gender"
                    value={userForm.gender}
                    onChange={formHandler}
                    required
                    className="w-full px-4 py-3 text-gray-700 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent"
                  >
                    <option value="">Select gender</option>
                    <option value="Male">Male</option>
                    <option value="Female">Female</option>
                    <option value="Other">Other</option>
                  </select>
                </div>
              </div>

              {/* Address */}
              <div>
                <label className="block mb-1.5 font-medium text-gray-700">Address</label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-indigo-500">
                    <Home size={18} />
                  </div>
                  <input
                    type="text"
                    name="address"
                    value={userForm.address}
                    onChange={formHandler}
                    placeholder="Enter your address"
                    required
                    className="w-full pl-10 pr-4 py-3 text-gray-700 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent"
                  />
                </div>
              </div>

              {/* Date of Birth */}
              <div>
                <label className="block mb-1.5 font-medium text-gray-700">Date of Birth</label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-indigo-500">
                    <Calendar size={18} />
                  </div>
                  <input
                    type="date"
                    name="dob"
                    value={userForm.dob}
                    onChange={formHandler}
                    required
                    className="w-full pl-10 pr-4 py-3 text-gray-700 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent"
                  />
                </div>
              </div>

              {/* Password */}
              <div>
                <label className="block mb-1.5 font-medium text-gray-700">Password</label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-indigo-500">
                    <Lock size={18} />
                  </div>
                  <input
                    type={showPassword ? "text" : "password"}
                    name="password"
                    value={userForm.password}
                    onChange={formHandler}
                    placeholder="Create a password"
                    minLength={6}
                    required
                    className="w-full pl-10 pr-12 py-3 text-gray-700 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent"
                  />
                  <button
                    type="button"
                    onClick={togglePasswordVisibility}
                    className="absolute inset-y-0 right-0 flex items-center pr-3 text-gray-500 hover:text-indigo-600"
                  >
                    {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                  </button>
                </div>
                <p className="text-xs text-gray-500 mt-1">Password must be at least 6 characters</p>
              </div>

              {/* Error Message */}
              {error && (
                <div className="bg-red-50 border border-red-200 text-red-600 py-3 px-4 rounded-lg text-sm">
                  {error}
                </div>
              )}

              {/* Submit Button */}
              <button
                type="submit"
                disabled={loading}
                className={`w-full py-3 rounded-lg font-bold text-white text-md transition duration-200 ${
                  loading 
                    ? 'bg-gray-400 cursor-not-allowed' 
                    : 'bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-700 hover:to-purple-700 shadow-md hover:shadow-lg'
                }`}
              >
                {loading ? 'Creating Account...' : 'Create Account'}
              </button>
            </form>

            {/* Login Redirect */}
            <div className="px-6 py-4 bg-gray-50 border-t border-gray-200 flex justify-center">
              <p className="text-gray-600">
                Already have an account?{' '}
                <Link to="/login" className="text-indigo-600 font-medium hover:text-indigo-800">
                  Log In
                </Link>
              </p>
            </div>
          </div>

          {/* Additional Info */}
          <div className="mt-6 text-center text-sm text-gray-500">
            <p>By creating an account, you agree to our{' '}
              <a href="#" className="text-indigo-600 hover:underline">Terms of Service</a>{' '}
              and{' '}
              <a href="#" className="text-indigo-600 hover:underline">Privacy Policy</a>
            </p>
          </div>
        </div>
      </div>

      {/* Footer */}
      <footer className="py-4 text-center text-sm text-gray-500">
        <p>© 2025 TaskFlow. All rights reserved.</p>
      </footer>
    </div>
  );
}

export default Signup;