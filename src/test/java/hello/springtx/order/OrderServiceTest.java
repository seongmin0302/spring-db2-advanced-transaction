package hello.springtx.order;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@SpringBootTest 
public class OrderServiceTest {

    @Autowired 
    OrderService orderService;

    @Autowired 
    OrderRepository orderRepository;

    @Test 
    void complete() throws NotEnoughMoneyException {
        Order order = new Order();
        order.setUsername("정상");

        orderService.order(order);

        Order findOrder = orderRepository.findById(order.getId()).get();
        Assertions.assertThat(findOrder.getPayStatus()).isEqualTo("완료");
    }

    @Test 
    void RuntimeException() {
        Order order = new Order();
        order.setUsername("예외");

        Assertions.assertThatThrownBy(()->orderService.order(order)).isInstanceOf(RuntimeException.class);

        Optional<Order> orderOptional = orderRepository.findById(order.getId());
        Assertions.assertThat(orderOptional.isEmpty()).isTrue();
    }

    @Test 
    void bixException() {
        Order order = new Order();
        order.setUsername("잔고부족");

        try {
            orderService.order(order);
        } catch (NotEnoughMoneyException e) {
            log.info("고객에게 잔고 부족을 알리고 별도의 계좌로 입급하도록 안내합니다.");
        }

        Order findOrder = orderRepository.findById(order.getId()).get();
        Assertions.assertThat(findOrder.getPayStatus()).isEqualTo("대기");
        
    }

}
