package com.example.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * 聊天记录
 *
 * @author lxx
 * @version V1.0.0
 * @date 2018-1-8
 */
@Entity
@Table(name = "ofchatlogs")
public class OfChatLogs {

    @Id
    @Column(name = "MESSAGEID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    /**
     * 发送者jid
     */
    @Column(name = "SESSIONJID")
    private String sessionJid;

    /**
     * 发送者姓名
     */
    @Column(name = "SENDER")
    private String sender;

    /**
     * 接收者姓名
     */
    @Column(name = "RECEIVER")
    private String receiver;

    /**
     * 创建时间
     */
    @Column(name = "CREATEDATE")
    private Date createDate;

    /**
     * 消息内容
     */
    @Column(name = "CONTENT")
    private String content;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getSessionJid() {
        return sessionJid;
    }

    public void setSessionJid(String sessionJid) {
        this.sessionJid = sessionJid;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
