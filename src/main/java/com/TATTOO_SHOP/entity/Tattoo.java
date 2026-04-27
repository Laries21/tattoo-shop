package com.TATTOO_SHOP.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tattoo")
public class Tattoo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "design_name")
    private String designName;

    @Column(name = "style")
    private String style;

    @Column(name = "price")
    private double price;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDesignName() { return designName; }
    public void setDesignName(String designName) { this.designName = designName; }

    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
