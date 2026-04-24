import axios from 'axios';

const API = axios.create({
  baseURL: 'http://localhost:3010/api',
});

export default API;
