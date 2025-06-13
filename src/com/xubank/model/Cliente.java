package com.xubank.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cliente {
    private String nome;
    private String cpf;
    private String senha;
    private List<Conta> contas;

    public Cliente(String nome, String cpf, String senha) {
        this.nome = nome;
        this.cpf = cpf;
        this.senha = senha; 
        this.contas = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public boolean autenticar(String senhaFornecida) {
        return this.senha.equals(senhaFornecida);
    }

    public void adicionarConta(Conta conta) {
        this.contas.add(conta);
    }

    public List<Conta> getContas() {
        return contas;
    }

    public double getSaldoTotalConsolidado() {
        return contas.stream().mapToDouble(Conta::getSaldo).sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(cpf, cliente.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }

    @Override
    public String toString() {
        return "Cliente {" +
               "nome='" + nome + '\'' +
               ", cpf='" + cpf + '\'' +
               ", saldoTotalConsolidado=R$ " + String.format("%.2f", getSaldoTotalConsolidado()) +
               '}';
    }
}