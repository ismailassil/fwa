package School1337.servlets;

import java.io.*;
import java.util.Collection;
import java.util.Properties;

import School1337.helper.Database;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "FileUploadServlet", urlPatterns = {"/fileupload", "/fileUpload"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 100
)
public class FileUpload extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");

        Collection<Part> fileParts = request.getParts();

        for (Part filePart : fileParts) {

            if (filePart.getSubmittedFileName() == null)
                continue;

            String fileName = filePart.getSubmittedFileName();
            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
            InputStream fileStream = filePart.getInputStream();

            Properties properties = new Properties();
            properties.setProperty("file_name", fileName);
            properties.setProperty("file_type", fileType);
            properties.setProperty("email", email);

            if (!Database.insertImage(properties, fileStream))
                System.out.println("Upload failed " + fileName);
        }

    }
}