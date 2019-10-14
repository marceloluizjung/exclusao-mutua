import java.util.ArrayList;

public class Semaforo {
    private ArrayList<Processo> processos = new ArrayList();
    private boolean isEleicao = false;
    private int idEleito;
    private Processo gerente;
    private boolean processVerify = true;
    private Processo processoAtual;

    public boolean isProcessVerify() {
        return processVerify;
    }

    public void setProcessVerify(boolean processVerify) {
        this.processVerify = processVerify;
    }

    public synchronized boolean isEleicao() {
        return isEleicao;
    }

    public Processo getGerente() {
        return gerente;
    }

    public void setGerente(Processo gerente) {
        this.gerente = gerente;
    }

    public ArrayList<Processo> getProcessos() {
        return processos;
    }

    public void setProcessos(ArrayList<Processo> processos) {
        this.processos = processos;
    }

    public synchronized void eleicao(int id) throws InterruptedException {

        if (!this.isEleicao) {
            try {
                this.isEleicao = true;
                Processo processo = this.processos.get(this.processos.size() - 1);
                for (Processo p : this.processos) {
                    if (p.getId() > processo.getId()) {
                        processo = p;
                    }
                }
                for (Processo p : this.processos) {
                    p.setGerente(processo);
                }
                this.idEleito = (int) processo.getId();
                System.out.println("Solicitação " + this.idEleito);
                processo.testeSaudeInstancia();
                this.setGerente(processo);
            } catch (NullPointerException n) {
                this.isEleicao = false;
                this.eleicao(id);
            }
            this.processVerify = true;
            this.isEleicao = false;
            notifyAll();
        } else {
            if (id != this.idEleito) {
                System.out.println("Parei " + id);
                wait();
            }
        }
    }

    public synchronized boolean requestGerente(Processo processo) {
        try {
            if (this.processVerify) {
                //this.processVerify = false;
                this.processoAtual = processo;

                wait(1000);

                this.gerente.testeSaudeInstancia();

                String response = this.gerente.response(processo);

                if (response.equalsIgnoreCase("OK")) {
                    this.gerente.consumirRecurso(processo);
                }

                this.gerente.liberarRecurso();
                this.processoAtual = null;
                this.processVerify = true;
            } else {
                if (processo.getId() != this.getGerente().getId()) {
                    wait();
                }
            }
            notifyAll();
            return true;
        } catch (NullPointerException | InterruptedException np) {
            this.processVerify = true;
            //notifyAll();
            return false;
        }
    }

    public synchronized void killGerente() {
        this.processos.remove(this.gerente);
        this.gerente.interrupt();
        this.gerente.stop();
        if (this.processoAtual.equals(this.gerente) || !this.isEleicao && this.gerente == null) {
            this.processVerify = true;
        }
        this.gerente = null;
        notifyAll();
    }

    public synchronized void killProcesso() {
        Processo processo = processos.get(0);
        this.processos.remove(processo);
        processo.interrupt();
        processo.stop();
        if (this.processoAtual.equals(processo)) {
            this.processVerify = true;
            notifyAll();
        }
    }

    public synchronized void verifica(Processo gerente) {
        if (this.processos.size() > 1 && (this.gerente != null && !this.gerente.isAlive()) && !this.isEleicao) {
            for (Processo p : this.processos) {
                p.setGerente(gerente);
            }
            this.processVerify = true;
            notifyAll();
        }
    }

}
