package com.TATTOO_SHOP.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "video_review")
public class VideoReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reviewer_name")
    private String reviewerName;

    @Column(name = "rating")
    private int rating;

    @Column(name = "feedback")
    private String feedback;

    // Stores full embed code (Instagram blockquote HTML, YouTube iframe, or plain URL)
    @Column(name = "embed_code", columnDefinition = "TEXT")
    private String embedCode;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public String getEmbedCode() { return embedCode; }
    public void setEmbedCode(String embedCode) { this.embedCode = embedCode; }
}
