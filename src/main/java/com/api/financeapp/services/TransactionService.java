package com.api.financeapp.services;

import com.api.financeapp.dtos.SingleTransactionDTO;
import com.api.financeapp.entities.SingleTransaction;
import com.api.financeapp.entities.Transaction;
import com.api.financeapp.entities.User;
import com.api.financeapp.repositories.SingleTransactionRepository;
import com.api.financeapp.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SingleTransactionRepository singleTransactionRepository;

    public List<Transaction> getAllTransactions(User user) {
        Optional<List<Transaction>> transactions = transactionRepository.findAllByUser(user);
        return transactions.orElseGet(ArrayList::new);

    }


    public SingleTransactionDTO convertSingleToDTO(SingleTransaction transaction){
        SingleTransactionDTO dto = new SingleTransactionDTO();
        dto.setAmount(transaction.getAmount());
        dto.setDescription(transaction.getDescription());
        dto.setDate(transaction.getDate().toString());
        dto.setCategoryName(transaction.getCategory().getName());
        return dto;
    }
    public List<SingleTransactionDTO> convertSinglesToDTOS(List<SingleTransaction> transactions){
        return transactions
                .stream()
                .map(this::convertSingleToDTO)
                .toList();
    }
    public SingleTransaction createSingleTransaction(SingleTransaction transaction) {
        return singleTransactionRepository.save(transaction);
    }
    public List<SingleTransaction> getAllSingleTransactions(User user) {
        Optional<List<SingleTransaction>> transactions = singleTransactionRepository.findAllByUser(user);
        return transactions.orElseGet(ArrayList::new);

    }
}
