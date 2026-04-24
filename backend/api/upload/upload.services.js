const crypto = require('crypto');
const fs = require('fs');
const jobs = new Map();
const hashes = new Map();
const Job = require('../../model/job.model');
const rabbit = require('../../config/rabbitmq');

const createJob = async (file) => {
  if (!file) throw new Error('CSV file required');

  const hash = crypto.createHash('sha256').update(fs.readFileSync(file.path)).digest('hex');

  // Check duplicate by hash
  const existingJob = await Job.findOne({ hash });

  if (existingJob) {
    return {
      jobId: existingJob.jobId,
      deduplicated: true,
    };
  }

  const jobId = crypto.randomUUID();

  // Insert new job into DB
  await Job.create({ jobId, hash, filePath: file.path, state: 'queued' });

  // Simulate async processing
  rabbit.getChannel().sendToQueue(rabbit.QUEUE, Buffer.from(JSON.stringify({ jobId })), { persistent: true });
  return { jobId };
};

const fetchStatus = async (jobId) => {
  const job = await Job.findOne({ jobId });

  if (!job) {
    throw new Error('Job not found');
  }

  return {
    jobId: job.jobId,
    state: job.state,
    progress: {
      processed: job.processedRows,
      total: job.totalRows,
    },
    summary:
      job.state === 'done'
        ? {
            totalRows: job.totalRows,
            validRows: job.validRows,
            invalidRows: job.invalidRows,
          }
        : null,

    error: job.state === 'failed' ? job.error : null,
  };
};

const jobList = async (query) => {
  const page = Number(query.page) || 1;
  const limit = Number(query.limit) || 10;

  const skip = (page - 1) * limit;

  const filter = {};

  if (query.state) {
    filter.state = query.state;
  }

  if (query.search) {
    filter.jobId = { $regex: query.search, $options: 'i' };
  }

  const [jobs, total] = await Promise.all([Job.find(filter).sort({ _id: -1 }).skip(skip).limit(limit).lean(), Job.countDocuments()]);

  return {
    results: jobs,
    pagination: { page, limit, total, totalPages: Math.ceil(total / limit) },
  };
};

module.exports = {
  createJob,
  fetchStatus,
  jobList,
};
