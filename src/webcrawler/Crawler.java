package webcrawler;

import com.sleepycat.je.Database;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;

public class Crawler extends WebCrawler {
    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);

    private static final Pattern FILTERS = Pattern.compile(
        ".*(\\.(ram|rm|smil|zip|rar|gz))$");
    
    private static final Pattern FILE_PATTERNS = Pattern.compile(".*(\\.(html|php|css|jsp|js|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|m4v|pdf"
            + "|ico|wmv|swf|wma|bmp|gif|svg|jpeg|jpg|png|tiff?))$");
    
    private String filesInfo = "";
    private String crawlUrl = "http";
    
    private final ArrayList<String> links = new ArrayList<String>();
    private final ArrayList<File> files = new ArrayList<File>();

    CrawlStat myCrawlStat;

    public Crawler() {
        myCrawlStat = new CrawlStat();
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        URL tmpURL = null;
        try {
            tmpURL = new URL(href);
        } catch (MalformedURLException ex) {
            java.util.logging.Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(FILE_PATTERNS.matcher(href).matches()) {
            if(tmpURL != null) { 
                int fileSize = getFileSize(tmpURL);
                File fileObj = new File(href, fileSize);
                files.add(fileObj);
            }
        }
        links.add(href);
        //return !FILTERS.matcher(href).matches() && (href.startsWith(crawlUrl));
        return !FILTERS.matcher(href).matches();
    }

    @Override
    public void visit(Page page) {
        logger.info("Visited: {}", page.getWebURL().getURL());
        myCrawlStat.incProcessedPages();
        
        //String url = page.getWebURL().getURL();
        
//        if(FILE_PATTERNS.matcher(url).matches()) {
//            int fileSize = page.getContentData().length;
//            String fileStr = "File: " + url + " (" + fileSize + " bytes)\r\n";
//            filesInfo = filesInfo.concat(fileStr);
//        }

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData parseData = (HtmlParseData) page.getParseData();
            Set<WebURL> links = parseData.getOutgoingUrls();
            myCrawlStat.incTotalLinks(links.size());
            try {
                myCrawlStat.incTotalTextSize(parseData.getText().getBytes("UTF-8").length);
            } catch (UnsupportedEncodingException ignored) {
                // Do nothing
            }
        }
        // We dump this crawler statistics after processing every 50 pages
        if ((myCrawlStat.getTotalProcessedPages() % 50) == 0) {
            dumpMyData();
        }
    }

    /**
     * This function is called by controller to get the local data of this crawler when job is
     * finished
     */
    @Override
    public Object getMyLocalData() {
        return myCrawlStat;
    }

    /**
     * This function is called by controller before finishing the job.
     * You can put whatever stuff you need here.
     */
    @Override
    public void onBeforeExit() {
        dumpMyData();
        try {
            writeLinksToFile(links);
            
            Collections.sort(files);
            for(File file : files) {
                String fileStr = "(" + file.getFileSize() + " bytes) File: " + file.getFileUrl() + "\r\n";
                filesInfo = filesInfo.concat(fileStr);
            }
            
            writeToFile(filesInfo);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void dumpMyData() {
        int id = getMyId();
        // You can configure the log to output to file
        logger.info("Crawler {} > Processed Pages: {}", id, myCrawlStat.getTotalProcessedPages());
        logger.info("Crawler {} > Total Links Found: {}", id, myCrawlStat.getTotalLinks());
        logger.info("Crawler {} > Total Text Size: {}", id, myCrawlStat.getTotalTextSize());
    }
    
//    public void writeToFile() throws IOException {
//        String str = "File(filesize): ";
//        BufferedWriter writer = new BufferedWriter(new FileWriter("data.txt"));
//        writer.write(str);
//        writer.close();
//    }
    
    public void writeToFile(String file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("data.txt"));
        writer.append(file);
        writer.close();
    }
    
    public void writeLinksToFile (ArrayList<String> links) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("links.txt"));
        String URLs = "";
        for(String link : links){
            URLs = URLs.concat(link + "\r\n");
        }
        
        writer.append(URLs);
        writer.close();
    }
    
    public String getCrawlUrl() {
        return crawlUrl;
    }

    public void setCrawlUrl(String crawlUrl) {
        this.crawlUrl = crawlUrl;
    }
    
    private static int getFileSize(URL url) {
        URLConnection conn = null;
        try {
            conn = url.openConnection();
            if(conn instanceof HttpURLConnection) {
                ((HttpURLConnection)conn).setRequestMethod("HEAD");
            }
            conn.getInputStream();
            return conn.getContentLength();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(conn instanceof HttpURLConnection) {
                ((HttpURLConnection)conn).disconnect();
            }
        }
    }
}