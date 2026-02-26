package co.com.proyecto.automatizacion.pages.pim;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.annotations.DefaultUrl;

/**
 * Page Object para el formulario Add Employee (módulo PIM).
 */
@DefaultUrl("https://opensource-demo.orangehrmlive.com/web/index.php/pim/addEmployee")
public class AddEmployeePage extends PageObject {

    @FindBy(css = "input[name='firstName']")
    public WebElementFacade inputFirstName;

    @FindBy(css = "input[name='middleName']")
    public WebElementFacade inputMiddleName;

    @FindBy(css = "input[name='lastName']")
    public WebElementFacade inputLastName;

    @FindBy(xpath = "(//form[contains(@class,'oxd-form')]//input[contains(@class,'oxd-input') and not(@type='password')])[4]")
    public WebElementFacade inputEmployeeId;

    @FindBy(xpath = "//div[3]/div/div[1]/div/div[2]/input")
    public WebElementFacade inputNewUsername;

    @FindBy(xpath = "(//div[@class=\"oxd-input-group oxd-input-field-bottom-space\"]//input[contains(@class,'oxd-input')][@type='password'])[1]")
    public WebElementFacade inputNewPassword;

    @FindBy(xpath = "(//div[@class=\"oxd-input-group oxd-input-field-bottom-space\"]//input[contains(@class,'oxd-input')][@type='password'])[2]")
    public WebElementFacade inputConfirmPassword;

    @FindBy(xpath = "//form[contains(@class,'oxd-form')]//button[@type='submit']")
    public WebElementFacade btnSave;

    @FindBy(css = "h6.orangehrm-main-title")
    public WebElementFacade tituloCompleteDetails;
}
