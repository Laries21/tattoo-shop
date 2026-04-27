-- Sample tattoo designs for Clarity Tattoo
-- Run this once to populate the tattoo table

INSERT IGNORE INTO tattoo (id, design_name, style, price, description, image_url) VALUES
(1, 'Dragon Sleeve', 'Blackwork', 8500, 'A powerful full-sleeve dragon design with intricate scales and bold linework. Perfect for those who want a statement piece.', NULL),
(2, 'Rose Mandala', 'Geometric', 4500, 'A stunning fusion of a blooming rose with sacred geometry mandala patterns. Elegant and timeless.', NULL),
(3, 'Watercolor Phoenix', 'Watercolor', 6000, 'A vibrant phoenix rising from the ashes rendered in flowing watercolor strokes. Symbolizes rebirth and strength.', NULL),
(4, 'Minimalist Mountain', 'Traditional', 2500, 'Clean, simple mountain range silhouette with fine linework. Ideal for first-time tattoo enthusiasts.', NULL),
(5, 'Koi Fish', 'Neo-Traditional', 5500, 'A beautifully detailed koi fish with bold colors and neo-traditional shading. Represents perseverance and good fortune.', NULL),
(6, 'Geometric Wolf', 'Geometric', 4000, 'A wolf portrait deconstructed into sharp geometric shapes and triangles. Modern and striking.', NULL),
(7, 'Lotus Flower', 'Watercolor', 3500, 'A delicate lotus flower in soft watercolor washes. Symbolizes purity, enlightenment, and new beginnings.', NULL),
(8, 'Skull & Roses', 'Traditional', 5000, 'Classic American traditional skull surrounded by roses. Bold outlines and vibrant colors that age beautifully.', NULL);
