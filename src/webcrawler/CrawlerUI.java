package webcrawler;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class CrawlerUI extends JPanel {
    //JFrame for application
    private static JFrame window = null;
    
    //JPanel for views
    private static JPanel mainPanel = null;
    
    //Crawler Control
    private static CrawlerController crawlControl = null;
    private static Crawler crawler = null;

    
    public static void main(String[] args) throws Exception {
        // Initialise JFrame window
        window = new JFrame("CDN Simple Web Crawler");
        window.setLocation(120, 80);
        window.setResizable(false);
        window.setLayout(new GridLayout(1, 2, 3, 3));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        mainPanel = new JPanel(new GridBagLayout());
        
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 10, 10, 10);
        
        //Initialise UI components
        JButton submitButton = new JButton("Submit");
        JLabel urlLabel = new JLabel("Crawl URL: ");
        JLabel dumpLabel = new JLabel("Dump Folder: ");
        JLabel limitLabel = new JLabel("Max Pages: ");
        JLabel crawlerLabel = new JLabel("Crawlers: ");
        JTextField urlField = new JTextField(20);
        JTextField dumpField = new JTextField(20);
        JTextField limitField = new JTextField(20);
        JTextArea outputConsole = new JTextArea(1,20);
        dumpField.setText("");
        limitField.setText("0");
        
        String[] crawlerNo = { "1", "2", "3", "4", "5"};
        JComboBox crawlerOptions = new JComboBox(crawlerNo);
        
        outputConsole.setText("Text will change once crawling completes.");
        outputConsole.setEditable(false);
        
        // Add components to the panel
        constraints.gridx = 0;
        constraints.gridy = 0;     
        mainPanel.add(urlLabel, constraints);
        constraints.gridx = 1;
        mainPanel.add(urlField, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 1;     
        mainPanel.add(dumpLabel, constraints);
        constraints.gridx = 1;
        mainPanel.add(dumpField, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 2;     
        mainPanel.add(limitLabel, constraints);
        constraints.gridx = 1;
        mainPanel.add(limitField, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 3;     
        mainPanel.add(crawlerLabel, constraints);
        constraints.gridx = 1;
        mainPanel.add(crawlerOptions, constraints);
        
        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.EAST;
        mainPanel.add(submitButton, constraints);
        
        constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.WEST;
        mainPanel.add(outputConsole, constraints);
        
        // set border for the panel
        mainPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Crawler Control"));
        
        Container contentPane = window.getContentPane();
        contentPane.add(mainPanel);
        window.setSize(new Dimension(450,350));
        window.setVisible(true);

        
        crawlControl = new CrawlerController();
        crawler = new Crawler();
        
        submitButton.addMouseListener(new MouseAdapter() {
           @Override
           public void mousePressed(MouseEvent e) {
               try {
                    //Set submit function
                    String crawlUrl = urlField.getText();
                    String dumpLocation = dumpField.getText();
                    int crawlLimit = Integer.valueOf(limitField.getText());
                    int crawlers = crawlerOptions.getSelectedIndex() + 1;
                    
                    crawler.setCrawlUrl(crawlUrl);
                    if(crawlLimit == 0) {
                        crawlControl.start(crawlUrl, dumpLocation, 10, crawlers);
                    }
                    else {
                        crawlControl.start(crawlUrl, dumpLocation, crawlLimit, crawlers);
                    }
                    outputConsole.setText("Crawling Completed. ");
               } catch (Exception ex) {
                    ex.printStackTrace();
               }
           }
        });

        //crawler.setCrawlUrl("http://159.65.132.146/");
        //crawlControl.start("http://159.65.132.146/", "C:\\Users\\jerry-chung\\Desktop\\WebCrawler\\Data", 10, 1);
    }
}
