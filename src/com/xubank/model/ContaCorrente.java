package com.xubank.model;

import java.util.Random;

public class ContaCorrente extends Conta {
    private double limiteCredito;

    public ContaCorrente(String numeroConta, Cliente cliente, double saldoInicial, double limiteCredito) {
        super(numeroConta, cliente, saldoInicial);
        this.limiteCredito = limiteCredito;
        adicionarTransacao(TipoTransacao.DEPOSITO, 0, "Limite de crédito definido: R$ " + String.format("%.2f", limiteCredito) );
    }
    
    @Override
    protected double getTaxaImposto() {
        return 0; // Não há imposto sobre rendimento para conta corrente
    }

    public double getLimiteCredito() {
        return limiteCredito;
    }

    @Override
    public void depositar(double valor) {
        if (valor <= 0) {
            System.out.println("Valor do depósito deve ser positivo.");
            return;
        }

        double saldoAnterior = this.saldo;
        this.saldo += valor;
        adicionarTransacao(TipoTransacao.DEPOSITO, valor, "Depósito");
        System.out.printf("Depósito de R$ %.2f realizado. Saldo atualizado: R$ %.2f%n", valor, this.saldo);

        if (saldoAnterior < 0) {
            double valorNegativo = Math.abs(saldoAnterior);
            double taxaFixa = 10.0;
            double taxaPercentual = valorNegativo * 0.03;
            double taxaTotal = taxaFixa + taxaPercentual;

            this.saldo -= taxaTotal;
            adicionarTransacao(TipoTransacao.TAXA_DEPOSITO_SALDO_NEGATIVO, taxaTotal, "Taxa depósito s/ saldo negativo");
            System.out.printf("Cobrada taxa de R$ %.2f por depósito em conta com saldo negativo. Saldo final: R$ %.2f%n", taxaTotal, this.saldo);
        }
    }

    @Override
    public boolean sacar(double valor) {
        if (valor <= 0) {
            System.out.println("Valor do saque deve ser positivo.");
            return false;
        }
        if (this.saldo + this.limiteCredito >= valor) {
            this.saldo -= valor;
            adicionarTransacao(TipoTransacao.SAQUE, valor, "Saque");
            System.out.printf("Saque de R$ %.2f realizado. Novo saldo: R$ %.2f (Limite disponível: R$ %.2f)%n",
                    valor, this.saldo, this.limiteCredito + Math.min(0, this.saldo));
            return true;
        } else {
            System.out.println("Saldo e limite de crédito insuficientes para saque.");
            return false;
        }
    }

    @Override
    public void aplicarRendimentoMensal(Random random) {
        // Conta corrente não tem rendimento 
    }

    @Override
    public void aplicarTaxasMensais() {
        // Conta corrente não tem taxas mensais específicas além da de depósito com saldo negativo 
    }
    
    @Override
    public String getTipoConta() {
        return "Conta Corrente";
    }
}