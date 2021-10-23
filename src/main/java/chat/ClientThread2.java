package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class ClientThread2 extends Thread {

   Socket socket = null;
   BufferedReader br = null;

   public ClientThread2(Socket sck, BufferedReader br) {
      this.socket = sck;
      this.br = br;
   }

   // 스레드로 서버로부터 계속 읽어오기
   public void run()
   {
      try {
         String line = null;
         // null값이 아니면 계속 읽어다 출력해주기
         while ((line = br.readLine()) != null) {
            System.out.println(line);
         }
      } catch (IOException e) {
         System.out.println("시스템을 종료합니다.");
      } finally {
         try {
            if (socket != null)
               socket.close();
            if (br != null)
               br.close();

         } catch (Exception e2) {
            e2.printStackTrace();
         }
      }
   }
}