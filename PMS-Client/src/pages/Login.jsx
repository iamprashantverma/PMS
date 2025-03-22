import { useState, useContext } from 'react';
import { toast } from 'react-toastify';
import { AuthContext } from '@/context/AuthContext';
import 'react-toastify/dist/ReactToastify.css';

function Login() {
  const { login } = useContext(AuthContext); 

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [userForm, setUserForm] = useState({ email: '', password: ''});
  
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
      <nav className="mb-4">
        <h1 className="text-2xl font-bold">Login</h1>
      </nav>

      <form
        onSubmit={loginHandler}
        className="bg-white p-6 rounded-lg shadow-md w-full max-w-md"
      >
        <label className="block mb-1 font-semibold">Email</label>
        <input
          placeholder="Enter your email"
          name="email"
          value={userForm.email}
          onChange={formHandler}
          required
          type="email"
          className="w-full p-2 mb-4 border rounded"
        />

        <label className="block mb-1 font-semibold">Password</label>
        <input
          placeholder="Enter your password"
          name="password"
          value={userForm.password}
          onChange={formHandler}
          minLength={4}
          required
          type="password"
          className="w-full p-2 mb-4 border rounded"
        />

        <button
          type="submit"
          className="bg-blue-600 hover:bg-blue-700 text-white w-full py-2 rounded disabled:opacity-50"
          disabled={loading}
        >
          {loading ? 'Logging in...' : 'Login'}
        </button>

        {error && (
          <p className="text-red-500 text-sm mt-3 text-center">{error}</p>
        )}
      </form>
    </div>
  );
}

export default Login;
