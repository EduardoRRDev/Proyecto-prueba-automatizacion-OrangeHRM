package co.com.proyecto.automatizacion.pages.pim;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.annotations.DefaultUrl;
import org.openqa.selenium.By;

/**
 * Page Object para la lista de empleados (módulo PIM).
 */
@DefaultUrl("https://opensource-demo.orangehrmlive.com/web/index.php/pim/viewEmployeeList")
public class EmployeeListPage extends PageObject {

    @FindBy(xpath = "(//input[@placeholder=\"Type for hints...\"])[1]")
    public WebElementFacade inputEmployeeName;

    @FindBy(xpath = "//div[contains(@class,'oxd-form-row') and contains(.,'Employee Id')]//input[contains(@class,'oxd-input')]")
    public WebElementFacade inputEmployeeId;

    @FindBy(xpath = "//button[@type=\"submit\"]")
    public WebElementFacade btnSearch;

    @FindBy(xpath = "//i[@class=\"oxd-icon bi-trash\"]")
    public WebElementFacade iconTrash;

    @FindBy(xpath = "//button[contains(@class,'oxd-button--label-danger') and contains(.,'Yes, Delete')]")
    public WebElementFacade btnConfirmDelete;

    public WebElementFacade cellIdInTable(String id) {
        return find(By.xpath("//div[text()=\"" + id + "\"]"));
    }

    public WebElementFacade cellNameInTable(String name) {
        return find(By.xpath("//div[text()=\"" + name + "\"]"));
    }

    public WebElementFacade cellLastNameInTable(String lastName) {
        return find(By.xpath("//div[text()=\"" + lastName + "\"]"));
    }
}
