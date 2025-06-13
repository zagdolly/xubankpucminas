package com.xubank.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public abstract class Conta {
    protected String numeroConta;
    protected double saldo;
    protected Cliente cliente;
    protected List<Transacao> historicoTransacoes;
    protected double rendimentoNaoTributado; // Para Renda Fixa e Investimento
    protected LocalDate dataCriacao;


    public Conta(String numeroConta, Cliente cliente, double saldoInicial) {
        this.numeroConta = numeroConta;
        this.cliente = cliente;
        this.saldo = saldoInicial;
        this.historicoTransacoes = new ArrayList<>();
        this.rendimentoNaoTributado = 0.0;
        this.dataCriacao = LocalDate.now();
        if (saldoInicial > 0) {
            adicionarTransacao(TipoTransacao.DEPOSITO, saldoInicial, "Depósito inicial");
        }
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public double getSaldo() {
        return saldo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<Transacao> getHistoricoTransacoes() {
        return historicoTransacoes;
    }
    
    public double getRendimentoNaoTributado() {
        return rendimentoNaoTributado;
    }

    public void adicionarTransacao(TipoTransacao tipo, double valor, String descricao) {
        Transacao transacao = new Transacao(tipo, valor, this.saldo, descricao);
        this.historicoTransacoes.add(transacao);
    }

    public void depositar(double valor) {
        if (valor <= 0) {
            System.out.println("Valor do depósito deve ser positivo.");
            return;
        }
        this.saldo += valor;
        adicionarTransacao(TipoTransacao.DEPOSITO, valor, "Depósito");
        System.out.printf("Depósito de R$ %.2f realizado. Novo saldo: R$ %.2f%n", valor, this.saldo);
    }

    public boolean sacar(double valor) {
        if (valor <= 0) {
            System.out.println("Valor do saque deve ser positivo.");
            return false;
        }
        if (this.saldo >= valor) {
            this.saldo -= valor;
            adicionarTransacao(TipoTransacao.SAQUE, valor, "Saque");
            System.out.printf("Saque de R$ %.2f realizado. Novo saldo: R$ %.2f%n", valor, this.saldo);
            return true;
        } else {
            System.out.println("Saldo insuficiente para saque.");
            return false;
        }
    }
    
    public void debitarImposto(double valorImposto, String descricao) {
        this.saldo -= valorImposto;
        this.rendimentoNaoTributado = Math.max(0, this.rendimentoNaoTributado - valorImposto / getTaxaImposto()); // Aproximação
        adicionarTransacao(TipoTransacao.IMPOSTO_SOBRE_RENDIMENTO, valorImposto, descricao);
    }

    public void zerarRendimentoNaoTributado() {
        this.rendimentoNaoTributado = 0;
    }

    protected abstract double getTaxaImposto(); // Para ser usado em debitarImposto

    public List<Transacao> emitirExtratoUltimoMes() {
        LocalDateTime umMesAtras = LocalDateTime.now().minusMonths(1);
        return historicoTransacoes.stream()
                .filter(t -> t.getData().isAfter(umMesAtras))
                .collect(Collectors.toList());
    }

    public abstract void aplicarRendimentoMensal(Random random);
    public abstract void aplicarTaxasMensais();
    public abstract String getTipoConta();

}