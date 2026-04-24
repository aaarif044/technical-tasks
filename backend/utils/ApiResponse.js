class ApiResponse {
  static success(res, message = 'success', data = {}, statusCode = 200) {
    return res.status(statusCode).json({ code: res.statusCode, success: true, message, data });
  }

  static error(res, message = 'Something went wrong', statusCode = 500, errors = null) {
    return res.status(statusCode).json({ code: res.statusCode, success: false, message, errors });
  }
}

module.exports = ApiResponse;
