# NOVIS Healthcare Equipment Assistant - Complete API Documentation

## API Overview

The NOVIS Healthcare Equipment Assistant provides a comprehensive RESTful API service that supports user authentication, product management, order processing, inquiry management, and user administration. All API endpoints use JSON format for data exchange.

## Basic Information

- **Base URL**: `http://localhost:8080/api`
- **Protocol**: HTTP/HTTPS
- **Data Format**: JSON
- **Character Encoding**: UTF-8

## Universal Response Format

All API responses follow this format:

```json
{
  "success": true/false,
  "message": "Description of operation result",
  "data": { ... } // Optional, contains returned data
}
```

## Error Handling

API errors follow HTTP status code standards. Common status codes:

- `200 OK`: Request successful
- `201 Created`: Resource created successfully
- `400 Bad Request`: Invalid request format or parameters
- `401 Unauthorized`: Not authenticated or authentication failed
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Internal server error

## Authentication & Authorization

The system uses JWT (JSON Web Token) for authentication. Authentication flow:

1. Client sends username and password to `/api/auth/login`
2. Server validates and returns JWT token upon success
3. Client includes `Bearer {token}` in subsequent request `Authorization` headers
4. Server validates token validity and permissions

### User Roles

- **partner**: Partners, can create orders and inquiries
- **sales**: Sales personnel, can reply to inquiries and manage products
- **ot**: Occupational therapists, have specific product query and assessment permissions
- **admin**: Administrators, have full system management permissions

---

## 1. Authentication API

### 1.1 User Login

**Endpoint**: `POST /api/auth/login`

**Description**: User authentication, returns JWT token

**Request Parameters**:

```json
{
  "username": "partner_jane",
  "password": "Password1!"
}
```

**Success Response** (200 OK):

```json
{
  "success": true,
  "message": "Successfully login",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "username": "partner_jane",
    "email": "jane@partner.com",
    "role": "partner"
  }
}
```

**Error Response** (400 Bad Request):

```json
{
  "success": false,
  "message": "Invalid username or password",
  "data": null
}
```

### 1.2 User Logout

**Endpoint**: `POST /api/auth/logout`

**Description**: User logout, clears security context

**Request Headers**:

```
Authorization: Bearer {token}
```

**Success Response** (200 OK):

```json
{
  "success": true,
  "message": "Successfully logged out",
  "data": null
}
```

---

## 2. Product API

### 2.1 Get Product List (Paginated)

**Endpoint**: `GET /api/products`

**Description**: Retrieve product list with pagination support

**Query Parameters**:
- `cursor`: Page number, starting from 0, default 0
- `size`: Items per page, default 10

**Example Request**:

```
GET /api/products?cursor=0&size=20
```

**Success Response** (200 OK):

```json
{
  "content": [
    {
      "id": 1,
      "name": "Comfort Foam Cushion",
      "sku": "SKU-CFC001",
      "size": "Standard",
      "description": "A medical-grade foam cushion for wheelchairs.",
      "categoryId": 4,
      "imageUrl": "https://example.com/image1.jpg",
      "features": "Foldable, high-density, washable cover",
      "price": 179.99,
      "hasExtraAttributes": true
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 98,
  "totalPages": 5,
  "last": false,
  "first": true
}
```

### 2.2 Get Product Details by ID

**Endpoint**: `GET /api/products/{id}`

**Description**: Retrieve detailed information for a product by ID

**Path Parameters**:
- `id`: Product ID

**Success Response** (200 OK):

```json
{
  "id": 1,
  "name": "Comfort Foam Cushion",
  "sku": "SKU-CFC001",
  "size": "Standard",
  "description": "A medical-grade foam cushion for wheelchairs.",
  "categoryId": 4,
  "imageUrl": "https://example.com/image1.jpg",
  "features": "Foldable, high-density, washable cover",
  "price": 179.99,
  "hasExtraAttributes": true
}
```

**Error Response** (404 Not Found):

