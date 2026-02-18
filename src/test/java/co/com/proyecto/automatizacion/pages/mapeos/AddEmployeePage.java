package co.com.proyecto.automatizacion.pages.mapeos;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.annotations.DefaultUrl;

/**
 * Page Object para el formulario Add Employee de OrangeHRM PIM.
 * Requiere estar logueado para acceder.
 * Locators optimizados: CSS preferido, name/id sobre XPath, selectores concisos.
 */
@DefaultUrl("https://opensource-demo.orangehrmlive.com/web/index.php/pim/addEmployee")
public class AddEmployeePage extends PageObject {

    // Campos básicos - CSS por name (más rápido y estable que XPath)
    @FindBy(css = "input[name='firstName']")
    public WebElementFacade inputFirstName;

    @FindBy(css = "input[name='middleName']")
    public WebElementFacade inputMiddleName;

    @FindBy(css = "input[name='lastName']")
    public WebElementFacade inputLastName;

    // Employee Id - 4º input (First, Middle, Last, Employee Id)
    @FindBy(xpath = "(//form[contains(@class,'oxd-form')]//input[contains(@class,'oxd-input') and not(@type='password')])[4]")
    public WebElementFacade inputEmployeeId;

    // Campos de login (Create Login Details) - input.oxd-input.oxd-input--active
    @FindBy(xpath = "//div[3]/div/div[1]/div/div[2]/input")
    public WebElementFacade inputNewUsername;

    @FindBy(xpath = "(//div[@class=\"oxd-input-group oxd-input-field-bottom-space\"]//input[contains(@class,'oxd-input')][@type='password'])[1]")
    public WebElementFacade inputNewPassword;

    @FindBy(xpath = "(//div[@class=\"oxd-input-group oxd-input-field-bottom-space\"]//input[contains(@class,'oxd-input')][@type='password'])[2]")
    public WebElementFacade inputConfirmPassword;

    // Botón Save - scoped al formulario Add Employee
    @FindBy(xpath = "//form[contains(@class,'oxd-form')]//button[@type='submit']")
    public WebElementFacade btnSave;

    // Mensaje de éxito - toast (tras guardar) o breadcrumb (cambio de vista)
    @FindBy(xpath = "//h6[@class=\"oxd-text oxd-text--h6 orangehrm-main-title\" and text()=\"Personal Details\"]")
    public WebElementFacade tituloCompleteDetails;
}
