package com.fx.payment.repository;

import com.fx.payment.entity.PaymentMessage;
import com.fx.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentMessageRepository extends JpaRepository<PaymentMessage, UUID> {

    Optional<PaymentMessage> findByMessageId(String messageId);

    Optional<PaymentMessage> findByTransactionId(String transactionId);

    Optional<PaymentMessage> findByUetr(String uetr);

    List<PaymentMessage> findByStatus(PaymentStatus status);

    boolean existsByMessageId(String messageId);
}
