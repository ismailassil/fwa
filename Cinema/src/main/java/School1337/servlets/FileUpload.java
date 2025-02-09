package School1337.servlets;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");

        Collection<Part> fileParts = request.getParts();

        for (Part filePart : fileParts) {

            if (filePart.getSubmittedFileName() == null)
                continue;

            String fileName = filePart.getSubmittedFileName();
            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
            List<String> fileTypes = Arrays.asList("jpg", "jpeg", "png", "gif", "svg", "tiff");
            boolean isExist = false;
            for (String type : fileTypes) {
                if (type.equals(fileType)) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist)
                continue;
            double fileSize = filePart.getSize();
            fileSize = fileSize / 1024 / 1024;
            InputStream fileStream = filePart.getInputStream();

            fileName = fileName.substring(0, fileName.lastIndexOf("."));

            Properties properties = new Properties();
            properties.setProperty("file_name", fileName);
            properties.setProperty("file_type", fileType.toLowerCase().trim());
            properties.setProperty("file_size", String.format("%.2f", fileSize) + " MB");
            properties.setProperty("email", email);

            if (!Database.insertImage(properties, fileStream))
                request.setAttribute("upload_status", "Upload Failed! " + fileName);
            else
                request.setAttribute("upload_status", fileName + " uploaded successfully");
        }
        response.sendRedirect("/profile");

    }
}