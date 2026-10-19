import { useState, useRef } from 'react';
import { Upload, X } from 'lucide-react';

export default function ImageUpload({ currentImageUrl, onFileSelect }) {
  const [preview, setPreview] = useState(currentImageUrl || null);
  const [dragOver, setDragOver] = useState(false);
  const inputRef = useRef(null);

  const handleFile = (file) => {
    if (!file || !file.type.startsWith('image/')) return;
    onFileSelect(file);
    setPreview(URL.createObjectURL(file));
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setDragOver(false);
    handleFile(e.dataTransfer.files[0]);
  };

  const handleRemove = () => {
    setPreview(null);
    onFileSelect(null);
    if (inputRef.current) inputRef.current.value = '';
  };

  return (
    <div>
      {preview ? (
        <div className="relative inline-block">
          <img
            src={preview.startsWith('blob:') ? preview : `${import.meta.env.VITE_API_URL || 'http://localhost:8080'}${preview}`}
            alt="Preview"
            className="w-40 h-40 object-cover rounded-lg border border-gray-200"
          />
          <button
            type="button"
            onClick={handleRemove}
            className="absolute -top-2 -right-2 bg-red-500 text-white rounded-full p-1 hover:bg-red-600"
          >
            <X size={14} />
          </button>
        </div>
      ) : (
        <div
          onClick={() => inputRef.current?.click()}
          onDragOver={(e) => { e.preventDefault(); setDragOver(true); }}
          onDragLeave={() => setDragOver(false)}
          onDrop={handleDrop}
          className={`w-full h-40 border-2 border-dashed rounded-lg flex flex-col items-center justify-center cursor-pointer transition ${
            dragOver ? 'border-indigo-500 bg-indigo-50' : 'border-gray-300 hover:border-gray-400'
          }`}
        >
          <Upload size={24} className="text-gray-400" />
          <p className="mt-2 text-sm text-gray-500">Click or drag image here</p>
          <p className="text-xs text-gray-400 mt-1">JPG, PNG, GIF, WebP (max 5MB)</p>
        </div>
      )}

      <input
        ref={inputRef}
        type="file"
        accept="image/*"
        className="hidden"
        onChange={(e) => handleFile(e.target.files[0])}
      />
    </div>
  );
}
