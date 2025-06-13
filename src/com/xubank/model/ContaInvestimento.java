package com.xubank.model;

import java.util.Random;

public class ContaInvestimento extends Conta {
    private static final double MIN_RENDIMENTO_MENSAL = -0.0060; // -0.60% 
    private static final double MAX_RENDIMENTO_MENSAL = 0.0150;  // 1.50% 
    private static final double IMPOSTO_SOBRE_RENDIMENTO = 0.225; // 22.5% 
    private static final double TAXA_SOBRE_RENDIMENTO_POSITIVO = 0.01; // 1% sobre rendimento mensal positivo 

    public ContaInvestimento(String numeroConta, Cliente cliente, double saldoInicial) {
        super(numeroConta, cliente, saldoInicial);
    }
    
    @Override
    protected double getTaxaImposto() {
        return IMPOSTO_SOBRE_RENDIMENTO;
    }

    @Override
    public boolean sacar(double valor) {
        if (valor <= 0) {
            System.out.println("Valor do saque deve ser positivo.");
            return false;
        }

        double impostoDevido = 0;
        if (this.rendimentoNaoTributado > 0) { // Só tributa rendimento positivo acumulado
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
        double rendimento = this.saldo * taxaRendimentoAtual;

        this.saldo += rendimento;
        if (rendimento > 0) {
            this.rendimentoNaoTributado += rendimento; // Acumula apenas rendimento bruto positivo
        } else if (rendimento < 0 && this.rendimentoNaoTributado > 0) {
             // Se rendimento é negativo, abate do rendimento não tributado acumulado
            this.rendimentoNaoTributado = Math.max(0, this.rendimentoNaoTributado + rendimento);
        }
        
        adicionarTransacao(TipoTransacao.RENDIMENTO, rendimento, String.format("Rendimento mensal (%.4f%%)", taxaRendimentoAtual * 100));
        System.out.printf("Conta %s (%s): Rendimento de R$ %.2f aplicado (taxa %.4f%%). Novo Saldo: R$ %.2f. Rendimento não tributado acumulado: R$ %.2f%n", numeroConta, getTipoConta(), rendimento, taxaRendimentoAtual*100, this.saldo, this.rendimentoNaoTributado);


        if (rendimento > 0) {
            double taxaAdministracao = rendimento * TAXA_SOBRE_RENDIMENTO_POSITIVO;
            this.saldo -= taxaAdministracao;
            adicionarTransacao(TipoTransacao.TAXA_MENSAL, taxaAdministracao, String.format("Taxa de administração (%.0f%% s/ rendimento positivo)", TAXA_SOBRE_RENDIMENTO_POSITIVO * 100));
            System.out.printf("Conta %s (%s): Taxa de administração sobre rendimento positivo de R$ %.2f aplicada. Novo Saldo: R$ %.2f%n", numeroConta, getTipoConta(), taxaAdministracao, this.saldo);
        }
    }

    @Override
    public void aplicarTaxasMensais() {
        // A taxa específica desta conta é sobre o rendimento positivo, já aplicada em aplicarRendimentoMensal.
    }
    
    @Override
    public String getTipoConta() {
        return "Conta Investimento";
    }
}