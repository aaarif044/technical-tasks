const fs = require('fs');
const csvParser = require('csv-parser');
const crypto = require('crypto');
const JobModel = require('../model/job.model');
const Transaction = require('../model/transaction.model');

const BATCH_SIZE = 100;
const PROGRESS_INTERVAL = 50;

const processJob = async (msg) => {
  const { jobId } = JSON.parse(msg.content.toString());

  console.log(`[WORKER] Received job: ${jobId}`);

  const job = await JobModel.findOne({ jobId });

  if (!job) {
    console.log(`[WORKER] Job not found: ${jobId}`);
    return;
  }

  try {
    await updateJob(jobId, { state: 'processing', error: null });

    console.log(`[WORKER] Processing started: ${jobId}`);

    await processCsv(jobId, job.filePath);
  } catch (error) {
    await failJob(jobId, error);
  }
};

const processCsv = (jobId, filePath) =>
  new Promise((resolve, reject) => {
    let stats = {
      totalRows: 0,
      processedRows: 0,
      validRows: 0,
      invalidRows: 0,
    };

    let batch = [];

    const stream = fs.createReadStream(filePath).pipe(csvParser());

    stream.on('data', async (row) => {
      stream.pause();

      try {
        stats.totalRows++;
        stats.processedRows++;

        const valid = isValidRow(row);

        if (valid) {
          stats.validRows++;
        } else {
          stats.invalidRows++;
        }

        batch.push(mapTransaction(jobId, row, valid));

        if (batch.length >= BATCH_SIZE) {
          await flushBatch(batch);
          batch = [];
        }

        if (stats.processedRows % PROGRESS_INTERVAL === 0) {
          await updateJob(jobId, {
            processedRows: stats.processedRows,
            totalRows: stats.totalRows,
          });

          console.log(`[WORKER] Progress ${jobId}: ${stats.processedRows}/${stats.totalRows}`);
        }

        stream.resume();
      } catch (error) {
        reject(error);
      }
    });

    stream.on('end', async () => {
      try {
        await flushBatch(batch);

        await updateJob(jobId, {
          state: 'done',
          ...stats,
        });

        console.log(`[WORKER] Completed ${jobId} | valid=${stats.validRows} invalid=${stats.invalidRows}`);

        resolve();
      } catch (error) {
        reject(error);
      }
    });

    stream.on('error', reject);
  });

const flushBatch = async (batch) => {
  if (!batch.length) return;

  await Transaction.insertMany(batch, {
    ordered: false,
  });
};

const updateJob = async (jobId, data) => {
  await JobModel.updateOne({ jobId }, data);
};

const failJob = async (jobId, error) => {
  await updateJob(jobId, {
    state: 'failed',
    error: error.message,
  });

  console.error(`[WORKER] Job failed: ${jobId}`, error);
};

const mapTransaction = (jobId, row, isValid) => ({
  jobId,
  isValid,
  ...row,
  hash: crypto.createHash('sha256').update(JSON.stringify(row)).digest('hex'),
});

const isValidRow = (row) => {
  return row.date && row.description && row.amount && row.category && !isNaN(Number(row.amount));
};

module.exports = processJob;
