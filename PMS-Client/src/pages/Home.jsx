import { useState, useEffect } from 'react';
import { Calendar, Clock, Users, CheckSquare, BarChart2, Bell, User, LogIn, UserPlus } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';
import { useNavigate } from 'react-router-dom';

export default function Home() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (user?.id) {
      navigate("/dashboard");
    }
  }, [user, navigate])

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Navigation */}
      <nav className="bg-indigo-600 text-white shadow-lg sticky top-0 z-10">
        <div className="container mx-auto px-4 py-3 flex justify-between items-center">
          <div className="flex items-center space-x-2">
            <CheckSquare className="h-6 w-6 md:h-8 md:w-8" />
            <span className="text-lg md:text-xl font-bold">TaskFlow</span>
          </div>
          
          <div className="hidden md:flex space-x-6">
            <a href="#features" className="hover:text-indigo-200 transition">Features</a>
            <a href="#testimonials" className="hover:text-indigo-200 transition">Testimonials</a>
            <a href="#pricing" className="hover:text-indigo-200 transition">Pricing</a>
          </div>
          
          <div className="flex items-center space-x-2">
            {isLoggedIn ? (
              <button 
                className="bg-white text-indigo-600 px-3 py-1 md:px-4 md:py-2 rounded-lg font-medium hover:bg-indigo-100 transition text-sm md:text-base"
                onClick={() => setIsLoggedIn(false)}
              >
                <span>Dashboard</span>
              </button>
            ) : (
              <div className="flex space-x-2">
                <button className="bg-indigo-700 text-white px-3 py-1 md:px-4 md:py-2 rounded-lg font-medium hover:bg-indigo-800 transition text-sm md:text-base">
                  <Link to="/login">Login</Link>
                </button>
                <button className="hidden sm:block bg-white text-indigo-600 px-3 py-1 md:px-4 md:py-2 rounded-lg font-medium hover:bg-indigo-100 transition text-sm md:text-base">
                  <Link to="/signup">Sign Up</Link>
                </button>
              </div>
            )}
          </div>
        </div>
      </nav>

      {/* Hero Section */}
      <section className="bg-gradient-to-r from-indigo-600 to-purple-600 text-white">
        <div className="container mx-auto px-4 py-12 md:py-20 flex flex-col md:flex-row items-center">
          <div className="md:w-1/2 mb-6 md:mb-0">
            <h1 className="text-3xl md:text-4xl lg:text-5xl font-bold mb-4">Manage Projects with Ease</h1>
            <p className="text-base md:text-lg mb-6">TaskFlow helps teams collaborate efficiently and deliver projects on time.</p>
            <div className="flex flex-col sm:flex-row space-y-3 sm:space-y-0 sm:space-x-4">
              <button className="bg-white text-indigo-600 px-5 py-2 rounded-lg font-bold hover:bg-indigo-100 transition">
                Get Started
              </button>
              <button className="border-2 border-white text-white px-5 py-2 rounded-lg font-bold hover:bg-white hover:text-indigo-600 transition">
                Watch Demo
              </button>
            </div>
          </div>
          <div className="md:w-1/2">
            <img src="/api/placeholder/600/400" alt="Dashboard" className="rounded-lg shadow-xl" />
          </div>
        </div>
      </section>

      {/* Stats */}
      <section className="py-6 bg-white shadow-md">
        <div className="container mx-auto px-4">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-center">
            <div>
              <p className="text-2xl md:text-3xl font-bold text-indigo-600">10k+</p>
              <p className="text-gray-600 text-sm md:text-base">Users</p>
            </div>
            <div>
              <p className="text-2xl md:text-3xl font-bold text-indigo-600">50k+</p>
              <p className="text-gray-600 text-sm md:text-base">Projects</p>
            </div>
            <div>
              <p className="text-2xl md:text-3xl font-bold text-indigo-600">99.9%</p>
              <p className="text-gray-600 text-sm md:text-base">Uptime</p>
            </div>
            <div>
              <p className="text-2xl md:text-3xl font-bold text-indigo-600">24/7</p>
              <p className="text-gray-600 text-sm md:text-base">Support</p>
            </div>
          </div>
        </div>
      </section>

      {/* Features */}
      <section id="features" className="py-12 bg-gray-50">
        <div className="container mx-auto px-4">
          <div className="text-center mb-8">
            <h2 className="text-2xl md:text-3xl font-bold text-gray-800 mb-3">Key Features</h2>
            <p className="text-gray-600 max-w-2xl mx-auto">Everything you need to manage projects efficiently</p>
          </div>
          
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {/* Feature 1 */}
            <div className="bg-white p-5 rounded-xl shadow-md hover:shadow-lg transition">
              <div className="p-3 bg-indigo-100 rounded-full w-12 h-12 flex items-center justify-center mb-3">
                <Calendar className="h-5 w-5 text-indigo-600" />
              </div>
              <h3 className="text-lg font-bold mb-2 text-gray-800">Project Timeline</h3>
              <p className="text-gray-600 text-sm">Track milestones and deadlines with interactive Gantt charts.</p>
            </div>
            
            {/* Feature 2 */}
            <div className="bg-white p-5 rounded-xl shadow-md hover:shadow-lg transition">
              <div className="p-3 bg-indigo-100 rounded-full w-12 h-12 flex items-center justify-center mb-3">
                <Users className="h-5 w-5 text-indigo-600" />
              </div>
              <h3 className="text-lg font-bold mb-2 text-gray-800">Team Collaboration</h3>
              <p className="text-gray-600 text-sm">Work together with real-time updates and task assignments.</p>
            </div>
            
            {/* Feature 3 */}
            <div className="bg-white p-5 rounded-xl shadow-md hover:shadow-lg transition">
              <div className="p-3 bg-indigo-100 rounded-full w-12 h-12 flex items-center justify-center mb-3">
                <Bell className="h-5 w-5 text-indigo-600" />
              </div>
              <h3 className="text-lg font-bold mb-2 text-gray-800">Smart Notifications</h3>
              <p className="text-gray-600 text-sm">Stay updated with alerts for deadlines and project changes.</p>
            </div>
          </div>
        </div>
      </section>

      {/* Testimonial Section */}
      <section id="testimonials" className="py-12 bg-indigo-50">
        <div className="container mx-auto px-4">
          <div className="text-center mb-8">
            <h2 className="text-2xl md:text-3xl font-bold text-gray-800 mb-3">What Our Users Say</h2>
            <p className="text-gray-600">Trusted by thousands of teams worldwide</p>
          </div>
          
          <div className="grid md:grid-cols-3 gap-6">
            <div className="bg-white p-5 rounded-xl shadow-md">
              <div className="flex items-center mb-3">
                <div className="bg-indigo-100 rounded-full w-10 h-10 flex items-center justify-center mr-3">
                  <span className="font-bold text-indigo-600">JD</span>
                </div>
                <div>
                  <h4 className="font-bold">Jane Doe</h4>
                  <p className="text-gray-600 text-xs">Project Manager</p>
                </div>
              </div>
              <p className="text-gray-700 text-sm">"TaskFlow has transformed how our team manages projects. It's increased our productivity by 40%."</p>
            </div>
            
            <div className="bg-white p-5 rounded-xl shadow-md">
              <div className="flex items-center mb-3">
                <div className="bg-indigo-100 rounded-full w-10 h-10 flex items-center justify-center mr-3">
                  <span className="font-bold text-indigo-600">MS</span>
                </div>
                <div>
                  <h4 className="font-bold">Mark Smith</h4>
                  <p className="text-gray-600 text-xs">CEO</p>
                </div>
              </div>
              <p className="text-gray-700 text-sm">"We've tried many tools, but none compare to TaskFlow. It's flexible enough for our agile workflows."</p>
            </div>
            
            <div className="bg-white p-5 rounded-xl shadow-md">
              <div className="flex items-center mb-3">
                <div className="bg-indigo-100 rounded-full w-10 h-10 flex items-center justify-center mr-3">
                  <span className="font-bold text-indigo-600">AL</span>
                </div>
                <div>
                  <h4 className="font-bold">Amy Lee</h4>
                  <p className="text-gray-600 text-xs">Team Lead</p>
                </div>
              </div>
              <p className="text-gray-700 text-sm">"The collaborative features have improved our team communication significantly."</p>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-12 bg-gradient-to-r from-indigo-600 to-purple-600 text-white">
        <div className="container mx-auto px-4 text-center">
          <h2 className="text-2xl md:text-3xl font-bold mb-4">Ready to streamline your workflow?</h2>
          <p className="mb-6">Join thousands of teams who use TaskFlow.</p>
          <div className="flex flex-col sm:flex-row justify-center space-y-3 sm:space-y-0 sm:space-x-4">
            <button className="bg-white text-indigo-600 px-6 py-2 rounded-lg font-bold hover:bg-indigo-100 transition">
              Start Free Trial
            </button>
            <button className="border-2 border-white bg-transparent text-white px-6 py-2 rounded-lg font-bold hover:bg-white hover:text-indigo-600 transition">
              Schedule Demo
            </button>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-gray-800 text-white py-8">
        <div className="container mx-auto px-4">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
            <div>
              <div className="flex items-center space-x-2 mb-3">
                <CheckSquare className="h-5 w-5" />
                <span className="font-bold">TaskFlow</span>
              </div>
              <p className="text-gray-400 text-sm">Simplifying project management.</p>
            </div>
            
            <div>
              <h3 className="font-bold mb-3">Product</h3>
              <ul className="space-y-1 text-gray-400 text-sm">
                <li><a href="#" className="hover:text-white transition">Features</a></li>
                <li><a href="#" className="hover:text-white transition">Pricing</a></li>
                <li><a href="#" className="hover:text-white transition">Integrations</a></li>
              </ul>
            </div>
            
            <div>
              <h3 className="font-bold mb-3">Resources</h3>
              <ul className="space-y-1 text-gray-400 text-sm">
                <li><a href="#" className="hover:text-white transition">Documentation</a></li>
                <li><a href="#" className="hover:text-white transition">Support</a></li>
                <li><a href="#" className="hover:text-white transition">Blog</a></li>
              </ul>
            </div>
            
            <div>
              <h3 className="font-bold mb-3">Company</h3>
              <ul className="space-y-1 text-gray-400 text-sm">
                <li><a href="#" className="hover:text-white transition">About Us</a></li>
                <li><a href="#" className="hover:text-white transition">Contact</a></li>
                <li><a href="#" className="hover:text-white transition">Privacy</a></li>
              </ul>
            </div>
          </div>
          
          <div className="border-t border-gray-700 mt-6 pt-6 flex flex-col md:flex-row justify-between items-center">
            <p className="text-gray-400 text-sm mb-4 md:mb-0">© 2025 TaskFlow. All rights reserved.</p>
            <div className="flex space-x-4">
              <a href="#" className="text-gray-400 hover:text-white transition">
                <svg className="h-5 w-5" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M8.29 20.251c7.547 0 11.675-6.253 11.675-11.675 0-.178 0-.355-.012-.53A8.348 8.348 0 0022 5.92a8.19 8.19 0 01-2.357.646 4.118 4.118 0 001.804-2.27 8.224 8.224 0 01-2.605.996 4.107 4.107 0 00-6.993 3.743 11.65 11.65 0 01-8.457-4.287 4.106 4.106 0 001.27 5.477A4.072 4.072 0 012.8 9.713v.052a4.105 4.105 0 003.292 4.022 4.095 4.095 0 01-1.853.07 4.108 4.108 0 003.834 2.85A8.233 8.233 0 012 18.407a11.616 11.616 0 006.29 1.84" />
                </svg>
              </a>
              <a href="#" className="text-gray-400 hover:text-white transition">
                <svg className="h-5 w-5" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M19 0h-14c-2.761 0-5 2.239-5 5v14c0 2.761 2.239 5 5 5h14c2.762 0 5-2.239 5-5v-14c0-2.761-2.238-5-5-5zm-11 19h-3v-11h3v11zm-1.5-12.268c-.966 0-1.75-.79-1.75-1.764s.784-1.764 1.75-1.764 1.75.79 1.75 1.764-.783 1.764-1.75 1.764zm13.5 12.268h-3v-5.604c0-3.368-4-3.113-4 0v5.604h-3v-11h3v1.765c1.396-2.586 7-2.777 7 2.476v6.759z" />
                </svg>
              </a>
              <a href="#" className="text-gray-400 hover:text-white transition">
                <svg className="h-5 w-5" fill="currentColor" viewBox="0 0 24 24">
                  <path fillRule="evenodd" clipRule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z" />
                </svg>
              </a>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
}