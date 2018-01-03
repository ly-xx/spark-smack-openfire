package com.example.openfire;

import com.daqsoft.commons.responseEntity.BaseResponse;
import com.daqsoft.commons.responseEntity.ResponseBuilder;
import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lxx
 * @version V1.0.0
 * @date 2017-12-28
 */

@Controller
public class TestController {

    //openfire服务器地址
    @Value("${openfire.server}")
    private String openfireServer;

    XMPPTCPConnection con;

    /**
     * 跳转登录页面
     *
     * @return
     */
    @RequestMapping(value = "/goLogin")
    public String goLogin() {
        return "/login";
    }

    /**
     * 跳转消息记录
     *
     * @return
     */
    @RequestMapping(value = "/getMsg")
    public String getMsg(String username, Map paramsMap) {
        paramsMap.put("username", username);
        return "/msg";
    }

    /**
     * 登录
     *
     * @param username 账号
     * @param psd      密码
     * @param paramMap
     * @return
     */
    @RequestMapping(value = "/login")
    public String login(String username, String psd, Map paramMap) {
        con = getConnection();
        try {
            con.connect();
            if (con.isConnected()) {
                //登录
                con.login(username, psd);
                Thread.sleep(1000);
                //登录成功后获取用户好友列表
                Roster roster = getContact();
                //监听消息
                getMsg(paramMap);
                //获取离线消息
                getOfflineMsg(paramMap);
                paramMap.put("friends", roster.getEntries());
                paramMap.put("key", "登录成功！");
                return "/index";
            }
        } catch (Exception e) {
            paramMap.put("key", "登录失败");
            e.printStackTrace();
        }
        return "/error";
    }

    /**
     * 消息发送
     *
     * @return
     */
    @RequestMapping(value = "/sendMsg")
    @ResponseBody
    public BaseResponse sendMsg(String username, String msg) {
        Map paramsMap = new HashMap();
        try {
            ChatManager manager = ChatManager.getInstanceFor(con);
            Chat chat = manager.createChat(username, null);
            chat.sendMessage(msg);
            paramsMap.put("username", username);
            paramsMap.put("msg", msg);
        } catch (Exception e) {
            return ResponseBuilder.custom().failed("发送失败").build();
        }
        return ResponseBuilder.custom().success().data(paramsMap).build();
    }

    /**
     * 添加监听
     *
     * @param paramsMap
     */
    public void getMsg(Map paramsMap) {
        ChatManager manager = ChatManager.getInstanceFor(con);
        //设置信息的监听
        final ChatMessageListener messageListener = new ChatMessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                //当消息返回为空的时候，表示用户正在聊天窗口编辑信息并未发出消息
                if (StringUtils.isNotBlank(message.getBody())) {
                    System.out.println("用户消息：" + message.getBody());
                }
            }
        };
        ChatManagerListener chatManagerListener = new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean arg1) {
                chat.addMessageListener(messageListener);
            }
        };
        manager.addChatListener(chatManagerListener);
    }


    /**
     * 获取好友联系人
     *
     * @return
     */
    public Roster getContact() {
        Roster roster = Roster.getInstanceFor(con);
        //获取好友组
        Collection<RosterGroup> rosterGroups = roster.getGroups();
        for (RosterGroup group : rosterGroups) {
            //获得每个组下面的好友
            List<RosterEntry> entries = group.getEntries();
            for (RosterEntry entry : entries) {
                //获得好友基本信息
                entry.getUser();
                entry.getName();
                entry.getType();
                entry.getStatus();
            }
        }
        return roster;
    }

    /**
     * 退出登录
     *
     * @return
     */
    @RequestMapping(value = "/logout")
    public String logout(Map paramMap) {
        try {
            con.instantShutdown();
            return "/goLogin";
        } catch (Exception e) {
            paramMap.put("key", "退出失败！");
            return "/error";
        }
    }

    /**
     * 获取离线消息
     */
    public void getOfflineMsg(Map paramsMap) {
        OfflineMessageManager messageManager = new OfflineMessageManager(con);
        try {
            List<Message> messageList = messageManager.getMessages();
            paramsMap.put("msgList", messageList);
            //将用户状态设为在线
            Presence presence = new Presence(Presence.Type.available);
            con.sendStanza(presence);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 创建XMPP连接
     *
     * @return
     */
    public XMPPTCPConnection getConnection() {
        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
        config.setServiceName(openfireServer);
        config.setHost(openfireServer);
        config.setPort(5222);
        //关闭安全认证
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        //将用户状态设为离线
        config.setSendPresence(false);
        //是否开启压缩
        config.setCompressionEnabled(false);
        XMPPTCPConnection con = new XMPPTCPConnection(config.build());
        SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
        SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
        SASLAuthentication.blacklistSASLMechanism("CRAM-MD5");
        return con;
    }
}
