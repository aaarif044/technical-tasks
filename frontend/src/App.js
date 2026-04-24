import React, { useEffect, useState } from 'react';
import API from './api';

import JobTable from './components/JobTable';
import UploadFile from './components/UploadFile';
import ProgressCard from './components/ProgressCard';
import './App.css';

export default function App() {
  const [jobs, setJobs] = useState([]);
  const [selectedJob, setSelectedJob] = useState(null);

  const [loading, setLoading] = useState(true);
  const [progressLoading, setProgressLoading] = useState(false);

  const fetchJobs = async () => {
    try {
      setLoading(true);

      const res = await API.get('/uploads');

      setJobs(res.data?.data.results || []);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const fetchProgress = async (jobId) => {
    try {
      setProgressLoading(true);

      const res = await API.get(`/uploads/status/${jobId}`);

      setSelectedJob(res.data.data);
    } catch (error) {
      console.error(error);
    } finally {
      setProgressLoading(false);
    }
  };

  useEffect(() => {
    fetchJobs();
  }, []);

  if (loading) {
    return (
      <div className='container'>
        <h1>CSV Dashboard</h1>
        <p>Loading jobs...</p>
      </div>
    );
  }

  return (
    <div className='container'>
      <h1>CSV Dashboard</h1>

      <UploadFile onUpload={fetchJobs} />

      <JobTable jobs={jobs} onProgress={fetchProgress} />

      {progressLoading ? <p>Loading progress...</p> : <ProgressCard job={selectedJob} />}
    </div>
  );
}
