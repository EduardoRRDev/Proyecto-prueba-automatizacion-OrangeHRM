package co.com.proyecto.automatizacion.pages.common;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.annotations.At;

/**
 * Page Object para el Dashboard principal (página común tras login).
 */
@At("https://opensource-demo.orangehrmlive.com/web/index.php/dashboard/index")
public class MainPage extends PageObject {

    @FindBy(xpath = "//h6[contains(@class,'oxd-topbar-header-breadcrumb') or contains(text(),'Dashboard')]")
    public WebElementFacade txtTitleMainPage;

    @FindBy(xpath = "//span[text()='PIM']/ancestor::a | //a[.//span[text()='PIM']]")
    public WebElementFacade linkPim;
}
