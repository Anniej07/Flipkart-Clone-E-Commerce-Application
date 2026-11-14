package edu.jsp.ecommerce.request;

import edu.jsp.ecommerce.enums.PaymentMethod;
import lombok.Data;

@Data
public class OrderRequest {
    private Long shippingAddressId;
    private AddressRequest newAddress;
    private PaymentMethod paymentMethod = PaymentMethod.COD;
}

