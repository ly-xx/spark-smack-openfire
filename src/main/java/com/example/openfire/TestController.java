package com.example.openfire;

import com.daqsoft.commons.responseEntity.BaseResponse;
import com.daqsoft.commons.responseEntity.ResponseBuilder;
import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.IQTypeFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaIdFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.iqregister.AccountManager;
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
     * 跳转首页
     * @param paramMap
     * @return
     */
    @RequestMapping(value = "/goIndex")
    public String goIndex(Map paramMap){
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

    /**
     * 跳转注册页面
     *
     * @return
     */
    @RequestMapping(value = "/goRegisterAccount")
    public String goRegisterAccount() {
        return "/registerAccount";
    }

    /**
     * 跳转添加好友界面
     * @return
     */
    @RequestMapping(value = "/goAddFriend")
    public String goAddFriend(){
        return "/addFriend";
    }

    /**
     * 登录
     *
     * @param username 账号
     * @param psd      密码
     * @return
     */
    @RequestMapping(value = "/login")
    @ResponseBody
    public BaseResponse login(String username, String psd) {
        try {
            con = getConnection();
            con.connect();
            if (con.isConnected()) {
                //登录
                con.login(username, psd);
                Roster.getInstanceFor(con).setSubscriptionMode(Roster.SubscriptionMode.manual);
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.custom().failed("登录失败").build();
        }
        return ResponseBuilder.custom().success().build();
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
                    System.out.println(message.getFrom()+ "：" + message.getBody());
                }
            }
        };
        //创建会话
        ChatManagerListener chatManagerListener = new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean arg1) {
                chat.addMessageListener(messageListener);
            }
        };
        manager.addChatListener(chatManagerListener);
    }


    /**
     * 注册新用户
     *
     * @param username 用户名
     * @param psd      密码
     * @return
     */
    @RequestMapping(value = "/registerAccount")
    @ResponseBody
    public BaseResponse registerAccount(String username, String psd) {
        try {
            con = getConnection();
            con.connect();
            AccountManager manager = AccountManager.getInstance(con);
            manager.createAccount(username, psd);
            //申请添加好友时如何处理请求
            // accept_all: 接收所有请求；reject_all: 拒绝所有请求；manual: 手工处理所有请求
            Roster.getInstanceFor(con).setSubscriptionMode(Roster.SubscriptionMode.manual);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.custom().failed("创建失败").build();
        }
        return ResponseBuilder.custom().success().build();
    }

    /**
     * 添加好友
     * @param username 用户名
     * @param name 昵称
     * @return
     */
    @RequestMapping(value = "/addFriend")
    @ResponseBody
    public BaseResponse addFriend(String username, String name){
        Roster roster = Roster.getInstanceFor(con);
        try {
            roster.createEntry(username, name, null);
            addFriendListener();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.custom().failed("请求发送失败").build();
        }
        return ResponseBuilder.custom().success().build();
    }

    /**
     * 添加好友监听
     */
    public void addFriendListener() {
        //条件过滤
        StanzaFilter filter = new AndFilter();
        StanzaListener listener = new StanzaListener() {
            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                Presence p = (Presence) packet;
                //p中可以得到对方的信息
                if (p.getType().toString().equals("subscrib")) {
                    System.out.println("发送好友请求");
                    //好友申请
                } else if (p.getType().toString().equals("subscribed")) {
                    System.out.println("通过好友请求");
                    //通过了好友请求
                } else if (p.getType().toString().equals("unsubscribe")) {
                    System.out.println("拒绝好友请求");
                    //拒绝好友请求
                }
            }
        };
        con.addSyncStanzaListener(listener, filter);
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
