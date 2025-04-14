import { useState, useContext } from 'react';
import { toast } from 'react-toastify';
import { AuthContext } from '@/context/AuthContext';
import 'react-toastify/dist/ReactToastify.css';
import { Link } from 'react-router-dom';
import { Mail, Lock, ArrowRight, Loader } from 'lucide-react';
import { useAuth } from '@/context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
function Login() {
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();
  const {user} = useAuth();
  useEffect(() => {
    if (user?.id) {
      navigate("/dashboard");
    }
  }, [user, navigate])
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [userForm, setUserForm] = useState({ email: '', password: '' });

  const formHandler = (e) => {
    const { name, value } = e.target;
    setUserForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const loginHandler = async (e) => {
    e.preventDefault();
    const { email, password } = userForm;

    if (!email || !password) {
      toast.error('Please fill in all fields');
      return;
    }

    try {
      setLoading(true);
      setError(null);

      await login({ email, password });

      toast.success('Login successful!');
    } catch (err) {
      toast.error(err.message || 'Login failed');
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen w-full flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 p-4">
      <div className="w-full max-w-md">
        <div className="bg-white rounded-2xl shadow-xl overflow-hidden">
          {/* Header Section */}
          <div className="bg-blue-600 p-6 text-center">
            <h1 className="text-3xl font-bold text-white">Welcome Back</h1>
            <p className="text-blue-100 mt-1">Login to your account</p>
          </div>

          {/* Form Section */}
          <form onSubmit={loginHandler} className="p-8">
            {/* Email */}
            <div className="mb-5">
              <label className="block mb-2 font-medium text-gray-700">Email</label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <Mail size={18} className="text-gray-400" />
                </div>
                <input
                  placeholder="Enter your email"
                  name="email"
                  value={userForm.email}
                  onChange={formHandler}
                  required
                  type="email"
                  className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
                />
              </div>
            </div>

            {/* Password */}
            <div className="mb-3">
              <label className="block mb-2 font-medium text-gray-700">Password</label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <Lock size={18} className="text-gray-400" />
                </div>
                <input
                  placeholder="Enter your password"
                  name="password"
                  value={userForm.password}
                  onChange={formHandler}
                  minLength={4}
                  required
                  type="password"
                  className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
                />
              </div>
            </div>

            {/* Forgot Password */}
            <div className="text-right mb-6">
              <Link to="/forgetPassword" className="text-sm text-blue-600 hover:underline font-medium">
                Forgot Password?
              </Link>
            </div>

            {/* Login Button */}
            <button
              type="submit"
              disabled={loading}
              className="bg-blue-600 hover:bg-blue-700 text-white w-full py-3 rounded-lg font-semibold transition duration-200 disabled:opacity-50 flex items-center justify-center"
            >
              {loading ? (
                <>
                  <Loader size={18} className="animate-spin mr-2" />
                  Logging in...
                </>
              ) : (
                <>
                  Login
                  <ArrowRight size={18} className="ml-2" />
                </>
              )}
            </button>

            {/* Error Message */}
            {error && (
              <div className="mt-4 p-3 bg-red-50 border border-red-200 rounded-lg">
                <p className="text-red-600 text-sm text-center">{error}</p>
              </div>
            )}

            {/* Signup Link */}
            <div className="mt-8 text-center text-gray-600">
              Don't have an account?{' '}
              <Link to="/signup" className="text-blue-600 font-medium hover:underline">
                Sign Up
              </Link>
            </div>
          </form>
        </div>
        
        {/* Optional: Add a brand/logo at the bottom */}
        <div className="mt-6 text-center">
          <p className="text-sm text-gray-500">© 2025 Your Company. All rights reserved.</p>
        </div>
      </div>
    </div>
  );
}

export default Login;