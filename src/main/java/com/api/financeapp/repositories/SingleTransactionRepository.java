package com.api.financeapp.repositories;

import com.api.financeapp.dtos.CategoryStatsDTO;
import com.api.financeapp.entities.SingleTransaction;
import com.api.financeapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface SingleTransactionRepository extends JpaRepository<SingleTransaction, Long> {

    boolean existsByIdAndUser(Long id, User user);


    Optional<List<SingleTransaction>> findAllByUserAndDateBetweenOrderByDateDesc(User user, LocalDate start, LocalDate end);

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



    @Query("SELECT new com.api.financeapp.dtos.CategoryStatsDTO(t.category, SUM(t.amount)) " +
            "FROM SingleTransaction t WHERE t.user = :user AND t.date BETWEEN :start AND :end AND t.amount > 0 " +
            "GROUP BY t.category")
    List<CategoryStatsDTO> sumPositiveByCategoryAndUserBetween(@Param("user") User user,
                                                               @Param("start") LocalDate start,
                                                               @Param("end") LocalDate end);

    @Query("SELECT new com.api.financeapp.dtos.CategoryStatsDTO(t.category, SUM(t.amount)) " +
            "FROM SingleTransaction t WHERE t.user = :user AND t.date BETWEEN :start AND :end AND t.amount < 0 " +
            "GROUP BY t.category")
    List<CategoryStatsDTO> sumNegativeByCategoryAndUserBetween(@Param("user") User user,
                                                               @Param("start") LocalDate start,
                                                               @Param("end") LocalDate end);

    List<String> findDistinctCategoriesByUser(User user);

    Optional<SingleTransaction> findByIdAndUser(Long id, User user);


    List<SingleTransaction> findAllByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);

}
