package School1337.helper;

public class FileRecord {
    private String fileName;
    private String size;
    private String mime;

    public FileRecord(String fileName, String size, String mime) {
        this.fileName = fileName;
        this.size = size;
        this.mime = mime;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSize() {
        return size;
    }

    public String getMime() {
        return mime;
    }
}
