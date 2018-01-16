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
@Table(name = "OFCHATHISTORY")
public class OfChatHistory {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * 发送方
     */
    @Column(name = "SENDER")
    private String sender;

    /**
     * 接收方
     */
    @Column(name = "RECEIVER")
    private String receiver;

    /**
     * 消息体
     */
    @Column(name = "CONTENT")
    private String content;

    /**
     * 语言
     */
    @Column(name = "LANG")
    private String lang = "cn";

    /**
     * 站点代码
     */
    @Column(name = "HDCODE")
    private String hdcode = "1001004_1";

    /**
     * 创建时间
     */
    @Column(name = "CREATEDATE")
    private Date createDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getHdcode() {
        return hdcode;
    }

    public void setHdcode(String hdcode) {
        this.hdcode = hdcode;
    }
}
