# Credit and Payment Module

This project is a REST API module that manages credit and payment operations.

## Installation

To install dependencies and run the application, follow these steps:

```sh
mvn clean install
mvn spring-boot:run
```

## API Endpoints

### User and Customer Management

#### Create User
**Endpoint:** `POST /api/createAppUser`

**Authorization:** Only `ADMIN`

**Body:**
```json
{
  "username": "string",
  "password": "string",
  "role": "string"
}
```

#### Create Customer
**Endpoint:** `POST /api/createCustomer`

**Authorization:** Only `ADMIN`

**Body:**
```json
{
  "name": "string",
  "surname": "string",
  "email": "string"
}
```

#### List Customers
**Endpoint:** `GET /api/listCustomers`

**Authorization:** Only `ADMIN`

---

### Credit Management

#### Create Loan
**Endpoint:** `POST /api/createLoan`

**Authorization:** `ADMIN` and `CUSTOMER`

**Body:**
```json
{
  "customerId": "long",
  "amount": "double",
  "term": "int"
}
```

#### List All Loans
**Endpoint:** `GET /api/listAllLoans`

**Authorization:** `ADMIN` and `CUSTOMER`

#### List Loans for a Specific Customer
**Endpoint:** `GET /api/listLoans?customerId={id}`

**Authorization:** `ADMIN` and `CUSTOMER`

#### List Loan Installments
**Endpoint:** `GET /api/listInstallments?loanId={id}`

**Authorization:** `ADMIN` and `CUSTOMER`

---

### Payment Management

#### Pay Loan Installment
**Endpoint:** `POST /api/payLoan`

**Authorization:** `ADMIN` and `CUSTOMER`

**Body:**
```json
{
  "customerId": "long",
  "loanId": "long",
  "amount": "double"
}
```

## Authorization
This API enforces access control based on user roles. `ADMIN` users have full access to all endpoints, while `CUSTOMER` users can only access their own data.

## Contribution
To contribute to this project, please create a `pull request`.

