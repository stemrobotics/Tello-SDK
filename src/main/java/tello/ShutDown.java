package tello;


public class ShutDown extends Thread{
    @Override
    public void run() {
        Server.shutDown();
    }
}