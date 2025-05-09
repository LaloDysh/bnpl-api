package com.bnpl.domain.model.error;

public class BnplException extends RuntimeException {
    private final String code;
    private final String error;
    private final String path;

    public BnplException(String code, String error, String message, String path) {
        super(message);
        this.code = code;
        this.error = error;
        this.path = path;
    }

    public String getCode() {
        return code;
    }

    public String getError() {
        return error;
    }

    public String getPath() {
        return path;
    }

    public static class InsufficientCreditLineException extends BnplException {
        public InsufficientCreditLineException(String path) {
            super("APZ000009", "INSUFFICIENT_CREDIT_LINE", "Insufficient credit line for loan amount", path);
        }
    }

    public static class CustomerNotFoundException extends BnplException {
        public CustomerNotFoundException(String path) {
            super("APZ000005", "CUSTOMER_NOT_FOUND", "Customer not found", path);
        }
    }

    public static class LoanNotFoundException extends BnplException {
        public LoanNotFoundException(String path) {
            super("APZ000008", "LOAN_NOT_FOUND", "Loan not found", path);
        }
    }

    public static class InvalidCustomerRequestException extends BnplException {
        public InvalidCustomerRequestException(String message, String path) {
            super("APZ000002", "INVALID_CUSTOMER_REQUEST", message, path);
        }
    }

    public static class InvalidLoanRequestException extends BnplException {
        public InvalidLoanRequestException(String message, String path) {
            super("APZ000006", "INVALID_LOAN_REQUEST", message, path);
        }
    }

    public static class InvalidAgeException extends BnplException {
        public InvalidAgeException(String path) {
            super("APZ000002", "INVALID_CUSTOMER_REQUEST", "Customer age must be between 18 and 65 years", path);
        }
    }

    public static class UnauthorizedException extends BnplException {
        public UnauthorizedException(String path) {
            super("APZ000007", "UNAUTHORIZED", "Unauthorized access", path);
        }
    }
}
