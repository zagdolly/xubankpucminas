package com.xubank.model;

import java.util.Random;

public class ContaRendaFixa extends Conta {
    private static final double MIN_RENDIMENTO_MENSAL = 0.0050; // 0.50% 
    private static final double MAX_RENDIMENTO_MENSAL = 0.0085; // 0.85% 
    private static final double TAXA_MENSAL = 20.0; // R$20 
    private static final double IMPOSTO_SOBRE_RENDIMENTO = 0.15; // 15% 
    
    @Override
    protected double getTaxaImposto() {
        return IMPOSTO_SOBRE_RENDIMENTO;
    }

    public ContaRendaFixa(String numeroConta, Cliente cliente, double saldoInicial) {
        super(numeroConta, cliente, saldoInicial);
    }

    @Override
    public boolean sacar(double valor) {
        if (valor <= 0) {
            System.out.println("Valor do saque deve ser positivo.");
            return false;
        }

        double impostoDevido = 0;
        if (this.rendimentoNaoTributado > 0) {
            impostoDevido = this.rendimentoNaoTributado * IMPOSTO_SOBRE_RENDIMENTO;
        }

        if (this.saldo >= valor + impostoDevido) {
            if (impostoDevido > 0) {
                this.saldo -= impostoDevido;
                adicionarTransacao(TipoTransacao.IMPOSTO_SOBRE_RENDIMENTO, impostoDevido, String.format("Imposto (%.1f%%) sobre rendimento", IMPOSTO_SOBRE_RENDIMENTO * 100));
                this.rendimentoNaoTributado = 0; // Zera após tributação
                 System.out.printf("Imposto de R$ %.2f debitado. Saldo parcial: R$ %.2f%n", impostoDevido, this.saldo);
            }
            this.saldo -= valor;
            adicionarTransacao(TipoTransacao.SAQUE, valor, "Saque");
            System.out.printf("Saque de R$ %.2f realizado. Novo saldo: R$ %.2f%n", valor, this.saldo);
            return true;
        } else {
            System.out.printf("Saldo insuficiente para saque (R$ %.2f) mais impostos (R$ %.2f). Saldo atual: R$ %.2f%n", valor, impostoDevido, this.saldo);
            return false;
        }
    }

    @Override
    public void aplicarRendimentoMensal(Random random) {
        double taxaRendimentoAtual = MIN_RENDIMENTO_MENSAL + (MAX_RENDIMENTO_MENSAL - MIN_RENDIMENTO_MENSAL) * random.nextDouble();
        if (this.saldo > 0) {
            double rendimento = this.saldo * taxaRendimentoAtual;
            this.saldo += rendimento;
            this.rendimentoNaoTributado += rendimento;
            adicionarTransacao(TipoTransacao.RENDIMENTO, rendimento, String.format("Rendimento mensal (%.4f%%)", taxaRendimentoAtual * 100));
            System.out.printf("Conta %s (%s): Rendimento de R$ %.2f aplicado (taxa %.4f%%). Novo Saldo: R$ %.2f. Rendimento não tributado acumulado: R$ %.2f%n", numeroConta, getTipoConta(), rendimento, taxaRendimentoAtual*100, this.saldo, this.rendimentoNaoTributado);
        }
    }

    @Override
    public void aplicarTaxasMensais() {
        if (this.saldo >= TAXA_MENSAL) {
            this.saldo -= TAXA_MENSAL;
            adicionarTransacao(TipoTransacao.TAXA_MENSAL, TAXA_MENSAL, "Taxa de manutenção mensal");
            System.out.printf("Conta %s (%s): Taxa mensal de R$ %.2f aplicada. Novo Saldo: R$ %.2f%n", numeroConta, getTipoConta(), TAXA_MENSAL, this.saldo);
        } else {
            // O que fazer se não há saldo para a taxa? O problema não especifica.
            // Por ora, apenas não cobra se o saldo for menor que a taxa.
            System.out.printf("Conta %s (%s): Saldo insuficiente (R$ %.2f) para cobrar taxa mensal de R$ %.2f.%n", numeroConta, getTipoConta(), this.saldo, TAXA_MENSAL);
        }
    }
    
    @Override
    public String getTipoConta() {
        return "Conta Renda Fixa";
    }
}