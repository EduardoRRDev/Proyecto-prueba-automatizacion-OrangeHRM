package co.com.proyecto.automatizacion.pages.mapeos;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.annotations.At;

/**
 * Page Object para la página principal después del login.
 * 
 * Esta página se muestra después de un login exitoso en la aplicación demo de Serenity.
 * 
 * @At: Indica la URL de esta página. Serenity puede usar esto para verificar
 *      que estás en la página correcta.
 */
@At("https://opensource-demo.orangehrmlive.com/web/index.php/dashboard/index")
public class MainPage extends PageObject {

    @FindBy(xpath = "//h6[contains(@class,'oxd-topbar-header-breadcrumb') or contains(text(),'Dashboard')]")
    public WebElementFacade txtTitleMainPage;

    // Menú lateral - PIM y Add Employee (OrangeHRM OXD)
    @FindBy(xpath = "//span[text()='PIM']/ancestor::a | //a[.//span[text()='PIM']]")
    public WebElementFacade linkPim;

}
