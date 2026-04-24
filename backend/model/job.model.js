const mongoose = require('mongoose');

const jobSchema = new mongoose.Schema(
  {
    jobId: {
      type: String,
      required: true,
      unique: true,
    },

    hash: {
      type: String,
      required: true,
      unique: true,
    },

    filePath: {
      type: String,
      required: true,
    },

    state: {
      type: String,
      enum: ['queued', 'processing', 'done', 'failed'],
      default: 'queued',
    },

    processedRows: {
      type: Number,
      default: 0,
    },

    totalRows: {
      type: Number,
      default: 0,
    },

    validRows: {
      type: Number,
      default: 0,
    },

    invalidRows: {
      type: Number,
      default: 0,
    },

    error: {
      type: String,
      default: null,
    },
  },
  {
    timestamps: true,
  },
);

module.exports = mongoose.model('Job', jobSchema);
