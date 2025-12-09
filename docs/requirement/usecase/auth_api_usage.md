# Auth API Usage Guide

## 1. /api/auth/login

- **Method:** POST
- **Description:** Authenticate user and return JWT token.
- **Request Body:**

```json
{
  "username": "string",
  "password": "string"
}
```

- **Response (200 OK):**

```json
{
  "token": "<JWT token>",
  "type": "Bearer",
  "id": 1,
  "username": "testuser",
  "email": "testuser@example.com",
  "role": "sales"
}
```

- **Response (401 Unauthorized):**

```json
{
  "message": "Bad credentials"
}
```

- **Security Notes:**
  - Use HTTPS for all requests.
  - JWT token must be stored securely on the client (e.g., HttpOnly cookie or secure storage).
  - Do not expose JWT in URLs or logs.
  - Rate-limit login attempts to prevent brute-force attacks.

## 2. /api/auth/logout

- **Method:** POST
- **Description:** Logout user (stateless, client should delete JWT).
- **Request Body:** _None_
- **Response (200 OK):**

```json
{
  "message": "Successfully logged out"
}
```

- **Security Notes:**
  - Backend is stateless; logout simply instructs client to delete JWT.
  - For higher security, consider implementing a token blacklist (requires persistent storage like Redis).
  - Always clear authentication data on the client after logout.

## Common Error Responses

- **401 Unauthorized:**
  - Invalid credentials or missing/expired JWT.
- **400 Bad Request:**
  - Malformed request body or missing required fields.

## Best Practices

- Always validate input on both client and server.
- Never log sensitive information (passwords, tokens).
- Use strong, random secrets for JWT signing (HS512 requires 64+ chars).
- Set appropriate JWT expiration (e.g., 1 hour) and refresh as needed.
