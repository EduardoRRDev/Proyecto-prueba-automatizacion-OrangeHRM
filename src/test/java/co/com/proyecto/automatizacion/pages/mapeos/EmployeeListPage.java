package co.com.proyecto.automatizacion.pages.mapeos;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.annotations.DefaultUrl;
import org.openqa.selenium.By;

/**
 * Page Object para la lista de empleados (Employee List) en OrangeHRM PIM.
 */
@DefaultUrl("https://opensource-demo.orangehrmlive.com/web/index.php/pim/viewEmployeeList")
public class EmployeeListPage extends PageObject {

    // Employee Name - (//input[@placeholder="Type for hints..."])[1]
    @FindBy(xpath = "(//input[@placeholder=\"Type for hints...\"])[1]")
    public WebElementFacade inputEmployeeName;

    // Employee Id - segundo input en formulario (sin --active para compatibilidad CI)
    @FindBy(xpath = "(//form//input[contains(@class,'oxd-input') and not(@type='password')])[2]")
    public WebElementFacade inputEmployeeId;

    // Botón Search - //button[@type="submit"]
    @FindBy(xpath = "//button[@type=\"submit\"]")
    public WebElementFacade btnSearch;

    // Icono eliminar (trash) - //i[@class="oxd-icon bi-trash"]
    @FindBy(xpath = "//i[@class=\"oxd-icon bi-trash\"]")
    public WebElementFacade iconTrash;

    // Botón confirmar eliminación "Yes, Delete"
    @FindBy(xpath = "//button[contains(@class,'oxd-button--label-danger') and contains(.,'Yes, Delete')]")
    public WebElementFacade btnConfirmDelete;

    // Validación en tabla - //div[text()="valor"] (xpaths según usuario)
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
