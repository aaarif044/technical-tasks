import React from 'react';

export default function JobTable({ jobs, onProgress }) {
  return (
    <div className='card'>
      <h2>Uploaded Files</h2>

      <table>
        <thead>
          <tr>
            <th>Job ID</th>
            <th>Status</th>
            <th>Total</th>
            <th>Processed</th>
            <th>Action</th>
          </tr>
        </thead>

        <tbody>
          {jobs.map((job) => (
            <tr key={job.jobId}>
              <td>{job.jobId}</td>
              <td>{job.state}</td>
              <td>{job.totalRows}</td>
              <td>{job.processedRows}</td>
              <td>
                <button onClick={() => onProgress(job.jobId)}>See Progress</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
