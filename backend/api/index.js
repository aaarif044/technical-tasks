const express = require('express');
const router = express.Router();

const fileUploadRoutes = require('./upload/upload.route');

router.use('/uploads', fileUploadRoutes);

module.exports = router;
