package chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
   /*
      Client접속 -> Thread 생성하면서 HashMap / socket 넣어준다
      HashMap은 key : nickname // value : socket(PrintWriter)
      socket에 연결 된 printWriter => Server에서 HashMap(hash)을 통해 각각 socket의 printWriter접근
    */
   static HashMap<String, Object> hash;
   private static final int PORT = 6000;

   public static void main(String[] args) {

      ServerSocket serverSocket = null;

      try {
         serverSocket = new ServerSocket();
         serverSocket.bind(new InetSocketAddress("0.0.0.0", PORT));
         log("starts... [port:" + PORT + "]");

         hash = new HashMap<String, Object>();

         while (true) {
            log("=== === === === === === ===");
            log("채팅 참여 => " + hash.size() + "명");
            log("유저 이름 => " + hash.keySet());
            log("대기중");
            log("=== === === === === === ===");

            Socket socket = serverSocket.accept();
            
            ServerThread2 chatThr = new ServerThread2(socket, hash);
            chatThr.start();
         }
      } catch (IOException e) {
         log(""+e);
      }
   }

   public static void log(String log) {
      System.out.println("[Server] " + log);
   }
}