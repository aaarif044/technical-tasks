const express = require('express');
const router = express.Router();
const controller = require('./upload.controller');
const upload = require('../../middleware/upload');
const validate = require('../../middleware/validate');
const { uploadSchema, statusSchema, jobListSchema } = require('./upload.validate');

router.route('/').post(upload.single('file'), validate(uploadSchema), controller.uploadCsv).get(validate(jobListSchema), controller.jobList);

router.get('/status/:jobId', validate(statusSchema), controller.getStatus);

module.exports = router;
