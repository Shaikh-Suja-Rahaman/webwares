# Postman Testing Instructions for ECommerce Store API

Complete guide to test all endpoints with EXACT parameters from backend code.

---

## 📋 PROJECT OVERVIEW

### What I Built

I developed a complete full-stack e-commerce web application called "Webwares" from scratch. The project includes a robust backend API built with Java and Spring Boot that handles user authentication, product management, shopping cart functionality, order processing, and payment integration with Razorpay. I implemented a MongoDB database to store all user data, products, orders, and transactions. The frontend is built using React and Vite, providing a modern, responsive user interface. The application supports two user roles: regular customers who can browse products, manage their cart, place orders, and make payments; and administrators who can add/edit/delete products, manage all orders, update order statuses, and view analytics dashboards. I deployed the entire backend API on Render.com, making it accessible via a live URL. The system includes comprehensive security features with JWT-based authentication, role-based access control, and secure payment processing.

---

### What is this Project?

This is a **full-stack E-Commerce Store** called **"Webwares"** - an online shopping platform where users can browse products, add them to cart, place orders, and make payments. Think of it like a mini Amazon or Flipkart!

### How it Works (Simple Explanation)

**For Regular Users:**
1. **Sign Up/Login** - Create your account or log in
2. **Browse Products** - Look at phones, clothes, gadgets, etc.
3. **Add to Cart** - Select items you want to buy
4. **Place Order** - Checkout and choose payment method
5. **Make Payment** - Pay using Razorpay, card, or cash on delivery

**For Admin Users:**
1. **Manage Products** - Add new products, update prices, delete items
2. **View All Orders** - See what customers ordered
3. **Update Order Status** - Mark orders as shipped, delivered, etc.
4. **View Analytics** - Check sales data and statistics

### Technology Stack

- **Backend**: Java + Spring Boot (handles all the business logic)
- **Database**: MongoDB (stores users, products, orders, etc.)
- **Frontend**: React + Vite (the user interface)
- **Payment**: Razorpay integration (for processing payments)
- **Hosting**: Render.com (where the API is deployed)

### What I Implemented

This document provides **complete Postman testing instructions** with:

✅ **All API Endpoints** - Every single endpoint your backend exposes
✅ **Exact Request Formats** - Copy-paste ready JSON bodies and form-data
✅ **Real Examples** - Actual product data, not just placeholders
✅ **Multiple Scenarios** - Different ways to use each endpoint
✅ **Expected Responses** - What you'll see when things work
✅ **Step-by-Step Guide** - How to set up requests in Postman

### Key Features Covered

1. **Authentication** (Register, Login)
2. **Product Management** (Create, Read, Update, Delete products)
3. **Shopping Cart** (Add items, update quantity, remove items)
4. **Order Processing** (Place orders, view orders, track status)
5. **Payment Integration** (Razorpay payment verification)
6. **Admin Analytics** (Sales data, order statistics)

### Understanding the Flow

```
User Journey:
Register → Login → Browse Products → Add to Cart → Place Order → Make Payment → Order Delivered

Admin Journey:
Login → Create Products → View Orders → Update Order Status → Check Analytics
```

---

## 🚀 DEPLOYMENT & SETUP

### Docker Containerization

The backend is containerized using Docker for easy deployment and consistent environments across different platforms:

**Dockerfile Features:**
- Multi-stage build for optimized image size
- Java 17 runtime environment
- Maven for dependency management
- Automated build and packaging
- Production-ready configuration

**To run locally with Docker:**
```bash
# Build the Docker image
docker build -t webwares-backend .

# Run the container
docker run -p 8080:8080 webwares-backend
```

---

### Cloud Deployment (Render.com)

The entire backend API is deployed on **Render.com** and is live at:
```
https://webwares.onrender.com/api
```

**Deployment Features:**
- ✅ Automatic deployments from Git repository
- ✅ Environment variable management for secrets
- ✅ MongoDB Atlas integration for database
- ✅ HTTPS/SSL enabled by default
- ✅ Auto-scaling and load balancing
- ✅ Health checks and monitoring
- ✅ Zero-downtime deployments

**Environment Variables Configured:**
- `MONGODB_URI` - MongoDB connection string
- `JWT_SECRET` - Secret key for JWT tokens
- `RAZORPAY_KEY_ID` - Razorpay API key
- `RAZORPAY_KEY_SECRET` - Razorpay secret key
- `CORS_ORIGINS` - Allowed frontend origins

