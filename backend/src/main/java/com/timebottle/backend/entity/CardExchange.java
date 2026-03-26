package com.timebottle.backend.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "card_exchanges")
public class CardExchange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private Integer senderId;

    @Column(name = "receiver_id", nullable = false)
    private Integer receiverId;

    @Column(name = "sender_card_id", nullable = false)
    private Long senderCardId;

    @Column(name = "sender_card_quantity", nullable = false)
    private Integer senderCardQuantity = 1;

    @Column(name = "receiver_card_id", nullable = false)
    private Long receiverCardId;

    @Column(name = "receiver_card_quantity", nullable = false)
    private Integer receiverCardQuantity = 1;

    @Column(name = "status", nullable = false, length = 10)
    private String status = "pending";

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @Transient
    private String senderName;
    @Transient
    private String senderAvatar;
    @Transient
    private String receiverName;
    @Transient
    private String receiverAvatar;
    @Transient
    private String senderCardName;
    @Transient
    private String senderCardImageUrl;
    @Transient
    private String receiverCardName;
    @Transient
    private String receiverCardImageUrl;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getSenderId() { return senderId; }
    public void setSenderId(Integer senderId) { this.senderId = senderId; }
    public Integer getReceiverId() { return receiverId; }
    public void setReceiverId(Integer receiverId) { this.receiverId = receiverId; }
    public Long getSenderCardId() { return senderCardId; }
    public void setSenderCardId(Long senderCardId) { this.senderCardId = senderCardId; }
    public Integer getSenderCardQuantity() { return senderCardQuantity; }
    public void setSenderCardQuantity(Integer senderCardQuantity) { this.senderCardQuantity = senderCardQuantity; }
    public Long getReceiverCardId() { return receiverCardId; }
    public void setReceiverCardId(Long receiverCardId) { this.receiverCardId = receiverCardId; }
    public Integer getReceiverCardQuantity() { return receiverCardQuantity; }
    public void setReceiverCardQuantity(Integer receiverCardQuantity) { this.receiverCardQuantity = receiverCardQuantity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public String getSenderAvatar() { return senderAvatar; }
    public void setSenderAvatar(String senderAvatar) { this.senderAvatar = senderAvatar; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public String getReceiverAvatar() { return receiverAvatar; }
    public void setReceiverAvatar(String receiverAvatar) { this.receiverAvatar = receiverAvatar; }
    public String getSenderCardName() { return senderCardName; }
    public void setSenderCardName(String senderCardName) { this.senderCardName = senderCardName; }
    public String getSenderCardImageUrl() { return senderCardImageUrl; }
    public void setSenderCardImageUrl(String senderCardImageUrl) { this.senderCardImageUrl = senderCardImageUrl; }
    public String getReceiverCardName() { return receiverCardName; }
    public void setReceiverCardName(String receiverCardName) { this.receiverCardName = receiverCardName; }
    public String getReceiverCardImageUrl() { return receiverCardImageUrl; }
    public void setReceiverCardImageUrl(String receiverCardImageUrl) { this.receiverCardImageUrl = receiverCardImageUrl; }
}
