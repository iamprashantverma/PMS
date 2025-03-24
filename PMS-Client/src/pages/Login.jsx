import { useState, useContext } from 'react';
import { toast } from 'react-toastify';
import { AuthContext } from '@/context/AuthContext';
import 'react-toastify/dist/ReactToastify.css';

function Login() {
  const { login } = useContext(AuthContext);

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
      // TODO: Redirect to dashboard or home page here
    } catch (err) {
      toast.error(err.message || 'Login failed');
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gray-100 px-4">
      <nav className="mb-6">
        <h1 className="text-3xl font-bold text-blue-600">Welcome Back</h1>
        <p className="text-gray-600 text-center mt-1">Login to your account</p>
      </nav>

      <form
        onSubmit={loginHandler}
        className="bg-white p-8 rounded-xl shadow-md w-full max-w-md"
      >
        {/* Email */}
        <label className="block mb-1 font-medium text-gray-700">Email</label>
        <input
          placeholder="Enter your email"
          name="email"
          value={userForm.email}
          onChange={formHandler}
          required
          type="email"
          className="w-full px-4 py-2 mb-4 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
        />

        {/* Password */}
        <label className="block mb-1 font-medium text-gray-700">Password</label>
        <input
          placeholder="Enter your password"
          name="password"
          value={userForm.password}
          onChange={formHandler}
          minLength={4}
          required
          type="password"
          className="w-full px-4 py-2 mb-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
        />

        {/* Forgot Password */}
        <div className="text-right mb-4">
          <a href="#" className="text-sm text-blue-600 hover:underline">
            Forgot Password?
          </a>
        </div>

        {/* Login Button */}
        <button
          type="submit"
          disabled={loading}
          className="bg-blue-600 hover:bg-blue-700 text-white w-full py-2 rounded-lg font-semibold transition duration-200 disabled:opacity-50"
        >
          {loading ? 'Logging in...' : 'Login'}
        </button>

        {/* Error Message */}
        {error && (
          <p className="text-red-500 text-sm mt-4 text-center">{error}</p>
        )}

        {/* Signup Link */}
        <div className="mt-6 text-center text-sm text-gray-600">
          Don't have an account?{' '}
          <a href="/signup" className="text-blue-600 font-medium hover:underline">
            Sign Up
          </a>
        </div>
      </form>
    </div>
  );
}

export default Login;
