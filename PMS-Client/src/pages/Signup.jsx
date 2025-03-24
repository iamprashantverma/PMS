import { useState } from 'react';
import { toast } from 'react-toastify';
import { signup } from '@/services/AuthService';

function Signup() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [userForm, setUserForm] = useState({
    name: '',
    email: '',
    phoneNo: '',
    role: 'USER',
    gender: '',
    dob: '',
    password: '',
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

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      {/* Navbar */}
      <nav className="w-full bg-white shadow-md px-6 py-4 flex justify-between items-center">
        <h2 className="text-3xl font-bold text-blue-600">bordio.</h2>
        <div className="flex items-center gap-2 text-sm sm:text-base">
          <p className="text-gray-700">Already have an account?</p>
          <button className="text-blue-600 font-medium hover:underline">Login</button>
        </div>
      </nav>

      {/* Signup Form */}
      <div className="flex-grow flex items-center justify-center p-4">
        <form
          onSubmit={handleSignup}
          className="bg-white shadow-lg rounded-xl p-8 w-full max-w-lg"
        >
          <h2 className="text-2xl sm:text-3xl font-semibold text-center text-gray-800 mb-6">
            Create your account
          </h2>

          {/* Form Fields */}
          {[
            { label: 'Name', name: 'name', type: 'text', placeholder: 'Enter your name' },
            { label: 'Email', name: 'email', type: 'email', placeholder: 'Enter your email' },
            { label: 'Phone Number', name: 'phoneNo', type: 'tel', placeholder: 'Enter your phone number' },
          ].map(({ label, name, type, placeholder }) => (
            <div key={name} className="mb-4">
              <label className="block mb-1 font-medium text-gray-700">{label}</label>
              <input
                type={type}
                name={name}
                value={userForm[name]}
                onChange={formHandler}
                placeholder={placeholder}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
              />
            </div>
          ))}

          {/* Gender */}
          <div className="mb-4">
            <label className="block mb-1 font-medium text-gray-700">Gender</label>
            <select
              name="gender"
              value={userForm.gender}
              onChange={formHandler}
              required
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
            >
              <option value="">Select gender</option>
              <option value="male">Male</option>
              <option value="female">Female</option>
              <option value="other">Other</option>
            </select>
          </div>

          {/* DOB */}
          <div className="mb-4">
            <label className="block mb-1 font-medium text-gray-700">Date of Birth</label>
            <input
              type="date"
              name="dob"
              value={userForm.dob}
              onChange={formHandler}
              required
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
            />
          </div>

          {/* Password */}
          <div className="mb-4">
            <label className="block mb-1 font-medium text-gray-700">Password</label>
            <input
              type="password"
              name="password"
              value={userForm.password}
              onChange={formHandler}
              placeholder="Enter a password"
              minLength={6}
              required
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
            />
          </div>

          {/* Submit Button */}
          <button
            type="submit"
            disabled={loading}
            className={`w-full py-3 mt-2 rounded-lg font-semibold text-white transition duration-200 ${
              loading ? 'bg-gray-400 cursor-not-allowed' : 'bg-blue-600 hover:bg-blue-700'
            }`}
          >
            {loading ? 'Signing up...' : 'Sign Up'}
          </button>

          {/* Error Message */}
          {error && (
            <p className="mt-4 text-sm bg-red-500 text-white py-2 px-3 rounded text-center">
              {error}
            </p>
          )}
        </form>
      </div>
    </div>
  );
}

export default Signup;
