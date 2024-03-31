package ru.egorov.bankaccount.bank.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String accountNumber;

    @JsonIgnore
    @OneToMany(mappedBy = "client")
    private List<Limit> limits;
    @JsonIgnore
    @OneToMany(mappedBy = "client")
    private List<Transaction> transactions;

    public Client(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
