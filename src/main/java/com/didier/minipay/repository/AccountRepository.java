package com.didier.minipay.repository;

import com.didier.minipay.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AccountRepository extends JpaRepository<Account, Long>{
}

