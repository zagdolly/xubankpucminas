package com.xubank.service;

import com.xubank.model.*;

import java.util.*;

public class XuBankService {
    private Map<String, Cliente> clientes; // CPF -> Cliente
    private Map<String, Conta> contas;     // NumeroConta -> Conta
    private int proximoNumeroConta = 1001;
    private final Random random = new Random();


    public XuBankService() {
        this.clientes = new HashMap<>();
        this.contas = new HashMap<>();
    }

    private String gerarNumeroConta() {
        return String.valueOf(proximoNumeroConta++);
    }

    public Cliente cadastrarCliente(String nome, String cpf, String senha) {
        if (clientes.containsKey(cpf)) {
            System.out.println("Erro: Cliente com este CPF já cadastrado.");
            return null;
        }
        Cliente novoCliente = new Cliente(nome, cpf, senha);
        clientes.put(cpf, novoCliente);
        System.out.println("Cliente " + nome + " cadastrado com sucesso!");
        return novoCliente;
    }

    public Cliente buscarClientePorCpf(String cpf) {
        return clientes.get(cpf);
    }
    
    public Collection<Cliente> getTodosClientes() {
        return clientes.values();
    }

    private Conta adicionarContaAoSistema(Conta conta) {
        contas.put(conta.getNumeroConta(), conta);
        conta.getCliente().adicionarConta(conta);
        System.out.println(conta.getTipoConta() + " número " + conta.getNumeroConta() + " criada para " + conta.getCliente().getNome());
        return conta;
    }

    public ContaCorrente criarContaCorrente(Cliente cliente, double saldoInicial, double limiteCredito) {
        String numeroConta = gerarNumeroConta();
        ContaCorrente cc = new ContaCorrente(numeroConta, cliente, saldoInicial, limiteCredito);
        return (ContaCorrente) adicionarContaAoSistema(cc);
    }

    public ContaPoupanca criarContaPoupanca(Cliente cliente, double saldoInicial) {
        String numeroConta = gerarNumeroConta();
        ContaPoupanca cp = new ContaPoupanca(numeroConta, cliente, saldoInicial);
        return (ContaPoupanca) adicionarContaAoSistema(cp);
    }

    public ContaRendaFixa criarContaRendaFixa(Cliente cliente, double saldoInicial) {
        String numeroConta = gerarNumeroConta();
        ContaRendaFixa crf = new ContaRendaFixa(numeroConta, cliente, saldoInicial);
        return (ContaRendaFixa) adicionarContaAoSistema(crf);
    }

    public ContaInvestimento criarContaInvestimento(Cliente cliente, double saldoInicial) {
        String numeroConta = gerarNumeroConta();
        ContaInvestimento ci = new ContaInvestimento(numeroConta, cliente, saldoInicial);
        return (ContaInvestimento) adicionarContaAoSistema(ci);
    }
    
    public Conta buscarContaPorNumero(String numeroConta) {
        return contas.get(numeroConta);
    }


    public void realizarDeposito(Conta conta, double valor) {
        if (conta != null) {
            conta.depositar(valor);
        } else {
            System.out.println("Conta não encontrada para depósito.");
        }
    }

    public boolean realizarSaque(Conta conta, double valor) {
        if (conta != null) {
            return conta.sacar(valor);
        } else {
            System.out.println("Conta não encontrada para saque.");
            return false;
        }
    }

    public void processarViradaDeMes() {
        System.out.println("\n--- PROCESSANDO VIRADA DE MÊS ---");
        for (Conta conta : contas.values()) {
            System.out.println("\nProcessando conta: " + conta.getNumeroConta() + " (" + conta.getTipoConta() + ") Saldo: R$ " + String.format("%.2f", conta.getSaldo()));
            conta.aplicarRendimentoMensal(random); // Passa o gerador Random
            conta.aplicarTaxasMensais();
        }
        System.out.println("--- VIRADA DE MÊS CONCLUÍDA ---");
    }
    
    // Métodos para diretoria 
    public Map<String, Double> getValorCustodiaPorTipoConta() {
        Map<String, Double> custodia = new HashMap<>();
        custodia.put("Conta Corrente", 0.0);
        custodia.put("Conta Poupança", 0.0);
        custodia.put("Conta Renda Fixa", 0.0);
        custodia.put("Conta Investimento", 0.0);

        for (Conta conta : contas.values()) {
            custodia.merge(conta.getTipoConta(), conta.getSaldo(), Double::sum);
        }
        return custodia;
    }

    public double getSaldoMedioContas() {
        if (contas.isEmpty()) {
            return 0.0;
        }
        double somaTodosSaldos = contas.values().stream().mapToDouble(Conta::getSaldo).sum();
        return somaTodosSaldos / contas.size();
    }

    public Cliente getClienteMaiorSaldo() {
        if (clientes.isEmpty()) return null;
        return clientes.values().stream()
                .max(Comparator.comparingDouble(Cliente::getSaldoTotalConsolidado))
                .orElse(null);
    }

    public Cliente getClienteMenorSaldo() {
         if (clientes.isEmpty()) return null;
        return clientes.values().stream()
                .min(Comparator.comparingDouble(Cliente::getSaldoTotalConsolidado))
                .orElse(null);
    }
}