package edu.jsp.ecommerce.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import edu.jsp.ecommerce.entity.Address;
import edu.jsp.ecommerce.entity.Cart;
import edu.jsp.ecommerce.entity.CartItem;
import edu.jsp.ecommerce.entity.Inventory;
import edu.jsp.ecommerce.entity.Order;
import edu.jsp.ecommerce.entity.OrderItem;
import edu.jsp.ecommerce.entity.User;
import edu.jsp.ecommerce.enums.OrderStatus;
import edu.jsp.ecommerce.exeception.BadRequestException;
import edu.jsp.ecommerce.exeception.ResourceNotFoundException;
import edu.jsp.ecommerce.repository.AddressRepository;
import edu.jsp.ecommerce.repository.CartRepositroy;
import edu.jsp.ecommerce.repository.OrderRepository;
import edu.jsp.ecommerce.repository.UserRepository;
import edu.jsp.ecommerce.request.OrderRequest;
import edu.jsp.ecommerce.response.OrderResponse;
import edu.jsp.ecommerce.service.CartService;
import edu.jsp.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final UserRepository userRepository;
	private final OrderRepository orderRepository;
	private final CartRepositroy cartRepositroy;
	private final AddressRepository addressRepository;
	private final CartService cartService;

	@Override
	public OrderResponse placeOrder(String email, OrderRequest request) {

		User user = findUser(email);

		Cart cart = cartRepositroy.findByUserId(user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Cart Not found!!!"));

		if (cart.getItems().isEmpty()) {
			throw new BadRequestException("Cart is empty!!!!");
		}

		Address address;
		if (request.getShippingAddressId() != null) {

			address = addressRepository.findById(request.getShippingAddressId())
					.orElseThrow(() -> new ResourceNotFoundException("Address Not found!!!"));

			if (!address.getUser().getId().equals(user.getId())) {
				throw new BadRequestException("Access  denied!!!!");
			}
		} else if (request.getNewAddress() != null) {

			List<Address> addresses = addressRepository.findByUserId(user.getId());

			address = Address.builder().fullName(request.getNewAddress().getFullName())
					.phone(request.getNewAddress().getPhone()).addressLine1(request.getNewAddress().getAddressLine1())
					.addressLine2(request.getNewAddress().getAddressLine2()).city(request.getNewAddress().getCity())
					.state(request.getNewAddress().getState()).postalCode(request.getNewAddress().getPostalCode())
					.country(request.getNewAddress().getCountry()).build();

			if (request.getNewAddress().isDefaultAddress()) {
				addresses.stream().forEach(a -> a.setDefaultAddress(false));
				addressRepository.saveAll(addresses);
			}

			address = addressRepository.save(address);

		} else {
			List<Address> addresses = addressRepository.findByUserId(user.getId());
			address = addresses.stream().filter(a -> a.isDefaultAddress()).findFirst().orElse(addresses.get(0));
		}

		List<CartItem> items = cart.getItems();

		List<OrderItem> orderItems =

				items.stream().map(item -> {
					Inventory inv = item.getProduct().getInventory();
					if (inv == null || inv.getQuantity() < item.getQuantity()) {
						throw new BadRequestException("Insufficient Quantuty!!!!");
					}
					inv.setQuantity(inv.getQuantity() - item.getQuantity());

					BigDecimal totalprice = item.getProduct().getPrice()
							.multiply(BigDecimal.valueOf(item.getQuantity()));

					return OrderItem.builder().product(item.getProduct()).quantity(item.getQuantity())
							.unitPrice(item.getProduct().getPrice()).totalPrice(totalprice).build();

				}).toList();

		BigDecimal total = BigDecimal.ZERO;

		for (OrderItem it : orderItems) {
			total.add(it.getTotalPrice());
		}

		Order order = Order.builder().user(user).totalAmount(total).shippingAddress(address)
				.paymentMethod(request.getPaymentMethod())
				.trackingNumber("TRK - " + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
				.status(OrderStatus.CONFIRMED).items(orderItems).build();

		orderItems.forEach(o -> o.setOrder(order));
		Order saved = orderRepository.save(order);

		cartService.clearCart(email);

		return mapToResponse(order);
	}

	@Override
	public OrderResponse getOrderById(Long id, String email) {

		User user = findUser(email);

		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found!!!"));

		if (!order.getUser().getId().equals(user.getId())) {
			throw new BadRequestException("Access denied!!");
		}

		return mapToResponse(order);
	}

	@Override
	public Page<OrderResponse> getUserOrders(String email, Pageable pageable) {

		User user = findUser(email);

		return orderRepository.findByUserId(user.getId(), pageable).map(this::mapToResponse);
	}

	@Override
	public Page<OrderResponse> getAllOrders(Pageable pageable) {
		return orderRepository.findAll(pageable).map(this::mapToResponse);
	}

	@Override
	public OrderResponse updateOrderStatus(Long id, OrderStatus status) {

		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found!!!"));
		order.setStatus(status);

		return mapToResponse(orderRepository.save(order));
	}

	@Override
	public void cancelOrder(Long id, String email, String reason) {

		User user = findUser(email);
		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));

		if (!order.getUser().getId().equals(user.getId())) {
			throw new BadRequestException("Access denied");
		}
		if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
			throw new BadRequestException("Cannot cancel order in status: " + order.getStatus());
		}

		order.setStatus(OrderStatus.CANCELLED);
		order.setCancellationReason(reason);

		order.getItems().forEach(item -> {
			Inventory inv = item.getProduct().getInventory();
			if (inv != null)
				inv.setQuantity(inv.getQuantity() + item.getQuantity());
		});

		orderRepository.save(order);

	}

	private User findUser(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
	}

	public OrderResponse mapToResponse(Order order) {

		List<OrderResponse.OrderItemResponse> items = order.getItems().stream().map(item -> {
			return OrderResponse.OrderItemResponse.builder().productId(item.getProduct().getId())
					.productName(item.getProduct().getName()).thumbnailUrl(item.getProduct().getThumbnailUrl())
					.quantity(item.getQuantity()).unitPrice(item.getProduct().getPrice())
					.totalPrice(item.getTotalPrice()).build();
		}).toList();

		Address adrs = order.getShippingAddress();

		String shipadrs = adrs != null
				? adrs.getFullName() + " , " + adrs.getAddressLine1() + " , " + adrs.getAddressLine2() + " , "
						+ adrs.getCity() + " , " + adrs.getState() + " , " + adrs.getPostalCode() + " , "
						+ adrs.getPhone()
				: "";

		return OrderResponse.builder().id(order.getId()).status(order.getStatus()).totalAmount(order.getTotalAmount())
				.trackingNumber(order.getTrackingNumber()).paymentMethod(order.getPaymentMethod()).items(items)
				.shippingAddress(shipadrs).createdAt(order.getCreatedAt()).build();

	}

}
