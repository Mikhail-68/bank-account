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
    private int id;
    private String accountNumber;

    @JsonIgnore
    @OneToMany(mappedBy = "client")
    private List<Limit> limits;

    public Client(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
