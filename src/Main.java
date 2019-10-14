import javax.sound.midi.SysexMessage;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    public static void main(String[] args) {
        final ArrayList<Processo> processos = new ArrayList();
        final Semaforo semaforo = new Semaforo();

        TimerTask task1 = new TimerTask() {
            int id = 0;
            boolean start = false;

            @Override
            public void run() {
                if (!semaforo.isEleicao() && semaforo.getGerente() != null || !start) {
                    Processo processo = new Processo();
                    processos.add(processo);
                    semaforo.setProcessos(processos);
                    processo.setId(id);
                    processo.setSemaforo(semaforo);
                    processo.start();
                    semaforo.verifica(processo);
                    System.out.println("Criei um processo");
                    id++;
                }
            }
        };

        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                if (semaforo.getGerente() != null && !semaforo.isEleicao()) {
                    semaforo.killGerente();
                    System.out.println("Matei o gerente");
                }
            }
        };

        TimerTask task3 = new TimerTask() {
            @Override
            public void run() {
                if (semaforo.getGerente() != null && !semaforo.isEleicao()) {
                    semaforo.killProcesso();
                    System.out.println("Matei um processo");
                }
            }
        };

        Timer timer = new Timer();
        timer.schedule(task1, 0, 4000);
        timer.schedule(task3, 16000, 16000);
        timer.schedule(task2, 20000, 20000);
    }
}
