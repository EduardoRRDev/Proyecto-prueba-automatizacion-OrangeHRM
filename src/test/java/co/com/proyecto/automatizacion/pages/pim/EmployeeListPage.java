package co.com.proyecto.automatizacion.pages.pim;

import net.serenitybdd.annotations.DefaultUrl;
import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page Object para la lista de empleados (módulo PIM).
 * Responsabilidad: mapear elementos y encapsular interacciones de bajo nivel con la página.
 */
@DefaultUrl("https://opensource-demo.orangehrmlive.com/web/index.php/pim/viewEmployeeList")
public class EmployeeListPage extends PageObject {

    @FindBy(xpath = "(//input[@placeholder='Type for hints...'])[1]")
    public WebElementFacade inputEmployeeName;

    @FindBy(xpath = "//button[@type='submit']")
    public WebElementFacade btnSearch;

    @FindBy(xpath = "//i[@class='oxd-icon bi-trash']")
    public WebElementFacade iconTrash;

    @FindBy(xpath = "//button[contains(@class,'oxd-button--label-danger') and contains(.,'Yes, Delete')]")
    public WebElementFacade btnConfirmDelete;

    /**
     * Selecciona la primera opción del autocomplete que contenga el texto buscado.
     * Retorna true si encontró y seleccionó una opción, false si no apareció el dropdown.
     */
    public boolean selectAutocompleteOption(String text) {
        List<WebElement> options = getDriver().findElements(
            By.xpath("//div[contains(@class,'oxd-autocomplete-option')]//span[contains(text(),'" + text + "')]")
        );
        if (!options.isEmpty()) {
            options.get(0).click();
            return true;
        }
        return false;
    }

    /**
     * Verifica si una celda con el texto exacto existe en la tabla visible.
     */
    public boolean isCellVisible(String text) {
        return !getDriver().findElements(
            By.xpath("//div[contains(@class,'oxd-table-cell')]//div[text()='" + text + "']")
        ).isEmpty();
    }

    /**
     * Retorna el WebElementFacade de una celda de la tabla por texto exacto.
     * Usar solo cuando se sabe que el elemento está en la página visible.
     */
    public WebElementFacade cellByText(String text) {
        return find(By.xpath("//div[contains(@class,'oxd-table-cell')]//div[text()='" + text + "']"));
    }
}