---

### Frontend Repository

The React + Vite frontend is hosted separately:

**Repository:** [https://github.com/Shaikh-Suja-Rahaman/webwares-frontend](https://github.com/Shaikh-Suja-Rahaman/webwares-frontend)

**Frontend Tech Stack:**
- React 18 with Hooks
- Vite for fast development and building
- React Router for navigation
- Axios for API calls
- Tailwind CSS for styling
- State management with Context API

**To run frontend locally:**
```bash
# Clone the frontend repository
git clone https://github.com/Shaikh-Suja-Rahaman/webwares-frontend.git

# Navigate to project directory
cd webwares-frontend

# Install dependencies
npm install

# Create .env file with backend URL
echo "VITE_API_BASE_URL=https://webwares.onrender.com/api" > .env

# Start development server
npm run dev
```

**Frontend will run on:** `http://localhost:5173`

**Production Build:**
```bash
npm run build
```

---

## Setup

**Base URL:** `https://webwares.onrender.com/api`

---

## 1. AUTHENTICATION ENDPOINTS

### 1.1 Register User

**Method:** `POST`
**URL:** `https://webwares.onrender.com/api/auth/register`
**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "Password123"
}
```

**Notes:**
- `name`: Required, cannot be blank
- `email`: Required, must be valid email format
- `password`: Required, minimum 6 characters

**Expected Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "tokenType": "Bearer",
  "roles": ["ROLE_USER"]
}
```

---

### 1.2 Login User

**Method:** `POST`
**URL:** `https://webwares.onrender.com/api/auth/login`
**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "email": "john.doe@example.com",
  "password": "Password123"
}
```

**Expected Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "tokenType": "Bearer",
  "roles": ["ROLE_USER"]
}
```

**Save the accessToken for subsequent requests!**

---

## 2. PRODUCT ENDPOINTS

### 2.1 Get All Products (with filters)

**Method:** `GET`
**URL:** `https://webwares.onrender.com/api/products`
**No authentication required**

**Query Parameters (all optional):**
- `page`: integer (default: 0)
- `size`: integer (default: 20)
- `sort`: string (default: "createdAt,desc")
- `category`: string (optional filter)
- `minPrice`: double (optional filter)
- `maxPrice`: double (optional filter)
- `inStock`: boolean (optional filter)

**Example URLs:**
```
https://webwares.onrender.com/api/products
https://webwares.onrender.com/api/products?page=0&size=10
https://webwares.onrender.com/api/products?category=electronics&inStock=true
https://webwares.onrender.com/api/products?minPrice=100&maxPrice=500
```

---

### 2.2 Get Product by ID

**Method:** `GET`
**URL:** `https://webwares.onrender.com/api/products/{id}`
**Example:** `https://webwares.onrender.com/api/products/507f1f77bcf86cd799439011`
**No authentication required**

---

### 2.3 Create Product (ADMIN ONLY)

**Method:** `POST`
**URL:** `https://webwares.onrender.com/api/products`
**Headers:**
```
Authorization: Bearer YOUR_ADMIN_TOKEN_HERE
```

**IMPORTANT:** Use `form-data`, NOT JSON!

#### How to Set Up in Postman:

1. Select **"Body"** tab
2. Choose **"form-data"** (NOT raw/JSON)
3. Add the following key-value pairs:

| Key | Type | Value |
|-----|------|-------|
| name | Text | iPhone 15 Pro Max |
| description | Text | The latest flagship iPhone with A17 Pro chip, titanium design, and advanced camera system. Features 6.7-inch Super Retina XDR display. |
| price | Text | 1199.99 |
| stock | Text | 45 |
| category | Text | electronics |
| image | File | (Select an image file from your computer) |

#### Example 1 - Electronics Product:
```
name: Samsung Galaxy S24 Ultra
description: Premium Android smartphone with 200MP camera, S Pen, and stunning AMOLED display
price: 1299.99
stock: 30
category: electronics
image: [Select image file]
```

#### Example 2 - Fashion Product:
```
name: Nike Air Max 2024
description: Comfortable running shoes with superior cushioning and breathable mesh upper
price: 159.99
stock: 100
category: fashion
image: [Select image file]
```

