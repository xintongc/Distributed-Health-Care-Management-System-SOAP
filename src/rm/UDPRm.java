package rm;

import Client.BytesUtil;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Map;

public class UDPRm {

    public synchronized static StdMaps getRemoteStdMaps(int port) throws ClassNotFoundException, IOException{
        DatagramSocket SocketCli=null;
        InetAddress IPAddress = null;
        byte[] bytes = null;
        byte[] data = null;
        DatagramPacket sendPacket = null;
        DatagramPacket incomingPacket = null;
        byte[] dataBack = null;

        StdMaps msg = null;

        try{
            SocketCli = new DatagramSocket();
            IPAddress = InetAddress.getByName("localhost");
            bytes = new byte[1024];
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO: handle exception
        }
        msg = new StdMaps("Connect for listing");

        data = BytesUtil.toByteArray(msg);
        sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
        SocketCli.send(sendPacket);
        incomingPacket = new DatagramPacket(bytes, bytes.length);
        SocketCli.receive(incomingPacket);

        dataBack = incomingPacket.getData();
        StdMaps message = (StdMaps) BytesUtil.toObject(dataBack);

        return message;
    }


    public synchronized static void recoverRemoteMaps(int port, StdMaps maps)throws ClassNotFoundException, IOException{
        DatagramSocket SocketCli=null;
        InetAddress IPAddress = null;
        byte[] incomingData = null;
        ByteArrayOutputStream outputStream = null;
        ObjectOutputStream os = null;
        byte[] data = null;
        DatagramPacket sendPacket = null;
        DatagramPacket incomingPacket = null;
        byte[] dataBack = null;
        ByteArrayInputStream in = null;
        ObjectInputStream is = null;
        StdMaps remoteMap;
        StdMaps msg=null;

        try {
            SocketCli = new DatagramSocket();
            IPAddress = InetAddress.getByName("localhost");
            incomingData = new byte[1024];
            msg = new StdMaps("Connect for modifying");
        }catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        outputStream = new ByteArrayOutputStream();
        os = new ObjectOutputStream(outputStream);
        os.writeObject(msg);
        data = outputStream.toByteArray();
        sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
        SocketCli.send(sendPacket);

        incomingPacket = new DatagramPacket(incomingData, incomingData.length);
        SocketCli.receive(incomingPacket);

        dataBack = incomingPacket.getData();
        in = new ByteArrayInputStream(dataBack);
        is = new ObjectInputStream(in);
        StdMaps msg1 = (StdMaps) is.readObject();
        remoteMap = msg1;

        StdMaps msg2 = new StdMaps(remoteMap);
        os.writeObject(msg2);
        data = outputStream.toByteArray();
        sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
        SocketCli.send(sendPacket);

    }
}
