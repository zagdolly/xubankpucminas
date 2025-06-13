package com.xubank.model;

import java.util.Random;

public class ContaPoupanca extends Conta {
    private static final double RENDIMENTO_MENSAL = 0.0060; // 0.60% 

    public ContaPoupanca(String numeroConta, Cliente cliente, double saldoInicial) {
        super(numeroConta, cliente, saldoInicial);
    }
    
    @Override
    protected double getTaxaImposto() {
        return 0; // Sem imposto 
    }

    @Override
    public void aplicarRendimentoMensal(Random random) {
        if (this.saldo > 0) {
            double rendimento = this.saldo * RENDIMENTO_MENSAL;
            this.saldo += rendimento;
            // Na poupança, o rendimento não é tributado no saque, então não acumula em rendimentoNaoTributado.
            adicionarTransacao(TipoTransacao.RENDIMENTO, rendimento, String.format("Rendimento mensal (%.2f%%)", RENDIMENTO_MENSAL * 100));
             System.out.printf("Conta %s (%s): Rendimento de R$ %.2f aplicado. Novo Saldo: R$ %.2f%n", numeroConta, getTipoConta(), rendimento, this.saldo);
        }
    }

    @Override
    public void aplicarTaxasMensais() {
        // Sem taxas mensais específicas 
    }
    
    @Override
    public String getTipoConta() {
        return "Conta Poupança";
    }
}