#### Example 3 - Home Product:
```
name: Dyson V15 Vacuum Cleaner
description: Powerful cordless vacuum with laser dust detection and advanced filtration
price: 649.99
stock: 25
category: home
image: [Select image file]
```

**Expected Response:**
```json
{
  "id": "678f1f77bcf86cd799439022",
  "name": "iPhone 15 Pro Max",
  "description": "The latest flagship iPhone...",
  "price": 1199.99,
  "stock": 45,
  "category": "electronics",
  "imageUrl": "https://webwares.onrender.com/uploads/abc123.jpg"
}
```

---

### 2.4 Update Product (ADMIN ONLY)

**Method:** `PUT`
**URL:** `https://webwares.onrender.com/api/products/{id}`
**Example:** `https://webwares.onrender.com/api/products/678f1f77bcf86cd799439022`
**Headers:**
```
Authorization: Bearer YOUR_ADMIN_TOKEN_HERE
```

**IMPORTANT:** Use `form-data`, NOT JSON!

#### How to Set Up in Postman:

1. Replace `{id}` in URL with actual product ID
2. Select **"Body"** tab
3. Choose **"form-data"** (NOT raw/JSON)
4. Add the following key-value pairs:

| Key | Type | Value |
|-----|------|-------|
| name | Text | iPhone 15 Pro Max (Updated) |
| description | Text | Updated description with new features and improved performance |
| price | Text | 1099.99 |
| stock | Text | 60 |
| category | Text | electronics |
| image | File | (Optional - Select new image to replace old one) |

#### Example 1 - Update Price and Stock:
```
URL: https://webwares.onrender.com/api/products/678f1f77bcf86cd799439022

name: Samsung Galaxy S24 Ultra
description: Premium Android smartphone with 200MP camera, S Pen, and stunning AMOLED display
price: 1099.99
stock: 50
category: electronics
image: [Leave empty if not changing image]
```

#### Example 2 - Update All Fields:
```
URL: https://webwares.onrender.com/api/products/678f1f77bcf86cd799439023

name: Nike Air Max 2024 Special Edition
description: Limited edition running shoes with premium materials and exclusive colorway
price: 189.99
stock: 75
category: fashion
image: [Select new image file]
```

**Expected Response:**
```json
{
  "id": "678f1f77bcf86cd799439022",
  "name": "iPhone 15 Pro Max (Updated)",
  "description": "Updated description...",
  "price": 1099.99,
  "stock": 60,
  "category": "electronics",
  "imageUrl": "https://webwares.onrender.com/uploads/xyz456.jpg"
}
```

---

### 2.5 Delete Product (ADMIN ONLY)

**Method:** `DELETE`
**URL:** `https://webwares.onrender.com/api/products/{id}`
**Headers:**
```
Authorization: Bearer YOUR_ADMIN_TOKEN_HERE
```

#### Example 1 - Delete Specific Product:
```
DELETE https://webwares.onrender.com/api/products/678f1f77bcf86cd799439022
```

#### Example 2 - Delete Another Product:
```
DELETE https://webwares.onrender.com/api/products/507f1f77bcf86cd799439011
```

**Steps in Postman:**
1. Select **DELETE** method
2. Enter URL with actual product ID
3. Go to **Headers** tab
4. Add: `Authorization: Bearer YOUR_ADMIN_TOKEN_HERE`
5. Click **Send**

**Expected Response:**
- Status: `200 OK` or `204 No Content`
- Body: Empty or success message

---

## 3. CART ENDPOINTS

### 3.1 Get My Cart

**Method:** `GET`
**URL:** `https://webwares.onrender.com/api/cart`
**Headers:**
```
Authorization: Bearer YOUR_TOKEN_HERE
```

**Expected Response:**
```json
{
  "id": "cart123",
  "items": [
    {
      "productId": "678f1f77bcf86cd799439022",
      "name": "iPhone 15 Pro Max",
      "price": 1199.99,
      "quantity": 2
    }
  ],
  "totalAmount": 2399.98
}
```

---

### 3.2 Add Item to Cart

**Method:** `POST`
**URL:** `https://webwares.onrender.com/api/cart/items`
**Headers:**
```
Authorization: Bearer YOUR_TOKEN_HERE
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "productId": "678f1f77bcf86cd799439022",
  "quantity": 2
}
```

