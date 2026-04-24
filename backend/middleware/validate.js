module.exports = (schema) => (req, res, next) => {
  const keys = ['body', 'params', 'query'];
  for (const key of keys) {
    if (schema[key]) {
      const { error } = schema[key].validate(req[key]);
      if (error) return res.status(400).json({ error: error.details[0].message });
    }
  }
  next();
};
