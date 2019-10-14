import java.util.ArrayList;

public class Processo extends Thread {
    private int id;
    private Processo gerente;
    private Semaforo semaforo;

    private ArrayList<Processo> requestQueue = new ArrayList<>();
    private boolean isConsumido = false;

    public Semaforo getSemaforo() {
        return semaforo;
    }

    public void setSemaforo(Semaforo semaforo) {
        this.semaforo = semaforo;
    }

    public Processo getGerente() {
        return gerente;
    }

    public void setGerente(Processo gerente) {
        this.gerente = gerente;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public synchronized String response(Processo processo) {
        try {
            if (!this.isConsumido) {
                this.isConsumido = true;
                return "OK";
            } else {
                this.requestQueue.add(processo);
                processo.wait(12000);
                this.requestQueue.remove(processo);
                return "";
            }
        } catch (InterruptedException e) {
            return "";
        }
    }

    public synchronized String consumirRecurso(Processo processo) {
        System.out.println("O processo " + processo.getId() + " está consumindo o recurso! > Início");
        try {
            for (int cont = 0; cont < 10; cont++) {
                processo.join(1000);
            }
        } catch (InterruptedException e) {
        }
        System.out.println("Termino processamento");
        return "O processo " + processo.getId() + " está consumindo o recurso!";
    }

    public void liberarRecurso() {
        System.out.println("Liberei visse!!");
        this.isConsumido = false;
        for (Processo processo : this.requestQueue) {
            processo.start();
        }
    }

    public boolean testeSaudeInstancia() {
        return true;
    }

    @Override
    public void run() {
        while (true) {
            try {

                if (this.gerente == null && !this.semaforo.getGerente().testeSaudeInstancia()) {
                    System.out.println("Eleição " + this.id);
                    this.semaforo.eleicao(this.id);
                } else if (this.gerente == null) {
                    this.setGerente(this.semaforo.getGerente());
                } else if (!this.semaforo.isEleicao() && this.semaforo.isProcessVerify()) {
                    if (this.semaforo.requestGerente(this)) {
                    } else {
                        this.semaforo.eleicao(this.id);
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                try {
                    this.semaforo.eleicao(this.id);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
