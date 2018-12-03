package BCActivPlant;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class ActivPlant {
	public static WebDriver driver;
	public WebDriverWait webForElement;
	public static JSONObject jsonObject = null;
	public static JSONTokener jsonTokener = null;
	public static Actions act;
	public static String arrayName = "";
	public static String savedMessage = "";
	public static String businessContextName = "";
	public static String renameElementValue = "";
	public static String selectBCName = "";
	public SoftAssert softAssert = new SoftAssert();
	
	
	public JsonArray fileCall(String arrayNamePass) throws FileNotFoundException
	{
		File jsonInputFile = new File("/home/linuxuser/rosy/activPlant.json");
	    InputStream is;
	    is = new FileInputStream(jsonInputFile);
	    // Create JsonReader from Json.
	    JsonReader reader = Json.createReader(is);
	    // Get the JsonObject structure from JsonReader.
	    JsonObject jsonObject = reader.readObject();
	    reader.close();
		JsonArray jsonArrayRename = jsonObject.getJsonArray(arrayNamePass);
		return jsonArrayRename;
	}
	
	public void selectBCNameFromList(String BCName) throws JSONException
	{
		// Open business context by selecting the business context name from the list
		webForElement.until(ExpectedConditions.visibilityOfElementLocated(By.className("dataTable")));
		WebElement Webtable = driver.findElement(By.className("dataTable"));
		List<WebElement> TotalRowCount = Webtable.findElements(By.xpath("//*[@id=\"EG_dataTable\"]/tbody/tr"));
		int RowIndex = 1;

		outer: for (WebElement rowElement : TotalRowCount) 
				{
					List<WebElement> TotalColumnCount = rowElement.findElements(By.xpath("td"));
					int ColumnIndex = 1;
					for (WebElement colElement : TotalColumnCount) 
					{
						if (colElement.getText().equals(BCName))
						{
							act.doubleClick(colElement).build().perform();
							break outer;
						}
						ColumnIndex = ColumnIndex + 1;
					}
					RowIndex = RowIndex + 1;
				}
	}

	@Test(priority=0)
	public void loginPage() 
	{
		try
		{
			String expectedTitle = "Enterprise Gateway";
			String actualTitle = "";
		
			jsonTokener = new JSONTokener(new FileReader("/home/linuxuser/businessContext.json"));
			jsonObject = new JSONObject(jsonTokener);
			System.setProperty("webdriver.gecko.driver", "/home/linuxuser/geckodriver");
			driver = new FirefoxDriver();
			driver.get(jsonObject.getString("BaseURL"));
			webForElement = new WebDriverWait(driver, 190);
			actualTitle = driver.getTitle();
			System.out.println(actualTitle);
			System.out.println(expectedTitle);
			
			// login
			webForElement.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
			driver.findElement(By.id("username")).sendKeys(jsonObject.getString("UserName"));
			driver.findElement(By.id("password")).sendKeys(jsonObject.getString("Password"));
			driver.findElement(By.name("login")).click();
			//Assert.assertEquals(actualTitle, expectedTitle);
			if(!actualTitle.equals(expectedTitle))
			{
				Reporter.log("Title Mismatch");
				Assert.fail("Title Mismatch");
			}
			
			 
		}
		catch (Exception e)
		{
			//Assert.fail("Error: "+ae.getMessage(),ae.getCause());
	
			//System.out.println(ae.getMessage().toString());
			//throw new AssertionError("A clear description of the failure");
			
		} 
	
	}
	public void NewBCName() 
	{
		try {
			//click Business
			webForElement.until(ExpectedConditions.visibilityOfElementLocated(By.id("Bus")));
			driver.findElement(By.id("Bus")).click();

			// function call
			String newBCName = jsonObject.getString("BusinessContextName");
			
			// click Business Context
			webForElement.until(ExpectedConditions.visibilityOfElementLocated(By.id("imgBuscontext")));
			Thread.sleep(2000);
			driver.findElement(By.id("imgBuscontext")).click();
			Thread.sleep(1000);

			// Click new
			webForElement.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("New")));
			driver.findElement(By.linkText("New")).click();
			
			Thread.sleep(1000);

			// enter the name of the business context
			driver.findElement(By.id("bcName")).clear();
			driver.findElement(By.id("bcName")).sendKeys(newBCName);
			String bcNameValue = driver.findElement(By.id("bcName")).getAttribute("value");
			System.out.println("value" + bcNameValue);
			driver.findElement(By.id("bcName")).sendKeys(Keys.RETURN);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
				
			}
	}		
	public void addlevel() 
	{
		try
		{
			arrayName = jsonObject.getString("keyValueforAddLevel");
			JsonArray jsonArrayValue = fileCall(arrayName);
			for (int j = 0; j < jsonArrayValue.size(); j++)
			{
				String key = "";
				JsonObject jsonObject1 = jsonArrayValue.getJsonObject(j);
				Iterator iter = jsonObject1.keySet().iterator();
				while (iter.hasNext()) 
				{
					key = (String) iter.next();
					Thread.sleep(1000);
					// click business context name
					driver.findElement(By.xpath
							("/html/body/form/div[1]/div[2]/form/div[2]/div[2]/div[2]/div/ul/li/span/a"))
						.click();
					webForElement.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Add Level")));
					driver.findElement(By.linkText("Add Level")).click();
					
					// Enter level name
					driver.findElement(By.xpath("//input[@id='editNode']")).sendKeys(key);
					driver.findElement(By.xpath("//input[@id='editNode']")).sendKeys(Keys.ENTER);

					//Add event to the level
					JsonArray plantElement = jsonObject1.getJsonArray(key);
					for (int plantElementCount = 0; plantElementCount < plantElement.size(); plantElementCount++) 
					{
						
						JsonObject eventObject =plantElement.getJsonObject(plantElementCount);
						JsonArray eventArray = eventObject.getJsonArray("PlantTreeStructure");
						JsonArray eventToClick = eventObject.getJsonArray("EventToClick");
						int index = eventArray.size();
						int indexIncrement = 0;
						act = new Actions(driver);
				            System.out.println("\nDirect Reports:");
				            			      
				            for(JsonValue eventValue : eventArray){
				               
				                
				                driver.findElement(By.xpath("//a[text()='"+ eventValue.toString()+"']"
										+ "/preceding-sibling::span[@class='dynatree-expander']")).click();
				                indexIncrement++;
				            }
				                
				                for(JsonValue eventToClickValue : eventToClick)
				                {
				                	webForElement.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(eventToClickValue.toString())));
				                	act.doubleClick(driver.findElement(By.linkText(eventToClickValue.toString()))).build().perform();
				                }
					}
				 }
			 }
			
		}catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public void renamePlantItem() throws Exception
	{
		try {
				arrayName = jsonObject.getString("keyValueforRenamePlantItem");
				JsonArray jsonArray = fileCall(arrayName);
				for (int j = 0; j < jsonArray.size(); j++) 
				{
					String keyRename = "";
					JsonObject jsonObjectRename = jsonArray.getJsonObject(j);
					int incrementValue = 0;
					Iterator iter = jsonObjectRename.keySet().iterator();
					while (iter.hasNext()) 
					{
						keyRename = (String) iter.next();
						incrementValue = incrementValue + 1;
						JsonArray RenameElement = jsonObjectRename.getJsonArray(keyRename);
						for (int RenameElementCount = 0; RenameElementCount < RenameElement.size(); RenameElementCount++)
						{
							driver.findElement(
							By.xpath("/html/body/form/div[1]/div[2]/form/div[2]/div[2]/div[2]/div/ul/li/ul/li["
									+ incrementValue + "]/ul/li[" + (RenameElementCount + 1) + "]/span/a"))
							.click();
							String RenameElementName = RenameElement.get(RenameElementCount).toString();
							//Thread.sleep(1000);
							webForElement.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Rename")));
							// Rename the plant name
							driver.findElement(By.linkText("Rename")).click();
							//webForElement.until(ExpectedConditions.presenceOfElementLocated(By.id("//input[@id='editNode']")));
							//webForElement.until(presenceO)
							driver.findElement(By.xpath("//input[@id='editNode']"))
							.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
							driver.findElement(By.xpath("//input[@id='editNode']")).sendKeys(RenameElementName);
							driver.findElement(By.xpath("//input[@id='editNode']")).sendKeys(Keys.ENTER);

						}

					}
				}

		}catch(Exception e)
		{
			System.out.println(e.getMessage().toString());
		}
	}
	
	@Test(priority=1)
	public void bccontext() throws JSONException, FileNotFoundException, InterruptedException
	{
		try {
			//function calls 
			NewBCName(); 
			addlevel();
			renamePlantItem();
			
			// Save business context
			driver.findElement(
			By.xpath("/html/body/form/div[1]/div[2]/form/div[2]/div[2]/table/tbody/tr[2]/td[7]/label/span"))
			.click();
			driver.findElement(By.id("Bcsave")).click();
			Thread.sleep(1000);
			savedMessage = driver.findElement(By.id("lblMessage")).getText();
			Assert.assertEquals(savedMessage,"BC saved successfully.");
			/*if(!savedMessage.equals("BC saved successfully."))
			{
				Reporter.log("Busines Context Name is not saved");
				Assert.fail("Error in saving Business Context Name");
				System.out.println("Business saved sucessfully");
			}*/
			}
			catch(Exception ae)
			{
				Assert.fail(ae.toString());
			}
		
	
	}
	@Test(priority=2)
	public void validateBCName() throws JSONException, InterruptedException, FileNotFoundException 
	{
		try {
				selectBCName = jsonObject.getString("BusinessContextNameSelected");
				selectBCNameFromList(selectBCName);
				Thread.sleep(1000);
							
				// click business context name expander
				driver.findElement(By.xpath("/html/body/form/div[1]/div[2]/form/div[2]/div[2]/div[2]/div/ul/li/span/span"))
				.click();
				Thread.sleep(1000);
				WebElement BCName = driver
				.findElement(By.xpath("/html/body/form/div[1]/div[2]/form/div[2]/div[2]/div[2]/div/ul/li/span/a"));
				businessContextName = BCName.getText();
				Assert.assertNotNull(businessContextName);
				Assert.assertEquals(businessContextName, jsonObject.getString("BusinessContextName"));
				
				// Calling json file
				arrayName = jsonObject.getString("keyValueforValidation");
				JsonArray jsonArrayRename = fileCall(arrayName);

				for (int j = 0; j < jsonArrayRename.size(); j++) 
				{
					String keyRename = "";
					JsonObject jsonObjectRename = jsonArrayRename.getJsonObject(j);
					int incrementValue = 0;
					Iterator iter = jsonObjectRename.keySet().iterator();
					while (iter.hasNext()) 
					{
						keyRename = (String) iter.next();
						System.out.println(keyRename);
						incrementValue = incrementValue + 1;
						String plantKey = "";
						WebElement levelName = driver.findElement(By.xpath("/html/body/form/div[1]/div[2]/form/div[2]/div[2]/div[2]/div/ul/li/ul/li["
						+ incrementValue + "]/span/a"));
						plantKey = levelName.getText();
						System.out.println(plantKey);
						Assert.assertNotNull(plantKey);
						Assert.assertEquals(keyRename, plantKey);
					
						// click level expander
						driver.findElement(By.xpath("/html/body/form/div[1]/div[2]/form/div[2]/div[2]/div[2]/div/ul/li/ul/li["
						+ incrementValue + "]/span/span")).click();
						JsonArray RenameElement = jsonObjectRename.getJsonArray(keyRename);
						for (int s = 0; s < RenameElement.size(); s++)
						{
							String RenameElementName = RenameElement.get(s).toString();
							WebElement renameValue = driver.findElement(
							By.xpath("/html/body/form/div[1]/div[2]/form/div[2]/div[2]/div[2]/div/ul/li/ul/li["
									+ incrementValue + "]/ul/li[" + (s + 1) + "]/span/a"));
							renameElementValue = renameValue.getText();
							Assert.assertNotNull(renameElementValue);
							Assert.assertEquals(renameElementValue, RenameElementName);
						}
					}
				}
			}catch (Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
	@Test(priority=3)
	public void SPLCharacterValidation() throws JSONException, InterruptedException, FileNotFoundException 
	{
		try {
				// click Business Context
				webForElement.until(ExpectedConditions.visibilityOfElementLocated(By.id("imgBuscontext")));
				driver.findElement(By.id("imgBuscontext")).click();
				Thread.sleep(1000);

				// selecting BC name from the list
				selectBCName = jsonObject.getString("BusinessContextNameSelected");
				selectBCNameFromList(selectBCName);

				// click business context name expander
				webForElement.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath("/html/body/form/div[1]/div[2]/form/div[2]/div[2]/div[2]/div/ul/li/span/span")));
				driver.findElement(By.xpath("/html/body/form/div[1]/div[2]/form/div[2]/div[2]/div[2]/div/ul/li/span/span"))
				.click();
				arrayName = jsonObject.getString("keyValueforValidation");
				JsonArray jsonArrayRename = fileCall(arrayName);
				for (int j = 0; j < jsonArrayRename.size(); j++) 
				{
					String keyRename = "";
					JsonObject jsonObjectRename = jsonArrayRename.getJsonObject(j);
					int incrementValue = 0;
					Iterator iter = jsonObjectRename.keySet().iterator();
					while (iter.hasNext()) 
					{
						keyRename = (String) iter.next();
						System.out.println(keyRename);
						incrementValue = incrementValue + 1;
						String plantKey = "";
						WebElement levelName = driver
						.findElement(By.xpath("/html/body/form/div[1]/div[2]/form/div[2]/div[2]/div[2]/div/ul/li/ul/li["
								+ incrementValue + "]/span/a"));
						plantKey = levelName.getText();
						System.out.println(plantKey);
						Assert.assertNotNull(plantKey);
						Assert.assertEquals(keyRename, plantKey);

						// click level expander
						driver.findElement(By.xpath("/html/body/form/div[1]/div[2]/form/div[2]/div[2]/div[2]/div/ul/li/ul/li["
						+ incrementValue + "]/span/span")).click();
						JsonArray RenameElement = jsonObjectRename.getJsonArray(keyRename);
						for (int s = 0; s < RenameElement.size(); s++) 
						{
							String RenameElementName = RenameElement.get(s).toString();
							String renameValue = jsonObject.getString("renameNegativeTest");
							if (RenameElementName.equals(renameValue)) 
							{
								WebElement renameElementValue = driver.findElement(
								By.xpath("/html/body/form/div[1]/div[2]/form/div[2]/div[2]/div[2]/div/ul/li/ul/li["
										+ incrementValue + "]/ul/li[" + (s + 1) + "]/span/a"));
								renameElementValue.click();

								// Rename the plant name
								driver.findElement(By.linkText("Rename")).click();
								driver.findElement(By.xpath("//input[@id='editNode']"))
								.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
								driver.findElement(By.xpath("//input[@id='editNode']"))
								.sendKeys(jsonObject.getString("enterSpecialCharacters"));
								driver.findElement(By.xpath("//input[@id='editNode']")).sendKeys(Keys.ENTER);

								// Check if valid column name is given
								savedMessage = driver.findElement(By.id("lblMessage")).getText();
								System.out.println(savedMessage);
								//Assert.assertEquals(savedMessage, "Special characters are not permitted.");
								if(!savedMessage.equals("Special characters are not permitted."))
								{
									Reporter.log("Special characters should not be allowed");
									Assert.fail("Special characters in column name");
								}
								break;
							}
						}
					}
				}
		
			}catch(Exception e)
			{
				
			}
		
	}
	@Test(priority=4)
	public void sameItemUnderSameLevelValidation() throws JSONException, FileNotFoundException, InterruptedException
	{
		try 
		{	
			// click Business Context
			webForElement.until(ExpectedConditions.visibilityOfElementLocated(By.id("imgBuscontext")));
			driver.findElement(By.id("imgBuscontext")).click();
			Thread.sleep(1000);

			// selecting BC name from the list
			selectBCName = jsonObject.getString("BusinessContextNameSelected");
			selectBCNameFromList(selectBCName);

			// click business context name expander
			webForElement.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("/html/body/form/div[1]/div[2]/form/div[2]/div[2]/div[2]/div/ul/li/span/span")));
			driver.findElement(By.xpath("/html/body/form/div[1]/div[2]/form/div[2]/div[2]/div[2]/div/ul/li/span/span"))
				.click();
			arrayName = jsonObject.getString("keyValueforValidation");
			JsonArray jsonArrayRename = fileCall(arrayName);

			for (int j = 0; j < jsonArrayRename.size(); j++) 
			{
				String keyRename = "";
				JsonObject jsonObjectRename = jsonArrayRename.getJsonObject(j);
				int incrementValue = 0;
				Iterator iter = jsonObjectRename.keySet().iterator();
				while (iter.hasNext()) 
				{
					keyRename = (String) iter.next();
					System.out.println(keyRename);
					incrementValue = incrementValue + 1;
					String plantKey = "";
					WebElement levelName = driver
						.findElement(By.xpath("/html/body/form/div[1]/div[2]/form/div[2]/div[2]/div[2]/div/ul/li/ul/li["
								+ incrementValue + "]/span/a"));
					plantKey = levelName.getText();
					System.out.println(plantKey);
					Assert.assertNotNull(plantKey);
					Assert.assertEquals(keyRename, plantKey);

					// click level expander
					driver.findElement(By.xpath("/html/body/form/div[1]/div[2]/form/div[2]/div[2]/div[2]/div/ul/li/ul/li["
						+ incrementValue + "]/span/span")).click();
					JsonArray RenameElement = jsonObjectRename.getJsonArray(keyRename);
					for (int s = 0; s < RenameElement.size(); s++) 
					{
						System.out.println(RenameElement.size());
						String RenameElementName = RenameElement.get(s).toString();
						String renameSameValue = jsonObject.getString("renameNegativeTest");
						if (RenameElementName.equals(renameSameValue)) 
						{
							WebElement renameElementValue = driver.findElement(
								By.xpath("/html/body/form/div[1]/div[2]/form/div[2]/div[2]/div[2]/div/ul/li/ul/li["
										+ incrementValue + "]/ul/li[" + (s + 1) + "]/span/a"));
							renameElementValue.click();

							// Rename the plant name
							driver.findElement(By.linkText("Rename")).click();
							driver.findElement(By.xpath("//input[@id='editNode']"))
								.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
							driver.findElement(By.xpath("//input[@id='editNode']"))
								.sendKeys(jsonObject.getString("enterRenameValue"));
							driver.findElement(By.xpath("//input[@id='editNode']")).sendKeys(Keys.ENTER);

							// Check if same name is given in the same level
							savedMessage = driver.findElement(By.id("lblMessage")).getText();
							System.out.println(savedMessage);
							//Assert.assertEquals(savedMessage, "Same name cannot be given in same level.");
							if(!savedMessage.equals("Same name cannot be given in same level."))
							{
								Reporter.log("Should not give same name in same level");
								Assert.fail("Same name is given in same level");
							}
							break;
						}
					}
				}
			}
		}catch(Exception e)
		{
			
		}

	}
	@Test(priority=5)
	public void maximumCharactersInRenamingValidation() throws InterruptedException, JSONException, FileNotFoundException 
	{
		try {
				// click Business Context
				webForElement.until(ExpectedConditions.visibilityOfElementLocated(By.id("imgBuscontext")));
				driver.findElement(By.id("imgBuscontext")).click();
				Thread.sleep(1000);

				// selecting BC name from the list
				selectBCName = jsonObject.getString("BusinessContextNameSelected");
				selectBCNameFromList(selectBCName);
		
				// click business context name expander
				webForElement.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("/html/body/form/div[1]/div[2]/form/div[2]/div[2]/div[2]/div/ul/li/span/span")));
				driver.findElement(By.xpath("/html/body/form/div[1]/div[2]/form/div[2]/div[2]/div[2]/div/ul/li/span/span"))
				.click();

				arrayName = jsonObject.getString("keyValueforValidation");
				JsonArray jsonArrayRename = fileCall(arrayName);
				for (int j = 0; j < jsonArrayRename.size(); j++) 
				{
					String keyRename = "";
					JsonObject jsonObjectRename = jsonArrayRename.getJsonObject(j);
					int incrementValue = 0;
					Iterator iter = jsonObjectRename.keySet().iterator();
					while (iter.hasNext())
					{
						keyRename = (String) iter.next();
						System.out.println(keyRename);
						incrementValue = incrementValue + 1;
						String plantKey = "";
						WebElement levelName = driver
						.findElement(By.xpath("/html/body/form/div[1]/div[2]/form/div[2]/div[2]/div[2]/div/ul/li/ul/li["
								+ incrementValue + "]/span/a"));
						plantKey = levelName.getText();
						System.out.println(plantKey);
						Assert.assertNotNull(plantKey);
						Assert.assertEquals(keyRename, plantKey);

						// click level expander
						driver.findElement(By.xpath("/html/body/form/div[1]/div[2]/form/div[2]/div[2]/div[2]/div/ul/li/ul/li["
						+ incrementValue + "]/span/span")).click();
						JsonArray RenameElement = jsonObjectRename.getJsonArray(keyRename);
						for (int s = 0; s < RenameElement.size(); s++) 
						{
							System.out.println(RenameElement.size());
							String RenameElementName = RenameElement.get(s).toString();
							String renameValue = jsonObject.getString("renameNegativeTest");
							if (RenameElementName.equals(renameValue))
							{
								WebElement renameElementValue = driver.findElement(
								By.xpath("/html/body/form/div[1]/div[2]/form/div[2]/div[2]/div[2]/div/ul/li/ul/li["
										+ incrementValue + "]/ul/li[" + (s + 1) + "]/span/a"));
								renameElementValue.click();

								// Rename the plant name
								driver.findElement(By.linkText("Rename")).click();
								driver.findElement(By.xpath("//input[@id='editNode']"))
								.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
								driver.findElement(By.xpath("//input[@id='editNode']"))
								.sendKeys(jsonObject.getString("MaximumCharactersInRenaming"));
						
								String inputtext = driver.findElement(By.xpath("//input[@id='editNode']"))
								.getAttribute("value");

								if (inputtext.length() == 50) 
								{
									System.out.println("Renaming allows maximum of 50 characters.Test case passed");
									
								}
								else 
								{
									Reporter.log("Maximum of 50 characters are allowed");
									Assert.fail("Should not allow more than 50 characters");
									
								}
								break;
							}
						}
					}
		
				}
		}catch(Exception e)
		{
			
		}
	}
	@Test(priority=9,enabled=false)
	public void logOut() throws Exception
	{
		try
			{
				//Click logout button
				WebElement logoutButton = driver.findElement(By.xpath("//img[@src='images/common/logout-icn.png']"));
				logoutButton.click();
			
			}catch(Exception e)
			{
				Assert.fail(e.toString());
			}
	}
	@AfterClass
	public void browserClose()
	{
		driver.close();
	}
	

}
