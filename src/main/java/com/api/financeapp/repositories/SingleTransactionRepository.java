package com.api.financeapp.repositories;

import com.api.financeapp.entities.Category;
import com.api.financeapp.entities.SingleTransaction;
import com.api.financeapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface SingleTransactionRepository extends JpaRepository<SingleTransaction, Long> {
    Optional<List<SingleTransaction>> findAllByUser(User user);

    List<SingleTransaction> findAllByCategoryAndUser(Category category, User user);
    boolean existsByIdAndUser(Long id, User user);

    void deleteByIdAndUser(Long id, User user);
    Optional<List<SingleTransaction>> findAllByUserAndCategory(User user, Category category);

    Optional<List<SingleTransaction>> findAllByUserAndDateBetween(User user, LocalDate start, LocalDate end);

    @Query(
            "SELECT SUM(st.amount) " +
                    "FROM SingleTransaction st " +
                    "WHERE st.user = :user"
    )
    Double sumAmountByUser(User user);

    @Query(
            "SELECT SUM(CASE WHEN st.amount > 0 THEN st.amount ELSE 0 END) " +
                    "FROM SingleTransaction st " +
                    "WHERE st.user = :user AND st.date BETWEEN :start AND :end"
    )
    Double sumIncomeByUserBetween(@Param("user") User user, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query(
            "SELECT SUM(CASE WHEN st.amount < 0 THEN st.amount ELSE 0 END) " +
                    "FROM SingleTransaction st " +
                    "WHERE st.user = :user AND st.date BETWEEN :start AND :end"
    )
    Double sumExpensesByUserBetween(@Param("user") User user, @Param("start") LocalDate start, @Param("end") LocalDate end);
    Optional<SingleTransaction> findByIdAndUser(Long id, User user);

    void deleteAllByCategoryAndUser(Category category, User user);
}
