import { useState, useEffect } from 'react';
import { Package, CheckCircle, AlertTriangle, XCircle, DollarSign } from 'lucide-react';
import * as dashboardApi from '../api/dashboardApi';
import StatCard from '../components/dashboard/StatCard';
import LowStockTable from '../components/dashboard/LowStockTable';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { formatCurrency } from '../utils/formatters';
import toast from 'react-hot-toast';

export default function DashboardPage() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    dashboardApi.getDashboardStats()
      .then(({ data }) => setStats(data))
      .catch(() => toast.error('Failed to load dashboard'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner />;
  if (!stats) return null;

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Dashboard</h1>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4 mb-8">
        <StatCard title="Total Products" value={stats.totalProducts} icon={Package} color="indigo" />
        <StatCard title="In Stock" value={stats.inStockCount} icon={CheckCircle} color="green" />
        <StatCard title="Low Stock" value={stats.lowStockCount} icon={AlertTriangle} color="amber" />
        <StatCard title="Out of Stock" value={stats.outOfStockCount} icon={XCircle} color="red" />
        <StatCard
          title="Inventory Value"
          value={formatCurrency(stats.totalInventoryValue)}
          icon={DollarSign}
          color="indigo"
        />
      </div>

      <LowStockTable items={stats.lowStockItems} />
    </div>
  );
}
