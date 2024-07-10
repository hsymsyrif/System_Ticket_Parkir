package javaapplication;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;

public class WebServiceClient {
    public static void main(String[] args) throws Exception {
        URL url = new URL("http://localhost:8080/user?wsdl");
        QName qname = new QName("http://user.service/", "UserService");

        Service service = Service.create(url, qname);
        UserService userService = service.getPort(UserService.class);

        String response = userService.login("testUser", "testPassword");
        System.out.println(response);
    }
}

