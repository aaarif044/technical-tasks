const mongoose = require('mongoose');

const transactionSchema = new mongoose.Schema(
  {
    jobId: {
      type: String,
      required: true,
      index: true,
    },

    isValid: {
      type: Boolean,
      default: true,
    },

    hash: String,
  },
  { timestamps: true, strict: false },
);

module.exports = mongoose.model('Transaction', transactionSchema);
