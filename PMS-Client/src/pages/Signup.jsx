import  { useState } from 'react';
import { toast } from 'react-toastify';
import { signup } from '@/services/AuthService';

function Signup() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [userForm, setUserForm] = useState({
    name: '',
    email: '',
    phoneNo: '',
    role: '',
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

  const handleSignup = async(e) => {
    setError(null);
    e.preventDefault();
    setLoading(true);
    try{
        const resp = await signup(userForm);
        console.log(resp)
        toast.success(resp.data.message);
    } catch(error){
        setError(error?.message);
        console.log(error);
    } finally{
        setLoading(false);
    }
  };

  return (
    <div >
      <form onSubmit={handleSignup}>
        <h2 >Sign Up</h2>

        <label >Name</label>
        <input
          type="text"
          name="name"
          value={userForm.name}
          onChange={formHandler}
          placeholder="Enter your name"
          required
          className="p-2 mb-2 border rounded"
        />

        <label >Email</label>
        <input
          type="email"
          name="email"
          value={userForm.email}
          onChange={formHandler}
          placeholder="Enter your email"
          required
          className="p-2 mb-4 border rounded"
        />

        <label >Phone Number</label>
        <input
          type="tel"
          name="phoneNo"
          value={userForm.phoneNo}
          onChange={formHandler}
          placeholder="Enter your phone number"
          className="p-2 mb-4 border rounded"
          required
        />

        <label >Role</label>
        <input
          type="text"
          name="role"
          value={userForm.role}
          onChange={formHandler}
          placeholder="e.g., student, admin"
          className="p-2 mb-4 border rounded"
          required
        />

        <label >Gender</label>
        <select
          name="gender"
          value={userForm.gender}
          onChange={formHandler}
          className=" p-2 mb-4 border rounded"
          required
        >
          <option value="">Select gender</option>
          <option value="male">Male</option>
          <option value="female">Female</option>
          <option value="other">Other</option>
        </select>

        <label >Date of Birth</label>
        <input
          type="date"
          name="dob"
          value={userForm.dob}
          onChange={formHandler}
          className="p-2 mb-4 border rounded"
          required
        />

        <label >Password</label>
        <input
          type="password"
          name="password"
          value={userForm.password}
          onChange={formHandler}
          placeholder="Enter a password"
          minLength={6}
          required
          className=" p-2 mb-4 border rounded"
        />

        <button
          type="submit"
          disabled={loading} >
          {loading ? 'Signing up...' : 'Sign Up'}
        </button>
        {error && <p className="bg-red-400 text-white p-2 mt-2 rounded">{error}</p>}
      </form>

    </div>
  );
}

export default Signup;
