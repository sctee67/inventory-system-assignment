import { useNavigate } from 'react-router-dom';
import { AlertTriangle } from 'lucide-react';

export default function LowStockTable({ items }) {
  const navigate = useNavigate();

  if (!items || items.length === 0) {
    return (
      <div className="bg-white rounded-xl border border-gray-200 p-6 text-center">
        <p className="text-gray-500 text-sm">No low stock items</p>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
      <div className="px-5 py-4 border-b border-gray-100 flex items-center gap-2">
        <AlertTriangle size={18} className="text-amber-500" />
        <h2 className="font-semibold text-gray-900">Low Stock Alerts</h2>
      </div>
      <table className="w-full text-sm">
        <thead>
          <tr className="bg-gray-50 text-left text-gray-500 text-xs uppercase tracking-wider">
            <th className="px-5 py-2.5">Product</th>
            <th className="px-5 py-2.5">SKU</th>
            <th className="px-5 py-2.5 text-right">Qty Left</th>
            <th className="px-5 py-2.5 text-right">Action</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-100">
          {items.map((item) => (
            <tr key={item.id} className="hover:bg-gray-50">
              <td className="px-5 py-3 font-medium text-gray-900">{item.name}</td>
              <td className="px-5 py-3 text-gray-600 font-mono text-xs">{item.sku}</td>
              <td className="px-5 py-3 text-right">
                <span className="inline-block px-2 py-0.5 bg-amber-100 text-amber-700 rounded-full text-xs font-medium">
                  {item.quantity}
                </span>
              </td>
              <td className="px-5 py-3 text-right">
                <button
                  onClick={() => navigate(`/products/${item.id}/edit`)}
                  className="text-indigo-600 hover:text-indigo-800 text-xs font-medium"
                >
                  Edit
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
