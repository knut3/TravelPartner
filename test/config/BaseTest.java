package config;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import play.test.FakeApplication;
import play.test.Helpers;

import com.avaje.ebean.Ebean;


public class BaseTest{
	public static FakeApplication app;
	public static String createDdl = "";
	public static String dropDdl = "";
	 
	  @BeforeClass
	  public static void startApp() throws IOException {
	    app = Helpers.fakeApplication(Helpers.inMemoryDatabase(), new TestGlobal());
	    Helpers.start(app);
	    
	    
	    // Reading the evolution file
	    String evolutionContent = FileUtils.readFileToString(
	        app.getWrappedApplication().getFile("conf/evolutions/default/1.sql"));
	 
	    // Splitting the String to get Create & Drop DDL
	    String[] splittedEvolutionContent = evolutionContent.split("# --- !Ups");
	    String[] upsDowns = splittedEvolutionContent[1].split("# --- !Downs");
	    createDdl = upsDowns[0];
	    dropDdl = upsDowns[1];
	  }
	 
	  @AfterClass
	  public static void stopApp() {
	    Helpers.stop(app);
	  }
	  
	  @Before
	  public void createCleanDb() {
	    Ebean.execute(Ebean.createCallableSql(dropDdl));
	    Ebean.execute(Ebean.createCallableSql(createDdl));
	  } 
}
