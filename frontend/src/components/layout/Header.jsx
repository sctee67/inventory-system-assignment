import { Menu, LogOut } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';

export default function Header({ onMenuClick }) {
  const { user, logout } = useAuth();

  return (
    <header className="h-16 bg-white border-b border-gray-200 flex items-center justify-between px-4 lg:px-6">
      <button onClick={onMenuClick} className="lg:hidden text-gray-500 hover:text-gray-700">
        <Menu size={22} />
      </button>

      <div className="hidden lg:block" />

      <div className="flex items-center gap-4">
        <span className="text-sm text-gray-600">
          {user?.username}
        </span>
        <button
          onClick={logout}
          className="flex items-center gap-1.5 text-sm text-gray-500 hover:text-red-600 transition"
        >
          <LogOut size={16} />
          Logout
        </button>
      </div>
    </header>
  );
}
