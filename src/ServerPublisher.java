import Server.MTLServer;
import Server.QUEServer;
import Server.SHEServer;

import javax.xml.ws.Endpoint;

public class ServerPublisher {

    public static void main(String[] args) {

        System.out.println("Publish the Service now");
        Endpoint.publish("http://127.0.0.1:9999/hw", new MTLServer());
        Endpoint.publish("http://127.0.0.1:8888/hw", new QUEServer());
        Endpoint.publish("http://127.0.0.1:7777/hw", new SHEServer());
        System.out.println("Done publishing");
    }
}
