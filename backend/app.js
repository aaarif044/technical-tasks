require('dotenv').config();

const express = require('express');
const routes = require('./api');
const connectDB = require('./config/db');
const { initRabbit, startWorker } = require('./config/rabbitmq');
const processJob = require('./workers/csvWorker');
const app = express();
const PORT = process.env.PORT || 5000;
const cors = require('cors');

app.use(
  cors({
    origin: 'http://localhost:3000',
  }),
);

/* Middlewares */
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

/* Routes */
app.use('/api', routes);

/* 404 Handler */
app.use((req, res) => {
  res.status(404).json({ code: res.statusCode, success: false, message: 'Route not found' });
});

/* Global Error Handler */
app.use((err, req, res, next) => {
  res.status(err.status || 500).json({
    code: res.statusCode,
    success: false,
    message: err.message || 'Internal Server Error',
  });
});

/* Start server after DB connection */
async function startServer() {
  try {
    await connectDB();

    await initRabbit();

    await startWorker(processJob);

    app.listen(PORT, () => {
      console.log(`Server running on port ${PORT}`);
    });
  } catch (error) {
    console.error('Startup failed:', error.message);
    process.exit(1);
  }
}

startServer();
