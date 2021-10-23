package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

public class ServerThread2 extends Thread {
	Socket socket; 		// Server에서 넘겨준 socket객체
	String nickName; 	// 사용자정의 nickName
	// 들어온 클라이언트의 printWriter객체와 nickName을 저장하고 관리해주는 해쉬맵
	HashMap<String, Object> hash;
	
	BufferedReader br;

	// Thread 생성 -> 닉네임 입력 받고 hash에 저장 (닉네임과 printWriter 쌍)
	public ServerThread2(Socket socket, HashMap<String, Object> hash) {
		this.socket = socket;
		this.hash = hash;

		try {
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
			br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

			pw.println("닉네임을 입력하세요");
			pw.flush();

			nickName = br.readLine();
			log("=== " + nickName + "연결" + " ===");

			// 해쉬맵에 추가하기 전이라 본인한테는 안 보내짐
			sendMsg(nickName + "님이 입장하였습니다", false, null);

			synchronized (hash) {
				hash.put(nickName, pw);
			}
		} catch (IOException e) {
			log("" + e);
		}
	}

	// Server가 Client한테 받아온 message를 다른 Client에게 보내줌
	@Override
	public void run() {
      String line = null;
      try {
         while ((line = br.readLine()) != null) { 
            // 클라이언트로부터 quit을 받으면 종료
            if (line.split(":")[1].equals("quit")) {
               log("=== " + nickName + "종료" + " ===");
               break;
            } else {
            	if(line.charAt(0) == '/') {
            		String receiver = line.split("@")[0];
					//System.out.println("귓속말 수신자 : " + receiver);
					sendMsg(line.split("@")[1], true, receiver);
            	}else {
            		// 데이터를 클라이언트들에게 전송
            		sendMsg(line, false, null);
            	}
            }
         }
      } catch (IOException e) {
         log("=== " + nickName + "강제종료" + " ===");
      } finally {
         // finally에 들어온다는 건 시스템을 종료한 경우
         synchronized (hash) {
            hash.remove(nickName);
         }
         sendMsg(nickName + "님이 퇴장하였습니다", false, null);
         
         log("=== === === === === === ===");
         log("채팅 참여 => " + hash.size() + "명");
         log("유저 이름 => " + hash.keySet());
         log("대기중");
         log("=== === === === === === ===");

         try {
            socket.close();
         } catch (IOException e) {
            log(""+e);
         }
      }
   }

	// Server가 받은 message를 HashMap에 있는 각각의 pw(client)에게 보내줌
	public void sendMsg(String msg, boolean isWhisper, String receiver) {
		synchronized (hash) {
			PrintWriter pw = null;
			Set<String> keys = hash.keySet();

			if (!isWhisper) {
				for (String key : keys) {
					pw = (PrintWriter) hash.get(key);
					pw.println(msg);
					pw.flush();
				}
			} else {
				if(hash.get(receiver.substring(1)) != null){
					pw = (PrintWriter) hash.get(receiver.substring(1));
					pw.println(msg);
					pw.flush();
				}
			}
		}
	}

	public static void log(String log) {
		System.out.println("[ServerThread2] " + log);
	}
}