Returns 404 status code when product doesn't exist

### 2.3 Search Products

**Endpoint**: `GET /api/products/search`

**Description**: Search products by keyword in product name

**Query Parameters**:
- `query`: Search keyword

**Example Request**:

```
GET /api/products/search?query=cushion
```

**Success Response** (200 OK):

```json
{
  "success": true,
  "message": null,
  "data": [
    {
      "id": 1,
      "name": "Comfort Foam Cushion",
      "sku": "SKU-CFC001",
      "size": "Standard",
      "description": "A medical-grade foam cushion for wheelchairs.",
      "categoryId": 4,
      "imageUrl": "https://example.com/image1.jpg",
      "features": "Foldable, high-density, washable cover",
      "price": 179.99,
      "hasExtraAttributes": true
    }
  ]
}
```

---

## 3. Category API

### 3.1 Get Top-Level Categories

**Endpoint**: `GET /api/categories`

**Description**: Retrieve all top-level categories (categories without parent)

**Success Response** (200 OK):

```json
[
  {
    "id": 1,
    "name": "Mobility Aids",
    "parentId": null,
    "children": [
      {
        "id": 3,
        "name": "Wheelchairs",
        "parentId": 1
      }
    ]
  },
  {
    "id": 2,
    "name": "Foam Supports",
    "parentId": null,
    "children": [
      {
        "id": 4,
        "name": "Seat Cushions",
        "parentId": 2
      }
    ]
  }
]
```

---

## 4. Order API

### 4.1 Get All Orders (Paginated)

**Endpoint**: `GET /api/orders`

**Description**: Retrieve all orders list, requires admin privileges

**Request Headers**:

```
Authorization: Bearer {admin_token}
```

**Query Parameters**:
- `cursor`: Page number, starting from 0, default 0
- `size`: Items per page, default 10

**Success Response** (200 OK):

```json
{
  "content": [
    {
      "id": 1,
      "userId": "123e4567-e89b-12d3-a456-426614174000",
      "totalAmount": 359.98,
      "status": "pending",
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T10:30:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 42,
  "totalPages": 5
}
```

### 4.2 Get My Orders

**Endpoint**: `GET /api/orders/my`

**Description**: Get order list for currently authenticated user

**Request Headers**:

```
Authorization: Bearer {token}
```

**Success Response** (200 OK):

```json
{
  "success": true,
  "message": null,
  "data": [
    {
      "id": 1,
      "userId": "123e4567-e89b-12d3-a456-426614174000",
      "totalAmount": 359.98,
      "status": "pending",
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T10:30:00"
    }
  ]
}
```

**Error Response** (401 Unauthorized):

```json
{
  "success": false,
  "message": "User not authenticated or user ID not available.",
  "data": null
}
```

### 4.3 Get Order Details by ID

**Endpoint**: `GET /api/orders/{orderId}`

**Description**: Get detailed information for specified order, including order items and product details

**Request Headers**:

```
Authorization: Bearer {token}
```

**Path Parameters**:
- `orderId`: Order ID

**Success Response** (200 OK):

```json
{
  "success": true,
  "message": null,
  "data": {
    "orderId": 1,
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "totalAmount": 359.98,
    "status": "pending",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00",
    "items": [
      {
        "id": 1,
        "productId": 1,
        "productName": "Comfort Foam Cushion",
        "quantity": 2,
        "unitPrice": 179.99,
        "subtotal": 359.98
      }
    ]
  }
}
```

**Error Response** (404 Not Found):

```json
{
  "success": false,
  "message": "Order not found",
  "data": null
}
```

### 4.4 Place Order

**Endpoint**: `POST /api/orders/place`

**Description**: Create new order for currently authenticated user

**Request Headers**:

