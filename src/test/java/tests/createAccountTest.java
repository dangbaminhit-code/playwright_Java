package tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.createAccount;

public class createAccountTest extends testBase{

@DataProvider(name = "dataCreate")
public Object[][] dataCreate(){
    return new Object[][]{
            {"m1@test.com","123456789@xX"},
            {"m2@test.com","Abcdef12@#"},
            {"m3@test.com","rty99!x"},
    };
}

    @Test(dataProvider = "dataCreate",description = "create new account")
    public void testCreateNewAccount(String email, String password){
        createAccount createNewAccount = new createAccount(page,baseUrl);
        createNewAccount.gotoCreateAccount();
        createNewAccount.createNewAccount(email,password);
    }
    @Test(description = "go to login page from create account page")
    public void goToLoginPage(){
        createAccount createNewAccount = new createAccount(page,baseUrl);
        createNewAccount.gotoCreateAccount();
        createNewAccount.goToLoginPage();
    }
}
