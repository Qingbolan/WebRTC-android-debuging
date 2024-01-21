package net.qingbolan.webrtcmeeting.constant;

import org.webrtc.PeerConnection;

import java.util.ArrayList;
import java.util.List;

public class ChatConst {
    public final static String CHAT_IP = "180.94.143.9"; // 聊天服务的ip
    public final static int CHAT_PORT = 9012; // 聊天服务的端口

    private final static String STUN_URL = "stun:192.168.101.3:3478";
    private final static String STUN_USERNAME = "webRTC";
    private final static String STUN_PASSWORD = "password1";

    // 获取ICE服务器列表
    public static List<PeerConnection.IceServer> getIceServerList() {
        List<PeerConnection.IceServer> iceServerList = new ArrayList<>();
        iceServerList.add(PeerConnection.IceServer.builder(STUN_URL)
                .setUsername(STUN_USERNAME).setPassword(STUN_PASSWORD).createIceServer());
        return iceServerList;
    }
}
