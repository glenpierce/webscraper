import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class WebScraper {

	private ArrayList<String> output = new ArrayList<>();

	public static void main(String[] args) {
		WebScraper newInstance = new WebScraper();
//		newInstance.run();
//		newInstance.getCitiesCount();
		newInstance.getPopulation();
	}

	private void getPopulation(){
//		WebDriver driver = new ChromeDriver();
		String cities = "City of Gaziantep, Turkey\n" +
				"Istanbul Metropolitan Municipality, Turkey\n" +
				"Izmir Metropolitan Municipality, Turkey\n" +
				"Kadiköy Municipality, Turkey\n" +
				"Nevsehir Municipality, Turkey\n" +
				"Yalova Municipality, Turkey\n";
		ArrayList<String> citiesList = new ArrayList<>();
		for(int i = 0; i < cities.length(); i++){
			try {
				citiesList.add(cities.substring(i, cities.indexOf('\n', i)));
				i = cities.indexOf('\n', i);
				System.out.println(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for(String city : citiesList){
			System.out.println(city);
		}
	}

	private void getCitiesCount(){

		WebDriver driver = new ChromeDriver();
		String baseUrl = "http://www.covenantofmayors.eu/about/signatories_en.html?q=Search+for+a+Signatory...&country_search=&population=&date_of_adhesion=&status=&commitments1=1&commitments2=1&commitments3=1";
		driver.navigate().to(baseUrl);

		for(int i = 0; i < 52; i++){
			WebElement mySelectElement = driver.findElement(By.xpath("//*[@id='form_search_list']/select[1]"));
			Select dropdown= new Select(mySelectElement);
			String countryName = dropdown.getOptions().get(i).getText();
			dropdown.selectByIndex(i);
			WebElement country = driver.findElement(By.xpath("//*[@id='left_content']/h3"));

			System.out.println(countryName + " " + country.getText());
		}
	}

	private void run() {

		File file = new File("webscraperFile.txt");
		System.out.println(file.getAbsolutePath());
		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream(file);
		} catch (Exception e){
			System.out.println("error getting FileOutputStream");
			return;
		}
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
		Writer writer = new BufferedWriter(outputStreamWriter);
		try{
			writer.write("First write\n");
//			writer.close();
		} catch (Exception e){
			System.out.println("error writing to file");
			return;
		}

		String lastAction = "";
		WebDriver driver = new ChromeDriver();
		output.add("\n");
		int lineNumber = 1170;
		output.add("id");
		output.add("city");
		output.add("country");
		output.add("pop");
		output.add("commitment 2020");
		output.add("commitment 2030");
		output.add("status");
		output.add("date of adhesion");
		output.add("plan date");
		output.add("emissions reduction target");
		output.add("Plan title");
		output.add("Plan link");
		output.add("Baseline emissions, tCO₂/cap");
		output.add("baseline year");
		output.add("emissions reduction target, tCO₂e");
		output.add("\n");

		for (int h = 30; h < 90; h++) {
//			for (int h = 10; h < 168; h++) {
			String baseUrl = "http://www.covenantofmayors.eu/about/signatories_en.html?q=Search+for+a+Signatory...&country_search=&population=&date_of_adhesion=&status=&commitments1=1&commitments2=1&commitments3=1";
			driver.navigate().to(baseUrl + "&start=" + h);
			List<WebElement> rows = driver.findElements(By.xpath("//*[@id=\"left_content\"]/table/tbody/tr"));
			int numberOfRows = rows.size();
			for (int i = 2; i < numberOfRows; i++) {
				lineNumber++;
				try {
					output.add(Integer.toString(lineNumber));
					lastAction = "getting city name";
					WebElement cityName = driver.findElement(By.xpath("//*[@id=\"left_content\"]/table/tbody/tr[" + i + "]/td[1]/a/strong"));
					output.add(cityName.getText());
					lastAction = "getting country";
					String country = driver.findElement(By.xpath("//*[@id=\"left_content\"]/table/tbody/tr[" + i + "]/td[1]/a")).getText();
					country = country.substring(country.indexOf(",") + 2);
					output.add(country);
					lastAction = "getting population";
					WebElement population = driver.findElement(By.xpath("//*[@id=\"left_content\"]/table/tbody/tr[" + i + "]/td[2]"));
					output.add(population.getText());
					lastAction = "getting commitments";
					WebElement commitments = driver.findElement(By.xpath("//*[@id=\"left_content\"]/table/tbody/tr[" + i + "]/td[3]"));
					String commitmentsString = commitments.getAttribute("innerHTML");
					String _2020 = ";";
					String _2030 = "";
					if(commitmentsString.contains("2020")){
						_2020 = "2020;";
					}
					if(commitmentsString.contains("2030")){
						_2030 = "2030";
					}
					output.add(_2020 + " " + _2030);
					lastAction = "getting status";
					WebElement status = driver.findElement(By.xpath("//*[@id=\"left_content\"]/table/tbody/tr[" + i + "]/td[4]"));
					String statusString = status.getAttribute("innerHTML");
					String statusResult = "";
					if(statusString.contains("Adhesion")){
						statusResult = "1";
					}
					if(statusString.contains("Action Plans")){
						statusResult = "2";
					}
					if(statusString.contains("Monitoring started")){
						statusResult = "3";
					}

					output.add(statusResult);

					lastAction = "clicking city name";
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", cityName);
					Thread.sleep(500);
					cityName.click();

					lastAction = "getting Date of Adhesion";
					WebElement dateOfAdhesion = driver.findElement(By.xpath("//*[@id=\"profile_overview\"]/table[2]/tbody/tr/td"));
					output.add(dateOfAdhesion.getText());

					//Action Plan

					List<WebElement> statusColumns = driver.findElements(By.xpath("//*[@id=\"profile_menu\"]/table/tbody/tr/td"));
					boolean hasActionPlan = false;
					if (statusColumns.size() > 1) {
						WebElement support = driver.findElement(By.xpath("//*[@id=\"profile_menu\"]/table/tbody/tr/td[2]"));
						String supportText = support.getText();
						hasActionPlan = supportText.equals("Action Plan");
					}

					if (hasActionPlan) {
						lastAction = "clicking Action Plan";
						driver.findElement(By.xpath("//*[@id=\"profile_menu\"]/table/tbody/tr/td[2]/a")).click();
						Thread.sleep(500);
						driver.switchTo().frame(driver.findElement(By.xpath("//*[@id=\"profile_seap\"]/iframe")));
						lastAction = "getting date of formal approval";
						WebElement dateOfFormalApproval = driver.findElement(By.xpath("//*[@id=\"content_iframe_graphs_edit\"]/div/div/div/div[1]/table/tbody/tr[1]/td"));
						//*[@id="content_iframe_graphs_edit"]/div/div/div/div[1]/table/tbody/tr[1]/td
						output.add(dateOfFormalApproval.getText());
						lastAction = "getting emission reduction target";
						WebElement emissionReductionTarget = driver.findElement(By.xpath("//*[@id=\"content_iframe_graphs_edit\"]/div/div/div/div[1]/table/tbody/tr[2]/td"));
						output.add(emissionReductionTarget.getText());
						lastAction = "getting plan title";
						try {
							WebElement planTitle = driver.findElement(By.xpath("//*[@id=\"content_iframe_graphs_edit\"]/div/div/div/table/tbody/tr/td[1]/a"));
							output.add(planTitle.getText());
							output.add(planTitle.getAttribute("href"));
						} catch (Exception e){
							output.add("error getting plan title");
							output.add("error getting plan title");
						}
						lastAction = "getting CO2 Tonnes";
						WebElement co2Tonnes = driver.findElement(By.xpath("//*[@id=\"gas_emission_and_final_consumption_per_capita_list\"]/tbody/tr/td[2]"));
						output.add(co2Tonnes.getText());
						lastAction = "getting base line year";
						WebElement baselineYear = driver.findElement(By.xpath("//*[@id=\"edit_synthesis_report_form\"]/div[1]/div/div/label"));
						output.add(baselineYear.getText());

						if (driver.findElements(By.xpath("//*[@id=\"greenhouse_gas_emissions_reduction_target_list\"]/tbody/tr/td[2]")).size() > 0) {
							lastAction = "getting other CO2 value";
							output.add(driver.findElement(By.xpath("//*[@id=\"greenhouse_gas_emissions_reduction_target_list\"]/tbody/tr/td[2]")).getText());
						}

						driver.switchTo().defaultContent();

						driver.navigate().back();
					}

					//Monitoring

					boolean hasMonitoring = false;

					if (statusColumns.size() > 4) {
						hasMonitoring = true;
					}

					if (hasMonitoring) {
						lastAction = "has monitoring, clicking monitoring";
						driver.findElement(By.xpath("//*[@id=\"profile_menu\"]/table/tbody/tr/td[3]/a")).click();

						lastAction = "getting number of monitoring years";
						driver.switchTo().frame(driver.findElement(By.xpath("//*[@id=\"profile_monitoring\"]/iframe")));
						int numberOfMonitoringYears = driver.findElements(By.xpath("//*[@id=\"monitoring_table\"]/table/tbody/tr")).size();
						output.add(Integer.toString(numberOfMonitoringYears));

						driver.switchTo().defaultContent();

						output.add(driver.getCurrentUrl());

						driver.navigate().back();
					}

					driver.navigate().to(baseUrl + "&start=" + h);
					output.add("\n");

					StringBuilder stringBuilder = new StringBuilder();
					for(String line : output){
						if(!line.equals("\n")) {
							stringBuilder.append(line + "; ");
						}
					}
					System.out.println(stringBuilder.toString());

					try {
						for (String line : output) {
							writer.write(line + (line.equals("\n") ? "" : "; "));
						}
					} catch (Exception e){
						System.out.println("failed to write line");
						return;
					}
					output.clear();

				} catch (Exception e) {
//					e.printStackTrace();
					System.out.println("error on line " + lineNumber + " " + lastAction);
					output.add("\n");
					driver.navigate().to(baseUrl + "&start=" + h);
					output.add("Error on line" + lineNumber);
					output.add("\n");
				}
			}
		}
		try {
			writer.close();
		} catch (Exception e){
			System.out.println("failed to close writer");
		}

//		StringBuilder stringBuilder = new StringBuilder();
//		for (String line : output) {
//			stringBuilder.append(line).append(line.equals("\n") ? "" : "; ");
//		}
//		logger.info(stringBuilder.toString());
	}
}
