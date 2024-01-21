package com.socketio.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.socketio.bean.ContactInfo;
import com.socketio.bean.JoinInfo;
import com.socketio.bean.MessageInfo;
import com.socketio.bean.RoomInfo;
import com.socketio.bean.RoomSet;

public class VideoChatServer {

    // client reference map
    private static Map<String, SocketIOClient> clientMap = new HashMap<>();
    // client name map
    private static Map<String, String> nameMap = new HashMap<>();
    // room map
    private static Map<String, RoomInfo> roomMap = new HashMap<>();
    
    public static void main(String[] args) {
        Configuration config = new Configuration();
        //config.setHostname("localhost");
        config.setPort(9012); // setting port
        final SocketIOServer server = new SocketIOServer(config);
        // add connect listener
        server.addConnectListener(client -> {
            System.out.println(client.getSessionId().toString()+"connected");
            clientMap.put(client.getSessionId().toString(), client);
        });
        // add disconnect listener
        server.addDisconnectListener(client -> {
            System.out.println(client.getSessionId().toString()+"disconnected");
            for (Map.Entry<String, SocketIOClient> item : clientMap.entrySet()) {
                if (client.getSessionId().toString().equals(item.getKey())) {
                    clientMap.remove(item.getKey());
                    break;
                }
            }
            nameMap.remove(client.getSessionId().toString());
        });

        // add message listener
        server.addEventListener("self_online", String.class, (client, name, ackSender) -> {
            System.out.println(client.getSessionId().toString()+"online: "+name);
            for (Map.Entry<String, SocketIOClient> item : clientMap.entrySet()) {
                if (!client.getSessionId().toString().equals(item.getKey())) {
                    item.getValue().sendEvent("friend_online", name);
                    client.sendEvent("friend_online", nameMap.get(item.getKey()));
                }
            }
            nameMap.put(client.getSessionId().toString(), name);
        });

        // add message listener
        server.addEventListener("self_offline", String.class, (client, name, ackSender) -> {
            System.out.println(client.getSessionId().toString()+"offline: "+name);
            for (Map.Entry<String, SocketIOClient> item : clientMap.entrySet()) {
                if (!client.getSessionId().toString().equals(item.getKey())) {
                    item.getValue().sendEvent("friend_offline", name);
                }
            }
            nameMap.remove(client.getSessionId().toString());
        });

        // add message listener
        server.addEventListener("IceInfo", JSONObject.class, (client, json, ackSender) -> {
            System.out.println(client.getSessionId().toString()+"ICE waiting: "+json.toString());
            String destId = json.getString("destination");
            for (Map.Entry<String, String> item : nameMap.entrySet()) {
                if (destId.equals(item.getValue())) {
                    clientMap.get(item.getKey()).sendEvent("IceInfo", json);
                    break;
                }
            }
        });

        // add SDP listener
        server.addEventListener("SdpInfo", JSONObject.class, (client, json, ackSender) -> {
            System.out.println(client.getSessionId().toString()+"SDP media: "+json.toString());
            String destId = json.getString("destination");
            for (Map.Entry<String, String> item : nameMap.entrySet()) {
                if (destId.equals(item.getValue())) {
                    clientMap.get(item.getKey()).sendEvent("SdpInfo", json);
                    break;
                }
            }
        });

        server.addEventListener("offer_converse", JSONObject.class, (client, json, ackSender) -> {
            System.out.println(client.getSessionId().toString()+"caller process: "+json.toString());
            ContactInfo contact = (ContactInfo) JSONObject.toJavaObject(json, ContactInfo.class);
            for (Map.Entry<String, String> item : nameMap.entrySet()) {
                if (contact.getTo().equals(item.getValue())) {
                    clientMap.get(item.getKey()).sendEvent("friend_converse", contact.getFrom());
                    break;
                }
            }
        });

        server.addEventListener("self_dial_in", JSONObject.class, (client, json, ackSender) -> {
            System.out.println(client.getSessionId().toString()+"accept the call: "+json.toString());
            ContactInfo contact = (ContactInfo) JSONObject.toJavaObject(json, ContactInfo.class);
            nameMap.put(client.getSessionId().toString(), contact.getFrom());
            for (Map.Entry<String, String> item : nameMap.entrySet()) {
                if (contact.getTo().equals(item.getValue())) {
                    clientMap.get(item.getKey()).sendEvent("other_dial_in", contact.getFrom());
                    break;
                }
            }
        });

        server.addEventListener("self_hang_up", JSONObject.class, (client, json, ackSender) -> {
            System.out.println(client.getSessionId().toString()+"hang down the call: "+json.toString());
            ContactInfo contact = (ContactInfo) JSONObject.toJavaObject(json, ContactInfo.class);
            for (Map.Entry<String, String> item : nameMap.entrySet()) {
                if (contact.getTo().equals(item.getValue())) {
                    clientMap.get(item.getKey()).sendEvent("other_hang_up", contact.getFrom());
                    break;
                }
            }
        });

        server.addEventListener("get_room_list", String.class, (client, userName, ackSender) -> {
            System.out.println(client.getSessionId().toString()+"get room list: "+userName);
            List<RoomInfo> roomList = new ArrayList<RoomInfo>();
            roomList.addAll(roomMap.values());
            RoomSet roomSet = new RoomSet(roomList);
            client.sendEvent("return_room_list", roomSet);
        });

        server.addEventListener("open_room", JSONObject.class, (client, json, ackSender) -> {
            System.out.println(client.getSessionId().toString()+"create room: "+json.toString());
            RoomInfo room = (RoomInfo) JSONObject.toJavaObject(json, RoomInfo.class);
            roomMap.put(room.getRoom_name(), room);
            for (Map.Entry<String, SocketIOClient> item : clientMap.entrySet()) {
                item.getValue().sendEvent("room_have_opened", room);
            }
        });

        server.addEventListener("close_room", String.class, (client, roomName, ackSender) -> {
            System.out.println(client.getSessionId().toString()+"close room: "+roomName);
            for (Map.Entry<String, SocketIOClient> item : clientMap.entrySet()) {
                item.getValue().sendEvent("room_have_closed", roomName);
            }
            roomMap.remove(roomName);
        });

        server.addEventListener("join_room", JSONObject.class, (client, json, ackSender) -> {
            System.out.println(client.getSessionId().toString()+"enter the room: "+json.toString());
            JoinInfo info = (JoinInfo) JSONObject.toJavaObject(json, JoinInfo.class);
            nameMap.put(client.getSessionId().toString(), info.getUser_name());
            if (!roomMap.containsKey(info.getGroup_name())) {
                System.out.println("roomMap.put "+info.getGroup_name());
                roomMap.put(info.getGroup_name(), new RoomInfo(info.getUser_name(), info.getGroup_name(), new HashMap<String, String>()));
            }
            for (Map.Entry<String, RoomInfo> room : roomMap.entrySet()) {
                if (info.getGroup_name().equals(room.getKey())) {
                    room.getValue().getMember_map().put(client.getSessionId().toString(), info.getUser_name());
                    for (Map.Entry<String, String> user : room.getValue().getMember_map().entrySet()) {
                        clientMap.get(user.getKey()).sendEvent("person_in_room", info.getUser_name());
                        System.out.println("notify person_in_room "+user.getKey()+" "+info.getUser_name());
                    }
                    System.out.println("person_count="+room.getValue().getMember_map().size());
                    client.sendEvent("person_count", room.getValue().getMember_map().size());
                }
            }
        });

        server.addEventListener("leave_room", JSONObject.class, (client, json, ackSender) -> {
            System.out.println(client.getSessionId().toString()+"leaved the room: "+json.toString());
            JoinInfo info = (JoinInfo) JSONObject.toJavaObject(json, JoinInfo.class);
            for (Map.Entry<String, RoomInfo> room : roomMap.entrySet()) {
                if (info.getGroup_name().equals(room.getKey())) {
                    room.getValue().getMember_map().remove(client.getSessionId().toString());
                    for (Map.Entry<String, String> user : room.getValue().getMember_map().entrySet()) {
                        clientMap.get(user.getKey()).sendEvent("person_out_room", info.getUser_name());
                    }
                }
            }
        });

        server.addEventListener("send_room_message", JSONObject.class, (client, json, ackSender) -> {
            System.out.println(client.getSessionId().toString()+"send message: "+json.toString());
            MessageInfo message = (MessageInfo) JSONObject.toJavaObject(json, MessageInfo.class);
            for (Map.Entry<String, RoomInfo> room : roomMap.entrySet()) {
                if (message.getTo().equals(room.getKey())) {
                    for (Map.Entry<String, String> user : room.getValue().getMember_map().entrySet()) {
                        if (!user.getValue().equals(message.getFrom())) {
                            clientMap.get(user.getKey()).sendEvent("receive_room_message", message);
                        }
                    }
                    break;
                }
            }
        });

        server.start(); // start Socket
    }

}
