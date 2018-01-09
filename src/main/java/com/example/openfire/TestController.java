package com.example.openfire;

import com.daqsoft.commons.responseEntity.BaseResponse;
import com.daqsoft.commons.responseEntity.ResponseBuilder;
import com.example.entity.OfChatHistory;
import com.example.service.OfChatLogsService;
import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

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

    HttpSession session;

    @Autowired
    OfChatLogsService chatLogsService;

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
     * @param friendName 好友姓名
     * @param paramsMap
     * @param jid
     * @param userName   登录姓名
     * @return
     */
    @RequestMapping(value = "/getMsg")
    public String getMsg(String friendName, Map paramsMap, String jid, String userName) {
        paramsMap.put("friendName", friendName);
        paramsMap.put("jid", jid);
        paramsMap.put("userName", userName);
        return "/msg";
    }

    /**
     * 跳转首页
     *
     * @param paramMap
     * @return
     */
    @RequestMapping(value = "/goIndex")
    public String goIndex(Map paramMap, String userName) {
        XMPPTCPConnection con = (XMPPTCPConnection) session.getAttribute(userName);
        //登录成功后获取用户好友列表
        Roster roster = getContact(con);
        //获取离线消息
        getOfflineMsg(paramMap, con);
        paramMap.put("userName", userName);
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
     * 跳转查询用户页面
     *
     * @return
     */
    @RequestMapping(value = "/goSearch")
    public String goSearch(String userName, Map paramsMap) {
        paramsMap.put("userName", userName);
        return "/search";
    }


    /**
     * 跳转好友添加页面
     *
     * @param userName 登录姓名
     * @param jid      好友jid
     * @param paramMap
     * @return
     */
    @RequestMapping(value = "/goAddFriend")
    public String goAddFriend(String userName, String jid, Map paramMap) {
        paramMap.put("jid", jid);
        paramMap.put("userName", userName);
        return "/addFriend";
    }

    /**
     * 用户查询
     *
     * @return
     */
    @RequestMapping(value = "/searchUser")
    @ResponseBody
    public BaseResponse searchUser(String searchName, String userName) {
        List<Map> mapList = new ArrayList<>();
        //查询服务器上的用户信息
        XMPPTCPConnection con = (XMPPTCPConnection) session.getAttribute(userName);
        UserSearchManager manager = new UserSearchManager(con);
        try {
            //搜索表单
            Form searchForm = manager.getSearchForm("search." + con.getServiceName());
            Form answerForm = searchForm.createAnswerForm();
//            if (StringUtils.isNotBlank(searchName)) {
                answerForm.setAnswer("Username", true);
                answerForm.setAnswer("search", searchName);
//            }
            ReportedData reportedData = manager.getSearchResults(answerForm, "search." + con.getServiceName());
            List<ReportedData.Row> rowList = reportedData.getRows();
            for (ReportedData.Row row : rowList) {
                Map objMap = new HashMap();
                objMap.put("jid", row.getValues("jid").get(0));
                objMap.put("userName", row.getValues("userName").get(0));
                objMap.put("name", row.getValues("name").get(0));
                mapList.add(objMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.custom().failed("查询失败").build();
        }
        return ResponseBuilder.custom().success().data(mapList).build();
    }

    /**
     * 登录
     *
     * @param userName 账号
     * @param psd      密码
     * @return
     */
    @RequestMapping(value = "/login")
    @ResponseBody
    public BaseResponse login(String userName, String psd, HttpServletRequest request) {
        try {
            XMPPTCPConnection con = getConnection(userName, request);
            if (!con.isConnected()) {
                con.connect();
            }
            if (con.isConnected()) {
                //登录
                con.login(userName, psd);
                //监听消息
//                getMsg(request, con);
                // accept_all: 接收所有请求；reject_all: 拒绝所有请求；manual: 手工处理所有请求
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
     * @param userName 登录姓名
     * @param msg      消息
     * @param jid      接收者jid
     * @return
     */
    @RequestMapping(value = "/sendMsg")
    @ResponseBody
    public BaseResponse sendMsg(String userName, String msg, String jid) {
        Map paramsMap = new HashMap();
        try {
            XMPPTCPConnection con = (XMPPTCPConnection) session.getAttribute(userName);
            ChatManager manager = ChatManager.getInstanceFor(con);
            Chat chat = manager.createChat(jid, null);
            chat.sendMessage(msg);
            paramsMap.put("userName", con.getUser());
            paramsMap.put("msg", msg);
            OfChatHistory chatLogs = new OfChatHistory();
            chatLogs.setContent(msg);
            chatLogs.setCreateDate(new Date());
            chatLogs.setReceiver(jid);
            chatLogs.setSender(userName);
            chatLogs.setSessionJid(jid);
            chatLogsService.save(chatLogs);
            System.out.println(con.getUser() + "：" + msg);
            List messageList = (List) session.getAttribute("messageList");
            if (null == messageList) {
                messageList = new ArrayList<>();
            }
            messageList.add(paramsMap);
            session.setAttribute("messageList", messageList);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.custom().failed("发送失败").build();
        }
        return ResponseBuilder.custom().success().data(paramsMap).build();
    }

    /**
     * 添加监听
     */
    public void getMsg(HttpServletRequest request, XMPPTCPConnection con) {
        session = request.getSession();
        ChatManager manager = ChatManager.getInstanceFor(con);
        //设置信息的监听
        final ChatMessageListener messageListener = new ChatMessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                //当消息返回为空的时候，表示用户正在聊天窗口编辑信息并未发出消息
                if (StringUtils.isNotBlank(message.getBody())) {
                    List messageList = (List) session.getAttribute("messageList");
                    if (null == messageList) {
                        messageList = new ArrayList<>();
                    }
                    Map objMap = new HashMap();
                    objMap.put("userName", message.getFrom());
                    objMap.put("msg", message.getBody());
                    System.out.println(message.getFrom() + "：" + message.getBody());
                    messageList.add(objMap);
                    session.setAttribute("messageList", messageList);
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
     * 获取消息
     *
     * @return
     */
    @RequestMapping(value = "/getMsgList")
    @ResponseBody
    public BaseResponse getMsgList(HttpServletRequest request) {
        session = request.getSession();
        List messageList = (List) session.getAttribute("messageList");
        if (null == messageList) {
            messageList = new ArrayList<>();
        }
        return ResponseBuilder.custom().success().data(messageList).build();
    }


    /**
     * 注册新用户
     *
     * @param userName 用户名
     * @param psd      密码
     * @return
     */
    @RequestMapping(value = "/registerAccount")
    @ResponseBody
    public BaseResponse registerAccount(String userName, String psd, HttpServletRequest request) {
        try {
            XMPPTCPConnection con = getConnection(userName, request);
            con.connect();
            AccountManager manager = AccountManager.getInstance(con);
            manager.createAccount(userName, psd);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.custom().failed("创建失败").build();
        }
        return ResponseBuilder.custom().success().build();
    }

    /**
     * 添加好友
     *
     * @param jid      jid
     * @param userName 用户名
     * @return
     */
    @RequestMapping(value = "/addFriend")
    @ResponseBody
    public BaseResponse addFriend(String jid, String userName) {
        XMPPTCPConnection con = (XMPPTCPConnection) session.getAttribute(userName);
        Roster roster = Roster.getInstanceFor(con);
        try {
            //好友验证
            roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
            roster.createEntry(jid, userName, null);
            addFriendListener(con);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.custom().failed("请求发送失败").build();
        }
        return ResponseBuilder.custom().success().build();
    }

    /**
     * 添加好友监听
     */
    public void addFriendListener(XMPPTCPConnection con) {
        //条件过滤
        StanzaFilter filter = new AndFilter();
        StanzaListener listener = new StanzaListener() {
            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                if (packet instanceof Presence) {
                    Presence p = (Presence) packet;
                    if (p.getType().toString().equals("subscribe")) {
                        System.out.println("请求添加好友");
                    } else if (p.getType().toString().equals("subscribed")) {
                        System.out.println("通过好友请求");
                    } else if (p.getType().toString().equals("unsubscribe")) {
                        System.out.println("拒绝好友请求");
                    }
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
    public Roster getContact(XMPPTCPConnection con) {
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
    public String logout(Map paramMap, String userName) {
        try {
            XMPPTCPConnection con = (XMPPTCPConnection) session.getAttribute(userName);
            con.instantShutdown();
            session.setAttribute(userName, null);
            return "/goLogin";
        } catch (Exception e) {
            paramMap.put("key", "退出失败！");
            return "/error";
        }
    }

    /**
     * 获取离线消息
     */
    public void getOfflineMsg(Map paramsMap, XMPPTCPConnection con) {
        OfflineMessageManager messageManager = new OfflineMessageManager(con);
        try {
            List<Message> messageList = messageManager.getMessages();
            paramsMap.put("msgList", messageList);
            //将用户状态设为在线
            Presence presence = new Presence(Presence.Type.available);
            con.sendStanza(presence);
            //删除数据库离线消息
            messageManager.deleteMessages();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 创建XMPP连接
     *
     * @return
     */
    public XMPPTCPConnection getConnection(String userName, HttpServletRequest request) {
        session = request.getSession();
        XMPPTCPConnection con = (XMPPTCPConnection) session.getAttribute(userName);
        if (null == con) {
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
            con = new XMPPTCPConnection(config.build());
            SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
            SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
            SASLAuthentication.blacklistSASLMechanism("CRAM-MD5");
            session.setAttribute(userName, con);
            return con;
        }
        return con;
    }
}
