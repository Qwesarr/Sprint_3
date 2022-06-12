package testData;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CourierTestData {
    //Данные кассира
    public String firstName = "Александр";
    public String password = "qwesar123";
    public String login = "Qwesar";

    public CourierTestData(){
    }

    public CourierTestData(String firstName, String password, String login){
        this.firstName = firstName;
        this.password=password;
        this.login=login;
    }

}