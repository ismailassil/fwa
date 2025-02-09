<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Main</title>
    <style>
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            background-color: #f4f4f4;
            margin: 0;
        }
        .container {
            text-align: center;
        }
        button {
            font-size: 24px;
            padding: 20px 40px;
            margin: 15px;
            border: none;
            border-radius: 10px;
            cursor: pointer;
            transition: 0.3s;
        }
        .login {
            background-color: #007bff;
            color: white;
        }
        .signup {
            background-color: #28a745;
            color: white;
        }
        button:hover {
            opacity: 0.8;
        }
    </style>
</head>
<body>
<div class="container">
    <button class="login" onclick="window.location.href='/signIn';">Login</button>
    <button class="signup" onclick="window.location.href='/signUp';">Sign Up</button>
</div>
</body>
</html>