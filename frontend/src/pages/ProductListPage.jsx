import { useState, useEffect, useCallback } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Plus, Pencil, Trash2, Package } from 'lucide-react';
import * as productApi from '../api/productApi';
import ProductFilters from '../components/products/ProductFilters';
import Pagination from '../components/common/Pagination';
import ConfirmDialog from '../components/common/ConfirmDialog';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { formatCurrency, formatDate } from '../utils/formatters';
import toast from 'react-hot-toast';

const STATUS_BADGE = {
  IN_STOCK: 'bg-green-100 text-green-700',
  LOW_STOCK: 'bg-amber-100 text-amber-700',
  OUT_OF_STOCK: 'bg-red-100 text-red-700',
};

const STATUS_LABEL = {
  IN_STOCK: 'In Stock',
  LOW_STOCK: 'Low Stock',
  OUT_OF_STOCK: 'Out of Stock',
};

export default function ProductListPage() {
  const navigate = useNavigate();
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [deleteTarget, setDeleteTarget] = useState(null);

  const [filters, setFilters] = useState({
    search: '',
    category: '',
    status: '',
    sortBy: 'createdAt',
    sortDir: 'desc',
    page: 0,
    size: 10,
  });

  const fetchProducts = useCallback(async () => {
    setLoading(true);
    try {
      const { data } = await productApi.getProducts(filters);
      setProducts(data.content);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
    } catch {
      toast.error('Failed to load products');
    } finally {
      setLoading(false);
    }
  }, [filters]);

  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  useEffect(() => {
    productApi.getCategories().then(({ data }) => setCategories(data)).catch(() => {});
  }, []);

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      await productApi.deleteProduct(deleteTarget.id);
      toast.success('Product deleted');
      setDeleteTarget(null);
      fetchProducts();
    } catch {
      toast.error('Failed to delete product');
    }
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Products</h1>
          <p className="text-sm text-gray-500 mt-1">{totalElements} total products</p>
        </div>
        <Link
          to="/products/new"
          className="flex items-center gap-2 px-4 py-2.5 bg-indigo-600 text-white text-sm font-medium rounded-lg hover:bg-indigo-700 transition"
        >
          <Plus size={18} />
          Add Product
        </Link>
      </div>

      <ProductFilters filters={filters} categories={categories} onChange={setFilters} />

      {loading ? (
        <LoadingSpinner />
      ) : products.length === 0 ? (
        <div className="text-center py-16">
          <Package size={48} className="mx-auto text-gray-300" />
          <p className="mt-3 text-gray-500">No products found</p>
        </div>
      ) : (
        <>
          <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="bg-gray-50 text-left text-gray-500 text-xs uppercase tracking-wider">
                    <th className="px-4 py-3">Product</th>
                    <th className="px-4 py-3">SKU</th>
                    <th className="px-4 py-3">Category</th>
                    <th className="px-4 py-3 text-right">Qty</th>
                    <th className="px-4 py-3 text-right">Price</th>
                    <th className="px-4 py-3">Status</th>
                    <th className="px-4 py-3">Date</th>
                    <th className="px-4 py-3 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {products.map((product) => (
                    <tr key={product.id} className="hover:bg-gray-50 transition">
                      <td className="px-4 py-3">
                        <div className="flex items-center gap-3">
                          {product.imageUrl ? (
                            <img
                              src={`${import.meta.env.VITE_API_URL || 'http://localhost:8080'}${product.imageUrl}`}
                              alt={product.name}
                              className="w-10 h-10 rounded-lg object-cover border border-gray-200"
                            />
                          ) : (
                            <div className="w-10 h-10 rounded-lg bg-gray-100 flex items-center justify-center">
                              <Package size={16} className="text-gray-400" />
                            </div>
                          )}
                          <span className="font-medium text-gray-900">{product.name}</span>
                        </div>
                      </td>
                      <td className="px-4 py-3 text-gray-600 font-mono text-xs">{product.sku}</td>
                      <td className="px-4 py-3 text-gray-600">{product.category}</td>
                      <td className="px-4 py-3 text-right text-gray-900 font-medium">{product.quantity}</td>
                      <td className="px-4 py-3 text-right text-gray-900">{formatCurrency(product.price)}</td>
                      <td className="px-4 py-3">
                        <span className={`inline-block px-2 py-1 rounded-full text-xs font-medium ${STATUS_BADGE[product.status]}`}>
                          {STATUS_LABEL[product.status]}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-gray-500 text-xs">{formatDate(product.createdAt)}</td>
                      <td className="px-4 py-3">
                        <div className="flex items-center justify-end gap-1">
                          <button
                            onClick={() => navigate(`/products/${product.id}/edit`)}
                            className="p-1.5 text-gray-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition"
                          >
                            <Pencil size={15} />
                          </button>
                          <button
                            onClick={() => setDeleteTarget(product)}
                            className="p-1.5 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition"
                          >
                            <Trash2 size={15} />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          <Pagination
            currentPage={filters.page}
            totalPages={totalPages}
            onPageChange={(page) => setFilters({ ...filters, page })}
          />
        </>
      )}

      <ConfirmDialog
        isOpen={!!deleteTarget}
        title="Delete Product"
        message={`Are you sure you want to delete "${deleteTarget?.name}"? This action cannot be undone.`}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  );
}
