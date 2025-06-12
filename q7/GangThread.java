public class GangThread extends Thread {
    private String ourGang;
    private String otherGang;
    private BasketballCourt court;
    private TurnIndicator turnIndicator;
    private RedHandkerchief ourHandkerchief;
    private RedHandkerchief othersHandkerchief;

    public GangThread(String ourGang, String otherGang, BasketballCourt court, TurnIndicator turnIndicator, RedHandkerchief ourHandkerchief, RedHandkerchief othersHandkerchief) {
        this.ourGang = ourGang;
        this.otherGang = otherGang;
        this.court = court;
        this.turnIndicator = turnIndicator;
        this.ourHandkerchief = ourHandkerchief;
        this.othersHandkerchief = othersHandkerchief;
    }

    public RedHandkerchief getOurHandkerchief() {
        return ourHandkerchief;
    }

    public RedHandkerchief getOtherHandkerchief() {
        return othersHandkerchief;
    }

    public String getOurGang() {
        return ourGang;
    }

    public String getOtherGang() {
        return otherGang;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (turnIndicator) {

                ourHandkerchief.tieRed();

                String otherGang = ourGang.equals("Sharks") ? "Jets" : "Sharks";

                turnIndicator.setTurn(otherGang);

                while (othersHandkerchief.isRedTied() && turnIndicator.getTurn().equals(otherGang)) {
                    try {
                        turnIndicator.wait();
                    } catch (InterruptedException e) {
                    }
                }

                court.goToCourt(ourGang);

                court.leaveCourt(ourGang);

                ourHandkerchief.untieRed();

                turnIndicator.notify();
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }
    }
}