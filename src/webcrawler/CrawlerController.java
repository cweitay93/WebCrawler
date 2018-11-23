package webcrawler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class CrawlerController {
    private static final Logger logger =
        LoggerFactory.getLogger(CrawlerController.class);
    
    private String crawlUrl = null;

    public void start(String url, String outputFolder, int limit, int crawlers) throws Exception {

        String rootFolder = outputFolder;
        if(rootFolder.isEmpty()){
            rootFolder = System.getProperty("user.dir");
        }
        int numberOfCrawlers = crawlers;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(rootFolder);
        if(limit != 0)
            config.setMaxPagesToFetch(limit);
        else
            config.setMaxPagesToFetch(1000);
        config.setPolitenessDelay(1000);
        config.setMaxDepthOfCrawling(10);
        config.setIncludeBinaryContentInCrawling(true);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        controller.addSeed(url);
        controller.start(Crawler.class, numberOfCrawlers);

        List<Object> crawlersLocalData = controller.getCrawlersLocalData();
        long totalLinks = 0;
        long totalTextSize = 0;
        int totalProcessedPages = 0;
        for (Object localData : crawlersLocalData) {
            CrawlStat stat = (CrawlStat) localData;
            totalLinks += stat.getTotalLinks();
            totalTextSize += stat.getTotalTextSize();
            totalProcessedPages += stat.getTotalProcessedPages();
        }

        logger.info("Aggregated Statistics:");
        logger.info("\tProcessed Pages: {}", totalProcessedPages);
        logger.info("\tTotal Links found: {}", totalLinks);
        logger.info("\tTotal Text Size: {}", totalTextSize);
    }
}