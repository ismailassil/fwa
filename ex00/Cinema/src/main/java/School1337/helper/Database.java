package School1337.helper;

import org.mindrot.jbcrypt.BCrypt;

import java.io.*;
import java.sql.*;
import java.util.*;

public class Database {
    private static final String URL = "jdbc:mariadb://localhost:3306/DataDB";
    private static final String USERNAME = "iassil";
    private static final String PASSWORD = "iassil";
    private static final String DRIVER = "org.mariadb.jdbc.Driver";
    private static final String checkForUserQuery = "SELECT password FROM users WHERE email = ?";
    private static final String getUserId = "SELECT id FROM users WHERE email = ?";
    private static final String getUserDataQuery = "SELECT * FROM users WHERE email = ?";
    private static final String insertFileQuery = "INSERT INTO upload_history (user_id, file_name, file_type, file_data) VALUES (?, ?, ?, ?)";
    private static final String insertUserQuery = "INSERT INTO users (firstName, lastName, phoneNumber, email, password, image) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String getUserLoginQuery = "SELECT * FROM logins_history WHERE user_id = ? ORDER BY login_time DESC";
    private static final String insertLoginQuery = "INSERT INTO logins_history (user_id, login_ip) VALUES (?, ?)";
    private static final String defaultImage = "/home/iassil/fwa/ex00/Cinema/src/main/webapp/images/default.jpg";

    static public boolean isUserExists(String email, String password) {
        try {
            Class.forName(DRIVER);

            try ( Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                  PreparedStatement statement = connection.prepareStatement(checkForUserQuery) )
            {
                statement.setString(1, email);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String hashed_Password = resultSet.getString("password");
                    return BCrypt.checkpw(password, hashed_Password);
                }
                return false;
            }
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found");
            throw new RuntimeException("Database Driver Loading Failed", e);
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            throw new RuntimeException("Database connection failed", e);
        }
    }

    static public boolean insertNewUser(Properties properties) {
        String firstName, lastName, phoneNumber, email, password, loginIp;

        firstName = properties.getProperty("firstName");
        lastName = properties.getProperty("lastName");
        phoneNumber = properties.getProperty("phoneNumber");
        email = properties.getProperty("email");
        password = properties.getProperty("password");
        loginIp = properties.getProperty("loginIp");

        if (isUserExists(email, password))
            return false;
        try {
            Class.forName(DRIVER);

            try( Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(insertUserQuery, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement logStatement = connection.prepareStatement(insertLoginQuery)) {

                statement.setString(1, firstName);
                statement.setString(2, lastName);
                statement.setString(3, phoneNumber);
                statement.setString(4, email);
                statement.setString(5, BCrypt.hashpw(password, BCrypt.gensalt(12)));

                File imageFile = new File(defaultImage);
                if (!imageFile.exists()) {
                    System.out.println("Image File not FOUND");
                    throw new RuntimeException("Image file not found");
                }

                byte[] imageBytes;
                try (FileInputStream fis = new FileInputStream(imageFile)) {
                    imageBytes = fis.readAllBytes();
                }
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
                statement.setBinaryStream(6, inputStream, imageBytes.length);

                int affected = statement.executeUpdate();
                if (affected > 0) {
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int userId = generatedKeys.getInt(1);
                            logStatement.setString(1, String.valueOf(userId));
                            logStatement.setString(2, loginIp);
                            logStatement.executeUpdate();
                            return true;
                        }
                    }
                }
                return false;
            }
        } catch (IOException e) {
            System.out.println("Here IOException");
            throw new RuntimeException("Database connection failed", e);
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found");
            throw new RuntimeException("Database Driver Loading Failed", e);
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            throw new RuntimeException("Database connection failed", e);
        }
    }

    static public Properties getUserData(String email) {
        Properties properties = new Properties();

        try {
            Class.forName(DRIVER);

            try ( Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement statement = connection.prepareStatement(getUserDataQuery);
            PreparedStatement logStatement = connection.prepareStatement(getUserLoginQuery)) {

                statement.setString(1, email);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    properties.setProperty("userEmail", email);
                    properties.setProperty("fullName", resultSet.getString("firstName") + " " + resultSet.getString("lastName"));

                    int userId = resultSet.getInt("id");
                    logStatement.setInt(1, userId);
                    ResultSet logResultSet = logStatement.executeQuery();

                    List<String> loginDates = new ArrayList<>();
                    List<String> loginTimes = new ArrayList<>();
                    List<String> loginIps = new ArrayList<>();

                    while (logResultSet.next()) {
                        Timestamp loginTime = logResultSet.getTimestamp("login_time");
                        if (loginTime != null) {
                            loginDates.add(new java.sql.Date(loginTime.getTime()).toString());
                            loginTimes.add(new java.sql.Time(loginTime.getTime()).toString());
                            loginIps.add(logResultSet.getString("login_ip"));
                        }
                    }

                    String loginDatesString = String.join(",", loginDates);
                    String loginTimesString = String.join(",", loginTimes);
                    String loginIpsString = String.join(",", loginIps);


                    properties.setProperty("login_dates", loginDatesString);
                    properties.setProperty("login_times", loginTimesString);
                    properties.setProperty("login_ips", loginIpsString);

                    InputStream imageInputStream = resultSet.getBinaryStream("image");

                    if (imageInputStream != null) {
                        byte[] imageBytes = imageInputStream.readAllBytes();
                        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                        properties.setProperty("profileImage", base64Image);
                    } else {
                        properties.setProperty("profileImage", "");
                        System.out.println("Image not found for user: " + email);
                    }
                }
            } catch (IOException e) {
                System.out.println("Here IOException");
                throw new RuntimeException("Database connection failed", e);
            }
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found");
            throw new RuntimeException("Database Driver Loading Failed", e);
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            throw new RuntimeException("Database connection failed", e);
        }

        return properties;
    }

    static public boolean insertImage(Properties properties, InputStream file_data) {
        String file_name = properties.getProperty("file_name");
        String file_type = properties.getProperty("file_type");
        String user_email = properties.getProperty("email");

        try {
            Class.forName(DRIVER);

            try ( Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                  PreparedStatement statement = connection.prepareStatement(getUserId);
                  PreparedStatement fileStatement = connection.prepareStatement(insertFileQuery)) {

                statement.setString(1, user_email);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    fileStatement.setInt(1, resultSet.getInt("id"));
                    fileStatement.setString(2, file_name);
                    fileStatement.setString(3, file_type);
                    fileStatement.setBlob(4, file_data);

                    int rows = fileStatement.executeUpdate();
                    return rows > 0;
                }
            }
            return false;
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found");
            throw new RuntimeException("Database Driver Loading Failed", e);
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            throw new RuntimeException("Database connection failed", e);
        }
    }
}

