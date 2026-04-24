import React from 'react';

export default function ProgressCard({ job }) {
  if (!job) return null;

  const processed = job?.progress?.processed || 0;
  const total = job?.progress?.total || 0;

  const progress = total > 0 ? Math.round((processed / total) * 100) : 0;

  const getStatusClass = (state) => {
    switch (state) {
      case 'queued':
        return 'badge queued';
      case 'processing':
        return 'badge processing';
      case 'done':
        return 'badge done';
      case 'failed':
        return 'badge failed';
      default:
        return 'badge';
    }
  };

  return (
    <div className='card'>
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: '18px',
        }}
      >
        <div>
          <h2
            style={{
              marginBottom: '6px',
            }}
          >
            Job Progress
          </h2>

          <p
            style={{
              fontSize: '14px',
              color: '#666',
            }}
          >
            ID: {job.jobId}
          </p>
        </div>

        <span className={getStatusClass(job.state)}>{job.state}</span>
      </div>

      <div
        style={{
          marginBottom: '10px',
        }}
      >
        <div className='progress-bar'>
          <div
            className='progress-fill'
            style={{
              width: `${progress}%`,
            }}
          />
        </div>

        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            marginTop: '8px',
            fontSize: '14px',
          }}
        >
          <span>
            {processed} / {total} Rows
          </span>

          <strong>{progress}%</strong>
        </div>
      </div>

      {job.summary && (
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit,minmax(140px,1fr))',
            gap: '12px',
            marginTop: '18px',
          }}
        >
          <div className='card'>
            <p>Total Rows</p>
            <h3>{job.summary.totalRows}</h3>
          </div>

          <div className='card'>
            <p>Valid Rows</p>
            <h3>{job.summary.validRows}</h3>
          </div>

          <div className='card'>
            <p>Invalid Rows</p>
            <h3>{job.summary.invalidRows}</h3>
          </div>
        </div>
      )}

      {job.error && (
        <div
          className='error'
          style={{
            marginTop: '18px',
            padding: '12px',
            background: '#fee2e2',
            borderRadius: '8px',
          }}
        >
          <strong>Error:</strong> {job.error}
        </div>
      )}
    </div>
  );
}
