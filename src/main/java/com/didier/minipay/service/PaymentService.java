package com.didier.minipay.service;

import com.didier.minipay.dto.CreatePaymentRequest;
import com.didier.minipay.dto.PaymentResponse;
import com.didier.minipay.exception.AccountNotFoundException;
import com.didier.minipay.exception.InsufficientFundsException;
import com.didier.minipay.exception.InvalidPaymentException;
import com.didier.minipay.model.Account;
import com.didier.minipay.model.Payment;
import com.didier.minipay.model.PaymentStatus;
import com.didier.minipay.repository.AccountRepository;
import com.didier.minipay.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AccountRepository accountRepository;

    public PaymentService(PaymentRepository paymentRepository, AccountRepository accountRepository) {
        this.paymentRepository = paymentRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public PaymentResponse create(String idempotencyKey, CreatePaymentRequest request) {

        Optional<Payment> existing = paymentRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            return toResponse(existing.get());
        }

        if (request.fromAccountId().equals(request.toAccountId())) {
            throw new InvalidPaymentException("La cuenta origen y destino no pueden ser la misma");
        }

        Long firstId = Math.min(request.fromAccountId(), request.toAccountId());
        Long secondId = Math.max(request.fromAccountId(), request.toAccountId());

        Account first = accountRepository.findByIdForUpdate(firstId)
                .orElseThrow(() -> new AccountNotFoundException(firstId));
        Account second = accountRepository.findByIdForUpdate(secondId)
                .orElseThrow(() -> new AccountNotFoundException(secondId));

        Account from = first.getId().equals(request.fromAccountId()) ? first : second;
        Account to = (from == first) ? second : first;

        if (from.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientFundsException(from.getId());
        }

        from.setBalance(from.getBalance().subtract(request.amount()));
        to.setBalance(to.getBalance().add(request.amount()));

        Payment payment = new Payment();
        payment.setIdempotencyKey(idempotencyKey);
        payment.setFromAccountId(from.getId());
        payment.setToAccountId(to.getId());
        payment.setAmount(request.amount());
        payment.setStatus(PaymentStatus.COMPLETED);

        return toResponse(paymentRepository.save(payment));
    }

    @Transactional(readOnly = true)
    public PaymentResponse findById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new InvalidPaymentException("Pago no encontrado: " + id));
        return toResponse(payment);
    }

    private PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(p.getId(), p.getFromAccountId(), p.getToAccountId(),
                p.getAmount(), p.getStatus());
    }
}