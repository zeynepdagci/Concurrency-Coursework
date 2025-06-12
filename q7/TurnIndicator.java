public class TurnIndicator {
    
    private String currentTurn = "";

    public synchronized String getTurn() {
        return currentTurn;
    }

    public synchronized void setTurn(String gang) {
        System.out.println("Current turn set to " + gang);
        currentTurn = gang;
    }
}