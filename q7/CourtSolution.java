import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CourtSolution implements ActionListener {
    private JFrame frame = new JFrame();
    private JButton startButton;
    private JButton stopButton;
    private JLabel sharksDisplay;
    private JLabel jetsDisplay;
    private JLabel courtDisplay;
    private JLabel court;
    private JLabel turnIndicator;
    private boolean run = false;
    private JPanel gangsAndCourtPanel;
    private GangThread sharksGang;
    private GangThread jetsGang;

    public CourtSolution() {
        frame = new JFrame("Court Simulation");
        frame.setLayout(new BorderLayout());
        frame.setSize(500, 400);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        court = new JLabel("Basketball Court: Empty", SwingConstants.CENTER);
        turnIndicator = new JLabel("Current Turn: Empty", SwingConstants.CENTER);

        panel.add(court);
        panel.add(turnIndicator);

        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        stopButton.setEnabled(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        startButton.addActionListener(this);
        stopButton.addActionListener(this);
        
        gangsAndCourtPanel = new JPanel();
        gangsAndCourtPanel.setLayout(new GridLayout(1,3));

        sharksDisplay = new JLabel("Sharks: NOT on court", SwingConstants.CENTER);
        sharksDisplay.setBackground(Color.WHITE);
        sharksDisplay.setOpaque(true);

        courtDisplay = new JLabel("Basketball Court: Empty", SwingConstants.CENTER);
        courtDisplay.setBackground(Color.WHITE);
        courtDisplay.setOpaque(true);

        jetsDisplay = new JLabel("Jets: NOT on court", SwingConstants.CENTER);
        jetsDisplay.setBackground(Color.WHITE);
        jetsDisplay.setOpaque(true);

        // gangsAndCourtPanel is created and three labels added which display gangs and court information
        gangsAndCourtPanel.add(sharksDisplay);
        gangsAndCourtPanel.add(courtDisplay);
        gangsAndCourtPanel.add(jetsDisplay);

        frame.add(gangsAndCourtPanel, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.NORTH); 
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Officer Krupke's solution");
        frame.setVisible(true);
    }

    private void start() {        
        run = true;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);

        BasketballCourt basketballCourt = new BasketballCourt();
        TurnIndicator turn = new TurnIndicator();
        RedHandkerchief sharksRedHandkerchief = new RedHandkerchief();
        RedHandkerchief jetsRedHandkerchief = new RedHandkerchief();

        //Sharks and Jets threads are started
        sharksGang = new GangThread("Sharks", "Jets", basketballCourt, turn, sharksRedHandkerchief, jetsRedHandkerchief){
            @Override
            public void run() {
                while(run) {
                    gangRun(sharksGang, turn, basketballCourt);  

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }       
            }
        };

        jetsGang = new GangThread("Jets", "Sharks", basketballCourt, turn, jetsRedHandkerchief, sharksRedHandkerchief){
            @Override
            public void run() {
                while(run) {
                    gangRun(jetsGang, turn, basketballCourt);  

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }       
            }
        };

        sharksGang.start();
        jetsGang.start();
    }

    public void gangRun(GangThread gang, TurnIndicator turn, BasketballCourt basketballCourt){
        String ourGang = gang.getOurGang();
        String otherGang = gang.getOtherGang();
        JLabel gangDisplay;
        JLabel gangText;

        if(gang.getOurGang().equals("Sharks")) {
            gangDisplay = sharksDisplay;
        } else {
            gangDisplay = jetsDisplay;
        }

        synchronized (turn) {
            /* Our gang ties red handkerchief.
            tieRed action is done */
            gang.getOurHandkerchief().tieRed();
            gangDisplay.setText(ourGang + ": tied red handkerchief");
            gangDisplay.setBackground(Color.RED);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }

            /* Our gang set the turn to other gang.
            setTurnIndicator action is done */
            turn.setTurn(otherGang);

            SwingUtilities.invokeLater(() -> {
                court.setText("Court: Empty");
                turnIndicator.setText("Current Turn: " + otherGang);
            });

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }

            /* If the other gang has tied red handkerchief and
                if it is their turn then our gang has to wait.
                checkOtherHandkerchief & checkTurnIndicator actions are done*/
            while (gang.getOtherHandkerchief().isRedTied() && turn.getTurn().equals(otherGang)) {
                try {
                    System.out.println(ourGang + " waiting and checking handkerchief and turn");
                    gangDisplay.setText(ourGang + " waiting and checking handkerchief and turn");
                    gangDisplay.setBackground(Color.YELLOW);
                    turn.wait();

                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
            }

            /* Our gang notifies the other gang that they tied red handkerchief and set the turn to them
                because otherwise they can both set the turn at the same time which leads to conflict */
            // Since there is only one another thread, notify() can be used instead of notifyAll()
            turn.notify();
        }

        /* If other gang has no red handkerchief tied or it is not the other gang's turn 
            then our gang can proceed with entering the basketball court.
            goToCourt action is done */
        if(!gang.getOtherHandkerchief().isRedTied() || !turn.getTurn().equals(otherGang)) {
            basketballCourt.goToCourt(ourGang);
            gangDisplay.setText(ourGang + " goes to court");
            gangDisplay.setBackground(Color.GREEN);
            courtDisplay.setText("Basketball Court: " + ourGang);
            courtDisplay.setBackground(Color.GREEN);

            SwingUtilities.invokeLater(() -> {
                court.setText("Court: Occupied by " + ourGang);
            });

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            basketballCourt.leaveCourt(ourGang);
            gangDisplay.setText(ourGang + " leaves the court");
            gangDisplay.setBackground(Color.LIGHT_GRAY);
            courtDisplay.setText("Basketball Court: Empty");
            courtDisplay.setBackground(Color.WHITE);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }

            /*untieRed action is done when our gang leaves the court */
            synchronized (turn) {
                gang.getOurHandkerchief().untieRed();
                gangDisplay.setText(ourGang + " untied red handkerchief");
                gangDisplay.setBackground(Color.WHITE);
                /* Once our gang untied their red handkerchief, they notify the other gang
                    so that if the other gang is waiting to go to court and if it is their
                    turn, they can check and go to court accordingly */
                // Since there is only one another thread, notify() can be used instead of notifyAll()
                turn.notify();
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }

            gangDisplay.setText(ourGang + " NOT on court");
            gangDisplay.setBackground(Color.WHITE);

            SwingUtilities.invokeLater(() -> {
                court.setText("Court: Empty");
                turnIndicator.setText("Current Turn: " + otherGang);
            });
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
    }

    /* If the start button is clicked, gang threads start running
     If the stop button is clicked, threads stop running */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            start();
            stopButton.setEnabled(true);
        } else if (e.getSource() == stopButton) {
            run = false;
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
        }
    }

    public static void main(String[] args) {
        new CourtSolution();
    }
}



