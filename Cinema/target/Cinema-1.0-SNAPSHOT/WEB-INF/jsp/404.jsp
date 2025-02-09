<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>404 - Page Not Found</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      text-align: center;
      background-color: #f8f9fa;
      margin: 0;
      padding: 50px;
    }
    .container {
      max-width: 600px;
      margin: auto;
      background: white;
      padding: 20px;
      box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
      border-radius: 5px;
    }
    h1 {
      font-size: 50px;
      color: #dc3545;
    }
    p {
      font-size: 18px;
      color: #6c757d;
    }
    a {
      text-decoration: none;
      color: #007bff;
      font-weight: bold;
    }
    a:hover {
      color: #0056b3;
    }
  </style>
</head>
<body>
<div class="container">
  <h1>404</h1>
  <p>Oops! The page you are looking for does not exist.</p>
  <p><a onclick="window.location.href='/';">Go Back Home</a></p>
</div>
</body>
</html>