**More Examples:**

#### Example 1 - Add Electronics:
```json
{
  "productId": "507f1f77bcf86cd799439011",
  "quantity": 1
}
```

#### Example 2 - Add Multiple Items:
```json
{
  "productId": "678f1f77bcf86cd799439023",
  "quantity": 3
}
```

**Notes:**
- `productId`: String (MongoDB ID), required, not blank
- `quantity`: Integer, required, minimum 1

---

### 3.3 Update Item Quantity

**Method:** `PUT`
**URL:** `https://webwares.onrender.com/api/cart/items/{productId}?quantity={newQuantity}`

#### Example 1 - Update to 5 items:
```
PUT https://webwares.onrender.com/api/cart/items/678f1f77bcf86cd799439022?quantity=5
```

#### Example 2 - Update to 1 item:
```
PUT https://webwares.onrender.com/api/cart/items/507f1f77bcf86cd799439011?quantity=1
```

**Headers:**
```
Authorization: Bearer YOUR_TOKEN_HERE
```

---

### 3.4 Remove Item from Cart

**Method:** `DELETE`
**URL:** `https://webwares.onrender.com/api/cart/items/{productId}`

#### Example 1:
```
DELETE https://webwares.onrender.com/api/cart/items/678f1f77bcf86cd799439022
```

#### Example 2:
```
DELETE https://webwares.onrender.com/api/cart/items/507f1f77bcf86cd799439011
```

**Headers:**
```
Authorization: Bearer YOUR_TOKEN_HERE
```

---

## 4. ORDER ENDPOINTS

### 4.1 Place Order

**Method:** `POST`
**URL:** `https://webwares.onrender.com/api/orders`
**Headers:**
```
Authorization: Bearer YOUR_TOKEN_HERE
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "paymentMethod": "RAZORPAY"
}
```

**Other Payment Method Examples:**
```json
{
  "paymentMethod": "COD"
}
```

```json
{
  "paymentMethod": "CARD"
}
```

**Notes:**
- `paymentMethod`: String, required, not blank
- Common values: "RAZORPAY", "COD", "CARD"
- Order is created from current cart items

**Expected Response:**
```json
{
  "id": "order123",
  "items": [
    {
      "productId": "678f1f77bcf86cd799439022",
      "name": "iPhone 15 Pro Max",
      "quantity": 2,
      "price": 1199.99
    }
  ],
  "total": 2399.98,
  "status": "PENDING",
  "paymentId": "pay_abc123",
  "createdAt": "2026-01-31T10:30:00Z"
}
```

---

### 4.2 Get My Orders

**Method:** `GET`
**URL:** `https://webwares.onrender.com/api/orders`
**Headers:**
```
Authorization: Bearer YOUR_TOKEN_HERE
```

---

### 4.3 Get Order by ID

**Method:** `GET`
**URL:** `https://webwares.onrender.com/api/orders/{id}`

#### Example:
```
GET https://webwares.onrender.com/api/orders/678f1f77bcf86cd799439025
```

**Headers:**
```
Authorization: Bearer YOUR_TOKEN_HERE
```

---

### 4.4 Get All Orders (ADMIN ONLY)

**Method:** `GET`
**URL:** `https://webwares.onrender.com/api/orders/admin`
**Headers:**
```
Authorization: Bearer YOUR_ADMIN_TOKEN_HERE
```

---

### 4.5 Update Order Status (ADMIN ONLY)

**Method:** `PUT`
**URL:** `https://webwares.onrender.com/api/orders/admin/{id}`
**Example:** `https://webwares.onrender.com/api/orders/admin/507f1f77bcf86cd799439011?status=SHIPPED`
**Headers:**
```
Authorization: Bearer YOUR_ADMIN_TOKEN_HERE
```

**Query Parameters:**
- `status`: OrderStatus enum (required)

**Possible status values:**
- `PENDING`
- `CONFIRMED`
- `SHIPPED`
- `DELIVERED`
- `CANCELLED`

**Full Example URL:**
```
https://webwares.onrender.com/api/orders/admin/507f1f77bcf86cd799439011?status=SHIPPED
```

---

## 5. PAYMENT ENDPOINTS

### 5.1 Get Razorpay Key

**Method:** `GET`
**URL:** `https://webwares.onrender.com/api/payments/razorpay/key`
**Headers:**
```
Authorization: Bearer YOUR_TOKEN_HERE
```

