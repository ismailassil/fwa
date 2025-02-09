<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="School1337.helper.LoginRecord" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="School1337.helper.FileRecord" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>User Profile</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      background-color: #f4f7f6;
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
      margin: 0;
      flex-direction: column;
    }
    .profile-container {
      background: #ffffff;
      padding: 20px;
      border-radius: 10px;
      box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
      width: 400px;
      text-align: center;
    }
    .profile-container img {
      width: 100px;
      height: 100px;
      border-radius: 50%;
      object-fit: cover;
    }
    .profile-container button {
      background-color: #007bff;
      color: white;
      border: none;
      padding: 10px;
      margin-top: 10px;
      border-radius: 5px;
      cursor: pointer;
    }
    .logout {
      background-color: #dc3545;
    }
    table {
      width: 100%;
      margin-top: 20px;
      border-collapse: collapse;
    }
    table, th, td {
      border: 1px solid #ccc;
    }
    th, td {
      padding: 10px;
      text-align: left;
    }
  </style>
</head>
<body>
<div class="profile-container">
  <p style="color: orange">${upload_status}</p>
  <h2>User Profile</h2>
  <img src="data:image/jpeg;base64,${profileImage}" alt="Profile Picture">
  <p align="left"><strong>FullName:</strong> ${fullName}</p>
  <p align="left"><strong>Email:</strong> ${userEmail}</p>

  <h3>Last Login Dates</h3>
  <table>
    <thead>
    <tr>
      <th>Date</th>
      <th>Time</th>
      <th>IP Address</th>
    </tr>
    </thead>
    <tbody>
    <tr>
    <%
      List<LoginRecord> loginHistory = (ArrayList<LoginRecord>) request.getAttribute("loginHistory");
      if (loginHistory != null) {
        for (LoginRecord login : loginHistory) {
    %>
    <tr>
      <td><%= login.getDate() %></td>
      <td><%= login.getTime() %></td>
      <td><%= login.getIp() %></td>
    </tr>
    <%
        }
      }
    %>
    </tr>
    </tbody>
  </table>

  <form action="/fileUpload" method="post" enctype="multipart/form-data">
    <input type="file" name="profilePicture[]" accept="image/*" required multiple>
    <button type="submit">Upload New Picture</button>
  </form>

  <form action="/profile" method="get">
    <button type="submit" name="logout" class="logout">Logout</button>
  </form>

  <h3>Upload History</h3>
  <table>
    <thead>
      <tr>
        <th>Filename</th>
        <th>Size</th>
        <th>Type</th>
      </tr>
    </thead>
    <tbody>
    <%
      List<FileRecord> imageHistory = (ArrayList<FileRecord>) request.getAttribute("imageHistory");
      if (imageHistory != null) {
        for (FileRecord image : imageHistory) {
    %>
    <tr>
      <td>
        <a href="" onclick="window.open('image?filename=<%=image.getFileName()%>', '_blank')">
          <%= image.getFileName() %>
        </a>
      </td>
      <td><%= image.getSize() %></td>
      <td><%= image.getMime() %></td>
    </tr>
    <%
        }
      }
    %>
    </tbody>
  </table>
</div>
</body>
</html>