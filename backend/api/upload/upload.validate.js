const Joi = require('joi');

const uploadSchema = {
  body: Joi.object({}),
};

const statusSchema = {
  params: Joi.object({
    jobId: Joi.string().required(),
  }),
};

const jobListSchema = {
  query: Joi.object({
    page: Joi.number().integer().min(1).default(1),
    limit: Joi.number().integer().min(1).max(100).default(10),
    state: Joi.string().valid('queued', 'processing', 'done', 'failed').optional(),
    search: Joi.string().trim().allow('').optional(),
  }),
};

module.exports = {
  uploadSchema,
  statusSchema,
  jobListSchema,
};