**Response:**
```json
{
  "keyId": "rzp_test_xxxxxxxx"
}
```

---

### 5.2 Verify Razorpay Payment

**Method:** `POST`
**URL:** `https://webwares.onrender.com/api/payments/razorpay/verify`
**Headers:**
```
Authorization: Bearer YOUR_TOKEN_HERE
Content-Type: application/x-www-form-urlencoded
```

**Body (x-www-form-urlencoded):**
```
razorpay_order_id=order_xxxxxxxxxxxxx
razorpay_payment_id=pay_xxxxxxxxxxxxx
razorpay_signature=xxxxxxxxxxxxxxxxxxxxxxxx
```

**In Postman:**
1. Select "Body" tab
2. Choose "x-www-form-urlencoded"
3. Add the three key-value pairs above

---

### 5.3 Mock Payment Webhook (ADMIN ONLY - for testing)

**Method:** `POST`
**URL:** `https://webwares.onrender.com/api/payments/webhook/mock`
**Headers:**
```
Authorization: Bearer YOUR_ADMIN_TOKEN_HERE
```

**Query Parameters:**
- `orderId`: string (required)
- `status`: string (required) - e.g., "SUCCESS", "FAILED"

**Example URL:**
```
https://webwares.onrender.com/api/payments/webhook/mock?orderId=507f1f77bcf86cd799439011&status=SUCCESS
```

---

## 6. ANALYTICS ENDPOINTS (ADMIN ONLY)

### 6.1 Get Analytics Summary

**Method:** `GET`
**URL:** `https://webwares.onrender.com/api/analytics`
**Headers:**
```
Authorization: Bearer YOUR_ADMIN_TOKEN_HERE
```

**Returns overall analytics data**

---

### 6.2 Get Orders Last 7 Days

**Method:** `GET`
**URL:** `https://webwares.onrender.com/api/analytics/orders/last7days`
**Headers:**
```
Authorization: Bearer YOUR_ADMIN_TOKEN_HERE
```

**Returns daily order statistics for the past week**

---

## TESTING WORKFLOW

### For Regular User:
1. **Register** (1.1) with name, email, password
2. **Login** (1.2) with email, password → Save token
3. **Browse products** (2.1, 2.2) - No auth needed
4. **Add to cart** (3.2) - Use product IDs from step 3
5. **View cart** (3.1)
6. **Update quantities** (3.3) if needed
7. **Place order** (4.1) with payment method
8. **View orders** (4.2, 4.3)
9. **Get Razorpay key** (5.1) for payment
10. **Verify payment** (5.2) after Razorpay callback

### For Admin User:
1. **Login** with admin credentials
2. **Create products** (2.3) with form-data
3. **Update products** (2.4)
4. **View all orders** (4.4)
5. **Update order status** (4.5)
6. **View analytics** (6.1, 6.2)
7. **Mock payments** (5.3) for testing

---

## CRITICAL NOTES

1. **IDs are MongoDB ObjectIds** - They look like: `507f1f77bcf86cd799439011`

2. **Product Create/Update uses multipart/form-data** - NOT JSON
   - Use form-data in Postman
   - All fields as text except image file

3. **Cart operations use productId as STRING** - Not a number

4. **Query parameters** - For update cart quantity and order status

5. **Token format** - Always use: `Bearer YOUR_TOKEN_HERE`

6. **No separate profile update endpoint** - User data managed through auth

7. **Payment flow:**
   - Place order → Get Razorpay key → Process payment → Verify payment

8. **Admin role required** for:
   - Creating/updating/deleting products
   - Viewing all orders
   - Updating order status
   - Analytics endpoints
   - Mock payment webhook

---

## COMMON ERRORS

- **401 Unauthorized** - Token missing or invalid
- **403 Forbidden** - User doesn't have required role (e.g., ADMIN)
- **400 Bad Request** - Validation errors (check required fields)
- **404 Not Found** - Invalid ID or resource doesn't exist

---

## SAMPLE MONGODB IDS FOR TESTING

Use these patterns (replace x with actual IDs from your database):
- Product ID: `507f1f77bcf86cd799439011`
- Order ID: `507f191e810c19729de860ea`
- User ID: `507f1f77bcf86cd799439012`