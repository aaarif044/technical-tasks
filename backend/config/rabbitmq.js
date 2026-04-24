const amqp = require('amqplib');

let channel;
let connection;

const QUEUE = 'csv_queue';

async function initRabbit(retries = 3, delay = 5000) {
  for (let attempt = 1; attempt <= retries; attempt++) {
    try {
      connection = await amqp.connect(process.env.MR_URL);

      connection.on('error', (err) => {
        console.error('RabbitMQ error:', err.message);
      });

      connection.on('close', () => {
        console.log('RabbitMQ connection closed');
      });

      channel = await connection.createChannel();

      await channel.assertQueue(QUEUE, {
        durable: true,
      });

      console.log('RabbitMQ connected');
      return;
    } catch (error) {
      console.error(`RabbitMQ connection attempt ${attempt}/${retries} failed: ${error.message}`);

      if (attempt === retries) {
        throw new Error('RabbitMQ connection failed after 3 retries');
      }

      await new Promise((resolve) => setTimeout(resolve, delay));
    }
  }
}

function getChannel() {
  if (!channel) {
    throw new Error('RabbitMQ not initialized');
  }

  return channel;
}

async function startWorker(processJob) {
  if (!channel) {
    throw new Error('RabbitMQ not initialized');
  }

  channel.consume(QUEUE, async (msg) => {
    if (!msg) return;
    try {
      await processJob(msg);

      channel.ack(msg);
    } catch (err) {
      console.error('Worker failed:', err);

      channel.nack(msg, false, false);
    }
  });

  console.log('Worker started');
}

module.exports = {
  initRabbit,
  getChannel,
  startWorker,
  QUEUE,
};
