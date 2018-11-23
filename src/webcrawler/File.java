package webcrawler;

public class File implements Comparable<File> {
    
    private String fileUrl;
    private int fileSize;
    
    public File(String fileUrl, int fileSize) {
        super();
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }
    
    @Override
    public int compareTo(File compareFile) {
        int compareBytes = ((File) compareFile).getFileSize();
        //ascending
        //return this.fileSize - compareBytes;
        
        //descending
        return compareBytes - this.fileSize;
    }
}
