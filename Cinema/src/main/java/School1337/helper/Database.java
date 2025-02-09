package School1337.helper;

import org.mindrot.jbcrypt.BCrypt;

import java.io.*;
import java.sql.*;
import java.util.*;

public class Database {
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;
    private static final Properties propertyApplication = new Properties();
    static {
        try (InputStream stream = Database.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (stream == null) {
                throw new RuntimeException("application.properties not found");
            } else {
                propertyApplication.load(stream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        URL = (String) propertyApplication.get("db.url");
        USERNAME = (String) propertyApplication.get("db.user");
        PASSWORD = (String) propertyApplication.get("db.password");
        if (URL.isEmpty() || USERNAME.isEmpty() || PASSWORD.isEmpty())
            throw new RuntimeException("Element is not set");
    }
    private static final String DRIVER = "org.mariadb.jdbc.Driver";
    private static final String checkForUserQuery = "SELECT password FROM users WHERE email = ?";
    private static final String getUserId = "SELECT id FROM users WHERE email = ?";
    private static final String getUserDataQuery = "SELECT * FROM users WHERE email = ?";
    private static final String insertFileQuery = "INSERT INTO upload_history (user_id, file_name, file_type, file_size, file_data) VALUES (?, ?, ?, ?, ?)";
    private static final String insertImageQuery = "UPDATE users SET image = ? WHERE id = ?";
    private static final String insertUserQuery = "INSERT INTO users (firstName, lastName, phoneNumber, email, password, image) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String getUserLoginQuery = "SELECT * FROM logins_history WHERE user_id = ? ORDER BY login_time DESC";
    private static final String insertLoginQuery = "INSERT INTO logins_history (user_id, login_ip) VALUES (?, ?)";
    private static final String getUserImageQuery = "SELECT * FROM upload_history WHERE user_id = ? ORDER BY file_name DESC";
    private static final String getUserImageNameQuery = "SELECT * FROM upload_history WHERE user_id = ? AND file_name = ?";
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
            PreparedStatement logStatement = connection.prepareStatement(getUserLoginQuery);
            PreparedStatement imageStatement = connection.prepareStatement(getUserImageQuery)) {

                statement.setString(1, email);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    properties.setProperty("userEmail", email);
                    properties.setProperty("fullName", resultSet.getString("firstName") + " " + resultSet.getString("lastName"));

                    int userId = resultSet.getInt("id");
                    logStatement.setInt(1, userId);
                    imageStatement.setInt(1, userId);
                    ResultSet logResultSet = logStatement.executeQuery();
                    ResultSet imageResultSet = imageStatement.executeQuery();

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

                    List<String> imagesName = new ArrayList<>();
                    List<String> imagesType = new ArrayList<>();
                    List<String> imagesSize = new ArrayList<>();

                    while (imageResultSet.next()) {
                        imagesName.add(imageResultSet.getString("file_name"));
                        imagesType.add("image/" + imageResultSet.getString("file_type"));
                        imagesSize.add(imageResultSet.getString("file_size"));
                    }

                    String loginDatesString = String.join(",", loginDates);
                    String loginTimesString = String.join(",", loginTimes);
                    String loginIpsString = String.join(",", loginIps);

                    String imagesNameString = String.join(",", imagesName);
                    String imagesTypeString = String.join(",", imagesType);
                    String imagesSizeString = String.join(",", imagesSize);

                    properties.setProperty("login_dates", loginDatesString);
                    properties.setProperty("login_times", loginTimesString);
                    properties.setProperty("login_ips", loginIpsString);

                    properties.setProperty("images_name", imagesNameString);
                    properties.setProperty("images_type", imagesTypeString);
                    properties.setProperty("images_size", imagesSizeString);

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

    static public boolean insertImage(Properties properties, InputStream file_data) throws IOException {
        String file_name = properties.getProperty("file_name");
        String file_type = properties.getProperty("file_type");
        String file_size = properties.getProperty("file_size");
        String user_email = properties.getProperty("email");

        byte[] fileBytes = file_data.readAllBytes();

        try {
            Class.forName(DRIVER);

            try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(getUserId);
                 PreparedStatement imageStatement = connection.prepareStatement(insertImageQuery);
                 PreparedStatement fileStatement = connection.prepareStatement(insertFileQuery)) {

                connection.setAutoCommit(false);

                statement.setString(1, user_email);
                try {
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        int user_id = resultSet.getInt("id");
                        imageStatement.setBlob(1, new ByteArrayInputStream(fileBytes));
                        imageStatement.setInt(2, user_id);
                        imageStatement.executeUpdate();

                        fileStatement.setInt(1, user_id);
                        fileStatement.setString(2, file_name);
                        fileStatement.setString(3, file_type);
                        fileStatement.setString(4, file_size);
                        fileStatement.setBlob(5, new ByteArrayInputStream(fileBytes));
                        fileStatement.executeUpdate();

                        connection.commit();
                        return true;
                    }
                } catch (Exception e) {
                    connection.rollback();
                    throw e;
                } finally {
                    connection.setAutoCommit(true);
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

    static public Properties getImage(String email, String fileName) throws SQLException, ClassNotFoundException {
        Properties properties = new Properties();
        properties.setProperty("status", "0");

        try {
            Class.forName(DRIVER);

            try ( Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                  PreparedStatement userStatement = connection.prepareStatement(getUserId);
                  PreparedStatement statement = connection.prepareStatement(getUserImageNameQuery)) {
                userStatement.setString(1, email);

                ResultSet resultSet = userStatement.executeQuery();
                if (!resultSet.next()) {
                    properties.setProperty("status", "2");
                    return properties;
                }
                int userId = resultSet.getInt("id");

                statement.setInt(1, userId);
                statement.setString(2, fileName);

                ResultSet resultSet1 = statement.executeQuery();
                if (!resultSet1.next()) {
                    properties.setProperty("status", "1");
                    return properties;
                }

                try (InputStream imageStream = resultSet1.getBinaryStream("file_data")) {
                    if (imageStream == null) {
                        properties.setProperty("status", "2");
                        System.out.println("Image `" + fileName + "` not found");
                    } else {
                        properties.setProperty("file_type", resultSet1.getString("file_type"));
                        byte[] streamByte = imageStream.readAllBytes();
                        String ImageToBase64 = Base64.getEncoder().encodeToString(streamByte);
                        properties.setProperty("file_data", ImageToBase64);
                    }

                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}

