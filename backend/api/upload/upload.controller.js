const { default: status } = require('http-status');
const ApiResponse = require('../../utils/apiResponse');
const catchAsync = require('../../utils/catchAsync');
const service = require('./upload.services');

const uploadCsv = catchAsync(async (req, res) => {
  const result = await service.createJob(req.file);
  ApiResponse.success(res, 'success', result, status.CREATED);
});

const getStatus = catchAsync(async (req, res, next) => {
  const result = await service.fetchStatus(req.params.jobId);
  ApiResponse.success(res, 'success', result, status.OK);
});

const jobList = catchAsync(async (req, res, next) => {
  const result = await service.jobList(req.query);
  ApiResponse.success(res, 'success', result, status.OK);
});

module.exports = {
  uploadCsv,
  getStatus,
  jobList,
};
