package com.github.dgaponov99.practicum.mybank.accounts.persistence.repository;

import com.github.dgaponov99.practicum.mybank.accounts.persistence.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
}
