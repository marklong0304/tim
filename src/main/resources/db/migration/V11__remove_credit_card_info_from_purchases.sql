update purchase set purchase_request_json = purchase_request_json::jsonb - 'creditCard';
