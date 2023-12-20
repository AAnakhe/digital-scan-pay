**Digital Scan Pay**
- Digital Scan Pay is a robust backend service built with Java and the Mutiny and Vert.x framework, with dependencies managed by Spring IOC, designed to facilitate an electronic ticketing system for drivers. The system enables drivers to conveniently pay their daily levies or tickets using a QR code. With features like user and admin sign-up, login authentication using JWT, profile viewing, transaction listing, QR code generation, and payment processing, Digital Scan Pay streamlines the ticket payment process for both users and administrators.

**Features**
1. User and Admin Authentication
Secure user and admin sign-up and login authentication.
JWT-based authentication for enhanced security.
2. Profile Management
Users can view and manage their profiles.
Admins have access to user profiles for efficient management.
3. Transaction Listing
Users can view a list of their transactions.
Admins can access the transaction history for all users.
4. QR Code Generation
Generate unique QR codes for each transaction, ensuring secure and efficient payments.
5. Payment Processing
Enable users to make payments for their tickets seamlessly.

- **TODO:** Integrate with external payment APIs for expanded payment options.

 **Getting Started
Prerequisites**
- Maven
- Java JDK (version 17)
- Spring IOC 
- Vert.x framework 
- Mutiny APIs (version 3.5.0)
Swagger UI for documentation

**Installation**
- git clone  https://github.com/AAnakhe/digital-scan-pay.git

**Documentation**
- Detailed API documentation is available through Swagger UI. Access the documentation at [Swagger Documentation](http://localhost:port/api/docs
)
 after starting the application.
