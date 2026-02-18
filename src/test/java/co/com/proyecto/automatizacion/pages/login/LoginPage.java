package co.com.proyecto.automatizacion.pages.login;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.annotations.findby.How;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.annotations.DefaultUrl;

/**
 * Page Object para la página de inicio de sesión (módulo login).
 */
@DefaultUrl("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login")
public class LoginPage extends PageObject {

    @FindBy(how = How.NAME, using = "username")
    public WebElementFacade inputUsername;

    @FindBy(how = How.NAME, using = "password")
    public WebElementFacade inputPassword;

    @FindBy(css = "button[type='submit']")
    public WebElementFacade btnLogin;
}
