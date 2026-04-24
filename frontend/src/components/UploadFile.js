import React, { useState } from 'react';
import API from '../api';

export default function UploadFile({ onUpload }) {
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  const MAX_SIZE_MB = 2;

  const uploadFile = async () => {
    if (!file) {
      setMessage('Please select a CSV file.');
      return;
    }

    try {
      setLoading(true);
      setMessage('');

      const formData = new FormData();

      formData.append('file', file);

      const res = await API.post('/uploads', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      const { jobId, deduplicated } = res.data.data;

      setMessage(deduplicated ? `Duplicate file detected. Existing Job ID: ${jobId}` : `Uploaded successfully. Job ID: ${jobId}`);

      setFile(null);

      setTimeout(() => {
        onUpload();
      }, 1200);
    } catch (error) {
      setMessage('Upload failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const selected = e.target.files?.[0];

    if (!selected) return;

    const isCsv = selected.name.toLowerCase().endsWith('.csv');

    const sizeMb = selected.size / (1024 * 1024);

    if (!isCsv) {
      setFile(null);
      setMessage('Only CSV files are allowed.');
      return;
    }

    if (sizeMb > MAX_SIZE_MB) {
      setFile(null);
      setMessage(`File size must be under ${MAX_SIZE_MB} MB.`);
      return;
    }

    setFile(selected);
    setMessage('');
  };

  return (
    <div className='card'>
      <div
        style={{
          marginBottom: '18px',
        }}
      >
        <h2
          style={{
            marginBottom: '6px',
          }}
        >
          Upload CSV File
        </h2>

        <p
          style={{
            color: '#666',
            fontSize: '14px',
            lineHeight: '1.6',
          }}
        >
          Upload a CSV file for processing and validation.
          <br />
          Accepted format:
          <strong> .csv</strong>
          <br />
          Maximum size:
          <strong> {MAX_SIZE_MB} MB</strong>
        </p>
      </div>

      <div
        style={{
          display: 'flex',
          gap: '12px',
          flexWrap: 'wrap',
          alignItems: 'center',
        }}
      >
        <input type='file' accept='.csv' onChange={handleChange} />

        <button onClick={uploadFile} disabled={loading}>
          {loading ? 'Uploading...' : 'Upload File'}
        </button>
      </div>

      {file && (
        <div
          style={{
            marginTop: '14px',
            fontSize: '14px',
            color: '#444',
          }}
        >
          Selected File: <strong>{file.name}</strong> ({(file.size / 1024).toFixed(1)}
          KB)
        </div>
      )}

      {message && (
        <div
          style={{
            marginTop: '16px',
            padding: '10px 14px',
            borderRadius: '8px',
            background: message.includes('successfully') ? '#dcfce7' : '#fee2e2',
            color: message.includes('successfully') ? '#166534' : '#991b1b',
            fontSize: '14px',
          }}
        >
          {message}
        </div>
      )}
    </div>
  );
}
