-- Function to calculate credit line based on age
CREATE OR REPLACE FUNCTION calculate_credit_line(date_of_birth DATE) 
RETURNS DECIMAL(10, 2) AS $$
DECLARE
    age INTEGER;
    credit_line DECIMAL(10, 2);
BEGIN
    age := DATE_PART('year', AGE(CURRENT_DATE, date_of_birth));
    
    IF age < 18 OR age > 65 THEN
        RAISE EXCEPTION 'Customer age must be between 18 and 65 years';
    END IF;
    
    IF age >= 18 AND age <= 25 THEN
        credit_line := 3000.00;
    ELSIF age >= 26 AND age <= 30 THEN
        credit_line := 5000.00;
    ELSE
        credit_line := 8000.00;
    END IF;
    
    RETURN credit_line;
END;
$$ LANGUAGE plpgsql;

-- Function to determine payment scheme based on customer data
CREATE OR REPLACE FUNCTION determine_payment_scheme(p_first_name VARCHAR, p_customer_id UUID) 
RETURNS VARCHAR AS $$
DECLARE
    first_char CHAR;
    id_value INTEGER;
BEGIN
    -- Rule 1: First name starts with C, L, or H
    IF p_first_name IS NOT NULL AND LENGTH(p_first_name) > 0 THEN
        first_char := UPPER(LEFT(p_first_name, 1));
        IF first_char IN ('C', 'L', 'H') THEN
            RETURN 'SCHEME_1';
        END IF;
    END IF;
    
    -- Rule 2: Customer ID is greater than 25
    -- Since UUID doesn't have natural ordering, we'll use a hash
    id_value := ABS(('x' || SUBSTR(p_customer_id::TEXT, 2, 8))::BIT(32)::INTEGER) % 100;
    IF id_value > 25 THEN
        RETURN 'SCHEME_2';
    END IF;
    
    -- Default: Scheme 2
    RETURN 'SCHEME_2';
END;
$$ LANGUAGE plpgsql;

-- Function to validate if customer has sufficient credit line
CREATE OR REPLACE FUNCTION validate_credit_line(p_customer_id UUID, p_loan_amount DECIMAL) 
RETURNS BOOLEAN AS $$
DECLARE
    available_amount DECIMAL(10, 2);
BEGIN
    SELECT available_credit_line_amount INTO available_amount
    FROM customers
    WHERE id = p_customer_id;
    
    IF available_amount >= p_loan_amount THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- Function to calculate commission amount based on scheme
CREATE OR REPLACE FUNCTION calculate_commission(p_amount DECIMAL, p_scheme VARCHAR) 
RETURNS DECIMAL(10, 2) AS $$
DECLARE
    interest_rate DECIMAL(5, 2);
    commission DECIMAL(10, 2);
BEGIN
    IF p_scheme = 'SCHEME_1' THEN
        interest_rate := 0.13;
    ELSE
        interest_rate := 0.16;
    END IF;
    
    commission := p_amount * interest_rate;
    RETURN ROUND(commission, 2);
END;
$$ LANGUAGE plpgsql;

-- Function to update customer credit line after loan creation
CREATE OR REPLACE FUNCTION update_credit_line_after_loan() 
RETURNS TRIGGER AS $$
BEGIN
    UPDATE customers
    SET available_credit_line_amount = available_credit_line_amount - NEW.amount
    WHERE id = NEW.customer_id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to update credit line after loan creation
CREATE TRIGGER trigger_update_credit_line_after_loan
AFTER INSERT ON loans
FOR EACH ROW
EXECUTE FUNCTION update_credit_line_after_loan();