```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Parameters**:

```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```

**Success Response** (201 Created):

```json
{
  "success": true,
  "message": null,
  "data": {
    "orderId": 1,
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "totalAmount": 1609.48,
    "status": "pending",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00",
    "items": [
      {
        "id": 1,
        "productId": 1,
        "productName": "Comfort Foam Cushion",
        "quantity": 2,
        "unitPrice": 179.99,
        "subtotal": 359.98
      },
      {
        "id": 2,
        "productId": 2,
        "productName": "Lightweight Wheelchair",
        "quantity": 1,
        "unitPrice": 1249.50,
        "subtotal": 1249.50
      }
    ]
  }
}
```

**Error Responses**:

- **401 Unauthorized**: User not authenticated
- **400 Bad Request**: Invalid request parameters or empty order items

---

## 5. Inquiry API

### 5.1 Get All Inquiries (Paginated)

**Endpoint**: `GET /api/enquiries`

**Description**: Retrieve all inquiries list with pagination support

**Query Parameters**:
- `cursor`: Page number, starting from 0, default 0
- `size`: Items per page, default 10

**Success Response** (200 OK):

```json
{
  "content": [
    {
      "id": 1,
      "question": "What is the warranty period for the wheelchair?",
      "answer": "The warranty period is 2 years from date of purchase.",
      "status": "answered",
      "createdBy": "123e4567-e89b-12d3-a456-426614174000",
      "answeredBy": "456e7890-f12c-23d4-b567-789012345678",
      "createdAt": "2024-01-10T09:15:00",
      "answeredAt": "2024-01-10T14:20:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 15,
  "totalPages": 2
}
```

### 5.2 Get My Inquiries (Paginated)

**Endpoint**: `GET /api/enquiries/my`

**Description**: Get inquiries list created by currently authenticated user

**Request Headers**:

```
Authorization: Bearer {token}
```

**Query Parameters**:
- `cursor`: Page number, starting from 0, default 0
- `size`: Items per page, default 10

**Success Response** (200 OK):

```json
{
  "success": true,
  "message": null,
  "data": {
    "content": [
      {
        "id": 1,
        "question": "What is the warranty period for the wheelchair?",
        "answer": "The warranty period is 2 years from date of purchase.",
        "status": "answered",
        "createdBy": "123e4567-e89b-12d3-a456-426614174000",
        "answeredBy": "456e7890-f12c-23d4-b567-789012345678",
        "createdAt": "2024-01-10T09:15:00",
        "answeredAt": "2024-01-10T14:20:00"
      }
    ],
    "totalElements": 3,
    "totalPages": 1
  }
}
```

**Error Response** (401 Unauthorized):

```json
{
  "success": false,
  "message": "User not authenticated.",
  "data": null
}
```

### 5.3 Create Inquiry

**Endpoint**: `POST /api/enquiries/send`

**Description**: Create new inquiry

**Request Headers**:

```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Parameters**:

```json
{
  "question": "What is the weight capacity of the wheelchair?"
}
```

**Success Response** (200 OK):

```json
{
  "success": true,
  "message": null,
  "data": {
    "id": 2,
    "question": "What is the weight capacity of the wheelchair?",
    "answer": null,
    "status": "pending",
    "createdBy": "123e4567-e89b-12d3-a456-426614174000",
    "answeredBy": null,
    "createdAt": "2024-01-15T10:30:00",
    "answeredAt": null
  }
}
```

**Error Responses**:

- **401 Unauthorized**: User not authenticated
- **400 Bad Request**: Question content is empty

```json
{
  "success": false,
  "message": "Question must not be empty.",
  "data": null
}
```

### 5.4 Reply to Inquiry

**Endpoint**: `POST /api/enquiries/{id}/reply`

**Description**: Reply to specified inquiry

**Request Headers**:

```
Authorization: Bearer {token}
Content-Type: application/json
```

**Path Parameters**:
- `id`: Inquiry ID

**Request Parameters**:

```json
{
  "answer": "The maximum weight capacity is 120kg (264 lbs)."
}
```

**Success Response** (200 OK):

```json
{
  "success": true,
  "message": null,
  "data": {
    "id": 2,
    "question": "What is the weight capacity of the wheelchair?",
    "answer": "The maximum weight capacity is 120kg (264 lbs).",
    "status": "answered",
    "createdBy": "123e4567-e89b-12d3-a456-426614174000",
    "answeredBy": "456e7890-f12c-23d4-b567-789012345678",
    "createdAt": "2024-01-15T10:30:00",
    "answeredAt": "2024-01-15T11:45:00"
  }
}
```

**Error Responses**:

- **404 Not Found**: Inquiry does not exist

```json
{
  "success": false,
  "message": "Enquiry not found.",
  "data": null
}
```

- **400 Bad Request**: Answer content is empty

```json
{
  "success": false,
  "message": "Answer must not be empty.",
  "data": null
}
```

---

## 6. User Management API

**Permission Requirement**: All user management operations require admin access only

### 6.1 Get All Users

**Endpoint**: `GET /api/users`

**Description**: Retrieve list of all users in the system

**Request Headers**:

```
Authorization: Bearer {admin_token}
Content-Type: application/json
```

**Success Response** (200 OK):

```json
{
  "success": true,
  "message": "Successfully retrieved all users",
  "data": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "username": "partner_jane",
      "email": "jane@partner.com",
      "role": "partner",
      "createdAt": "2024-01-15T10:30:00",
      "shippingAddress": "123 Main St, City, State",
      "billingAddress": "456 Billing Ave, City, State"
    },
    {
      "id": "456e7890-f12c-23d4-b567-789012345678",
      "username": "sales_john",
      "email": "john@novis.com",
      "role": "sales",
      "createdAt": "2024-01-10T09:15:00",
      "shippingAddress": null,
      "billingAddress": null
    }
  ]
}
```

**Error Responses**:

- **401 Unauthorized**: User not authenticated
- **403 Forbidden**: Insufficient permissions, admin access required

### 6.2 Create User

**Endpoint**: `POST /api/users`

**Description**: Create new user account

**Request Headers**:

```
Authorization: Bearer {admin_token}
Content-Type: application/json
```

**Request Parameters**:

```json
{
  "username": "new_user",
  "email": "newuser@example.com",
  "password": "SecurePassword123",
  "role": "partner",
  "shippingAddress": "Optional shipping address",
  "billingAddress": "Optional billing address"
}
```

**Field Validation**:
- `username`: Required, 3-50 characters, must be unique
- `email`: Required, valid email format, must be unique
- `password`: Required, 6-100 characters
- `role`: Required, must be one of: `partner`, `sales`, `ot`, `admin`
- `shippingAddress`: Optional string
- `billingAddress`: Optional string

**Success Response** (200 OK):

```json
{
  "success": true,
  "message": "User created successfully",
  "data": {
    "id": "789e0123-f45c-67d8-e901-234567890123",
    "username": "new_user",
    "email": "newuser@example.com",
    "role": "partner",
    "createdAt": "2024-01-20T14:25:30",
    "shippingAddress": "Optional shipping address",
    "billingAddress": "Optional billing address"
  }
}
```

**Error Responses**:

- **400 Bad Request**: Username already exists

```json
{
  "success": false,
  "message": "Username already exists",
  "data": null
}
```

- **400 Bad Request**: Email already exists

```json
{
  "success": false,
  "message": "Email already exists",
  "data": null
}
```

### 6.3 Update User Role

**Endpoint**: `PUT /api/users/{id}/role`

**Description**: Update role of specified user

**Request Headers**:

```
Authorization: Bearer {admin_token}
Content-Type: application/json
```

**Path Parameters**:
- `id`: User ID (UUID string)

**Request Parameters**:

```json
{
  "role": "sales"
}
```

**Field Validation**:
- `role`: Required, must be one of: `partner`, `sales`, `ot`, `admin`

**Success Response** (200 OK):

```json
{
  "success": true,
  "message": "User role updated successfully",
  "data": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "username": "partner_jane",
    "email": "jane@partner.com",
    "role": "sales",
    "createdAt": "2024-01-15T10:30:00",
    "shippingAddress": "123 Main St, City, State",
    "billingAddress": "456 Billing Ave, City, State"
  }
}
```

**Error Responses**:

- **404 Not Found**: User does not exist

```json
{
  "success": false,
  "message": "User does not exist",
  "data": null
}
```

### 6.4 Delete User

**Endpoint**: `DELETE /api/users/{id}`

**Description**: Delete specified user account. Admin accounts cannot be deleted

**Request Headers**:

```
Authorization: Bearer {admin_token}
Content-Type: application/json
```

**Path Parameters**:
- `id`: User ID (UUID string)

**Success Response** (200 OK):

```json
{
  "success": true,
  "message": "User deleted successfully",
  "data": "User partner_jane has been deleted"
}
```

**Error Responses**:

- **400 Bad Request**: Cannot delete admin account

```json
{
  "success": false,
  "message": "Cannot delete admin account",
  "data": null
}
```

- **404 Not Found**: User does not exist

```json
{
  "success": false,
  "message": "User does not exist",
  "data": null
}
```

---

## 7. Hello API

### 7.1 Get Greeting Message

**Endpoint**: `GET /api/hello`

**Description**: Get system greeting message, typically used for testing API connectivity

**Success Response** (200 OK):

```json
{
  "success": true,
  "message": "Hello from NOVIS Healthcare Equipment Assistant!",
  "data": null
}
```

**Error Response** (500 Internal Server Error):

```json
{
  "success": false,
  "message": "Service unavailable",
  "data": null
}
```

---

## Permission Control

The system implements role-based permission control:

### User Role Permissions

- **partner**:
  - Can view products and categories
  - Can create and view their own orders
  - Can create inquiries
  - Can view their own inquiries

- **sales**:
  - Has all partner permissions
  - Can reply to inquiries
  - Can view all inquiries

- **ot (Occupational Therapist)**:
  - Has specific product query and assessment permissions
  - Can view detailed product information
  - Can create and reply to inquiries

- **admin**:
  - Has full system management permissions
  - Can perform all user management operations
  - Can view all orders
  - Can manage all inquiries

### User Management Permissions

User management functionality requires the following permissions (admin only):

- `USER_READ`: View all users list
- `USER_CREATE`: Create new users
- `USER_UPDATE`: Modify user roles
- `USER_DELETE`: Delete users (cannot delete admin accounts)

---

## API Security Best Practices

1. **HTTPS**: Always use HTTPS in production environments
2. **JWT Token Management**: 
   - Tokens have expiration times
   - Refresh tokens regularly
   - Store tokens securely
3. **Input Validation**: All inputs are validated
4. **Permission Checks**: Each endpoint performs appropriate permission verification
5. **Password Security**: Passwords are encrypted using BCrypt before storage
6. **Audit Logs**: Record audit logs for important operations

---

## Rate Limiting

To prevent abuse, the API implements the following rate limiting policies:

- Base limit per IP address per minute: 60 requests
- Authentication requests per user per minute: 10 requests
- Exceeding limits returns 429 status code (Too Many Requests)

---

## Common Error Messages

- `"User not authenticated"` - JWT token missing or invalid
- `"Insufficient permissions"` - User has insufficient permissions
- `"Username already exists"` - Attempting to create user with existing username
- `"Email already exists"` - Attempting to create user with existing email
- `"User does not exist"` - Attempting to update/delete non-existent user
- `"Cannot delete admin account"` - Attempting to delete admin user
- `"Order not found"` - Order does not exist
- `"Enquiry not found"` - Inquiry does not exist

---

## API Versioning

Current API version is v1. Future version updates may be implemented through:

1. URL prefix changes, e.g., `/api/v2/...`
2. Version specification in request headers, e.g., `Accept: application/vnd.novis.v2+json`

---

## Support & Contact

For technical support or questions, please contact:
- Technical Team: tech-support@novis.com
- Documentation Version: v1.0
- Last Updated: January 2024