package no.nb.nna.veidemann.brregseedimport.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.GZIPInputStream;

public class BrregDownloadService {

    private static final Logger logger = LoggerFactory.getLogger(BrregDownloadService.class);
    private StopWatch stopWatch = new StopWatch("Download data set from brreg.no");

    public BrregDownloadService() {
    }

    public StopWatch getStopWatch() {
        return stopWatch;
    }
    public void downloadAndUnzipDataset(String link, String input, String output) throws IOException {

        stopWatch = new StopWatch("Download data set from brreg.no");
        stopWatch.start();

//        //String downloadURL = settings.getBrregDownloadLink();
//        //String gzipFile = settings.getGzipFile();
//        //String jsonFile = settings.getJsonFile();
//
//        String gzipFile = "/home/andreasbo/app/brregdownload/orglist.gz";
//
//        String jsonFile = "/home/andreasbo/app/brregdownload/orglist.json";

        URL url = null;
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());

        FileOutputStream fos = new FileOutputStream(input);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();

        byte[] buffer = new byte[1024];

        try {
            GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(input));
            FileOutputStream out = new FileOutputStream(output);

            int len;
            while ((len = gzis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            gzis.close();
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stopWatch.stop();
    }
}