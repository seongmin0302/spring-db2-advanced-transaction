package hello.springtx.propagation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@SpringBootTest 
public class MemberServiceTest {
    @Autowired 
    MemberService memberService;
    @Autowired 
    MemberRepository memberRepository;
    @Autowired 
    LogRepository logRepository;

    @Test 
    void outerTxOff_success() {
        String username = "outerTxOff_success";
        memberService.joinV1(username);
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isPresent());
    }

    @Test 
    void outerTxOff_fail() {
        String username = "로그예외_outerTxOff_fail";
        org.assertj.core.api.Assertions.assertThatThrownBy(()->memberService.joinV1(username)).isInstanceOf(RuntimeException.class);

        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isEmpty());
    }

    @Test 
    void singleTx() {
        String username = "singleTx";
        memberService.joinV1(username);

        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isPresent());
    }

     @Test 
    void outerTxOn_success() {
        String username = "outerTxOn_success";
        memberService.joinV1(username);

        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isPresent());
    }

    @Test 
    void outerTxOn_fail() {
        String username = "로그예외_outerTxOn_fail";
        org.assertj.core.api.Assertions.assertThatThrownBy(()->memberService.joinV1(username)).isInstanceOf(RuntimeException.class);

        Assertions.assertTrue(memberRepository.find(username).isEmpty());
        Assertions.assertTrue(logRepository.find(username).isEmpty());
    }

    @Test 
    void recoverException_fail() {
        String username = "로그예외_recoverException_fail";
        org.assertj.core.api.Assertions.assertThatThrownBy(()->memberService.joinV2(username)).isInstanceOf(UnexpectedRollbackException.class);

        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isEmpty());
    }

    @Test 
    void recoverException_success() {
        String username = "로그예외_recoverException_success";
        memberService.joinV2(username);

        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isEmpty());
    }

}
