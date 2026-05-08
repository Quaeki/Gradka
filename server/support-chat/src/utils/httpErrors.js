function badRequest(message) {
  return httpError(400, message);
}

function httpError(statusCode, message) {
  const error = new Error(message);
  error.statusCode = statusCode;
  return error;
}

module.exports = { badRequest, httpError };
