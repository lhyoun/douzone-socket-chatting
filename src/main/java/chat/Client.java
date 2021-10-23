package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
 
public class Client {
 
   private static final String SERVER_IP = "127.0.0.1";
   private static final int SERVER_PORT = 6000;
   
    public static void main(String[] args) {
        Socket socket = null;
        BufferedReader br = null;
        PrintWriter pw = null;
        String nickName = null;
        
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
         
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            
            String str = br.readLine();
            System.out.println(str);
            
            nickName = keyboard.readLine();
            pw.println(nickName);//nickName 스트링에 담아보내기
            pw.flush();
            
            System.out.println("=== === 연결 되었습니다 (귓속말 사용법 '/상대닉네임 메시지') === ===");
            
            //서버로 부터 계속 읽어오는 스레드 실행
            ClientThread2 clientThread = new ClientThread2(socket,br);
            clientThread.start();
            
            String line = null;
            while((line = keyboard.readLine())!=null)
            {
            	if(line.charAt(0) == '/') {
            		String receiver = line.split(" ")[0];
            		pw.println(receiver + "@" + nickName + ":" + line.split(" ")[1]);
                	pw.flush();	
            	}else {
            		pw.println(nickName + ":" + line);
                	pw.flush();	
            	}
            	
            	if(line.equals("quit"))
                {
                    System.out.println("시스템을 종료합니다.");
                    pw.println(nickName + ":" + line);
                	pw.flush();	
                    break;
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(br != null)
                    br.close();
                if(pw != null)
                    pw.close();
                if(socket != null)
                    socket.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}