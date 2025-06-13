package com.xubank.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transacao {
    private final LocalDateTime data;
    private final TipoTransacao tipo;
    private final double valor;
    private final double saldoAposTransacao;
    private final String descricao;

    public Transacao(TipoTransacao tipo, double valor, double saldoAposTransacao, String descricao) {
        this.data = LocalDateTime.now();
        this.tipo = tipo;
        this.valor = valor;
        this.saldoAposTransacao = saldoAposTransacao;
        this.descricao = descricao;
    }

    public LocalDateTime getData() {
        return data;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public double getValor() {
        return valor;
    }

    public double getSaldoAposTransacao() {
        return saldoAposTransacao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return String.format("[%s] %-30s %-10s: R$ %.2f (Saldo: R$ %.2f)",
                data.format(formatter), descricao, tipo, valor, saldoAposTransacao);
    }
}