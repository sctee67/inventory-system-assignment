import { Search } from 'lucide-react';

const STATUS_OPTIONS = [
  { value: '', label: 'All Status' },
  { value: 'IN_STOCK', label: 'In Stock' },
  { value: 'LOW_STOCK', label: 'Low Stock' },
  { value: 'OUT_OF_STOCK', label: 'Out of Stock' },
];

const SORT_OPTIONS = [
  { value: 'createdAt', label: 'Date Added' },
  { value: 'name', label: 'Name' },
  { value: 'price', label: 'Price' },
  { value: 'quantity', label: 'Quantity' },
];

export default function ProductFilters({ filters, categories, onChange }) {
  const update = (key, value) => onChange({ ...filters, [key]: value, page: 0 });

  return (
    <div className="flex flex-col sm:flex-row gap-3 mb-4">
      <div className="relative flex-1">
        <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
        <input
          type="text"
          placeholder="Search products..."
          value={filters.search}
          onChange={(e) => update('search', e.target.value)}
          className="w-full pl-9 pr-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none"
        />
      </div>

      <select
        value={filters.category}
        onChange={(e) => update('category', e.target.value)}
        className="px-3 py-2 border border-gray-300 rounded-lg text-sm bg-white focus:ring-2 focus:ring-indigo-500 outline-none"
      >
        <option value="">All Categories</option>
        {categories.map((cat) => (
          <option key={cat} value={cat}>{cat}</option>
        ))}
      </select>

      <select
        value={filters.status}
        onChange={(e) => update('status', e.target.value)}
        className="px-3 py-2 border border-gray-300 rounded-lg text-sm bg-white focus:ring-2 focus:ring-indigo-500 outline-none"
      >
        {STATUS_OPTIONS.map(({ value, label }) => (
          <option key={value} value={value}>{label}</option>
        ))}
      </select>

      <select
        value={filters.sortBy}
        onChange={(e) => update('sortBy', e.target.value)}
        className="px-3 py-2 border border-gray-300 rounded-lg text-sm bg-white focus:ring-2 focus:ring-indigo-500 outline-none"
      >
        {SORT_OPTIONS.map(({ value, label }) => (
          <option key={value} value={value}>{label}</option>
        ))}
      </select>

      <button
        onClick={() => update('sortDir', filters.sortDir === 'asc' ? 'desc' : 'asc')}
        className="px-3 py-2 border border-gray-300 rounded-lg text-sm bg-white hover:bg-gray-50 transition"
      >
        {filters.sortDir === 'asc' ? '↑ Asc' : '↓ Desc'}
      </button>
    </div>
  );
}
