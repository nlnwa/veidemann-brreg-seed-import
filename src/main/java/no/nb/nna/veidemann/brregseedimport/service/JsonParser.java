package no.nb.nna.veidemann.brregseedimport.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import no.nb.nna.veidemann.brregseedimport.model.Organization.Organization;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class JsonParser {

    private File filename;
    private List<Organization> organizationList;
    private StopWatch stopWatch = new StopWatch("Importing JSON");

    public JsonParser(String filename) {
        this.filename = new File(filename);
    }

    public StopWatch getStopWatch() { return stopWatch;}

    public List<Organization> getOrganzationList(){
        return organizationList;
    }




    public void parseJsonFile() throws FileNotFoundException {
        stopWatch.start();
        long timeInMillis = Calendar.getInstance().getTimeInMillis();
        JsonReader reader = new JsonReader(new FileReader(filename));
        Type listType = new TypeToken<ArrayList<Organization>>() {
        }.getType();
        organizationList = new Gson().fromJson(reader, listType);
        for (Organization o : organizationList) {
            o.setTimestamp(timeInMillis);
        }
        stopWatch.stop();
    }
}





















